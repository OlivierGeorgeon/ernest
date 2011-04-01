package ernest;

/**
 * An element of Ernest's visual system.
 * @author Olivier
 */
public interface IObservation 
{
	/**
	 * @param landmark
	 */
	void setLandmark(ILandmark landmark);
	
	/**
	 * @return The landmark in the focus
	 */
	ILandmark getLandmark();

	/**
	 * @param distance
	 */
	void setDistance(int distance);
	
	/**
	 * @return Ernest's current distance to the landmark
	 */
	int getDistance();
	
	/**
	 * @param direction
	 */
	void setDirection(int direction);
	
	/**
	 * @return The landmark's directin in retinotopic reference
	 */
	int getDirection();
	
	/**
	 * @param dynamicFeature
	 */
//	void setDynamicFeature(String dynamicFeature);
	
	/**
	 * @return The changes in this focus over the last interaction cycle.
	 */
	String getDynamicFeature();
	
	/**
	 * @param satisfaction
	 */
	void setSatisfaction(int satisfaction);
	
	/**
	 * @return the satisfaction associated with the change over the last interaction cycle.
	 */
	int getSatisfaction();
	
	/**
	 * Record the focus's properties into the trace
	 * @param tracer
	 * @param element
	 */
	void trace(ITracer tracer, String element);
	
	void setMotivation(int motivation);
	int getMotivation();
	
	void setDynamicFeature(IObservation previousObservation);
}
