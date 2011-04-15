package ernest;

/**
 * A bundle of sensory stimulations.
 * May correspond to a physical object according to Hume's bundle theory (http://en.wikipedia.org/wiki/Bundle_theory)
 * @author Olivier
 */
public interface IBundle 
{
	
	/**
	 * @return This bundle's visual stimulation 
	 */
	IStimulation getVisualStimulation();
	IStimulation getTactileStimulation();
	IStimulation getGustatoryStimulation();
	
	/**
	 * @param clock Ernest's current clock value
	 * @return this bundle's motivation at the given time
	 */
	int getAttractiveness(int clock);

	void setLastTimeBundled(int clock);
	
}
