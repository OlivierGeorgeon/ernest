package imos;

import java.util.ArrayList;
import java.util.List;

import spas.IPlace;
import spas.ISpas;
import ernest.IEffect;
import ernest.IEnaction;
import ernest.ISensorymotorSystem;
import ernest.ITracer;


/**
 * The Intrinsically Motivated Schema mechanism.
 * @author ogeorgeon
 */
public interface IImos 
{

	/**
	 * The main method of the Intrinsic Motivation System.
	 * Follow-up the sequence at hand, and chooses the next primitive interaction to try to enact. 
	 * @param enaction The current enaction.
	 */
	public void step(IEnaction enaction); 

	public void track(IEnaction enaction); 
	public ArrayList<IAct> getActivationList();
	public ArrayList<IAct> getActs();
	public ArrayList<ISchema> getSchemas();

	/**
	 * Constructs a new interaction in episodic memory.
	 * or retrieve the the interaction if it already exists.
	 * The interaction's action is recorded as a primitive schema.
	 * If there is no stimuli, the interaction is marked as the schema's succeeding or failing interaction.
	 * @param moveLabel The move label
	 * @param effectLabel The effect label
	 * @param satisfaction The interaction's satisfaction.
	 * @return The act that was created or that already existed.
	 */
	public IAct addInteraction(String moveLabel, String effectLabel, int satisfaction); 

	/**
	 * @param tracer The tracer used to generate the activity traces
	 */
	public void setTracer(ITracer<Object> tracer);
	
	/**
	 * Get a description of the agent's internal state. (for visualization in the environment).
	 * @return A representation of Ernest's internal state
	 */
	public String getInternalState();
	
	/**
	 * The counter of cognitive cycles.
	 * @return The current cognitive cycle number.
	 */
	public int getCounter();
	
	/**
	 * @param sensorimotorSystem The sensorimotor system
	 */
	public void setSensorimotorSystem(ISensorymotorSystem sensorimotorSystem);

    /**
     * Can be used to initialize Ernest with inborn composite schemes
     * @param contextAct The context act
     * @param intentionAct The intention act
     * @return The created composite interaction 
     */
    public IAct addCompositeInteraction(IAct contextAct, IAct intentionAct);

}
