package spas;


public class Salience implements ISalience 
{
	private int m_direction;
	private int m_distance;
	private int m_span;
	private int m_attractiveness;
	private IBundle m_bundle;
	private int m_value;
	private int m_type;
	private float m_theta;
	private float m_distancef;
	private float m_spanf;
	
	Salience(int value, int type, float theta, float distance, float span)
	{
		m_value = value;
		m_type = type;
		m_theta = theta;
		m_distancef = distance;
		m_spanf = span;
	}
	
	Salience(int value, int direction, int span)
	{
		m_value = value;
		m_type = 0;
		m_direction = direction;
		m_span = span;
	}

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
	 * Saliences are equal if they have the same value and the same type. 
	 * This is not used.  
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
			ret = (m_value == other.getValue()) || (m_type == other.getType());
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

	public void setType(int type) 
	{
		m_type = type;
	}

	public int getType() 
	{
		return m_type;
	}

	public void setDirection(float direction) 
	{
		m_theta = direction;
	}

	public void setSpan(float span) 
	{
		m_spanf = span;
	}

	public float getDirectionf()
	{
		return m_theta;
	}

	public float getSpanf() 
	{
		return m_spanf;
	}
}
