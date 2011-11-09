package spas;

import javax.vecmath.Vector3f;

/**
 * A sensory stimulation.
 * @author Olivier
 */
public class Stimulation implements IStimulation 
{
	private int m_modality;
	private int m_value;
	private Vector3f m_position;
	
	/**
	 * Create a stimulation from a type and a value
	 * @param modality The stimulation's type
	 * @param value The stimulation's value
	 */
	public Stimulation(int modality, int value)
	{
		m_modality = modality;
		m_value = value; 		
	}
	
	/**
	 * Create a stimulation from a type and a value
	 * @param modality The stimulation's type
	 * @param value The stimulation's value
	 * @param position The stimulation's position in egocentric referential.
	 */
	public Stimulation(int modality, int value, Vector3f position)
	{
		m_modality = modality;
		m_value = value; 		
		m_position = position;
	}
	
//	public void setPosition(Vector3f position) 
//	{
//		m_position = position;
//	}

	public Vector3f getPosition() 
	{
		return m_position;
	}

	public int getModality() 
	{
		return m_modality;
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
	 * Stimulations are equal if they have the same modality and value. 
	 * (this equality criteria is used to find saliences).
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
			ret = (other.getModality() == m_modality) && (other.getValue() == m_value);
		}
		return ret;
	}

}
