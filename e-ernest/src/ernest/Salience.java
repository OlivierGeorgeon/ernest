package ernest;

import java.awt.Color;

public class Salience implements ISalience 
{
	private int m_direction;
	private int m_distance;
	private int m_span;
	private Color m_color;
	private int m_attractiveness;
	
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

	public void setColor(Color color) 
	{
		m_color = color;
	}

	public Color getColor() 
	{
		return m_color;
	}

	public void setAttractiveness(int attractiveness) 
	{
		m_attractiveness = attractiveness;
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
	
	/**
	 * Icons are equal if they have the same color. 
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
			ret = other.getColor().equals(m_color);
		}
		
		return ret;
	}
	public String getHexColor() 
	{
		String s = String.format("%06X", m_color.getRGB()  & 0x00ffffff); 
		return s;
	}
}