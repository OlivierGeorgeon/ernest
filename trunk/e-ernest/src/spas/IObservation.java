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
	//String getStimuli();
	
	/**
	 * This obervation's satisfaction value
	 * @return the satisfaction associated with the change over the last interaction cycle.
	 */
	//int getSatisfaction();
	
	/**
	 * Record this observation into the trace
	 * @param tracer
	 * @param element
	 */
	//void trace(ITracer tracer, String element);
	
	/**
	 * Generate the stimuli for imos.
	 * The stimuli come from: 
	 * - The kinematic feature.
	 * - The variation in attractiveness and in direction of the object of interest. 
	 * @param act The enacted act.
	 */
	//void setDynamicFeature(IAct act);
	
	/**
	 * @param kinematicStimulation
	 */
	void setKinematic(IStimulation kinematicStimulation);
	
	/**
	 * @return This observation's kinematic stimulation
	 */
	IStimulation getKinematicStimulation();
	
	/**
	 * @param gustatoryStimulation The gustatory stimulation;
	 */
	public void setGustatory(IStimulation gustatoryStimulation);
	
	/**
	 * @return The gustatory stimulation;
	 */
	public IStimulation getGustatoryStimulation();
	
	/**
	 * @return The bundle of current interest.
	 */
	//public IBundle getFocusBundle();
	
	/**
	 * Initialize this observation by anticipating the consequences of the intended act on the previous observation.
	 * Does not predict the square in front (leave it unchanged).
	 * TODO Anticipation should be refined using visual information.
	 * @param previousObservation The previous observation on which we construct the anticipated observation.
	 */
	//public void anticipate(IObservation previousObservation);

	/**
	 * @param salience The salience of current attention
	 */
	//public void setSalience(ISalience salience);
	
	/**
	 * Set this observation's focus bundle.
	 * @param bundle The focus bundle.
	 */
	//public void setFocusBundle(IBundle bundle);
	
	/**
	 * @return The salience of current attention 
	 */
	//public ISalience getSalience();
	
	/**
	 * @param direction The direction of the salience of current attention.
	 * Radian trigonometric.
	 */
	public void setDirection(float direction);
	
	/**
	 * @return The direction of the salience of current attention
	 * Radian trigonometric.
	 */
	public float getDirection();
	
	/**
	 * @param attractiveness The attractiveness of the salience of current attention
	 */
	public void setAttractiveness(int attractiveness);
	
	/**
	 * @return The attractiveness of the salience of current attention
	 */
	public int getAttractiveness();
	
}
