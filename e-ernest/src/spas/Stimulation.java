package spas;

import javax.vecmath.Vector3f;

import ernest.Ernest;

/**
 * A sensory stimulation.
 * @author Olivier
 */
public class Stimulation implements IStimulation 
{
	private int m_type;
	private int m_value;
	private Vector3f m_position;
	
	/**
	 * Create a visual stimulation.
	 * @param red The red component.
	 * @param green The green component.
	 * @param blue The blue component.
	 * @param position The position in egocentric coordinates. 
	 */
//	public Stimulation(int red, int green, int blue, Vector3f position)
//	{
//		m_type = Ernest.MODALITY_VISUAL;
//		m_value = red*65536 + green*256 + blue;
//		m_position = position;
//	}

	/**
	 * Create a stimulation from its type and its value
	 * @param type The stimulation's type
	 * @param value The stimulation's value
	 */
	public Stimulation(int type, int value)
	{
		m_type = type;
		m_value = value; 		
	}
	
	public void setPosition(Vector3f position) 
	{
		m_position = position;
	}

	public Vector3f getPosition() 
	{
		return m_position;
	}

	public int getType() 
	{
		return m_type;
	}

	public int getValue() 
	{
		return m_value;
	}

	public String getHexColor() 
	{
		int r = m_value/65536;
		int g = (m_value - r * 65536)/256;
		int b = m_value - r * 65536 - g * 256;
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
	
	/**
	 * Stimulations are equal if they have the same type and value. 
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
			IStimulation other = (IStimulation)o;
			ret = (other.getType() == m_type) && (other.getValue() == m_value);
		}
		
		return ret;
	}

}
