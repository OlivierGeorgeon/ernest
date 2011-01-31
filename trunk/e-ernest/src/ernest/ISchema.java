package ernest;

/**
 * A Schema is a sequence of interaction between Ernest and its environment.
 * Primitive schemas represent a single interaction step.
 * Composite schemas are an association of two acts and therefore represent an association of two sub-schemas. 
 * @author Olivier
 * @author mcohen
 */
public interface ISchema 
{

	/**
	 * @return The schema's serial unique identifier used for debug.
	 */
	public int getId();
		
	/**
	 * @return The schema's succeeding act.
	 */
	public IAct getSucceedingAct();

	/**
	 * @return The schema's failing act.
	 */
	public IAct getFailingAct();

	/**
	 * Gives the act that results from the schema's enaction with a given feedback status.
	 * @param s The status of the resulting act that is asked for. 
	 * @return The schema's succeeding act if status is true, the schema's failing act if status is false.
	 */
	public IAct resultingAct(boolean s);

	/**
	 * @param a The schema's succeeding act.
	 */
	public void setSucceedingAct(IAct a);
	
	/**
	 * @param a The schema's failing act.
	 */
	public void setFailingAct(IAct a);
		
	/**
	 * @return The schema's context act.
	 */
	public IAct getContextAct();
	/**
	 * @return The schema's intention act.
	 */
	public IAct getIntentionAct();

	/**
	 * @param a The schema's context act.
	 */
	public void setContextAct(IAct a);

	/**
	 * @param a The schema's intention act.
	 */
	public void setIntentionAct(IAct a);
	
	/**
	 * @param a The act that prescribes this schema for enaction. 
	 */
	public void setPrescriberAct(IAct a);

	/**
	 * @return The act that prescribes this schema for enaction.
	 */
	public IAct getPrescriberAct();

	/**
	 * @param p The pointer that points to the currently enacted subact.
	 */
	public void setPointer(int p);

	/**
	 * @return The pointer that points to the currently enacted subact.
	 */
	public int  getPointer();

	/**
	 * @return The schema's length
	 */
	public int getLength();
	
	/**
	 * Get the schema's label.
	 * If the schema is a primitive schema then the tag can be interpreted by the environment.
	 * @return The schema's tag.
	 */
	public String getLabel(); 

	/**
	 * @return The schema's weight.
	 */
	public int  getWeight();

	/**
	 * @return True if primitive, false if composite.
	 */
	public boolean isPrimitive();

	/**
	 * Increment the schema's weight (add 1).
	 */
	public void incWeight();

	/**
	 * @param weight The schema's weight.
	 */
	public void setWeight(int weight);
	
}
