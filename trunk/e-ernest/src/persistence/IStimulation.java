package persistence;


/**
 * An element of Ernest's sensory state.
 * @author Olivier
 */
public interface IStimulation 
{
	/**
	 * @return The stimulation's color. Used for visualization in the trace. 
	 */
	//EColor getColor();
	
	/**
	 * @return The hexadecimal color of the stimulation's color
	 */
	String getHexColor();
	
	/**
	 * @return The stimulation's distance. The visual system is assumed to get a sense of distance.
	 */
	int getDistance();
	
	/**
	 * @param distance
	 */
	void setDistance(int distance);
	
	/**
	 * @return The type of the stimulus 
	 */
	int getType();

	/**
	 * @return The value of the stimulus 
	 */
	int getValue();
	
}
