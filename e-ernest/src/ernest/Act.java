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
	
	private String m_label = "";
	/** The noème's module */
	private int m_module = 0;
	private int m_confidence = Ernest.HYPOTHETICAL_NOEME;
	private int m_activation = 0;
	private int m_length = 1;
	
	public void setSatisfaction(int s)         { m_satisfaction = s; }
	public void setConfidence(int c)           { m_confidence = c; }
	public void setPrescriberSchema(ISchema s) { m_prescriberSchema = s; }
	public void setActivation(int a)           { m_activation = a; }
	
	public boolean getStatus()                 { return m_status; }
	public int     getSatisfaction()           { return m_satisfaction; }
	public int     getModule()           	   { return m_module; }
	public int     getConfidence()             { return m_confidence; }
	public ISchema getPrescriberSchema()       { return m_prescriberSchema; }
	public ISchema getSchema()                 { return m_schema; }
	public int     getActivation()             { return m_activation; }
	public int     getLength()                 { return m_length; }

	/**
	 * Constructor for a composite succeeding central act 
	 * @param s The act's schema
	 * @return the created act
	 */
	public static IAct createCompositeSucceedingAct(ISchema s)
	{
		if (s.isPrimitive())
		{
			System.out.println("Error creating a composite act. Schema " + s + " is not a composite schema.");
			return null;
		}
		else
		{
			String label = "(" + s.getLabel() +")";
			int satisfaction = s.getContextAct().getSatisfaction() + s.getIntentionAct().getSatisfaction();
			int module = s.getModule();
		
			return new Act(label, s, true, satisfaction, module, Ernest.HYPOTHETICAL_NOEME);
		}
	}
	
	/**
	 * Constructor for a composite failing central act. 
	 * @param s The act's schema.
	 * @param satisfaction The failing satisfaciton.
	 * @return the created act.
	 */
	public static IAct createCompositeFailingAct(ISchema s, int satisfaction)
	{
		if (s.isPrimitive())
		{
			System.out.println("Error creating a composite act. Schema " + s + " is not a composite schema.");
			return null;
		}
		else
		{
			String label = "[" + s.getLabel() +"]";
			int module = s.getModule();
			// The failing act is RELIABLE because its schema had to be reliable to be enacted and 
			// making it possible to experience its failure.
			return new Act(label, s, false, satisfaction, module, Ernest.RELIABLE_NOEME);
		}
	}
	
	/**
	 * Create a noème
	 * @param label The noème's label
	 * @param s The noème's schema if any
	 * @param status The noème's status if any: True for success, false for failure
	 * @param satisfaction The act's satisfaction value.
	 * @param type the module
	 * @param confidence The degree of confidence Ernest has in this noème
	 * @return The craeted act.
	 */
	public static IAct createAct(String label, ISchema s, boolean status, int satisfaction, int module, int confidence)
	{
		return new Act(label, s, status, satisfaction, module, confidence);
	}
	
	/**
	 * The abstract constructor for a noème
	 * @param label The noème's label
	 * @param s The noème's schema if any
	 * @param status The noème's status if any: True for success, false for failure
	 * @param type the module
	 * @param confidence The degree of confidence Ernest has in this noème
	 */
	protected Act(String label, ISchema s, boolean status, int satisfaction, int module, int confidence)
	{
		m_label = label;
		m_schema = s;
		m_status = status;
		m_satisfaction = satisfaction;
		m_module = module;
		m_confidence = confidence;
		if (s == null)
			m_length = 1;
		else 
			m_length = s.getLength();
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
	
	/**
	 * The greatest noème is that that has the greatest activation. 
	 */
	public int compareTo(IAct a) 
	{
		return new Integer(a.getActivation()).compareTo(m_activation);
	}


}
