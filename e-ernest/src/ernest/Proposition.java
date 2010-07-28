package ernest;

/**
 * Represents a proposal that ernest enacts and Act.  A proposal consists of 
 * the proposed act and a weight that determines the strength of the proposal.
 * Proposals with higher weight are given priority.  This class represents a
 * default implementation for a Proposition.
 * @author mcohen
 *
 */public class Proposition implements IProposition
{
	private IAct m_act = null;
	private Integer m_weightedProp = null;
	
	public static IProposition createProposition(IAct a)
	{ return new Proposition(a); }
	
	public int getWP()
	{
		return m_weightedProp.intValue();
	}

	public IAct getAct()
	{
		return m_act;
	}
	
	public int compareTo(IProposition o) 
	{
		return new Integer(o.getWP()).compareTo(m_weightedProp);
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
			Proposition other = (Proposition)o;
			ret = other.m_act == m_act;
		}
		
		return ret;
	}

	public int hashCode()
    {
		int ret = m_act.hashCode();
		return ret;
    }	
	
	
	public String toString()
	{
		String s = String.format("[%s, %s]", m_act , m_weightedProp);  
		return s;
	}
	
	private void calculate()
	{
		ISchema s = m_act.getSchema();
		int w = s.getWeight();
		
		if (s.isPrimitive())
			m_weightedProp = new Integer(0);
		else
			m_weightedProp = new Integer(w * s.getIntentionAct().getSat());
	}

	private Proposition(IAct a)
	{
		m_act = a;
		calculate();
	}
}
