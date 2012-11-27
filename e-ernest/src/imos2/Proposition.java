package imos2;

import imos.IAct;
import imos.ISchema;

/**
 * A proposal that Ernest enacts an interaction. 
 * @author ogeorgeon
 */
public class Proposition implements IProposition 
{
	private IInteraction m_interaction;
	private int m_weight = 0; 
	private int m_expectation = 0;
	
	/**
	 * Constructor. 
	 * @param i The proposed interaction.
	 * @param w The proposal's weight.
	 * @param e The proposal's expectation.
	 */
	public Proposition(IInteraction i, int w, int e)
	{
		m_interaction = i;
		m_weight = w;
		m_expectation = e;
	}

	public int compareTo(IProposition o) 
	{
		return new Integer(o.getWeight()).compareTo(m_weight);
	}

	public IInteraction getInteraction() 
	{
		return m_interaction;
	}

	public int getWeight() 
	{
		return m_weight;
	}

	public int getExpectation() 
	{
		return m_expectation;
	}

	public void update(int w, int e) 
	{
		m_weight += w;
		m_expectation +=e;
	}
	
	/**
	 * Two propositions are equal if they propose the same interaction. 
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
			ret = other.m_interaction == m_interaction;
		}
		
		return ret;
	}
}
