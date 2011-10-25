package spas;

import java.util.ArrayList;
import java.util.List;

import imos.IAct;
import ernest.Ernest;
import ernest.ITracer;

public class Spas implements ISpas 
{
	
	/** The Tracer. */
	private ITracer m_tracer = null; 

	/** Ernest's internal clock  */
	private int m_clock;

	/** Ernest's persistence momory  */
	private PersistenceMemory m_persistenceMemory = new PersistenceMemory();
	
	/** The current local map  */
	private IObservation m_observation  = new Observation();;

	/** The anticipated local map  */
	private IObservation m_anticipation = new Observation();;

	public void setTracer(ITracer tracer) 
	{
		m_tracer = tracer;
		m_persistenceMemory.setTracer(tracer);
	}

	public void tick() 
	{
		m_persistenceMemory.tick();
		if (m_tracer != null)
			m_tracer.addEventElement("clock", m_clock + "");
		
		m_clock++;
	}

	public void resetAnticipation() 
	{
		m_anticipation = m_observation;
	}

	public IObservation anticipate(IAct act) 
	{
		m_anticipation = new Observation();
		m_anticipation.anticipate(m_observation, act);
		return m_anticipation;
	}

	public IObservation getAnticipation() 
	{
		return m_anticipation;
	}

	public IStimulation addStimulation(int red, int green, int blue,
			int distance) 
	{
		return m_persistenceMemory.addStimulation(red, green, blue, distance);
	}

	public IStimulation addStimulation(int type, int value) 
	{
		return m_persistenceMemory.addStimulation(type, value);
	}

	public IObservation adjust(IStimulation[] visualCortex,
			IStimulation[][] tactileCortex, IStimulation kinematicStimulation,
			IStimulation gustatoryStimulation) 
	{
		m_observation = m_anticipation;

		List<ISalience> saliences = new ArrayList<ISalience>(Ernest.RESOLUTION_COLLICULUS);
		//EColor frontColor = null;
		IStimulation frontVisualStimulation = null;
		
		// Create a List of the various saliences in the visual field

		IStimulation stimulation = visualCortex[0];
		int span = 1;
		int sumDirection = 0;
		for (int i = 1 ; i < Ernest.RESOLUTION_RETINA; i++)
		{
			if (visualCortex[i].equals(stimulation))
			{
				// measure the salience span and average direction
				span++;
				sumDirection += i * 10;
			}
			else 
			{	
				// record the previous salience
				ISalience salience = new Salience(stimulation.getValue(), (int) (sumDirection / span + .5), span);
				//salience.setDirection((int) (sumDirection / span + .5));
				salience.setDistance(stimulation.getDistance());
				//salience.setSpan(span);
				//salience.setValue(stimulation.getValue());
				salience.setBundle(m_persistenceMemory.seeBundle(stimulation));
				salience.setAttractiveness(m_persistenceMemory.attractiveness(stimulation) + 5 * span );
				saliences.add(salience);
				if (salience.getDirection() >= 50 &&  salience.getDirection() <= 60 && span >= 3 )
					//frontColor = stimulation.getColor();
					frontVisualStimulation = stimulation;
				// look for the next salience
				stimulation = visualCortex[i];
				span = 1;
				sumDirection = i * 10;
			}
		}
		// record the last salience
		ISalience last = new Salience(stimulation.getValue(), (int) (sumDirection / span + .5), span);
		//last.setDirection((int) (sumDirection / span + .5));
		last.setDistance(stimulation.getDistance());
		//last.setSpan(span);
		//last.setValue(stimulation.getValue());
		last.setBundle(m_persistenceMemory.seeBundle(stimulation));
		last.setAttractiveness(m_persistenceMemory.attractiveness(stimulation) + 5 * span );
		saliences.add(last);
		if (last.getDirection() >= 50 &&  last.getDirection() <= 60 && span >= 3 )
			frontVisualStimulation = stimulation;
			//frontColor = stimulation.getColor();

		// Tactile salience of fish 
		// Generates fictitious bundles when touching a fish (this helps).
		// TODO use touch fish-eat bundles
		
		m_observation.setMap(tactileCortex);
		IBundle bundleFish = m_persistenceMemory.touchBundle(Ernest.STIMULATION_TOUCH_FISH);
		m_observation.setTactileMap(bundleFish);
		//m_observation.setTactileMap(BUNDLE_TOUCH_FISH);
		
		// Tactile salience of walls.
		
		ISalience tactileSalience = m_observation.getTactileSalience();
		if (tactileSalience != null)
			saliences.add(tactileSalience);
		

		// Add the various saliences in the local map to the list
		// Each bundle in the local map creates a salience.
		
		if (m_observation.getBundle(1, 0) != null)
		{
			ISalience salience = new Salience(m_observation.getBundle(1, 0).getValue(), 55, 4);
			//salience.setDirection(55);
			//salience.setSpan(4);
			//salience.setValue(m_observation.getBundle(1, 0).getValue());
			salience.setBundle(m_observation.getBundle(1, 0));
			salience.setAttractiveness(m_observation.getBundle(1, 0).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(0, 0) != null)
		{
			ISalience salience = new Salience(m_observation.getBundle(0, 0).getValue(), 85, 4);
			//salience.setDirection(85);
			//salience.setSpan(4);
			//salience.setValue(m_observation.getBundle(0, 0).getValue());
			salience.setBundle(m_observation.getBundle(0, 0));
			salience.setAttractiveness(m_observation.getBundle(0, 0).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(2, 0) != null)
		{
			ISalience salience = new Salience(m_observation.getBundle(2, 0).getValue(), 25, 4);
			//salience.setDirection(25);
			//salience.setSpan(4);
			//salience.setValue(m_observation.getBundle(2, 0).getValue());
			salience.setBundle(m_observation.getBundle(2, 0));
			salience.setAttractiveness(m_observation.getBundle(2, 0).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(0, 1) != null)
		{
			ISalience salience = new Salience(m_observation.getBundle(0, 1).getValue(), 110, 4);
			//salience.setDirection(110);
			//salience.setSpan(4);
			//salience.setValue(m_observation.getBundle(0, 1).getValue());
			salience.setBundle(m_observation.getBundle(0, 1));
			salience.setAttractiveness(m_observation.getBundle(0, 1).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(2, 1) != null)
		{
			ISalience salience = new Salience(m_observation.getBundle(2, 1).getValue(), 0, 4);
			//salience.setDirection(0);
			//salience.setSpan(4);
			//salience.setValue(m_observation.getBundle(2, 1).getValue());
			salience.setBundle(m_observation.getBundle(2, 1));
			salience.setAttractiveness(m_observation.getBundle(2, 1).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(0, 2) != null)
		{
			ISalience salience = new Salience(m_observation.getBundle(0, 2).getValue(), 140, 4);
			//salience.setDirection(140);
			//salience.setSpan(4);
			//salience.setValue(m_observation.getBundle(0, 2).getValue());
			salience.setBundle(m_observation.getBundle(0, 2));
			salience.setAttractiveness(m_observation.getBundle(0, 2).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(2, 2) != null)
		{
			ISalience salience = new Salience(m_observation.getBundle(2, 2).getValue(), -25, 4);
			//salience.setDirection(-25);
			//salience.setSpan(4);
			//salience.setValue(m_observation.getBundle(2, 2).getValue());
			salience.setBundle(m_observation.getBundle(2, 2));
			salience.setAttractiveness(m_observation.getBundle(2, 2).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		
		// Find the most attractive salience in the list (There is at least a wall)
		
		int maxAttractiveness = 0;
		int direction = 0;
		for (ISalience salience : saliences)
			if (Math.abs(salience.getAttractiveness()) > Math.abs(maxAttractiveness))
			{
				maxAttractiveness = salience.getAttractiveness();
				direction = salience.getDirection();
				m_observation.setSalience(salience);
				m_observation.setFocusBundle(salience.getBundle());
			}

		m_observation.setAttractiveness(maxAttractiveness);
		m_observation.setDirection(direction);
		
		// Taste
		
		m_observation.setGustatory(gustatoryStimulation);

		// Kinematic
		
		m_observation.setConfirmation(kinematicStimulation.equals(m_observation.getKinematic()));
		m_observation.setKinematic(kinematicStimulation);
		
		// Bundle the tactile stimulation with the kinematic stimulation
		
		if (kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_BUMP))
		{
			if (m_observation.getBundle(1, 0) == null)
				m_persistenceMemory.createTactoKinematicBundle(tactileCortex[1][0], Ernest.STIMULATION_KINEMATIC_BUMP);
			else
			{
				m_persistenceMemory.addKinematicStimulation(m_observation.getBundle(1, 0), kinematicStimulation);
				//m_observation.getBundle(1, 0).setKinematicStimulation(kinematicStimulation);
				//m_observation.getBundle(1, 0).trace(m_tracer, "bundle");
			}
		}

		// Bundle the tactile stimulation with the gustatory stimulation
		
		if (gustatoryStimulation.equals(Ernest.STIMULATION_GUSTATORY_FISH))
		{
			if (m_observation.getBundle(1, 1) == null)
				m_persistenceMemory.createTactoGustatoryBundle(tactileCortex[1][1], Ernest.STIMULATION_GUSTATORY_FISH);				
			else
			{
				m_persistenceMemory.addGustatoryStimulation(m_observation.getBundle(1, 1), gustatoryStimulation);
				//m_observation.getBundle(1, 1).setGustatoryStimulation(gustatoryStimulation);
				//m_observation.getBundle(1, 1).trace(m_tracer, "bundle");
			}
		}
		
		// If the current stimulation does not match the anticipated local map then the local map is cleared.
		// TODO The criteria for deciding whether the matching is correct or incorrect need to be learned ! 

		if (m_observation.getBundle(1, 1) != null && m_observation.getBundle(1, 1).getTactileStimulation().equals(Ernest.STIMULATION_TOUCH_WALL))
			m_observation.clearMap();

		// Bundle the visual stimulation with the tactile stimulation in front
		
		if (frontVisualStimulation != null )
		{
			if (m_observation.getBundle(1, 0) == null)
			{
				if (!tactileCortex[1][0].equals(Ernest.STIMULATION_TOUCH_EMPTY))		
				{
					IBundle bundle = m_persistenceMemory.createVisioTactileBundle(frontVisualStimulation, tactileCortex[1][0]);
					m_observation.setFrontBundle(bundle);
				}
			}
			else
			{
				m_persistenceMemory.addVisualStimulation(m_observation.getBundle(1, 0), frontVisualStimulation);
				//m_observation.getBundle(1, 0).setVisualStimulation(frontVisualStimulation);
				//m_observation.getBundle(1, 0).trace(m_tracer, "bundle");
			}
		}
//		else
//			if (tactileCortex[1][0].equals(Ernest.STIMULATION_TOUCH_WALL) && m_observation.getKinematic().equals(Ernest.STIMULATION_KINEMATIC_BUMP))		
//			{
//				IBundle bundle = addBundle(EColor.BLACK, Ernest.STIMULATION_TOUCH_WALL);
//				bundle.setKinematicStimulation(Ernest.STIMULATION_KINEMATIC_BUMP);
//				m_observation.setFrontBundle(bundle);
//			}
				
		return m_observation;
	}
	
}
