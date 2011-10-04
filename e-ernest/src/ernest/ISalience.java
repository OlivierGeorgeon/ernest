package ernest;

/**
 * A set of sensory stimulations that are salient as a whole.
 * (contiguous visual stimulations with the same color, or contiguous tactile stimulations with the same feeling)
 * @author Olivier
 */
public interface ISalience 
{
	/**
	 * @param direction The central direction of the salience in retinotopic coordinates
	 */
	void setDirection(int direction);
	
	/**
	 * @param distance The distance of the salience (not used anymore except for debug)
	 */
	void setDistance(int distance);
	
	/**
	 * @param span The span of the salience (number of pixels or of tactile cells)
	 */
	void setSpan(int span);
	
	/**
	 * @param color The color for display in the environment
	 */
	void setColor(EColor color);
	
	/**
	 * @param attractiveness The salience's attractiveness
	 */
	void setAttractiveness(int attractiveness);

	/**
	 * @param The bundle evoked by this salience
	 */
	void setBundle(IBundle bundle);

	/**
	 * @return The central direction of the salience in retinotopic coordinates
	 */
	int getDirection();
	
	/**
	 * @return The distance of the salience (not used anymore except for debug)
	 */
	int getDistance();
	
	/**
	 * @return The span of the salience (number of pixels or of tactile cells)
	 */
	int getSpan();
	
	/**
	 * @return The color of the salience for display in the environment
	 */
	EColor getColor();

	/**
	 * @return The salience's attractiveness
	 */
	int getAttractiveness();

	/**
	 * @return The bundle evoked by this salience
	 */
	IBundle getBundle();

}
