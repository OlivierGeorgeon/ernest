package ernest;

/**
 * An act is the association of a schema with the feedback the agent receives 
 * when trying to enact that schema. Concretely, an act associates a schema 
 * with a binary feedback status: Succeed (true) or Fail (false).  
 * An act also has a satisfaction value that represents Ernest's satisfaction to enact the act.
 * @author mcohen
 * @author ogeorgeon
 */
public interface IAct  extends Comparable<IAct>
{
	/**
	 * @return The act's enaction status. True for success, false for failure.
	 */
	public boolean getStatus();
	
	/**
	 * @return The act's string representation.
	 */
	public String getLabel();
	
	/**
	 * @return The act's satisfaction value.
	 */
	public int getSatisfaction();
	
	/**
	 * @param s The act's satisfaction value.
	 */
	public void setSatisfaction(int s);
	
	/**
	 * @param c The act's confidence status.
	 */
	public void setConfidence(int c);
	
	/**
	 * @param s The schema that prescribes this act during enaction.
	 */
	public void setPrescriberSchema(ISchema s);
	
	/**
	 * @param a The act's activation value
	 */
	public void setActivation(int a);
	
	/**
	 * @return The schema that prescribed this act during enaction.
	 */
	public ISchema getPrescriberSchema();
	
	/**
	 * @return the act's schema.
	 */
	public ISchema getSchema();

	/**
	 * @return The type of that noème. So far, only SENSORYMOTOR or ICON.
	 */
	public int getModule();

	/**
	 * @return The confidence status of that noème.
	 */
	public int getConfidence();

	/**
	 * @return The noème's activation value.
	 */
	public int getActivation();

	/**
	 * @return The noème's lenght (eigher its schema's length or 1 if the noème has no schema).
	 */
	public int getLength();
}
