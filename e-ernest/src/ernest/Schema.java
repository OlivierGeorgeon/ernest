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
	//public static final int REG_SENS_THRESH = 10;	
	
	private int m_weight = Integer.MIN_VALUE; 
	private IAct m_succeedingAct = null;
	private IAct m_failingAct = null;
	private IAct m_contextAct = null;
	private IAct m_intentionAct = null;	
	private int m_id;
	private String m_tag = null; 
	private boolean m_isPrimitive = true;
	
	private boolean m_isActivated = false;

	public static ISchema createSchema(int id)
	{ return new Schema(id); }

	public static ISchema createSchema(int id, String tag, int successSat, int failureSat)
	{ return new Schema(id, tag, successSat, failureSat); }
	
	public IAct getSucceedingAct() 
	{	return m_succeedingAct; }

	public IAct getFailingAct() 
	{	return m_failingAct; }

	public void setSucceedingAct(IAct a)
	{ m_succeedingAct = a;	}

	public void setFailingAct(IAct a)
	{ m_failingAct = a;	}
		
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
	
	/**
	 * Initialize or reinitialize the schema's succeeding act 
	 * @author mcohen
	 * @author ogeorgeon
	 */
	public void initSucceedingAct() 
	{
		if (!isPrimitive())
		{
			m_tag = m_contextAct.getTag() +  m_intentionAct.getTag(); 
			if (m_succeedingAct == null)
				m_succeedingAct = Ernest.factory().createAct(this,true, (m_contextAct.getSat() + 
											       		  m_intentionAct.getSat()));
			else
				m_succeedingAct.setSat(m_contextAct.getSat() + 
											 m_intentionAct.getSat());
		}
	}
	
	/**
	 * Initialize or reinitialize the schema's failing act
	 * @return the failing act 
	 * @author ogeorgeon
	 */
	public IAct initFailingAct(int satisfaction) 
	{
		if (!isPrimitive())
		{
			if (m_failingAct == null)
				m_failingAct = Ernest.factory().createAct(this,false, satisfaction);
			else
				// If the failing act already exists then 
				//  its satisfaction is averaged with the previous value
				m_failingAct.setSat((m_failingAct.getSat() + satisfaction)/2);
		}
		return m_failingAct;
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
		String s;
		if (isPrimitive())
			s = String.format("[%s %s w=%s]", 
				getSucceedingAct(), getFailingAct(), getWeight());
		else
			s = String.format("[%s %s <C:S%s, I:S%s> w=%s]", 
					getSucceedingAct(), getFailingAct(), getContextAct().getSchema().hashCode(), getIntentionAct().getSchema().hashCode(), getWeight());
		return s;
	}

	private Schema(int id)
	{
		m_id = id;
		m_isPrimitive = false;
		m_weight = 0;
	}
	
	private Schema(int id, String tag, int successSat, int failureSat)
	{
		m_id = id;
		m_tag = tag;
		m_isPrimitive = true;
		m_weight = Algorithm.REG_SENS_THRESH + 1;
		m_succeedingAct = Ernest.factory().createAct(this, true, successSat);
		m_failingAct = Ernest.factory().createAct(this, false, failureSat);
	}

	public boolean isActivated() 
	{
		return m_isActivated;
	}

	public void setActivated(boolean b) 
	{
		m_isActivated = b;
	}

}
