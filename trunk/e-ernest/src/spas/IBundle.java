package spas;

import ernest.ITracer;

/**
 * A bundle of sensory stimulations. 
 * So far, a bundle is defined by its visual and its tactile stimulation. 
 * Other stimulations are optional.
 * A bundle may correspond to a physical object according to Hume's bundle theory (http://en.wikipedia.org/wiki/Bundle_theory)
 * @author Olivier
 */
public interface IBundle 
{
	
	/**
	 * @return The bundle's visual stimulation.
	 */
	int getVisualValue(); 
	
	/**
	 * @param stimulation The visual stimulation.
	 */
	public void setVisualValue(int visualValue); 
	
	public void setTactileValue(int tactileValue);
	
	/**
	 * @return The value of the visual stimulation if any, or of the tactile stimulation
	 */
	int getValue();
	
	/**
	 * @return The bundle's tactile stimulation.
	 */
	int getTactileValue();
	
	/**
	 * @param gustatoryValue The bundle's gustatory stimulation.
	 */
	void setGustatoryValue(int gustatoryValue); 
	
	/**
	 * @return The bundle's gustatory stimulation.
	 */
	int getGustatoryValue();
	
	/**
	 * @param kinematicValue The bundle's kinematic stimulation.
	 */
	void setKinematicValue(int kinematicValue);
	
	/**
	 * @return The bundle's kinematic stimulation.
	 */
	int getKinematicValue();
	
	/**
	 * ATTRACTIVENESS_OF_FISH (400) if this bundle's gustatory stimulation is STIMULATION_TASTE_FISH.
	 * ATTRACTIVENESS_OF_FISH + 10 (410) if the fish is touched.
	 * Otherwise ATTRACTIVENESS_OF_UNKNOWN (200) if this bundle has been forgotten,
	 * or 0 if this bundle has just been visited.
	 * @param clock Ernest's current clock value.
	 * @return This bundle's attractiveness at the given time.
	 */
	int getExtrapersonalAttractiveness(int clock);
	int getPeripersonalAttractiveness(int clock);

	/**
	 * Updating the bundle's time value will impact its attractiveness (bundles remain unattractive for a while after being checked)
	 * @param clock The current value of Ernest's clock when the bundle is checked. 
	 */
	void setLastTimeBundled(int clock);
	
	/**
	 * Trace this bundle.
	 * @param tracer The tracer.
	 * @param label The label of the element that contains the bundle's data in the trace. 
	 */
	void trace(ITracer tracer, String label);
	
}
