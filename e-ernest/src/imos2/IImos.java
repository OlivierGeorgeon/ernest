package imos2;

import java.util.ArrayList;
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
	public void setMaxSchemaLength(int maxSchemaLength);

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
	 * @return The list of interactions.
	 */
	public ArrayList<IAct> getActs();
	
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
	public IAct addAct(String label, int satisfaction); 

	/**
	 * @param tracer The tracer used to generate the activity traces
	 */
	public void setTracer(ITracer<Object> tracer);
	
	/**
	 * The counter of interaction cycles.
	 * @return The current interaction cycle number.
	 */
	public int getCounter();
	
	/**
	 * @param enaction The previous enaction
	 * @return The next list of propositions
	 */
	public ArrayList<IProposition> propose(IEnaction enaction);
	
}
