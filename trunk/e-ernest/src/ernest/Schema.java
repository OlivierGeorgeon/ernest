package ernest;

import java.io.ObjectInputStream.GetField;

/**
 * A Schema is a pattern of interaction between the agent and its environment. 
 * Specifically, schemas can either succeed of fail when the agent tries to enact 
 * them in the environment.  This class represents a default implementation for
 * a Schema.
 * @author mcohen
 * @author ogeorgeon
 */
public class Schema implements ISchema 
{
	public static final int REG_SENS_THRESH = 4;	
	
	private int m_weight = Integer.MIN_VALUE; 
	private IAct m_successAct = null;
	private IAct m_failureAct = null;
	private IAct m_contextAct = null;
	private IAct m_intentionAct = null;	
	private int m_id;
	private String m_tag = null; 
	private boolean m_isPrimitive = true;

	public static ISchema createSchema(int id)
	{ return new Schema(id); }

	public static ISchema createSchema(int id, String tag, int successSat, int failureSat)
	{ return new Schema(id, tag, successSat, failureSat); }
	
	public IAct getSuccessAct() 
	{	return m_successAct; }

	public IAct getFailureAct() 
	{	return m_failureAct; }

	public void setSuccessAct(IAct a)
	{ m_successAct = a;	}

	public void setFailureAct(IAct a)
	{ m_failureAct = a;	}
		
	public IAct getContextAct() 
	{ return m_contextAct; }

	public IAct getIntentionAct() 
	{ return m_intentionAct; }

	public void setContextAct(IAct a)
	{ m_contextAct = a;	}
	
	public void setIntentionAct(IAct a)
	{ m_intentionAct = a; }
	
	public int getWeight() 
	{ return m_weight; }

	public boolean isPrimitive() 
	{ return m_isPrimitive;	}

	public void incWeight()
	{ m_weight++; }
	
	public void updateSuccessSatisfaction() 
	{
		if (!isPrimitive())
		{
			m_tag = m_contextAct.getTag() + "-" + m_intentionAct.getTag(); 
			m_contextAct.getSchema().updateSuccessSatisfaction();
			m_intentionAct.getSchema().updateSuccessSatisfaction();
			if (m_successAct == null)
				m_successAct = Ernest.factory().createAct(this,true, (m_contextAct.getSat() + 
											       		  m_intentionAct.getSat()));
			else
				m_successAct.setSat(m_contextAct.getSat() + 
											 m_intentionAct.getSat());
		}
	}
	
	/**
	 * Schemas are equal if they have the same context act and intention act 
	 * @author mcohen
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
			ISchema other = (ISchema)o;
			ret = (other.getContextAct() == getContextAct() &&
				   other.getIntentionAct() == getIntentionAct());
		}
		
		return ret;
	}

	/**
	 * Returns an identifier of the schema 
	 * @author ogeorgeon
	 */
	public int hashCode()
    {
		//int ret = (getContextAct().hashCode() * 10) + getIntentionAct().hashCode();
		return m_id;
    }	
	
	/**
	 * Returns the schema's tag 
	 * @author ogeorgeon
	 */
	public String getTag()
    {
		return m_tag;
    }	
	
	public String toString()
	{
		String s = String.format("[S%s, %s, %s, (S:%s F:%s) (C:%s, I:%s)]", 
				hashCode(), getTag(), getWeight(), getSuccessAct(), getFailureAct(), getContextAct(), getIntentionAct());  
		return s;
	}

	private Schema(int id)
	{
		m_id = id;
		m_isPrimitive = false;
		m_weight = 1;
	}
	
	private Schema(int id, String tag, int successSat, int failureSat)
	{
		m_id = id;
		m_tag = tag;
		m_isPrimitive = true;
		m_weight = REG_SENS_THRESH + 1;
		m_successAct = Ernest.factory().createAct(this, true, successSat);
		m_failureAct = Ernest.factory().createAct(this, false, failureSat);
	}
}
