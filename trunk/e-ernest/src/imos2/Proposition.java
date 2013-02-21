package imos2;

import java.util.ArrayList;

/**
 * A proposition that Ernest enacts an interaction. 
 * @author ogeorgeon
 */
public class Proposition implements IProposition 
{
	private IInteraction m_interaction;
	private int m_weight = 0; 
	private boolean m_transferred = false;
	private int m_pros = 0;
	private int m_cons = 0;
	
	// The list of alternate interactions of the proposed interaction.
	private ArrayList<IInteraction> m_alternateInteractions = new ArrayList<IInteraction>();

	/**
	 * Constructor. 
	 * @param i The proposed interaction.
	 * @param w The proposal's weight.
	 * @param e The proposal's expectation.
	 */
	public Proposition(IInteraction i, int w)
	{
		m_interaction = i;
		m_weight = w;
		if (w > 0) m_pros+=w; else m_cons+=w;
	}

	public int compareTo(IProposition o) 
	{
		//Transferred propositions are smaller 
		int oo = 0 ; //(o.getTransferred() ?  -10000 : 0);
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
	
	public void addWeight(int w) 
	{
		m_weight += w;
		if (w > 0) m_pros+=w; else m_cons+=w;
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
		String s = m_interaction + " with weight = " + m_weight/10;// + " transferred = " + m_transferred;
		return s;
	}

	public void setTransferred(boolean transferred) 
	{
		m_transferred = transferred;
	}

	public boolean getTransferred() 
	{
		return m_transferred;
	}

	public int getAngst() 
	{
		int angst = 1;
		if (m_pros != 0)
			angst = (int) Math.round(- 10f * m_cons / m_pros);
		return angst;
	}

	
//	private void updateProCon(int w)
//	{
//		if (w > 0) m_pros+=w; else m_cons+=w;
//	}
}
