package spas;


/**
 * A set of sensory stimulations that are salient as a whole.
 * (contiguous visual stimulations with the same color, or contiguous tactile stimulations with the same feeling)
 * @author Olivier
 */
public interface ISalience 
{
	/**
	 * @param distance The distance of the salience (not used anymore except for debug)
	 */
	void setDistance(int distance);
	
	/**
	 * @param span The span of the salience (number of pixels or of tactile cells)
	 */
	void setSpan(int span);
	
	/**
	 * @param value The salience's value
	 */
	void setValue(int value);
	
	/**
	 * @param attractiveness The salience's attractiveness
	 */
	void setAttractiveness(int attractiveness);

	/**
	 * @param bundle The bundle evoked by this salience.
	 */
	void setBundle(IBundle bundle);

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
	 * @return The bundle evoked by this salience
	 */
	IBundle getBundle();
	
	void setType(int type);
	
	int getType();
	
	void setDirection(float direction);
	
	float getDirection();

	void setSpan(float span);
	
}
