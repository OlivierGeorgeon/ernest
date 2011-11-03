package spas;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import imos.IAct;
import ernest.Ernest;
import ernest.ITracer;

/**
 * The spatial system.
 * Maintains the local space map and the persistence memory.
 * @author Olivier
 */
public class Spas implements ISpas 
{
	
	/** The Tracer. */
	private ITracer m_tracer = null; 

	/** Ernest's internal clock  */
	private int m_clock;

	/** Ernest's persistence momory  */
	private PersistenceMemory m_persistenceMemory = new PersistenceMemory();
	
	/** Ernest's local space memory  */
	private LocalSpaceMemory m_localSpaceMemory = new LocalSpaceMemory();
	
	/** The visual stimulation in front of Ernest  */
	IStimulation m_frontVisualStimulation;

	/** The list of saliences in Ernest's locals space memory  */
	ArrayList<ISalience> m_salienceList;

	/** The anticipated local map  */
	ISalience m_focusSalience = null;
	
	/** The anticipated local map  */
	IBundle m_focusBundle = null;
	
	IStimulation m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_FORWARD;
	IStimulation m_gustatoryStimulation = Ernest.STIMULATION_GUSTATORY_NOTHING;
	
	public void setTracer(ITracer tracer) 
	{
		m_tracer = tracer;
		m_persistenceMemory.setTracer(tracer);
	}

	public IObservation step(IAct act, IStimulation[] visualCortex,
			IStimulation[][] tactileCortex, IStimulation kinematicStimulation,
			IStimulation gustatoryStimulation) 
	{
		// Tick the clock
		m_persistenceMemory.tick();
		if (m_tracer != null)
			m_tracer.addEventElement("clock", m_clock + "");		
		m_clock++;
		
		m_gustatoryStimulation = gustatoryStimulation;
		m_kinematicStimulation = kinematicStimulation;

		// Construct the new observation from the previous one.
		m_focusSalience = null;
		m_focusBundle = null;
		IObservation observation = new Observation();
		observation.setGustatory(gustatoryStimulation);
		observation.setKinematic(kinematicStimulation);

		// Update the local space memory
		m_localSpaceMemory.update(act, kinematicStimulation);
		m_localSpaceMemory.Trace(m_tracer);

		List<ISalience> saliences = new ArrayList<ISalience>();
		
		// Get the list of saliences. 
		
		saliences = getSaliences(visualCortex, tactileCortex);
		//saliences = m_salienceList;

		// Find the most attractive salience in the list (abs value) (There is at least a wall)
		
		int maxAttractiveness = 0;
		float direction = 0;
		for (ISalience salience : saliences)
			if (Math.abs(salience.getAttractiveness()) > Math.abs(maxAttractiveness))
			{
				maxAttractiveness = salience.getAttractiveness();
				direction = salience.getDirection();
				m_focusSalience = salience;
				m_focusBundle = salience.getBundle();
			}

		observation.setAttractiveness(maxAttractiveness);
		observation.setDirection(direction);
		
		// Bundle the tactile stimulation with the kinematic stimulation.

		IBundle frontBundle = m_localSpaceMemory.getBundle(LocalSpaceMemory.DIRECTION_AHEAD);
		IBundle hereBundle = m_localSpaceMemory.getBundle(LocalSpaceMemory.DIRECTION_HERE);
		
		if (kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_BUMP) )
		{
			if (frontBundle == null)
			{
				if (tactileCortex[1][0].equals(Ernest.STIMULATION_TOUCH_WALL))
				{
					if (m_frontVisualStimulation == null)
					{
						IBundle b = m_persistenceMemory.createTactoKinematicBundle(tactileCortex[1][0], Ernest.STIMULATION_KINEMATIC_BUMP);
						m_localSpaceMemory.addLocation(b, LocalSpaceMemory.DIRECTION_AHEAD);
					}
					else
					{
						IBundle b = m_persistenceMemory.addBundle(m_frontVisualStimulation, tactileCortex[1][0], Ernest.STIMULATION_KINEMATIC_BUMP, Ernest.STIMULATION_GUSTATORY_NOTHING);
						m_localSpaceMemory.addLocation(b, LocalSpaceMemory.DIRECTION_AHEAD);
					}
				}
			}
			else if (frontBundle.getTactileStimulation().equals(Ernest.STIMULATION_TOUCH_WALL))
				m_persistenceMemory.addKinematicStimulation(frontBundle, kinematicStimulation);
		}

		// Bundle the tactile stimulation with the gustatory stimulation
		
		if (gustatoryStimulation.equals(Ernest.STIMULATION_GUSTATORY_FISH))
		{
			// Discrete environment. The fish bundle is the hereBundle.
			if (hereBundle == null)
			{
				IBundle b = m_persistenceMemory.createTactoGustatoryBundle(Ernest.STIMULATION_TOUCH_FISH, Ernest.STIMULATION_GUSTATORY_FISH);
				m_localSpaceMemory.addLocation(b, LocalSpaceMemory.DIRECTION_HERE);
			}
			else if (hereBundle.getTactileStimulation().equals(Ernest.STIMULATION_TOUCH_FISH))
			{
				m_persistenceMemory.addGustatoryStimulation(hereBundle, gustatoryStimulation);
				m_localSpaceMemory.addLocation(hereBundle, LocalSpaceMemory.DIRECTION_AHEAD);
			}
			
			// Continuous environment. The fish bundle is the frontBundle
			if (frontBundle == null) // Continuous environment. 
			{
				IBundle b = m_persistenceMemory.createTactoGustatoryBundle(Ernest.STIMULATION_TOUCH_FISH, Ernest.STIMULATION_GUSTATORY_FISH);
				m_localSpaceMemory.addLocation(b, LocalSpaceMemory.DIRECTION_AHEAD);
			}
			else if (frontBundle.getTactileStimulation().equals(Ernest.STIMULATION_TOUCH_FISH))
			{
				m_persistenceMemory.addGustatoryStimulation(frontBundle, gustatoryStimulation);
				m_localSpaceMemory.addLocation(frontBundle, LocalSpaceMemory.DIRECTION_AHEAD);
			}
		}
		
		// If the current stimulation does not match the anticipated local map then the local map is cleared.
		// TODO The criteria to decide whether the matching is correct or incorrect need to be learned ! 

		if ((hereBundle != null && hereBundle.getTactileStimulation().equals(Ernest.STIMULATION_TOUCH_WALL)) ||
			(frontBundle != null && !frontBundle.getTactileStimulation().equals(tactileCortex[1][0])))
			//m_observation.clearMap();
			m_localSpaceMemory.clear();

		// Bundle the visual stimulation with the tactile stimulation.
		
		if (m_frontVisualStimulation != null )
		{
			if (frontBundle == null)
			{
				if (!tactileCortex[1][0].equals(Ernest.STIMULATION_TOUCH_EMPTY))		
				{
					IBundle bundle = m_persistenceMemory.createVisioTactileBundle(m_frontVisualStimulation, tactileCortex[1][0]);
					m_localSpaceMemory.addLocation(bundle, LocalSpaceMemory.DIRECTION_AHEAD);
				}
			}
			else
			{
				m_persistenceMemory.addVisualStimulation(frontBundle, m_frontVisualStimulation);
				m_localSpaceMemory.addLocation(frontBundle, LocalSpaceMemory.DIRECTION_AHEAD);
			}
		}
		m_frontVisualStimulation = null;	
		
		// Trace the spatial update.
		if (m_tracer != null) 
		{
			Object e = m_tracer.addEventElement("focus");
			m_tracer.addSubelement(e, "salience", m_focusSalience.getHexColor());
			
			if (m_focusBundle != null)
				m_tracer.addSubelement(e, "bundle", m_focusBundle.getHexColor());
		}

		return observation;
	}
	
	public IStimulation addStimulation(int type, int value) 
	{
		return m_persistenceMemory.addStimulation(type, value);
	}

	public int getValue(int i, int j)
	{
		if (i == 1 && j == 0 && Ernest.STIMULATION_KINEMATIC_BUMP.equals(m_kinematicStimulation))
			return Ernest.STIMULATION_KINEMATIC_BUMP.getValue();
		else if (i == 1 && j == 1 && Ernest.STIMULATION_GUSTATORY_FISH.equals(m_gustatoryStimulation))
			return Ernest.STIMULATION_GUSTATORY_FISH.getValue();
		else
		{
			Vector3f position = new Vector3f(1 - j, i - 1, 0);
			return m_localSpaceMemory.getValue(position);
		}
	}

	/**
	 * Get the list of the saliences in the environment.
	 * @param visualCortex The visual cortex.
	 * @param tactileCortex The tactile cortex.
	 * @return The salience list.
	 */
	private List<ISalience> getSaliences(IStimulation[] visualCortex,
				IStimulation[][] tactileCortex)
	   {
		// Create a List of the various saliences in the visual field

		List<ISalience> saliences = new ArrayList<ISalience>(Ernest.RESOLUTION_COLLICULUS);

		IStimulation stimulation = visualCortex[0];
		int span = 1;
		int sumDirection = 0;
		float theta = - 11 * (float)Math.PI / 24; 
		float sumDirectionf = theta;
		float spanf = (float)Math.PI / 12;
		for (int i = 1 ; i < Ernest.RESOLUTION_RETINA; i++)
		{
			theta += (float)Math.PI / 12;
			if (visualCortex[i].equals(stimulation))
			{
				// measure the salience span and average direction
				span++;
				sumDirection += i * 10;
               sumDirectionf += theta;
               spanf += (float)Math.PI / 12;
			}
			else 
			{	
				// Record the previous salience
				ISalience salience = new Salience(stimulation.getValue(), 0, sumDirectionf / span, 0, spanf);
				salience.setBundle(m_persistenceMemory.seeBundle(stimulation));
				salience.setAttractiveness(m_persistenceMemory.attractiveness(stimulation) + 5 * span );
				saliences.add(salience);
				if (salience.getValue() == visualCortex[5].getValue() && salience.getValue() == visualCortex[6].getValue())
					m_frontVisualStimulation = stimulation;
				// look for the next salience
				stimulation = visualCortex[i];
				span = 1;
				sumDirection = i * 10;
       		spanf = (float)Math.PI / 12;
       		sumDirectionf = theta;
			}
		}
		// record the last salience
		ISalience last = new Salience(stimulation.getValue(), 0, sumDirectionf / span, 0, spanf);
		last.setBundle(m_persistenceMemory.seeBundle(stimulation));
		last.setAttractiveness(m_persistenceMemory.attractiveness(stimulation) + 5 * span );
		saliences.add(last);
		if (last.getValue() == visualCortex[5].getValue() && last.getValue() == visualCortex[6].getValue())
			m_frontVisualStimulation = stimulation;
			//frontColor = stimulation.getColor();

		// Tactile salience of fish 
		// Generates fictitious bundles when touching a fish (this helps).
		// TODO use touch fish-eat bundles		
		//m_observation.setMap(tactileCortex);
		//IBundle bundleFish = m_persistenceMemory.touchBundle(Ernest.STIMULATION_TOUCH_FISH);
		//m_observation.setTactileMap(bundleFish);
		
		// Tactile salience of walls.
		
		//ISalience tactileSalience = m_observation.getTactileSalience();
		ISalience tactileSalience = getTactileSalience(tactileCortex);
		if (tactileSalience != null)
			saliences.add(tactileSalience);

	   return saliences;
   }

    /**
     * Check from salient tactile features in Ernest's tactile map. 
     * So far, only detects walls.
     * TODO: more general tactile salience map.
     * @param tactileMap The tactile cortex.
     * @return The tactile salience. Null if no wall in front of Ernest. 
     */
   private ISalience getTactileSalience(IStimulation[][] tactileMap)
    {
    	ISalience salience = null;
	
        IStimulation[] tactileStimulations = new Stimulation[7];
        tactileStimulations[0] = tactileMap[2][2];
        tactileStimulations[1] = tactileMap[2][1];
        tactileStimulations[2] = tactileMap[2][0];
        tactileStimulations[3] = tactileMap[1][0];
        tactileStimulations[4] = tactileMap[0][0];
        tactileStimulations[5] = tactileMap[0][1];
        tactileStimulations[6] = tactileMap[0][2];

        int span = 0;
        int sumDirection = 0;
        float theta = - 3 * (float)Math.PI /4; 
        float sumDirectionf = 0;
        float spanf = 0;
        boolean front = false;
        for (int i = 0 ; i < 7; i++)
        {
        	if (tactileStimulations[i].equals(Ernest.STIMULATION_TOUCH_WALL))
        	{
				// measure the salience span and average direction
        		span++;
                sumDirection += i * 10;
                sumDirectionf += theta;
                spanf += (float)Math.PI / 4;
                if (i == 3) // Ernest's front
                	front = true;
        	}
        	else
        	{
        		// record the previous salience if it is frontal
        		if (front)
		        {
    				salience = new Salience(Ernest.STIMULATION_TOUCH_WALL.getValue(), 0, sumDirectionf / span, 0, spanf);
					IBundle b = m_persistenceMemory.touchBundle(Ernest.STIMULATION_TOUCH_WALL);
					salience.setDirection(sumDirectionf / span);
					salience.setSpan(spanf);
					if (b != null)
					{
						salience.setBundle(b);
						salience.setAttractiveness(b.getAttractiveness(m_clock));
						salience.setValue(b.getValue());
					}
					else
						salience.setAttractiveness(Ernest.ATTRACTIVENESS_OF_HARD);
		        }
        		
        		// look for the next salience
        		front = false;
        		span = 0;
        		sumDirection = 0;
        		spanf = 0;
        		sumDirectionf = 0;
        	}
        	theta += (float)Math.PI / 4;
        }
		// record the last salience if it is frontal
		if (front)
        {
			salience = new Salience(Ernest.STIMULATION_TOUCH_WALL.getValue(), 0, sumDirectionf / span, 0, spanf);
			IBundle b = m_persistenceMemory.touchBundle(tactileStimulations[6]);
			salience.setDirection(sumDirectionf / spanf);
			salience.setSpan(spanf);
			if (b != null)
			{
				salience.setBundle(b);
				salience.setAttractiveness(b.getAttractiveness(m_clock));
				salience.setValue(b.getValue());
			}
			else
				salience.setAttractiveness(Ernest.ATTRACTIVENESS_OF_HARD);
        }
        return salience;
    }
   
	/**
	 * @param salienceList The list of saliences in Ernest's colliculus.
	 */
	public void setSalienceList(ArrayList<ISalience> salienceList)
	{
		m_salienceList = salienceList;
		
		// Find the salience's attractiveness.
		for (ISalience salience : m_salienceList)
		{
			if (salience.getType() == Ernest.MODALITY_VISUAL)
			{
				IStimulation stimulation = new Stimulation(Ernest.MODALITY_VISUAL, salience.getValue());
				IBundle b = m_persistenceMemory.seeBundle(stimulation);
				salience.setBundle(b);
				salience.setAttractiveness(m_persistenceMemory.attractiveness(stimulation) + (int)(5 * salience.getSpan()) );
			}
			else
			{
				
				IStimulation stimulation = new Stimulation(Ernest.MODALITY_TACTILE, salience.getValue());
				IBundle b = m_persistenceMemory.touchBundle(stimulation);
				if (b != null)
				{
					salience.setBundle(b);
					salience.setAttractiveness(b.getAttractiveness(m_clock));
					//salience.setValue(b.getValue());
				}
				else
					salience.setAttractiveness(Ernest.ATTRACTIVENESS_OF_HARD);
			}
		}
	}
}
