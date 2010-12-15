package ernest;

import java.io.ObjectInputStream.GetField;

/**
 * A Schema is a pattern of interaction between Ernest and its environment. 
 * Specifically, schemas can either succeed of fail when Ernest tries to enact 
 * them in the environment.  
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
	
	/** 
	 * The Schemq's tag
	 * If the schema is a primitive schema then the tag can be interpreted by the environment. 
	 */
	private String m_tag = null; 
	
	private boolean m_isPrimitive = true;
	private int m_length = 1;
	
	private boolean m_isActivated = false;

	/**
	 * Constructor for a primitive schema.
	 * Create a new primitive schema with a label that the environment can interpret.
	 * @param id Index of the schema.
	 * @param label A string code that represents the schema.  
	 * @return The created schema.
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
        m_weight = Ernest.INFINITE; // needs to be greater than the regularity threshold
	}

	/**
	 * Constructor for a composite schema.
	 * Create a new composite schema with a context act and an intention act. 
	 * @param id Index of the schema.
	 * @param contextAct The context act of the schema. 
	 * @param intentionAct The intention act of the schema. 
	 * @return The schema made from the provided context act and intention act, whether the schema was created or it already existed.
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
		m_tag = contextAct.getTag() + intentionAct.getTag();
		m_length = contextAct.getSchema().getLength() + intentionAct.getSchema().getLength();
	}
	
	/**
	 * Get the Schema's length.
	 * @return The schema's length.
	 */
	public int getLength()
	{
		return m_length;
	}

	/**
	 * Get the Schema's succeeding act.
	 * @return The schema's succeeding act.
	 */
	public IAct getSucceedingAct() 
	{	return m_succeedingAct; }

	/**
	 * Get the Schema's failing act.
	 * @return The schema's failing act.
	 */
	public IAct getFailingAct() 
	{	return m_failingAct; }

	/**
	 * Get the Schema's resulting act.
	 * @param status The status of the resulting act that is asked for. 
	 * @return The schema's succeeding act if status is true, the schema's failing act if status is false.
	 */
	public IAct getResultingAct(boolean status) 
	{	
		if (status)
			return m_succeedingAct;
		else
			return m_failingAct; 
	}

	/**
	 * Set the Schema's succeeding act.
	 * @param a The schema's succeeding act.
	 */
	public void setSucceedingAct(IAct a)
	{ m_succeedingAct = a;	}

	/**
	 * Set the Schema's failing act.
	 * @param a The schema's failing act.
	 */
	public void setFailingAct(IAct a)
	{ m_failingAct = a;	}
		
	/**
	 * Get the Schema's context act.
	 * @return The schema's context act.
	 */
	public IAct getContextAct() 
	{ return m_contextAct; }

	/**
	 * Get the Schema's intention act.
	 * @return The schema's intention act.
	 */
	public IAct getIntentionAct() 
	{ return m_intentionAct; }

	/**
	 * Set the Schema's context act.
	 * @param a The schema's context act.
	 */
	public void setContextAct(IAct a)
	{ m_contextAct = a;	}
	
	/**
	 * Set the Schema's intention act.
	 * @param a The schema's intention act.
	 */
	public void setIntentionAct(IAct a)
	{ m_intentionAct = a; }
	
	/**
	 * Get the Schema's weight.
	 * @return The schema's weight.
	 */
	public int getWeight() 
	{ return m_weight; }

	/**
	 * Get the Schema's primitive property.
	 * @return True if primitive, false if composite.
	 */
	public boolean isPrimitive() 
	{ return m_isPrimitive;	}

	/**
	 * Increment the schema's weight (add 1).
	 */
	public void incWeight()
	{ m_weight++; }
	
	/**
	 * Set the shema's weight.
	 * @param weight The schema's weight.
	 */
	public void setWeight(int weight)
	{ m_weight = weight; }

	/**
	 * Schemas are equal if they have the same tag. 
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
			ret = (getTag().equals(other.getTag()));
			//ret = (other.getContextAct() == getContextAct() &&
			//	   other.getIntentionAct() == getIntentionAct());
		}
		
		return ret;
	}

	/**
	 * Returns an identifier of the schema 
	 */
//	public int hashCode()
//    {
//		//int ret = (getContextAct().hashCode() * 10) + getIntentionAct().hashCode();
//		return m_id;
//    }	
	
	/**
	 * Get the schema's Tag.
	 * If the schema is a primitive schema then the tag can be interpreted by the environment.
	 * @return The schema's tag.
	 */
	public String getTag()
    {
		return m_tag;
    }	
	
	/**
	 * Get a textual representation of the  schema for debug.
	 * @return The schema's textual representation.
	 */
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

//	public boolean isActivated() 
//	{
//		return m_isActivated;
//	}

//	public void setActivated(boolean b) 
//	{
//		m_isActivated = b;
//	}
	
	/**
	 * Set the schema's prescriber act.
	 * @param a The act that prescribes this schema for enaction. 
	 */
	public void setPrescriberAct(IAct a)
	{
		m_prescriberAct = a;
	}
	
	/**
	 * Get the schema's prescriber act.
	 * @return The act that prescribes this schema for enaction.
	 */
	public IAct getPrescriberAct()
	{
		return m_prescriberAct;
	}

//	public void setPrescriberSchema(ISchema s)
//	{
//		m_prescriberSchema = s;
//	}
	
//	public ISchema getPrescriberSchema()
//	{
//		return m_prescriberSchema;
//	}

	/**
	 * Set the pointer that points to the subact that is currently being enacted.
	 * @param p The pointer that points to the currently enacted subact.
	 */
	public void setPointer(int p)
	{
		m_pointer = p;
	}
	
	/**
	 * Get the pointer that points to the subact that is currently being enacted.
	 * @return The pointer that points to the currently enacted subact.
	 */
	public int getPointer()
	{
		return m_pointer;
	}

}
