package ernest;

/**
 * Represents an activation of a schema
 * A schema is activated when its context act belongs to the current context
 * This class represents a default implementation for an activation.
 * @author ogeorgeon
 *
 */public class Activation implements IActivation
{
	private ISchema m_schema = null;
	private IAct m_intention = null;
	private Integer m_weight = null;
	
	public static Activation createActivation(ISchema s)
	{ return new Activation(s); }
	
	public ISchema getSchema() 
	{
		return m_schema;
	}

	public IAct getIntention()
	{
		return m_intention;
	}
	
	public int getWeight()
	{
		return m_weight.intValue();
	}

	public int compareTo(IActivation o) 
	{
		return new Integer(o.getWeight()).compareTo(m_weight);
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
			Activation other = (Activation)o;
			ret = other.m_schema == m_schema;
		}
		
		return ret;
	}

	public int hashCode()
    {
		int ret = m_schema.hashCode();
		return ret;
    }	
	
	
	public String toString()
	{
		String s = String.format("[%s, %s (%s)]", m_schema , m_intention, m_weight);  
		return s;
	}
	
	private Activation(ISchema s)
	{
		m_schema = s;
		m_intention = s.getIntentionAct();
		m_weight = new Integer(m_schema.getWeight() * m_intention.getSat());
	}
}
