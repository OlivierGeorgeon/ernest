package ernest;

/**
 * An activation consists of the activated noème and a weight that determines the strength of the activation.
 * Schemas that receive the highest summarized activation weight are selected for evocation. 
 * @author ogeorgeon
 */
public class Activation implements IActivation
{
	private IAct m_noeme = null;
	private Integer m_weight = null;
	
	/**
	 * Constructor. 
	 * @param n The activated noème.
	 * @param w The proposal's weight
	 */
	public Activation(IAct n, int w)
	{
		m_noeme = n;
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
	public IAct getNoeme()
	{
		return m_noeme;
	}
	
	/**
	 * The greatest proposal is that that has the greatest weight. 
	 */
	public int compareTo(IActivation o) 
	{
		return new Integer(o.getWeight()).compareTo(m_weight);
	}

	/**
	 * Two proposals are equal if they propose the same noeme. 
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
			ret = other.getNoeme() == m_noeme;
		}
		
		return ret;
	}

	/**
	 * Generate a textual representation of the proposal for debug.
	 * @return A string that represents the proposal. 
	 */
	public String toString()
	{
		String s = String.format("%s w=%s", m_noeme , m_weight);  
		return s;
	}
	
}
