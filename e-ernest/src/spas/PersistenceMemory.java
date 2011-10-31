package spas;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import ernest.Ernest;
import ernest.ITracer;

/**
 * Maintains the memory of bundles.
 * @author ogeorgeon
 */
public class PersistenceMemory 
{

	/** The Tracer. */
	private ITracer m_tracer = null; 

	/** Ernest's internal clock  */
	private int m_clock;

	/** A list of all the stimulations ever identified. */
	public List<IStimulation> m_stimulations = new ArrayList<IStimulation>(20);
	
	/** A list of all the bundles ever identified. */
	public List<IBundle> m_bundles = new ArrayList<IBundle>(20);
	
	/**
	 * @param tracer The tracer
	 */
	public void setTracer(ITracer tracer)
	{
		m_tracer = tracer;
	}

	/**
	 * Tick the persistence memory clock.
	 * Used to simulate a decay in persistence memory
	 */
	public void tick()
	{
		if (m_tracer != null)
			m_tracer.addEventElement("clock", m_clock + "");
		
		m_clock++;
	}
		
	/**
	 * Add a stimulation to static memory if it does not already exist.
	 * @param red Component of the landmark's color.
	 * @param green Component of the landmark's color.
	 * @param blue Component of the landmark's color.
	 * @param position The position of the stimulation in egocentric coordinates.
	 * @return the new stimulation if created or the already existing landmark.
	 */
//	public IStimulation addStimulation(int red, int green, int blue, Vector3f position)
//	{
//		IStimulation l = new Stimulation(red, green, blue, position);
//		
//		int i = m_stimulations.indexOf(l);
//		if (i == -1)
//			// The landmark does not exist
//			m_stimulations.add(l);
//		else 
//		{
//			// The landmark already exists: return a pointer to it.
//			l =  m_stimulations.get(i);
//			l.setPosition(position);
//		}
//		return l;
//	}
	
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
	 * Create a bundle with a visual and a tactile stimulation.
	 * @param visualStimulation The visual stimulation.
	 * @param tactileStimulation The tactile stimulation.
	 * @return The bundle.
	 */
	public IBundle createVisioTactileBundle(IStimulation visualStimulation, IStimulation tactileStimulation)
	{
		return addBundle(visualStimulation, tactileStimulation, Ernest.STIMULATION_KINEMATIC_FORWARD, Ernest.STIMULATION_GUSTATORY_NOTHING);
	}
	
	/**
	 * Create a bundle with a tactile and a gustatory stimulation.
	 * @param tactileStimulation The tactile stimulation.
	 * @param gustatoryStimulation The gustatory stimulation.
	 * @return The bundle
	 */
	public IBundle createTactoGustatoryBundle(IStimulation tactileStimulation, IStimulation gustatoryStimulation)
	{
		return  addBundle(Ernest.STIMULATION_VISUAL_UNSEEN, tactileStimulation, Ernest.STIMULATION_KINEMATIC_FORWARD, gustatoryStimulation);
	}
	/**
	 * Create a bundle with a tactile and a gustatory stimulation.
	 * @param tactileStimulation The tactile stimulation.
	 * @param gustatoryStimulation The gustatory stimulation.
	 * @return The bundle
	 */
	public IBundle createTactoKinematicBundle(IStimulation tactileStimulation, IStimulation kinematicStimulation)
	{
		return  addBundle(Ernest.STIMULATION_VISUAL_UNSEEN, tactileStimulation, kinematicStimulation, Ernest.STIMULATION_GUSTATORY_NOTHING);
	}
	/**
	 * Add a bundle to static memory if it does not already exist
	 * @param visualStimulation The bundle's visual stimulation.
	 * @param tactileStimulation The bundle's tactile stimulation.
	 * @return the new bundle if created or the already existing bundle.
	 */
	public IBundle addBundle(IStimulation visualStimulation, IStimulation tactileStimulation, IStimulation kinematicStimulation, IStimulation gustatoryStimulation)
	{
		IBundle bundle = new Bundle(visualStimulation, tactileStimulation, kinematicStimulation, gustatoryStimulation);
		
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
	
	public void addVisualStimulation(IBundle bundle, IStimulation stimulation)
	{
		if (!bundle.getVisualStimulation().equals(stimulation))
		{
			bundle.setVisualStimulation(stimulation);
			bundle.trace(m_tracer, "bundle");
		}
	}
	
//	public void addTactileStimulation(IBundle bundle, IStimulation stimulation)
//	{
//		if (!bundle.getTactileStimulation().equals(stimulation))
//		{
//			bundle.setTactileStimulation(stimulation);
//			bundle.trace(m_tracer, "bundle");
//		}
//	}
	public void addKinematicStimulation(IBundle bundle, IStimulation stimulation)
	{
		if (!bundle.getKinematicStimulation().equals(stimulation))
		{
			bundle.setKinematicStimulation(stimulation);
			bundle.trace(m_tracer, "bundle");
		}
	}
	
	public void addGustatoryStimulation(IBundle bundle, IStimulation stimulation)
	{
		if (!bundle.getGustatoryStimulation().equals(stimulation))
		{
			bundle.setGustatoryStimulation(stimulation);
			bundle.trace(m_tracer, "bundle");
		}
	}
	
	/**
	 * TODO manage different bundles with more than one visual salience 
	 * @param stimulation The stimulation
	 * @return The motivation value generated by this stimulation.
	 * (Either the bundle's motivation or the base motivation if this stimulation evokes no bundle.
	 */
	public int attractiveness(IStimulation stimulation)
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
	 * Returns the first bundle found form a visual stimulation.
	 * TODO manage different bundles that have the same color.
	 * TODO manage different bundles with more than one visual stimulation. 
	 * @param stimulation The visual stimulation.
	 * @return The bundle that match this stimulation.
	 */
	public IBundle seeBundle(IStimulation stimulation)
	{
		for (IBundle bundle : m_bundles)
			if (bundle.getVisualStimulation().equals(stimulation))
				return bundle;

		return null;
	}

	/**
	 * Returns the first bundle found form a tactile stimulation.
	 * @param stimulation The visual stimulation.
	 * @return The bundle that match this stimulation.
	 */
	public IBundle touchBundle(IStimulation stimulation)
	{
		for (IBundle bundle : m_bundles)
			if (bundle.getTactileStimulation().equals(stimulation))
				return bundle;

		return null;
	}
	
}
	
