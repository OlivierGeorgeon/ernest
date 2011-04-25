package ernest;

import java.awt.Color;

import tracing.ITracer;

/**
 * An element of Ernest's visual system.
 * @author Olivier
 */
public interface IObservation 
{
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
	
	/**
	 * Computes the observation's dynamic features
	 * @param act
	 * @param previousObservation
	 */
	void setDynamicFeature(IAct act, IObservation previousObservation);
	
	void setKinematic(int kinematic);
	int getKinematic();
	void taste(IStimulation taste);
//	int getTaste();
	public void setVisual(IStimulation stimulation); 
	public IStimulation getVisual(); 
	
	String getLabel();
	
	int getSpan();
	void setSpan(int span);

	public void setMap(IStimulation[][] tactileMatrix);

	/**
	 * Translate the observation
	 * TODO the observation transformations should be learned rather than hard coded (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	//public void forward(IObservation previousObservation);
	
	/**
	 * Rotate the observation
	 * TODO the observation transformations should be learned rather than hard coded  (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	//public void turnRight(IObservation previousObservation);

	/**
	 * Rotate the observation
	 * TODO the observation transformations should be learned rather than hard coded  (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	//public void turnLeft(IObservation previousObservation);

	public int getTactile(int x, int y);
	public Color getColor(int x, int y);
	public IBundle getBundle(int x, int y);
	
	void setFrontBundle(IBundle bundle);

}
