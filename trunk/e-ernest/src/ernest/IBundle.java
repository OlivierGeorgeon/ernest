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
	 * TOP_MOTIVATION (400) if this bundle's gustatory motivation is STIMULATION_TASTE_FISH.
	 * Otherwise BASE_MOTIVATION (200) if this bundle has been forgotten,
	 * or 0 if this bundle has just been visited.
	 * @param clock Ernest's current clock value.
	 * @return This bundle's attractiveness at the given time.
	 */
	int getAttractiveness(int clock);

	void setLastTimeBundled(int clock);
	
}
