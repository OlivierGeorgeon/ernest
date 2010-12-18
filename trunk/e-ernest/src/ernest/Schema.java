package ernest;

/**
 * A Schema is a pattern of interaction between Ernest and its environment. 
 * Specifically, schemas can either succeed of fail when Ernest tries to enact 
 * them in the environment.  
 * @author mcohen
 * @author ogeorgeon
 */
public class Schema implements ISchema 
{
	/** The icon's width (first coordinate: x) */
	public static final int ICON_WIDTH = 2;
	/** The icon's height (second coordinate: y) */
	public static final int ICON_HEIGHT = 1;
	
	/** The icon's matrix of pixels */
	private int[][] m_matrix = new int[ICON_WIDTH][ICON_HEIGHT]; 

	private int m_id = 0;
	private String m_tag = null; 
	private int m_type = 0;

	private IAct m_succeedingAct = null;
	private IAct m_failingAct = null;
	private IAct m_contextAct = null;
	private IAct m_intentionAct = null;	
	private int m_weight = 0; 
	private int m_length = 1;
	private boolean m_isPrimitive = true;

	private IAct m_prescriberAct = null;
	private int m_pointer = 0;	
	
	
	public void setSucceedingAct(IAct a) { m_succeedingAct = a; }
	public void setFailingAct(IAct a)    { m_failingAct = a; }
	public void setContextAct(IAct a)    { m_contextAct = a; }
	public void setIntentionAct(IAct a)	 { m_intentionAct = a; }
	public void setWeight(int weight)    { m_weight = weight; }
	public void setPrescriberAct(IAct a) { m_prescriberAct = a; }
	public void setPointer(int p)        { m_pointer = p; }

	public int  getId()                  { return m_id;}
	public int  getType()                { return m_type;}
	public int  getLength()              { return m_length;}
	public IAct getSucceedingAct()       { return m_succeedingAct; }
	public IAct getFailingAct()          { return m_failingAct; }
	public IAct getContextAct()          { return m_contextAct; }
	public IAct getIntentionAct()        { return m_intentionAct; }
	public int  getWeight()              { return m_weight; }
	public IAct getPrescriberAct()       { return m_prescriberAct; }
	public int  getPointer()	         { return m_pointer; }
	
	public void incWeight()              { m_weight++; }

	public boolean isPrimitive()         { return m_isPrimitive; }

	public IAct resultingAct(boolean status) 
	{	
		if (status) return m_succeedingAct;
		else return m_failingAct; 
	}
	
	/**
	 * Constructor for a primitive schema.
	 * Create a new primitive schema with a label that the environment can interpret.
	 * @param id The schema's unique serial number.
	 * @param label A string code that represents the schema.  
	 * @return The created schema.
	 */
	public static ISchema createMotorSchema(int id, String label)
	{ 
		return new Schema(id, label);
	}
	
	private Schema(int id, String label)
	{
		m_id = id;
		m_tag = label;
		m_isPrimitive = true;
        m_weight = Ernest.INFINITE; // needs to be greater than the regularity threshold to support enaction of higher level schemas
	}

	/**
	 * Constructor for a sensor schema.
	 * Create a new sensor schema with a matrix icon.
	 * @param id The schema's unique serial number.
	 * @param label A label for debug.  
	 * @return The created sensor schema.
	 */
	public static ISchema createSensorSchema(int id, String label, int [][] matrix)
	{ 
		return new Schema(id, label, matrix);
	}
	
	private Schema(int id, String label, int [][] matrix)
	{
		m_id = id;
		m_tag = label;
		m_matrix = matrix;
		m_isPrimitive = true;
        m_weight = 0; 
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
		m_tag = contextAct.getLabel() + intentionAct.getLabel();
		m_length = contextAct.getSchema().getLength() + intentionAct.getSchema().getLength();
	}
	
	/**
	 * Schemas are equal if they have the same string and the same type. 
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
			ret = (getTag().equals(other.getTag()) &&
					getType() == other.getType() );
		}
		
		return ret;
	}

	public String getTag()               
	{ 
		return m_tag; 
	}	

	/**
	 * Generate a textual representation of the  schema for debug.
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
					getSucceedingAct(), getFailingAct(), getContextAct().getSchema().getId(), getIntentionAct().getSchema().getId(), getWeight());
		return s;
	}

}
