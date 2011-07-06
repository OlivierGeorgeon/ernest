package ernest;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;


/**
 * Ernest's static system contains all the landmarks ever created.
 * It offers methods to record and refresh new landmarks.
 * @author ogeorgeon
 */
public class StaticSystem 
{

	/** The Tracer. */
	private ITracer m_tracer = null; 

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
	 * @param color The bundle's color
	 * @param tactileStimulation Second stimulation
	 * @return the new bundle if created or the already existing landmark
	 */
	public IBundle addBundle(EColor color, IStimulation tactileStimulation)
	{
		IBundle bundle = new Bundle(color,tactileStimulation);
		
		int i = m_bundles.indexOf(bundle);
		if (i == -1)
		{
			m_bundles.add(bundle);
			if (m_tracer != null) {
				Object b = m_tracer.addEventElement("bundle");
				m_tracer.addSubelement(b, "color", bundle.getHexColor());
				m_tracer.addSubelement(b, "tactile", bundle.getTactileStimulation().getValue() + "");
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
	 * Update the current Observation based on the anticiapted observatio and on to the sensory stimulations.
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
		EColor frontColor = null;

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
				salience.setColor(stimulation.getColor());
				salience.setAttractiveness(attractiveness(stimulation) + 5 * span );
				saliences.add(salience);
				if (salience.getDirection() >= 50 &&  salience.getDirection() <= 60 && sumDirection >= 3 )
					frontColor = stimulation.getColor();

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
		last.setColor(stimulation.getColor());
		last.setAttractiveness(attractiveness(stimulation) + 5 * span );
		saliences.add(last);
		if (last.getDirection() >= 50 &&  last.getDirection() <= 60 && sumDirection >= 3 )
			frontColor = stimulation.getColor();

		// The somatotopic map
		
		m_observation.setMap(tactileCortex);
		m_observation.setTactileMap();
		
		// Add the various saliences in the local map to the list
		if (m_observation.getBundle(1, 0) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(55);
			salience.setSpan(4);
			salience.setColor(m_observation.getBundle(1, 0).getColor());
			salience.setAttractiveness(m_observation.getBundle(1, 0).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(0, 0) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(85);
			salience.setSpan(4);
			salience.setColor(m_observation.getBundle(0, 0).getColor());
			salience.setAttractiveness(m_observation.getBundle(0, 0).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(2, 0) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(25);
			salience.setSpan(4);
			salience.setColor(m_observation.getBundle(2, 0).getColor());
			salience.setAttractiveness(m_observation.getBundle(2, 0).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(0, 1) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(110);
			salience.setSpan(4);
			salience.setColor(m_observation.getBundle(0, 1).getColor());
			salience.setAttractiveness(m_observation.getBundle(0, 1).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(2, 1) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(0);
			salience.setSpan(4);
			salience.setColor(m_observation.getBundle(2, 1).getColor());
			salience.setAttractiveness(m_observation.getBundle(2, 1).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(0, 2) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(140);
			salience.setSpan(4);
			salience.setColor(m_observation.getBundle(0, 2).getColor());
			salience.setAttractiveness(m_observation.getBundle(0, 2).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		else if (m_observation.getBundle(2, 2) != null)
		{
			ISalience salience = new Salience();
			salience.setDirection(-25);
			salience.setSpan(4);
			salience.setColor(m_observation.getBundle(2, 2).getColor());
			salience.setAttractiveness(m_observation.getBundle(2, 2).getAttractiveness(m_clock) + 20);
			saliences.add(salience);
		}
		
		ISalience tactileSalience = m_observation.getTactileSalience();
		if (tactileSalience != null)
			saliences.add(tactileSalience);
		
		// Find the most attractive salience in the list (There is at least a wall)
		
		int maxAttractiveness = 0;
		int direction = 0;
		for (ISalience salience : saliences)
			if (Math.abs(salience.getAttractiveness()) > Math.abs(maxAttractiveness))
			{
				maxAttractiveness = salience.getAttractiveness();
				direction = salience.getDirection();
				m_observation.setSalience(salience);
			}

		m_observation.setAttractiveness(maxAttractiveness);
		m_observation.setDirection(direction);
		
		// Taste
		
		m_observation.setGustatory(gustatoryStimulation);
		
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
		
		if (frontColor != null )
		{
			if (!tactileCortex[1][0].equals(Ernest.STIMULATION_TOUCH_EMPTY))		
			{
				IBundle bundle = addBundle(frontColor, tactileCortex[1][0]);
				m_observation.setFrontBundle(bundle);
			}
		}
		else
			if (tactileCortex[1][0].equals(Ernest.STIMULATION_TOUCH_WALL))		
			{
				IBundle bundle = addBundle(EColor.BLACK, Ernest.STIMULATION_TOUCH_WALL);
				bundle.setKinematicStimulation(Ernest.STIMULATION_KINEMATIC_BUMP);
				m_observation.setFrontBundle(bundle);
			}
				
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
		if (stimulation.getColor().equals(Ernest.COLOR_WALL))
			return 0;
		
		// Recognized bundles return their attractiveness (depends on time elapsed since last check)
		for (IBundle bundle : m_bundles)
			if (bundle.getColor().equals(stimulation.getColor()))
				return bundle.getAttractiveness(m_clock);

		// Stimulations not recognized get the attractiveness of unknown.
		return Ernest.ATTRACTIVENESS_OF_UNKNOWN;
	}

	/**
	 * Generate the anticipated observation from the previous observation and the current intention.
	 * @param schema The schema whose effects we want to anticipate.
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
	
