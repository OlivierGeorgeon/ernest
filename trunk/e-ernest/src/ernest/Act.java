package ernest;

/**
 * An act is the association of a schema with the feedback the agent receives 
 * when trying to enact that schema. Concretely, an act associates a schema 
 * with a binary feedback status: succeed (S) or fail (F).  This class 
 * represents a default implementation for an Act. 
 * @author mcohen
 * @author ogeorgeon
 */
public class Act implements IAct
{
	/** The act's status. True = Success, False = Failure */
	private boolean m_status = false;
	/** The act's satisfaction value. Represents Ernest's satisfaction to enact the act */
	private int m_satisfaction = 0;
	/** The act's schema */
	private ISchema m_schema = null;
		/** The schema that prescribes this act during enaction */
	private ISchema m_prescriberSchema = null;
	
	
	public void setSatisfaction(int s)         { m_satisfaction = s; }
	public void setPrescriberSchema(ISchema s) { m_prescriberSchema = s; }
	
	public boolean getStatus()                 { return m_status; }
	public int     getSatisfaction()           { return m_satisfaction; }
	public ISchema getPrescriberSchema()       { return m_prescriberSchema; }
	public ISchema getSchema()                 { return m_schema; }

	/**
	 * Constructor 
	 * @param s The act's schema
	 * @param success The act's status
	 * @param satisfaction The act's satisfaction value
	 */
	public Act(ISchema s, boolean success, int satisfaction)
	{
		m_schema = s;
		m_status = success;
		m_satisfaction = satisfaction;
	}
	
	/**
	 * @return The act's string representation
	 */
	public String getTag()
	{
		String s = String.format("%s%s%s", 
				getStatus() ? "(" : "[", getSchema().getTag() , getStatus() ? ")" : "]");  
		return s;
	}
	
	public String toString()
	{
		String s = String.format("(S%s %s s=%s)", 
				getSchema().getId() , getTag(), getSatisfaction());  
		return s;
	}
	
	/**
	 * Acts are equals if they have the same schema and status 
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			IAct other = (IAct)o;
			ret = (other.getSchema() == getSchema() &&
				   other.getStatus() == getStatus());
		}
		
		return ret;
	}
	

}
