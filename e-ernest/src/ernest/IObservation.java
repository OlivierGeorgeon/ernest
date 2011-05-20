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
	 * Computes the observation's dynamic features used to generte the enacted act.
	 * @return The changes in this focus over the last interaction cycle.
	 */
	String getDynamicFeature();
	
	/**
	 * @param satisfaction
	 */
	void setSatisfaction(int satisfaction);
	
	/**
	 * This obervation's satisfaction value
	 * @return the satisfaction associated with the change over the last interaction cycle.
	 */
	int getSatisfaction();
	
	/**
	 * Record this observation into the trace
	 * @param tracer
	 * @param element
	 */
	void trace(ITracer tracer, String element);
	
	/**
	 * Computes the observation's dynamic features
	 * @param act
	 * @param previousObservation
	 */
	void setDynamicFeature(IAct act);
	
	
	/**
	 * @param kinematicStimulation
	 */
	void setKinematic(IStimulation kinematicStimulation);
	
	/**
	 * @return This observation's kinematic stimulation
	 */
	IStimulation getKinematic();
	
	/**
	 * @param taste
	 */
	void setGustatory(IStimulation taste);
	
	/**
	 * @return The label of the enacted act
	 */
	String getLabel();
	
	/**
	 * Set this observation's somatotopic map
	 * @param tactileMatrix The matrix of tactile stimulations.
	 */
	public void setMap(IStimulation[][] tactileMatrix);

	/**
	 * Get the color of a location in the local map for representation in the local map display
	 * @param x x coordinate in the local map
	 * @param y y coordinate in the local map
	 * @return the cell's color in the local map
	 */
	public Color getColor(int x, int y);
	
	/**
	 * Get the bundle in a specific cell in the local map
	 * @param x x coordinate in the local map
	 * @param y z coordinate in the local map
	 * @return the bundle in the local map
	 */
	public IBundle getBundle(int x, int y);
	
	/**
	 * Set the bundle in front of Ernest (coordinate (1,0) in the local map)
	 * @param bundle The bundle to set in front of Ernest.
	 */
	void setFrontBundle(IBundle bundle);

	/**
	 * Predicts the consequences of an intention on the current observation.
	 * So far, only predicts the local map. 
	 * Does not predict the square in front (leave it unchanged).
	 * @param previousObservation The previous observation on which we construct the anticipated observation.
	 * @param schema The intention schema.
	 * @return Whether the schema is expected to succeed or fail.
	 */
	public void anticipate(IObservation previousObservation, IAct act);

	public void setConfirmation(boolean confirmation);
	public boolean getStatus();
	
	/**
	 * @return true if the anticipation was confirmed, false if the anticipation was incorrect
	 */
	public boolean getConfirmation();
	
	public void setSalience(ISalience salience);
	public ISalience getSalience();
	
	/**
	 * Clear the local map in this observation 
	 * (in case the adjustment of the anticipated map fails due to too much discrepancy) 
	 */
	public void clearMap();
	
	public void setDirection(int direction);
	public int getDirection();
	public void setPreviousDirection(int direction);
	public int getPreviousDirection();
	public void setAttractiveness(int attractiveness);
	public int getAttractiveness();
	//public void setPreviousAttractiveness(int attractiveness);
	public int getPreviousAttractiveness();
	
	public void setTactileMap();

	
    /**
     * @return the possible wall salience in front of Ernest 
     */
    public ISalience getTactileSalience();

	
}
