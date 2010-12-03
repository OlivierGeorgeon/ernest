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
	
	private int m_weight = 0; 
	private IAct m_succeedingAct = null;
	private IAct m_failingAct = null;
	private IAct m_contextAct = null;
	private IAct m_intentionAct = null;	
	private IAct m_prescriberAct = null;
	private ISchema m_prescriberSchema = null;
	private int m_id;
	private int m_pointer = 0;
	private String m_tag = null; 
	private boolean m_isPrimitive = true;
	
	private boolean m_isActivated = false;

	/**
	 * Constructor for a primitive schema
	 * Create a new primitive schema with a label that the environment can interpret 
	 * @author ogeorgeon
	 */
	public static ISchema createPrimitiveSchema(int id, String label)
	{ 
		return new Schema(id, label);
	}
	
	private Schema(int id, String label)
	{
		m_id = id;
		m_tag = label;
		m_isPrimitive = true;
        m_weight = Ernest.REG_SENS_THRESH + 1;
	}

	/**
	 * Constructor for a composite schema
	 * Create a new composite schema with context act and an intention act 
	 * @author ogeorgeon
	 */
	public static ISchema createCompositeSchema(int id, IAct contextAct, IAct intentionAct)
	{ 
		return new Schema(id, contextAct, intentionAct); 
	}

	private Schema(int id, IAct contextAct, IAct intentionAct)
	{
		m_id = id;
		m_isPrimitive = false;
		m_contextAct = contextAct;
		m_intentionAct = intentionAct;
		m_tag = contextAct.getTag() +  intentionAct.getTag(); 
	}
	
	public IAct getSucceedingAct() 
	{	return m_succeedingAct; }

	public IAct getFailingAct() 
	{	return m_failingAct; }

	public IAct getResultingAct(boolean status) 
	{	
		if (status)
			return m_succeedingAct;
		else
			return m_failingAct; 
	}

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

	public boolean isActivated() 
	{
		return m_isActivated;
	}

	public void setActivated(boolean b) 
	{
		m_isActivated = b;
	}
	
	public void setPrescriberAct(IAct a)
	{
		m_prescriberAct = a;
	}
	
	public IAct getPrescriberAct()
	{
		return m_prescriberAct;
	}

	public void setPrescriberSchema(ISchema s)
	{
		m_prescriberSchema = s;
	}
	
	public ISchema getPrescriberSchema()
	{
		return m_prescriberSchema;
	}

	public void setPointer(int p)
	{
		m_pointer = p;
	}
	
	public int getPointer()
	{
		return m_pointer;
	}

}
