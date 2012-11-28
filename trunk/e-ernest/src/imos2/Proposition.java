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
	private String m_moveLabel = "";
	private boolean m_transferred = false;
	
	/**
	 * Constructor. 
	 * @param i The proposed interaction.
	 * @param w The proposal's weight.
	 * @param e The proposal's expectation.
	 */
	public Proposition(IInteraction i, int w, int e, String m)
	{
		m_interaction = i;
		m_weight = w;
		m_expectation = e;
		m_moveLabel = m;
	}

	public int compareTo(IProposition o) 
	{
		//Transferred propositions are smaller 
		int oo = (o.getTransferred() ?  -10000 : 0);
		int ot = (m_transferred ? -10000 : 0);
		int c = new Integer(o.getWeight() + oo).compareTo(m_weight + ot);
		return c; 
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

	/**
	 * Generate a textual representation of the proposition for debug.
	 * @return A string that represents the proposition. 
	 */
	public String toString()
	{
		String s = m_interaction + " with weight = " + m_weight/10 + " transferred = " + m_transferred;
		return s;
	}

	public void setMoveLabel(String move) 
	{
		m_moveLabel = move;
	}

	public String getMoveLabel() 
	{
		return m_moveLabel;
	}

	public void addWeight(int w) 
	{
		m_weight += w;
	}

	public void setTransferred(boolean transferred) 
	{
		m_transferred = transferred;
	}

	public boolean getTransferred() 
	{
		return m_transferred;
	}
}
