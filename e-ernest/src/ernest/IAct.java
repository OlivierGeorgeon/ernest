package ernest;

/**
 * An act is the association of a schema with the feedback the agent receives 
 * when trying to enact that schema. Concretely, an act associates a schema 
 * with a binary feedback status: Succeed (true) or Fail (false).  
 * An act also has a satisfaction value that represents Ernest's satisfaction to enact the act.
 * @author mcohen
 * @author ogeorgeon
 */
public interface IAct
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
	 * @param s The schema that prescribes this act during enaction.
	 */
	public void setPrescriberSchema(ISchema s);
	
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
	public int getType();
}
