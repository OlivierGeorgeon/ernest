package imos2;

import imos.IAct;
import imos.ISchema;

public class MoveProposition implements IMoveProposition 
{
	private String m_move ="";
	private int m_weight = 0; 
	private int m_expectation = 0;
	private IInteraction m_interaction = null;

	/**
	 * Constructor. 
	 * @param s The proposed schema.
	 * @param w The proposal's weight
	 * @param e The proposal's expectaion. If positive: success, if negative failure.
	 */
	public MoveProposition(String s, int w, int e, IInteraction a)
	{
		m_move = s;
		m_weight = w;
		m_expectation = e;
		m_interaction = a;
	}

	public String getMove() 
	{
		return m_move;
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

	public void update(int w, int e, IInteraction i) 
	{
		m_weight += w;
		
		// if this act has a higher expectation than the previous one then this act replaces the previous one.
		if (m_expectation < e)
		{
			m_interaction = i;
			m_expectation = e;
		}
	}
	/**
	 * Two propositions are equal if they propose the same move. 
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
			MoveProposition other = (MoveProposition)o;
			ret = other.m_move.equals(m_move);
		}
		
		return ret;
	}
	public int compareTo(IMoveProposition o) 
	{
		return  new Integer(o.getWeight()).compareTo(m_weight);
	}
	
	/**
	 * Generate a textual representation of the proposition for debug.
	 * @return A string that represents the proposition. 
	 */
	public String toString()
	{
		String s = m_move + " with weight = " + m_weight/10 + " for expected interaction " + m_interaction;
		return s;
	}

}
