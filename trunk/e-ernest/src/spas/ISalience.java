package spas;

import javax.vecmath.Vector3f;


/**
 * A set of sensory stimulations that are salient as a whole.
 * (contiguous visual stimulations with the same color, or contiguous tactile stimulations with the same feeling)
 * @author Olivier
 */
public interface ISalience 
{
	/**
	 * @param value The salience's value
	 */
	void setValue(int value);
	
	/**
	 * @param span The salience's span.
	 */
	void setSpan(float span);
	
	/**
	 * @param attractiveness The salience's attractiveness
	 */
	void setAttractiveness(int attractiveness);

	/**
	 * @return The distance of the salience (not used anymore except for debug)
	 */
	float getDistance();
	
	/**
	 * @return The span of the salience (number of pixels or of tactile cells)
	 */
	float getSpan();
	
	/**
	 * @return The value of the salience for display in the trace
	 */
	int getValue();

	/**
	 * @return The salience's attractiveness
	 */
	int getAttractiveness();

	/**
	 * @return The salience's modality.
	 */
	int getModality();
	
	/**
	 * @return The salience's direction.
	 */
	float getDirection();

	/**
	 * @return The salience's color hexadecimal code.
	 */
	public String getHexColor(); 
	
	/**
	 * @return true if the salience overlaps Ernest's front, false if not.
	 */
	public boolean isFrontal();
	
	/**
	 * @return The position of this salience in egocentric referential.
	 */
	public Vector3f getPosition();
	
}
