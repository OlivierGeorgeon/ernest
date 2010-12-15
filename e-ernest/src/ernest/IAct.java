package ernest;

/**
 * An act is the association of a schema with the feedback the agent receives 
 * when trying to enact that schema. Concretely, an act associates a schema 
 * with a binary feedback status: succeed (S) or fail (F).  
 * @author mcohen
 * @author ogeorgeon
 */
public interface IAct 
{
	/**
	 * 
	 * @return if the act was successful or not
	 */
	public boolean isSuccess();
	public String getTag();
	public int getSat();
	public void setSat(int s);
	
	public void setPrescriberSchema(ISchema s);
	public ISchema getPrescriberSchema();
	
	public ISchema getSchema();
}
