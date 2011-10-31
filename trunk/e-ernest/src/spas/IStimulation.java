package spas;

import javax.vecmath.Vector3f;


/**
 * An element of Ernest's sensory state.
 * @author Olivier
 */
public interface IStimulation 
{
	/**
	 * @return The hexadecimal color of the stimulation's color
	 */
	String getHexColor();
	
	/**
	 * @return The stimulation's position in egocentric coordinates. 
	 */
	Vector3f getPosition();
	
	/**
	 * @param position The stimulation's position in egocentric coordinates.
	 */
	void setPosition(Vector3f position);
	
	/**
	 * @return The type of the stimulus 
	 */
	int getType();

	/**
	 * @return The value of the stimulus 
	 */
	int getValue();
	
}
