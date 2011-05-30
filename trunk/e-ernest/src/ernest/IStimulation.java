package ernest;

/**
 * An element of Ernest's sensory state.
 * @author Olivier
 */
public interface IStimulation 
{
	/**
	 * @return The stimulation's color. Used for visualisation in the trace. 
	 */
	EColor getColor();
	
	/**
	 * @return The hexadecimal color of the stimulation's color
	 */
	String getHexColor();
	
	/**
	 * @return The stimulation's distance. The visual system is assumed to get a sense of distance.
	 */
	int getDistance();
	void setDistance(int distance);
	
	/**
	 * @returns The type of the stimulus 
	 */
	int getType();

	/**
	 * @returns The value of the stimulus 
	 */
	int getValue();
	
}
