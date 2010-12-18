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
	
	/** The noème's type */
	private String m_label = "";
	private int m_type = 0;
	
	public void setSatisfaction(int s)         { m_satisfaction = s; }
	public void setPrescriberSchema(ISchema s) { m_prescriberSchema = s; }
	
	public boolean getStatus()                 { return m_status; }
	public int     getSatisfaction()           { return m_satisfaction; }
	public int     getType()           		   { return m_type; }
	public ISchema getPrescriberSchema()       { return m_prescriberSchema; }
	public ISchema getSchema()                 { return m_schema; }

	/**
	 * The abstract constructor for any kind of noème
	 * @param label
	 * @param type
	 */
	public Act(String label, int type)
	{
		m_label = label;
		m_type = type;
	}
	
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
		m_label = String.format("%s%s%s", 
				getStatus() ? "(" : "[", getSchema().getTag() , getStatus() ? ")" : "]");  

	}
	
	/**
	 * @return The act's string representation
	 */
	public String getLabel()
	{
		return m_label;
	}
	
	public String toString()
	{
		String s = String.format("(S%s %s s=%s)", 
				getSchema().getId() , getLabel(), getSatisfaction());  
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
