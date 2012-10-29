package imos;

import java.util.ArrayList;
import ernest.IEnaction;
import ernest.ITracer;

/**
 * The Intrinsically Motivated Schema mechanism (Ernest's sequential system).
 * @author ogeorgeon
 */
public interface IImos 
{
	/**
	 * @param regularityThreshold The regularity sensibility threshold.
	 */
	public void setRegularityThreshold(int regularityThreshold);

	/**
	 * Track the enaction at hand. 
	 * @param enaction The current enaction.
	 */
	public void track(IEnaction enaction); 
	
	/**
	 * Terminates the enaction at hand
	 * Record and reinforce new schemas and construct the final context.
	 * @param enaction The current enaction that is being terminated.
	 */
	public void terminate(IEnaction enaction);
	
	/**
	 * @return The list of acts.
	 */
	public ArrayList<IAct> getActs();
	
	/**
	 * @return The list of schemes
	 */
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
	 * The counter of interaction cycles.
	 * @return The current interaction cycle number.
	 */
	public int getCounter();
	
    /**
     * Can be used to initialize Ernest with inborn composite schemes
     * @param contextAct The context act
     * @param intentionAct The intention act
     * @return The created composite interaction 
     */
    public IAct addCompositeInteraction(IAct contextAct, IAct intentionAct);

}
