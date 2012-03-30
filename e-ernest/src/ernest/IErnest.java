package ernest;

import imos.IAct;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import spas.IObservation;
import spas.IPlace;
import spas.ISegment;


/**
 * The interface through which the environment can use an Ernest agent. 
 * @author ogeorgeon
 */
public interface IErnest 
{

	/**
	 * Set Ernest's fundamental learning parameters.
	 * Use null to leave a value unchanged.
	 * @param regularityThreshold The Regularity Sensibility Threshold.
	 * @param schemaMaxLength The Maximum Schema Length.
	 */
	public void setParameters(int regularityThreshold, int schemaMaxLength); 
	
	/**
	 * Set Ernest's sensorymotor system.
	 * (The sensorymotor system is instantiated by the environment so that the environment can choose the suitable sensorymotor system)
	 * @param sensorymotorSystem Ernest's sensorymotor system
	 */
	public void setSensorymotorSystem(ISensorymotorSystem sensorymotorSystem);
	
    /**
	 * Initialize the tracer that generates Ernest's activity trace.
	 * (The tracer is instantiated by the environment so that the environment can choose the suitable tracer and also use it to trace things) 
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer tracer);

	/**
	 * Get a description of Ernest's internal state.
	 * (This is used to display Ernest's internal state in the environment)
	 * @return A representation of Ernest's internal state
	 */
	public String internalState();
	
	/**
	 * Run Ernest one step.
	 * (All environments return at least a boolean feedback from Ernest's actions) 
	 * @param status The status received as feedback from the previous schema enaction.
	 * @return The next primitive schema to enact.
	 */
	public String step(boolean status);
	
	/**
	 * Run Ernest one step.
	 * @param stimuli The matrix of stimuli received from the environment.
	 * @return The next primitive schema to enact.
	 */
	public int[] step(int[][] stimuli);
	
	/**
	 * Update Ernest on each environment refresh 
	 * (not necessarilly a cognitive step for Ernest).
	 * @param stimuli The matrix of stimuli received from the environment.
	 * @return The next primitive schema to enact.
	 */
	public int[] update(int[][] stimuli); 

	/**
	 * @param i x coordinate (0 = left, 2 = right)
	 * @param j y coordinate (0 = ahead, 2 = behind)
	 * @return The value in the corresponding place in the environment. 
	 */
	public int getValue(int i, int j);

	/**
	 * @return The value of Ernet's current attention. 
	 */
	public int getAttention();
	
	/**
	 * @param schemaLabel The action label in the environment.
	 * @param stimuliLabel The stimuli.
	 * @param satisfaction The satisfaction.
	 * @return The created or already existing act.
	 */
	public IAct addInteraction(String schemaLabel, String stimuliLabel, int satisfaction);

	/**
	 * @param placeList The list of places in Ernest's local space memory.
	 */
	//public void setPlaceList(ArrayList<IPlace> placeList);
	public void setSegmentList(ArrayList<ISegment> segmentList);
	
	/**
	 * @return The list of places in Ernest's local space memory.
	 */
	public ArrayList<IPlace> getPlaceList();
	
	/**
	 * @return The counter of cognitive cycles.
	 */
	public int getCounter();
	
	/**
	 * @return The counter of updates from the spatial system.
	 */
	public int getUpdateCount();
	
	public IPlace getFocusPlace();	
}