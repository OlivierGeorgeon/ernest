package imos;

/**
 * A proposition of in intention act.
 * The proposed act and the weight that determines the strength of the proposal.
 * the proposed schema and a weight that determines the strength of the proposal.
 * Act propositions are used to select the schema proposition
 * @author ogeorgeon
 */
public class ActProposition implements IActProposition
{
	private IAct m_act = null;
	private int m_weight = 0; 
	private int m_expectation = 0;
	
	/**
	 * Constructor. 
	 * @param a The proposed schema.
	 * @param w The proposal's weight
	 */
	public ActProposition(IAct a, int w, int e)
	{
		m_act = a;
		m_weight = w;
		m_expectation = e;
	}

	/**
	 * Update an existing proposal.
	 * @param w The weight to add to the existing weight.
	 */
	public void update(int w, int e)
	{
		m_weight += w;
		m_expectation += e;
	}

	/**
	 * @return The proposition's weight.
	 */
	public int getWeight()
	{
		return m_weight;
	}

	/**
	 * @return The proposition's expectation.
	 */
	public int getExpectation()
	{
		return m_expectation;
	}

	/**
	 * Get the proposal's schema.
	 * @return The proposal's schema.
	 */
	public IAct getAct()
	{
		return m_act;
	}

	/**
	 * The greatest proposal is that that has the greatest weight. 
	 * @return the order of comparison
	 */
	public int compareTo(IActProposition o) 
	{
		return new Integer(o.getWeight()).compareTo(m_weight);
	}

	/**
	 * Two propositions are equal if they propose the same act. 
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
			ActProposition other = (ActProposition)o;
			ret = other.m_act == m_act;
		}
		
		return ret;
	}

	/**
	 * Generate a textual representation of the proposal for debug.
	 * @return A string that represents the proposal. 
	 */
	public String toString()
	{
		String s = m_act + " proposition_weight=" + m_weight;
		return s;
	}
}
