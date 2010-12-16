package ernest;

/**
 * A pattern of interaction between Ernest and its environment. 
 * Specifically, schemas can either succeed of fail when Ernest tries to enact 
 * them in the environment.
 * @author mcohen
 *
 */
public interface ISchema 
{

	/**
	 * @return The schema's number (schema's serial unique identifier)
	 */
	public int getId();
	
	/**
	 * @return The schema's type. MOTOR or SENSOR
	 */
	public int getType();
	
	/**
	 * Get the Schema's succeeding act.
	 * @return The schema's succeeding act.
	 */
	public IAct getSucceedingAct();

	/**
	 * Get the Schema's failing act.
	 * @return The schema's failing act.
	 */
	public IAct getFailingAct();

	/**
	 * Get the Schema's resulting act.
	 * @param s The status of the resulting act that is asked for. 
	 * @return The schema's succeeding act if status is true, the schema's failing act if status is false.
	 */
	public IAct resultingAct(boolean s);

	/**
	 * Set the Schema's succeeding act.
	 * @param a The schema's succeeding act.
	 */
	public void setSucceedingAct(IAct a);
	
	/**
	 * Set the Schema's failing act.
	 * @param a The schema's failing act.
	 */
	public void setFailingAct(IAct a);
		
	/**
	 * Get the Schema's context act.
	 * @return The schema's context act.
	 */
	public IAct getContextAct();
	/**
	 * Get the Schema's intention act.
	 * @return The schema's intention act.
	 */
	public IAct getIntentionAct();

	/**
	 * Set the Schema's context act.
	 * @param a The schema's context act.
	 */
	public void setContextAct(IAct a);

	/**
	 * Set the Schema's intention act.
	 * @param a The schema's intention act.
	 */
	public void setIntentionAct(IAct a);
	
	/**
	 * Set the schema's prescriber act.
	 * @param a The act that prescribes this schema for enaction. 
	 */
	public void setPrescriberAct(IAct a);

	/**
	 * Get the schema's prescriber act.
	 * @return The act that prescribes this schema for enaction.
	 */
	public IAct getPrescriberAct();

	/**
	 * Set the pointer that points to the subact that is currently being enacted.
	 * @param p The pointer that points to the currently enacted subact.
	 */
	public void setPointer(int p);

	/**
	 * Get the pointer that points to the subact that is currently being enacted.
	 * @return The pointer that points to the currently enacted subact.
	 */
	public int  getPointer();

	/**
	 * Get the Schema's length.
	 * @return The schema's length
	 */
	public int getLength();
	
	/**
	 * Get the schema's Tag.
	 * If the schema is a primitive schema then the tag can be interpreted by the environment.
	 * @return The schema's tag.
	 */
	public String getTag(); 

	/**
	 * Get the Schema's weight.
	 * @return The schema's weight.
	 */
	public int  getWeight();

	/**
	 * Get the Schema's primitive property.
	 * @return True if primitive, false if composite.
	 */
	public boolean isPrimitive();

	/**
	 * Increment the schema's weight (add 1).
	 */
	public void incWeight();

	/**
	 * Set the shema's weight.
	 * @param weight The schema's weight.
	 */
	public void setWeight(int weight);
	
}
