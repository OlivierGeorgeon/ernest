package ernest;

/**
 * An act is the association of a schema with the feedback the agent receives 
 * when trying to enact that schema. Concretely, an act associates a schema 
 * with a binary feedback status: succeed (S) or fail (F).  This class 
 * represents a default implementation for an Act. 
 *  
 * @author mcohen
 */
public class Act implements IAct
{
	private boolean m_success = false;
	private int m_satisfaction = 0;
	private ISchema m_schema = null;
	
	public static IAct createAct(ISchema s, boolean success, int satisfaction)
	{ return new Act(s, success, satisfaction); }
	
	public boolean isSuccess() 
	{
		return m_success;
	}

	public int getSat() 
	{
		return m_satisfaction;
	}

	public void setSat(int s) 
	{
		m_satisfaction = s;
	}

	public ISchema getSchema() 
	{
		return m_schema;
	}
	
	public void setSchema(ISchema s)
	{
		m_schema = s;
	}
	
	public String toString()
	{
		String s = String.format("[ID:%s, S/F:%s SAT:%s]", 
				getSchema().getId() , isSuccess() ? "S" : "F", getSat());  
		return s;
	}
	
	public boolean equals(Object o)
	{
		boolean ret = false;
		if (o instanceof IAct)
		{
			IAct other = (IAct)o;
			ret = (other.getSat() == getSat() &&
				   other.getSchema() == this.getSchema() &&
				   other.isSuccess() == isSuccess());
		}
		else 
		{
			ret = false;
		}
		
		return ret;
	}
	
	private Act(ISchema s, boolean success, int satisfaction)
	{
		m_success = success;
		m_satisfaction = satisfaction;
		m_schema = s;
	}
}
