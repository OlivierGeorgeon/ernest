package spas;


/**
 * A set of sensory stimulations that are salient as a whole.
 * (contiguous visual stimulations with the same color, or contiguous tactile stimulations with the same feeling).
 * @author Olivier
 */
public class Salience implements ISalience 
{
	private int m_value;
	private int m_modality;
	private float m_direction;
	private float m_distance;
	private float m_span;
	private int m_attractiveness;
	private IBundle m_bundle;
	
	public Salience(int value, int modality, float direction, float distance, float span)
	{
		m_value = value;
		m_modality = modality;
		m_direction = direction;
		m_distance = distance;
		m_span = span;
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

	public float getDistance() 
	{
		return m_distance;
	}

	public float getDirection() 
	{
		return m_direction;
	}

	public float getSpan() 
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
			ret = (m_value == other.getValue()) || (m_modality == other.getType());
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

	public void setType(int modality) 
	{
		m_modality = modality;
	}

	public int getType() 
	{
		return m_modality;
	}

	public void setDirection(float direction) 
	{
		m_direction = direction;
	}

	public void setSpan(float span) 
	{
		m_span = span;
	}

}
