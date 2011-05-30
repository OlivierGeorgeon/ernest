package ernest;

/**
 * An activation consists of the activated act and a weight that determines the strength of the activation.
 * Primitive acts that receive the highest summarized activation weight are selected for enaction. 
 * @author ogeorgeon
 */
public class Activation implements IActivation
{
	private IAct m_act = null;
	private Integer m_weight = null;
	
	/**
	 * Constructor. 
	 * @param n The activated act.
	 * @param w The proposal's weight
	 */
	public Activation(IAct n, int w)
	{
		m_act = n;
		m_weight = w;
	}

	/**
	 * Update an existing activation.
	 * @param w The weight to add to the existing weight.
	 */
	public void update(int w)
	{
		m_weight += w;
	}

	/**
	 * Get the activation's weight.
	 * @return The proposal's weight.
	 */
	public int getWeight()
	{
		return m_weight.intValue();
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
	 */
	public int compareTo(IActivation o) 
	{
		return new Integer(o.getWeight()).compareTo(m_weight);
	}

	/**
	 * Two proposals are equal if they propose the same act. 
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
			Activation other = (Activation)o;
			ret = other.getAct() == m_act;
		}
		
		return ret;
	}

	/**
	 * Generate a textual representation of the proposal for debug.
	 * @return A string that represents the proposal. 
	 */
	public String toString()
	{
//		String s = String.format("%s w=%s", m_act , m_weight);  
		String s = m_act + " w=" + m_weight;
		return s;
	}
	
}
