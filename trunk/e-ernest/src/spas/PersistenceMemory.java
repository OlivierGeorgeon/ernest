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
	 * Add a bundle to static memory if it does not already exist
	 * @param visualStimulation The bundle's visual stimulation.
	 * @param tactileStimulation The bundle's tactile stimulation.
	 * @return the new bundle if created or the already existing bundle.
	 */
	public IBundle addBundle(int visualValue, int tactileValue, int clock)
	{
		IBundle bundle = new Bundle(visualValue, tactileValue);//, kinematicValue, gustatoryValue);
		
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
		if (tactileValue != Ernest.STIMULATION_TOUCH_EMPTY)// || kinematicValue != Ernest.STIMULATION_KINEMATIC_FORWARD || gustatoryValue != Ernest.STIMULATION_GUSTATORY_NOTHING)
			bundle.setLastTimeBundled(clock);

		return bundle;
	}
	
//	/**
//	 * Returns the first bundle found form a visual stimulation.
//	 * TODO manage different bundles that have the same color.
//	 * TODO manage different bundles with more than one visual stimulation.
//	 * TODO manage bundles that have no tactile stimulation. 
//	 * @param stimulation The visual stimulation.
//	 * @return The bundle that match this stimulation.
//	 */
//	public IBundle seeBundle(int visualValue)
//	{
//		for (IBundle bundle : m_bundles)
//			// Return only bundles that have also a tactile stimulation
//			if (bundle.getVisualValue() == visualValue && bundle.getTactileValue() != Ernest.STIMULATION_TOUCH_EMPTY)
//				return bundle;
//
//		return null;
//	}
//
//	/**
//	 * Returns the first bundle found form a tactile stimulation.
//	 * TODO evoke different kind of bundles 
//	 * @param stimulation The visual stimulation.
//	 * @return The bundle that match this stimulation.
//	 */
//	public IBundle touchBundle(int tactileValue)
//	{
//		for (IBundle bundle : m_bundles)
//			// So far, only consider bump and eat bundles
//			if (bundle.getTactileValue() == tactileValue && bundle.getAffordanceList().size() > 0)
//					//(bundle.getKinematicValue() != Ernest.STIMULATION_KINEMATIC_FORWARD || bundle.getGustatoryValue() != Ernest.STIMULATION_GUSTATORY_NOTHING))
//				return bundle;
//
//		return null;
//	}
}
	
