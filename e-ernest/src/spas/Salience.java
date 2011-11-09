package spas;

import javax.vecmath.Vector3f;


/**
 * A set of sensory stimulations that are salient as a whole.
 * (contiguous visual stimulations with the same color, or contiguous tactile stimulations with the same feeling).
 * @author Olivier
 */
public class Salience implements ISalience 
{
	private int m_value;
	private int m_modality;
	private Vector3f m_position;
	private float m_span;
	private int m_attractiveness;
	private IBundle m_bundle;
	
	/**
	 * Constructor. 
	 * @param value The salience value.
	 * @param modality The salience modality (MODALITY_VISUAL or MODALITY_TACTILE.
	 * @param direction The average direction in egocentric referential.
	 * @param distance The distance in egocentric referential.
	 * @param span The angular span.
	 */
	public Salience(int value, int modality, float direction, float distance, float span)
	{
		m_value = value;
		m_modality = modality;
		m_span = span;
		m_position = new Vector3f((float)(distance * Math.cos((double)direction)), (float)(distance * Math.sin((double)direction)), 0f);
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
		return m_position.length();
	}

	public float getDirection() 
	{
		return (float)Math.atan2((double)m_position.y, (double)m_position.x);
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
			ret = (m_value == other.getValue()) || (m_modality == other.getModality());
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

	public int getModality() 
	{
		return m_modality;
	}

	public void setSpan(float span) 
	{
		m_span = span;
	}
	
	public String getHexColor() 
	{
		return getHexColor(m_value);
	}

	private String getHexColor(int rgb) 
	{
		int r = rgb/65536;
		int g = (rgb - r * 65536)/256;
		int b = rgb - r * 65536 - g * 256;
		String s = format(r) + format(g) + format(b);

		return s;
	}
	
	private String format(int i)
	{
		if (i == 0)
			return "00";
		else if (i < 16)
			return "0" + Integer.toString(i, 16).toUpperCase();
		else
			return Integer.toString(i, 16).toUpperCase();
	}

	public boolean isFrontal()
	{
		return (getDirection() - m_span / 2 < 0 && getDirection() + m_span / 2 > 0 );
	}
	
	public Vector3f getPosition()
	{
		return m_position;
	}

}
