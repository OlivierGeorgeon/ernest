package imos;

import ernest.ITracer;

/**
 * The Intrinsic Motivation System.
 * Maintain lists of acts that represent the agent's current situation
 * Control the current enaction.
 * @author ogeorgeon
 */
public interface IImos 
{

	/**
	 * The main method of the Intrinsic Motivation System.
	 * Follow-up the sequence at hand, and chooses the next primitive interaction to try to enact. 
	 * @param primitiveEnaction The last actually enacted primitive interaction.
	 * @return The primitive interaction to try to enact next. 
	 */
	public IAct step(IAct primitiveEnaction); 

	/**
	 * @param tracer The tracer used to generate the activity traces
	 */
	public void setTracer(ITracer tracer);
	
	/**
	 * Get a description of the agent's internal state. (for visualization in the environment).
	 * @return A representation of Ernest's internal state
	 */
	public String getInternalState();
	
	/**
	 * Create a primitive act and a primitive schema.
	 * @param schemaLabel The schema's label that is interpreted by the environment.
	 * @param status The act's succeed or fail status 
	 * @param satisfaction The act's satisfaction 
	 * @return the created primitive act.
	 */
	public IAct addPrimitiveAct(String schemaLabel, boolean status, int satisfaction); 
	
	/**
	 * Create a new act in episodic memory based on an existing schema.
	 * @param label The act's label.
	 * @param schema The act's schema.
	 * @param status Thea act's status.
	 * @param satisfaction The act's satisfaction.
	 * @param confidence The act's confidence.
	 * @return The act that was created or that already existed.
	 */
	public IAct addAct(String label, ISchema schema, boolean status, int satisfaction, int confidence);
	
}
