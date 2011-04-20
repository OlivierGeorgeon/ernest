package ernest;

import tracing.ITracer;

/**
 * An element of Ernest's visual system.
 * @author Olivier
 */
public interface IObservation 
{
	/**
	 * @param hexColor this observation's main color in hexadecimal code.
	 */
	void setHexColor(String hexColor);
	
	/**
	 * @return this observation's main color in hexadecimal code.
	 */
	String getHexColor();

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
	
	/**
	 * @param attractiveness This observation's attractiveness
	 */
	void setAttractiveness(int attractiveness);
	
	/**
	 * @return This observation's attractiveness
	 */
	int getAttractiveness();
	
	void setKinematic(int kinematic);
	int getKinematic();
	void setTaste(int taste);
	int getTaste();
	public void setVisual(IStimulation stimulation); 
	public IStimulation getVisual(); 
	
	String getLabel();
	
	void setDynamicFeature(IAct act, IObservation previousObservation);
	void setDynamicFeature2(IAct act, IObservation previousObservation);
	
	int getSpan();
	void setSpan(int span);


}
