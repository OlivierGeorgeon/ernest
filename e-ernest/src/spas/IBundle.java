package spas;

import imos.IAct;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

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
	 * @param visualValue The visual value.
	 */
	public void setVisualValue(int visualValue); 
	
	/**
	 * @param tactileValue The tactile value.
	 */
	public void setTactileValue(int tactileValue);
	
	/**
	 * @return The bundle's tactile stimulation.
	 */
	int getTactileValue();
	
	/**
	 * @return The value of the visual stimulation if any, or of the tactile stimulation
	 */
	int getValue();
	
	/**
	 * ATTRACTIVENESS_OF_FISH (400) if this bundle's gustatory stimulation is STIMULATION_TASTE_FISH.
	 * ATTRACTIVENESS_OF_FISH + 10 (410) if the fish is touched.
	 * Otherwise ATTRACTIVENESS_OF_UNKNOWN (200) if this bundle has been forgotten,
	 * or 0 if this bundle has just been visited.
	 * @param clock Ernest's current clock value.
	 * @return This bundle's attractiveness at the given time.
	 */
	int getExtrapersonalAttractiveness(int clock);
	
	/**
	 * @param clock Ernst's current clcok value.
	 * @return The bundle's peripersonal attractiveness at a given time. 
	 */
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
	
	public void addAct(IAct act); 
	public boolean hasAct(IAct act);
	public boolean isConsistent(IAct act);

	void addAffordance(IAct act, IPlace place, Vector3f relativePosition, float relativeOrientation, int proclivity, int value);

	public ArrayList<IAffordance> getAffordanceList();
	
	public IAct activateAffordance(Vector3f relativePosition);

}
