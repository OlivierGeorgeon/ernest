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
	
	public int getClock()
	{
		return m_clock;
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
	 * Create a bundle with a visual and a tactile stimulation.
	 * @param visualStimulation The visual stimulation.
	 * @param tactileStimulation The tactile stimulation.
	 * @return The bundle.
	 */
	public IBundle createVisioTactileBundle(int visualValue, int tactileValue)
	{
		if (m_tracer != null)
		{
			Object e = m_tracer.addEventElement("cooccurrence");
			m_tracer.addSubelement(e, "stimulus_1", hexColor(visualValue));
			m_tracer.addSubelement(e, "stimulus_0", hexColor(tactileValue));
		}
		return addBundle(visualValue, tactileValue, Ernest.STIMULATION_KINEMATIC_FORWARD, Ernest.STIMULATION_GUSTATORY_NOTHING);
	}
	
	/**
	 * Create a bundle with a tactile and a gustatory stimulation.
	 * @param tactileStimulation The tactile stimulation.
	 * @param gustatoryStimulation The gustatory stimulation.
	 * @return The bundle
	 */
	public IBundle createTactoGustatoryBundle(int tactileValue, int gustatoryValue)
	{
		if (m_tracer != null)
		{
			Object e = m_tracer.addEventElement("cooccurrence");
			m_tracer.addSubelement(e, "stimulus_1", hexColor(gustatoryValue));
			m_tracer.addSubelement(e, "stimulus_0", hexColor(tactileValue));
		}
		return  addBundle(Ernest.STIMULATION_VISUAL_UNSEEN, tactileValue, Ernest.STIMULATION_KINEMATIC_FORWARD, gustatoryValue);
	}
	/**
	 * Create a bundle with a tactile and a gustatory stimulation.
	 * @param tactileStimulation The tactile stimulation.
	 * @param gustatoryStimulation The gustatory stimulation.
	 * @return The bundle
	 */
	public IBundle createTactoKinematicBundle(int tactileValue, int kinematicValue)
	{
		if (m_tracer != null)
		{
			Object e = m_tracer.addEventElement("cooccurrence");
			m_tracer.addSubelement(e, "stimulus_1", hexColor(kinematicValue));
			m_tracer.addSubelement(e, "stimulus_0", hexColor(tactileValue));
		}
		return  addBundle(Ernest.STIMULATION_VISUAL_UNSEEN, tactileValue, kinematicValue, Ernest.STIMULATION_GUSTATORY_NOTHING);
	}
	/**
	 * Add a bundle to static memory if it does not already exist
	 * @param visualStimulation The bundle's visual stimulation.
	 * @param tactileStimulation The bundle's tactile stimulation.
	 * @return the new bundle if created or the already existing bundle.
	 */
	public IBundle addBundle(int visualValue, int tactileValue, int kinematicValue, int gustatoryValue)
	{
		IBundle bundle = new Bundle(visualValue, tactileValue, kinematicValue, gustatoryValue);
		
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
		
		// This bundle is considered confirmed or visited
		if (tactileValue != Ernest.STIMULATION_TOUCH_EMPTY || kinematicValue != Ernest.STIMULATION_KINEMATIC_FORWARD || gustatoryValue != Ernest.STIMULATION_GUSTATORY_NOTHING)
			bundle.setLastTimeBundled(m_clock);

		return bundle;
	}
	
	public void addVisualValue(IBundle bundle, int visualValue)
	{
		// TODO Possibly change the visual value of an existing bundle.
		if (bundle.getVisualValue() == Ernest.STIMULATION_VISUAL_UNSEEN)
		{
			bundle.setVisualValue(visualValue);
			if (m_tracer != null)
			{
				Object e = m_tracer.addEventElement("cooccurrence");
				m_tracer.addSubelement(e, "stimulus_1", hexColor(visualValue));
				bundle.trace(m_tracer, "bundle");
			}
		}
	}
	
	public void addKinematicValue(IBundle bundle, int kinematiValue)
	{
		// TODO Possibly change the kinematic stimulation of a bump bundle
		if (bundle.getKinematicValue() == Ernest.STIMULATION_KINEMATIC_FORWARD)
		{
			bundle.setKinematicValue(kinematiValue);
			if (m_tracer != null)
			{
				Object e = m_tracer.addEventElement("cooccurrence");
				m_tracer.addSubelement(e, "stimulus_1", hexColor(kinematiValue));
				m_tracer.addSubelement(e, "stimulus_0", hexColor(bundle.getTactileValue()));
				bundle.trace(m_tracer, "bundle");
			}
		}
	}
	
	public void addGustatoryValue(IBundle bundle, int gustatoryValue)
	{
		if (bundle.getGustatoryValue() != gustatoryValue)
		{
			bundle.setGustatoryValue(gustatoryValue);
			if (m_tracer != null)
			{
				Object e = m_tracer.addEventElement("cooccurrence");
				m_tracer.addSubelement(e, "stimulus_0", hexColor(gustatoryValue));
				bundle.trace(m_tracer, "bundle");
			}
		}
	}

	/**
	 * Returns the first bundle found form a visual stimulation.
	 * TODO manage different bundles that have the same color.
	 * TODO manage different bundles with more than one visual stimulation.
	 * TODO manage bundles that have no tactile stimulation. 
	 * @param stimulation The visual stimulation.
	 * @return The bundle that match this stimulation.
	 */
	public IBundle seeBundle(int visualValue)
	{
		for (IBundle bundle : m_bundles)
			// Return only bundles that have also a tactile stimulation
			if (bundle.getVisualValue() == visualValue && bundle.getTactileValue() != Ernest.STIMULATION_TOUCH_EMPTY)
				return bundle;

		return null;
	}

	/**
	 * Returns the first bundle found form a tactile stimulation.
	 * TODO evoke different kind of bundles 
	 * @param stimulation The visual stimulation.
	 * @return The bundle that match this stimulation.
	 */
	public IBundle touchBundle(int tactileValue)
	{
		for (IBundle bundle : m_bundles)
			// So far, only consider bump and eat bundles
			if (bundle.getTactileValue() == tactileValue && 
					(bundle.getKinematicValue() != Ernest.STIMULATION_KINEMATIC_FORWARD || bundle.getGustatoryValue() != Ernest.STIMULATION_GUSTATORY_NOTHING))
				return bundle;

		return null;
	}
	
	private String hexColor(int value) 
	{
		int r = value/65536;
		int g = (value - r * 65536)/256;
		int b = value - r * 65536 - g * 256;
		String s = format(r) + format(g) + format(b);

		return s;
	}
	
	private String format(int i)
	{
		if (i == 0)
			return "00";
		else if (i < 16)
			return "0" + Integer.toString(i, 16).toUpperCase();
		else
			return Integer.toString(i, 16).toUpperCase();
	}

}
	
