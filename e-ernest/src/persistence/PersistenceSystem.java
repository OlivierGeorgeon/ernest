package persistence;

import imos.IAct;

import java.util.ArrayList;
import java.util.List;
import ernest.Ernest;
import ernest.IObservation;
import ernest.ISalience;
import ernest.ITracer;
import ernest.Observation;
import ernest.Salience;

/**
 * The Persistence System contains all the bundles ever created.
 * It offers methods to record and refresh new bundles.
 * @author ogeorgeon
 */
public class PersistenceSystem 
{

	/** The Tracer. */
	private ITracer m_tracer = null; 

	/** Gray bundle that can arise curiosity */	
	public static IBundle BUNDLE_TOUCH_FISH = Bundle.createTactoGustatoryBundle(Ernest.STIMULATION_TOUCH_FISH, Ernest.STIMULATION_GUSTATORY_FISH);
	
	/** Ernest's internal clock  */
	private int m_clock;

	/** A list of all the stimulations ever identified. */
	public List<IStimulation> m_stimulations = new ArrayList<IStimulation>(20);
	
	/** A list of all the bundles ever identified. */
	public List<IBundle> m_bundles = new ArrayList<IBundle>(20);
	
	/** The current observations */
	private IObservation m_observation = new Observation();
	
	/** The anticipated observation */
	private IObservation m_anticipation = new Observation();
	
	/**
	 * @param tracer The tracer
	 */
	public void setTracer(ITracer tracer)
	{
		m_tracer = tracer;
	}

	/**
	 * Tick Ernest's clock.
	 * Used to simulate a decay in Static memory
	 */
	public void tick()
	{
		if (m_tracer != null)
			m_tracer.addEventElement("clock", m_clock + "");
		
		m_clock++;
	}
	
	/**
	 * @return Ernest's current time
	 */
	public int getClock()
	{
		return m_clock;
	}
	
	/**
	 * Add a stimulation to static memory if it does not already exist
	 * @param red Component of the landmark's color
	 * @param green Component of the landmark's color
	 * @param blue Component of the landmark's color
	 * @param distance The distance of the stimulation (or intensity)
	 * @return the new stimulation if created or the already existing landmark
	 */
	public IStimulation addStimulation(int red, int green, int blue, int distance)
	{
		IStimulation l = new Stimulation(red,green,blue,distance);
		
		int i = m_stimulations.indexOf(l);
		if (i == -1)
			// The landmark does not exist
			m_stimulations.add(l);
		else 
		{
			// The landmark already exists: return a pointer to it.
			l =  m_stimulations.get(i);
			l.setDistance(distance);
		}
		return l;
	}
	
	/**
	 * Add a bundle to static memory if it does not already exist
	 * @param visualStimulation The bundle's visual stimulation.
	 * @param tactileStimulation The bundle's tactile stimulation.
	 * @return the new bundle if created or the already existing bundle.
	 */
	public IBundle addBundle(IStimulation visualStimulation, IStimulation tactileStimulation)
	{
		IBundle bundle = Bundle.createVisioTactileBundle(visualStimulation,tactileStimulation);
		
		int i = m_bundles.indexOf(bundle);
		if (i == -1)
		{
			m_bundles.add(bundle);
			if (m_tracer != null) {
				bundle.trace(m_tracer, "bundle");
			}
		}
		else 
			// The bundle already exists: return a pointer to it.
			bundle =  m_bundles.get(i);
		
		bundle.setLastTimeBundled(m_clock);

		return bundle;
	}
	
	/**
	 * Add a stimulation to static memory if it does not already exist
	 * @param type The stimulation's type
	 * @param value The stimulation's value
	 * @return the new landmark if created or the already existing landmark
	 */
	public IStimulation addStimulation(int type, int value)
	{
		IStimulation l = new Stimulation(type, value);
		
		int i = m_stimulations.indexOf(l);
		if (i == -1)
			// The landmark does not exist
			m_stimulations.add(l);
		else 
			// The landmark already exists: return a pointer to it.
			l =  m_stimulations.get(i);
		return l;
	}
	
	/**
	 * Update the current Observation based on the anticipated observation and on to the sensory stimulations.
	 * @param visualCortex The set of visual stimulations in the visual cortex.
	 * @param tactileCortex The set of tactile stimulations in the tactile cortex.
	 * @param kinematicStimulation The kinematic stimulation.
	 * @param gustatoryStimulation The gustatory stimulation.
	 * @return A pointer to the current observation that has been updated.
	 */
	public IObservation adjust(IStimulation[] visualCortex, IStimulation[][] tactileCortex, IStimulation kinematicStimulation, IStimulation gustatoryStimulation)
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
				ISalience salience = new Salience();
				salience.setDirection((int) (sumDirection / span + .5));
				salience.setDistance(stimulation.getDistance());
				salience.setSpan(span);
				salience.setValue(stimulation.getValue());
				salience.setBundle(seeBundle(stimulation));
				salience.setAttractiveness(attractiveness(stimulation) + 5 * span );
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
		ISalience last = new Salience();
		last.setDirection((int) (sumDirection / span + .5));
		last.setDistance(stimulation.getDistance());
		last.setSpan(span);
		last.setValue(stimulation.getValue());
		last.setBundle(seeBundle(stimulation));
		last.setAttractiveness(attractiveness(stimulation) + 5 * span );
		saliences.add(last);
		if (last.getDirection() >= 50 &&  last.getDirection() <= 60 && span >= 3 )
			frontVisualStimulation = stimulation;
			//frontColor = stimulation.getColor();

		// Tactile salience of fish 
		// Generates fictitious bundles when touching a fish (this helps).
		// TODO use touch fish-eat bundles
		
		m_observation.setMap(tactileCortex);
		m_observation.setTactileMap();
		
		// Tactile salience of walls.
		
		ISalience tactileSalience = m_observation.getTactileSalience();
		if (tactileSalience != null)
			saliences.add(tactileSalience);
		

		// Add the various saliences in the local map to the list
		// Each bundle in the local map creates a salience.
		
		if (m_observation.getBundle(1, 0) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(55);
			salience.setSpan(4);
			salience.setValue(m_observation.getBundle(1, 0).getValue());
			salience.setBundle(m_observation.getBundle(1, 0));
			salience.setAttractiveness(m_observation.getBundle(1, 0).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(0, 0) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(85);
			salience.setSpan(4);
			salience.setValue(m_observation.getBundle(0, 0).getValue());
			salience.setBundle(m_observation.getBundle(0, 0));
			salience.setAttractiveness(m_observation.getBundle(0, 0).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(2, 0) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(25);
			salience.setSpan(4);
			salience.setValue(m_observation.getBundle(2, 0).getValue());
			salience.setBundle(m_observation.getBundle(2, 0));
			salience.setAttractiveness(m_observation.getBundle(2, 0).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(0, 1) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(110);
			salience.setSpan(4);
			salience.setValue(m_observation.getBundle(0, 1).getValue());
			salience.setBundle(m_observation.getBundle(0, 1));
			salience.setAttractiveness(m_observation.getBundle(0, 1).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(2, 1) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(0);
			salience.setSpan(4);
			salience.setValue(m_observation.getBundle(2, 1).getValue());
			salience.setBundle(m_observation.getBundle(2, 1));
			salience.setAttractiveness(m_observation.getBundle(2, 1).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(0, 2) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(140);
			salience.setSpan(4);
			salience.setValue(m_observation.getBundle(0, 2).getValue());
			salience.setBundle(m_observation.getBundle(0, 2));
			salience.setAttractiveness(m_observation.getBundle(0, 2).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(2, 2) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(-25);
			salience.setSpan(4);
			salience.setValue(m_observation.getBundle(2, 2).getValue());
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
		if (gustatoryStimulation.equals(Ernest.STIMULATION_GUSTATORY_FISH))
		{
			if (m_observation.getBundle(1, 1) != null && !m_observation.getBundle(1, 1).getGustatoryStimulation().equals(Ernest.STIMULATION_GUSTATORY_FISH))
			{
				m_observation.getBundle(1, 1).setGustatoryStimulation(gustatoryStimulation);
				m_observation.getBundle(1, 1).trace(m_tracer, "bundle");				
			}
		}
		
		// Kinematic
		
		m_observation.setConfirmation(kinematicStimulation.equals(m_observation.getKinematic()));
		m_observation.setKinematic(kinematicStimulation);

		// If the current stimulation does not match the anticipated local map then the local map is cleared.
		// TODO The criteria for deciding whether the matching is correct or incorrect need to be learned ! 

		if (m_observation.getBundle(1, 1) != null && m_observation.getBundle(1, 1).getTactileStimulation().equals(Ernest.STIMULATION_TOUCH_WALL))
			m_observation.clearMap();
		
		// Check peripersonal space.
		
		m_observation.setTactileMap();

		// Bundle the visual icon with the tactile stimulation in front
		
		if (frontVisualStimulation != null )
		{
			if (!tactileCortex[1][0].equals(Ernest.STIMULATION_TOUCH_EMPTY))		
			{
				IBundle bundle = addBundle(frontVisualStimulation, tactileCortex[1][0]);
				m_observation.setFrontBundle(bundle);
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
		
	/**
	 * TODO manage different bundles with more than one visual salience 
	 * @param stimulation The stimulation
	 * @return The motivation value generated by this stimulation.
	 * (Either the bundle's motivation or the base motivation if this stimulation evokes no bundle.
	 */
	private int attractiveness(IStimulation stimulation)
	{
		// Walls are never attractive
		if (stimulation.equals(Ernest.STIMULATION_VISUAL_WALL))
			return 0;
		
		// Recognized bundles return their attractiveness (depends on time elapsed since last check)
		for (IBundle bundle : m_bundles)
			if (bundle.getVisualStimulation().equals(stimulation))
				return bundle.getAttractiveness(m_clock);

		// Stimulations not recognized get the attractiveness of unknown.
		return Ernest.ATTRACTIVENESS_OF_UNKNOWN;
	}

	/**
	 * TODO manage bundles that have no visual stimulations.
	 * TODO manage different bundles with more than one visual salience. 
	 * @param stimulation The visual stimulation.
	 * @return The bundle that match this stimulation.
	 */
	private IBundle seeBundle(IStimulation stimulation)
	{
		for (IBundle bundle : m_bundles)
			if (bundle.getVisualStimulation().equals(stimulation))
				return bundle;

		return null;
	}

	/**
	 * @param stimulation The visual stimulation.
	 * @return The bundle that match this stimulation.
	 */
	private IBundle touchBundle(IStimulation stimulation)
	{
		for (IBundle bundle : m_bundles)
			if (bundle.getTactileStimulation().equals(stimulation))
				return bundle;

		return null;
	}

	/**
	 * Generate the anticipated observation from the previous observation and the current intention.
	 * @param act The act whose effects we want to anticipate.
	 * @return A pointer to the anticipated observation.
	 */
	public IObservation anticipate(IAct act)
	{
		m_anticipation = new Observation();
		m_anticipation.anticipate(m_observation, act);
		return m_anticipation;
	}

	/**
	 * @return The anticipated observation
	 */
	public IObservation getAnticipation()
	{
		//return m_observation;
		return m_anticipation;
	}
	
	/**
	 * 
	 */
	public void resetAnticipation()
	{
		m_anticipation = m_observation;
	}
}
	
