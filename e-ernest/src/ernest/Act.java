package ernest;

/**
 * An act is the association of a schema with the feedback the agent receives 
 * when trying to enact that schema. Concretely, an act associates a schema 
 * with a binary feedback status: succeed (S) or fail (F).  This class 
 * represents a default implementation for an Act. 
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
	
	/**
	 * The act's litteral label 
	 * @author ogeorgeon
	 */
	public String getTag()
	{
		String s = String.format("%s/%s", 
				getSchema().getTag() , isSuccess() ? "S" : "F");  
		return s;
	}
	
	public String toString()
	{
		String s = String.format("<S%s, %s (%s)>", 
				getSchema().hashCode() , isSuccess() ? "S" : "F", getSat());  
		return s;
	}
	
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
			ret = (other.getSat() == getSat() &&
				   other.getSchema() == getSchema() &&
				   other.isSuccess() == isSuccess());
		}
		
		return ret;
	}
	
	public int hashCode()
    {
		int ret = (getSchema().hashCode() * 10) + (isSuccess() == true ? 0 : 1);
		return ret;
    }	
	
	private Act(ISchema s, boolean success, int satisfaction)
	{
		m_success = success;
		m_satisfaction = satisfaction;
		m_schema = s;
	}
}
