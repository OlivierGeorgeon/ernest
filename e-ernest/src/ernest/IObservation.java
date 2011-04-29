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
//	void setAttractiveness(int attractiveness);
	
	/**
	 * @return This observation's attractiveness
	 */
//	int getAttractiveness();
	
	/**
	 * Computes the observation's dynamic features
	 * @param act
	 * @param previousObservation
	 */
	void setDynamicFeature(IAct act, IObservation previousObservation);
	
	void setKinematic(IStimulation kinematicStimulation);
	IStimulation getKinematic();
	void taste(IStimulation taste);
//	int getTaste();
	
	String getLabel();
	
	//int getSpan();
	//void setSpan(int span);

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

	/**
	 * Predicts the consequences of an intention on the current observation.
	 * So far, only predicts the local map. 
	 * @param act The intention act
	 */
	public void anticipate(IObservation previousObservation, IAct act);
	
	/**
	 * @return true if the anticipation was confirmed, false if the anticipation was incorrect
	 */
	public boolean getConfirmation();
	
	public void setIcon(IIcon icon);
	public IIcon getIcon();
}
