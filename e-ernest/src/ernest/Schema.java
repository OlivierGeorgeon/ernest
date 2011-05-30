package ernest;

/**
 * A Schema is a sequence of interaction between Ernest and its environment.
 * Primitive schemas represent a single interaction step.
 * Composite schemas are an association of two acts and therefore represent an association of two sub-schemas. 
 * @author mcohen
 * @author ogeorgeon
 */
public class Schema implements ISchema 
{

	private int     	m_id = 0;
	private String  	m_label = null; 
	private IAct    	m_succeedingAct = null;
	private IAct    	m_failingAct = null;
	private IAct    	m_contextAct = null;
	private IAct    	m_intentionAct = null;	
	private int     	m_weight = 0; 
	private int     	m_length = 1;
	private boolean 	m_isPrimitive = true;
	private IAct    	m_prescriberAct = null;
	private int     	m_pointer = 0;	
	
	/**
	 * Constructor for a primitive schema.
	 * Create a new primitive schema with a label that the environment can interpret.
	 * @param id The schema's unique serial number.
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
		m_label = label;
		m_isPrimitive = true;
        m_weight = Ernest.INFINITE; // needs to be greater than the regularity threshold to support enaction of higher level schemas
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
		m_label = contextAct.getLabel() + intentionAct.getLabel();
		m_length = contextAct.getLength() + intentionAct.getLength();
	}
		
	public void setSucceedingAct(IAct a) 
	{ 
		m_succeedingAct = a; 
	}
	
	public void setFailingAct(IAct a)    
	{ 
		m_failingAct = a; 
	}
	
	public void setContextAct(IAct a)    
	{ 
		m_contextAct = a; 
	}
	
	public void setIntentionAct(IAct a)	 
	{ 
		m_intentionAct = a; 
	}
	
	public void setPrescriberAct(IAct a) 
	{ 
		m_prescriberAct = a; 
	}
	
	public void setPointer(int p)        
	{ 
		m_pointer = p; 
	}

	public int  getId()                  
	{ 
		return m_id;
	}
	
	public int  getLength()              
	{ 
		return m_length;
	}
	
	public IAct getSucceedingAct()       
	{ 
		return m_succeedingAct; 
	}
	
	public IAct getFailingAct()          
	{ 
		return m_failingAct;
	}
	
	public IAct getContextAct()          
	{ 
		return m_contextAct; 
	}
	
	public IAct getIntentionAct()        
	{ 
		return m_intentionAct; 
	}
	
	public int  getWeight()              
	{ 
		return m_weight; 
	}
	
	public IAct getPrescriberAct()       
	{ 
		return m_prescriberAct; 
	}
	
	public int  getPointer()	         
	{ 
		return m_pointer; 
	}
	
	public void setWeight(int weight)    
	{ 
		m_weight = weight; 
		if (m_weight > Ernest.REG_SENS_THRESH)
		{
			getSucceedingAct().setConfidence(Ernest.RELIABLE);
			// (The failing act is created RELIABLE)
		}
	}
	
	public void incWeight()              
	{ 
		m_weight++;
		if (m_weight > Ernest.REG_SENS_THRESH)
			getSucceedingAct().setConfidence(Ernest.RELIABLE);
			// (The failing act is created RELIABLE)
	}

	public boolean isPrimitive()         
	{ 
		return m_isPrimitive; 
	}

	public IAct resultingAct(boolean status) 
	{	
		if (status) return m_succeedingAct;
		else return m_failingAct; 
	}
	
	/**
	 * Schemas are equal if they have the same label. 
	 * @return True if schemas are equal, false if schemas are different
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
			ret = (getLabel().equals(other.getLabel()));
		}
		
		return ret;
	}

	public String getLabel()               
	{ 
		return m_label; 
	}	

	/**
	 * Generate a textual representation of the  schema for debug.
	 * @return The schema's textual representation.
	 */
	public String toString()
	{		
		String s;
		if (isPrimitive())
//			s = String.format("[%s %s w=%s]", 
//				getSucceedingAct(), getFailingAct(), getWeight());
			s = "[" + getSucceedingAct() + " " + getFailingAct() + " w=" + getWeight() + "]";
		else
//			s = String.format("[%s %s <C:S%s, I:S%s> w=%s]", 
//					getSucceedingAct(), getFailingAct(), getContextAct().getLabel(), getIntentionAct().getLabel(), getWeight());
			s = "[" + getSucceedingAct() + " " + getFailingAct() + " <C:S" + getContextAct().getLabel() +
				", I:S" + getIntentionAct().getLabel() + "> w=" + getWeight() + "]";

//		s = String.format("\"%s w=%s\"", m_label, getWeight());
		s = "\"" + m_label + " w=" + getWeight() + "\"";
		return s;
	}

}
