package ernest;

/**
 * A Schema is a pattern of interaction between the agent and its environment. 
 * Specifically, schemas can either succeed of fail when the agent tries to enact 
 * them in the environment.  This class represents a default implementation for
 * a Schema.
 * @author mcohen
 *
 */
public class Schema implements ISchema 
{
	public static final int REG_SENS_THRESH = 4;	
	
	private static int m_nextId_s = 1;
	
	private int m_weight = Integer.MIN_VALUE; 
	private IAct m_successAct = null;
	private IAct m_failureAct = null;
	private IAct m_contextAct = null;
	private IAct m_intentionAct = null;	
	private String m_id = null;
	private boolean m_isPrimitive = true;

	public static ISchema createSchema()
	{ return new Schema(); }

	public static ISchema createSchema(String id, int successSat, int failureSat)
	{ return new Schema(id, successSat, failureSat); }
	
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

	public String getId() 
	{ return m_id; }
	
	public void incWeight()
	{ m_weight++; }
	
	public void updateSuccessSatisfaction() 
	{
		if (!isPrimitive())
		{
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
	
	public boolean equals(Object o)
	{
		boolean ret = false;
		if (o instanceof ISchema)
		{
			ISchema other = (ISchema)o;
			ret = (other.getContextAct() == getContextAct() &&
				   other.getIntentionAct() == getIntentionAct());
		}
		else 
		{
			ret = false;
		}
		
		return ret;
	}

	public String toString()
	{
		String s = String.format("[ID:%s, W:%s, C:%s, I:%s]", getId() , getWeight(), getContextAct(), getIntentionAct());  
		return s;
	}

	private Schema()
	{
		m_id = "S" + m_nextId_s++;
		m_isPrimitive = false;
		m_weight = 1;
	}
	
	private Schema(String id, int successSat, int failureSat)
	{
		m_nextId_s++;
		m_id = id;
		m_isPrimitive = true;
		m_weight = REG_SENS_THRESH + 1;
		m_successAct = Ernest.factory().createAct(this, true, successSat);
		m_failureAct = Ernest.factory().createAct(this, false, failureSat);
	}
}
