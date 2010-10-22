package ernest;

/**
 * Represents a proposal that Ernest enacts a Schema.  A proposal consists of 
 * the proposed schema and a weight that determines the strength of the proposal.
 * Proposals with higher weight are given priority.  This class represents a
 * default implementation for a Proposition.
 * @author mcohen
 *
 */public class Proposition implements IProposition
{
	private ISchema m_schema = null;
	private Integer m_weight = null;
	private Integer m_expectation = null;
	
	public static IProposition createProposition(ISchema s, int w, int e)
	{ return new Proposition(s,w,e); }
	
	public void update(int w, int e)
	{
		m_weight += w;
		m_expectation += e;
	}

	public int getWeight()
	{
		return m_weight.intValue();
	}

	public int getExpectation()
	{
		return m_expectation.intValue();
	}

	public ISchema getSchema()
	{
		return m_schema;
	}
	
	public int compareTo(IProposition o) 
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
			Proposition other = (Proposition)o;
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
		String s = String.format("%s w=%s e=%s", m_schema , m_weight, m_expectation);  
		return s;
	}
	
	private Proposition(ISchema s, int w, int e)
	{
		m_schema = s;
		m_weight = w;
		m_expectation = e;
	}
}
