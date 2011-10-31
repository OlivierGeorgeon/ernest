package spas;

import ernest.ITracer;
import imos.IAct;


/**
 * The main construct that represents Ernest's current observation of its situation.
 * @author Olivier
 */
public interface IObservation 
{
	/**
	 * @return This observation's dynamic feature: reflect the changes in the salience of attention over the last interaction cycle.
	 */
	String getStimuli();
	
	/**
	 * @param satisfaction The satisfaction of the enacted act
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
	 * Generate the stimuli for imos.
	 * The stimuli come from: 
	 * - The kinematic feature.
	 * - The variation in attractiveness and in direction of the object of interest. 
	 * @param act The enacted act.
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
	public int getColor(int x, int y);
	
	/**
	 * Get the bundle in a specific cell in the local map
	 * @param x x coordinate in the local map
	 * @param y z coordinate in the local map
	 * @return the bundle in the local map
	 */
	public IBundle getBundle(int x, int y);
	
	/**
	 * @return The bundle of current interest.
	 */
	public IBundle getFocusBundle();
	
	/**
	 * Set the bundle in front of Ernest (coordinate (1,0) in the peripersonal map)
	 * @param bundle The bundle to set in front of Ernest.
	 */
	void setFrontBundle(IBundle bundle);

	/**
	 * Initialize this observation by anticipating the consequences of the intended act on the previous observation.
	 * Does not predict the square in front (leave it unchanged).
	 * TODO Anticipation should be refined using visual information.
	 * @param previousObservation The previous observation on which we construct the anticipated observation.
	 * @param act The intention act.
	 */
	public void anticipate(IObservation previousObservation, IAct act);

	/**
	 * @param confirmation True If and only if the adjusted observation matches the anticipated observation
	 * (the criteria used to evaluate whether there is a match need to be define by the modeler)
	 */
	public void setConfirmation(boolean confirmation);
	
	/**
	 * @return true if the anticipation was confirmed, false if the anticipation was incorrect
	 */
	public boolean getConfirmation();
	
	/**
	 * @param salience The salience of current attention
	 */
	public void setSalience(ISalience salience);
	
	/**
	 * Set this observation's focu bundle
	 */
	public void setFocusBundle(IBundle bundle);
	
	/**
	 * @return The salience of current attention 
	 */
	public ISalience getSalience();
	
	/**
	 * Clear the local map in this observation 
	 * (in case the adjustment of the anticipated map fails due to too much discrepancy) 
	 */
	public void clearMap();
	
	/**
	 * @param direction The direction of the salience of current attention
	 */
	public void setDirection(float direction);
	
	/**
	 * @return The direction of the salience of current attention
	 */
	public float getDirection();
	/**
	 * @param direction The direction of the salience of previous attention
	 */
	public void setPreviousDirection(float direction);
	
	/**
	 * @return The direction of the salience of previous attention 
	 */
	public float getPreviousDirection();
	
	/**
	 * @param attractiveness The attractiveness of the salience of current attention
	 */
	public void setAttractiveness(int attractiveness);
	
	/**
	 * @return The attractiveness of the salience of current attention
	 */
	public int getAttractiveness();
	
	/**
	 * @return The attractiveness of the salience of previous attention 
	 */
	public int getPreviousAttractiveness();
	
	/**
	 * Test if Ernest touches a fish
	 * Creates a gray bundle in the peripersonal map if Ernest touches a fish (no more than one gray bundle)
	 */
	//public void setTactileMap(IBundle bundleFish);
	
	/**
	 * Get a stimulation in the tactile map
	 * @param x X coordinate in the tactile map (left to right)
	 * @param y Y coordinate in the tactile map (top to bottom)
	 * @return the tactile stimulation perceived in the tactile map
	 */
	public IStimulation getTactileStimulation(int x, int y);

}
