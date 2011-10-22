package ernest;

import persistence.IBundle;

public class Salience implements ISalience 
{
	private int m_direction;
	private int m_distance;
	private int m_span;
	private int m_attractiveness;
	private IBundle m_bundle;
	private int m_value;
	
	public void setDirection(int direction) 
	{
		m_direction = direction;
	}

	public void setDistance(int distance) 
	{
		m_distance = distance;
	}

	public void setSpan(int span) 
	{
		m_span = span;
	}

	public void setAttractiveness(int attractiveness) 
	{
		m_attractiveness = attractiveness;
	}

	public void setBundle(IBundle bundle) 
	{
		m_bundle = bundle;
	}

	public int getDirection() 
	{
		return m_direction;
	}

	public int getDistance() 
	{
		return m_distance;
	}

	public int getSpan() 
	{
		return m_span;
	}

	public int getAttractiveness() 
	{
		return m_attractiveness;
	}
	
	public IBundle getBundle() 
	{
		return m_bundle;
	}
	
	/**
	 * Saliences are equal if they have the same bundle. 
	 * This is not used. TODO find the right criteria of it is ever used. 
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
			ISalience other = (ISalience)o;
			//ret = other.getColor().equals(m_color);
			ret = other.getBundle().equals(m_bundle);
		}
		
		return ret;
	}
	
	public void setValue(int value)
	{
		m_value = value;
	}
	public int getValue()
	{
		return m_value;
	}
}
