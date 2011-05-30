package ernest;

/**
 * Represents a proposal for enacting a Schema. 
 * A proposal consists of the proposed schema, 
 * a weight that determines the strength of the proposal,
 * and an expectation that determines the prediction that the schema will succeed.
 * @author mcohen
 * @author ogeorgeon
 */
public class Proposition implements IProposition
{
	private ISchema m_schema = null;
	private Integer m_weight = null;
	private Integer m_expectation = null;
	
	/**
	 * Constructor. 
	 * @param s The proposed schema.
	 * @param w The proposal's weight
	 * @param e The proposal's expectaion. If positive: success, if negative failure.
	 */
	public Proposition(ISchema s, int w, int e)
	{
		m_schema = s;
		m_weight = w;
		m_expectation = e;
	}

	/**
	 * Update an existing proposal.
	 * @param w The weight to add to the existing weight.
	 * @param e The expectation to add to the existing expectation.
	 */
	public void update(int w, int e)
	{
		m_weight += w;
		m_expectation += e;
	}

	/**
	 * Get the proposal's weight.
	 * @return The proposal's weight.
	 */
	public int getWeight()
	{
		return m_weight.intValue();
	}

	/**
	 * Get the proposal's expectation.
	 * @return The proposal's expectation.
	 */
	public int getExpectation()
	{
		return m_expectation.intValue();
	}

	/**
	 * Get the proposal's schema.
	 * @return The proposal's schema.
	 */
	public ISchema getSchema()
	{
		return m_schema;
	}

	public IAct getAct()
	{
		return (m_expectation >= 0 ? m_schema.getSucceedingAct() : m_schema.getFailingAct());
	}
	
	/**
	 * The greatest proposal is that that has the greatest weight. 
	 */
	public int compareTo(IProposition o) 
	{
		return new Integer(o.getWeight()).compareTo(m_weight);
	}

	/**
	 * Two proposals are equal if they propose the same schema. 
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
			Proposition other = (Proposition)o;
			ret = other.m_schema == m_schema;
		}
		
		return ret;
	}

	/**
	 * Generate a textual representation of the proposal for debug.
	 * @return A string that represents the proposal. 
	 */
	public String toString()
	{
		int satisfaction = 0;
		if (m_expectation >= 0) satisfaction = m_schema.getSucceedingAct().getSatisfaction();
		else satisfaction = m_schema.getFailingAct().getSatisfaction();
			
//		String s = String.format("%s w=%s e=%s s=%s", m_schema , m_weight, m_expectation, satisfaction);  
		String s = m_schema + " w=" + m_weight + " e=" + m_expectation + " s=" + satisfaction;
		return s;
	}
	
}
