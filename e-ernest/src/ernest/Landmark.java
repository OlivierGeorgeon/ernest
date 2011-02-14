package ernest;

import java.awt.Color;

/**
 * A landmark is an item that can be identified by its visual features.
 * Ernest 9.0 identifies landmarks by their color.
 * @author Olivier
 */
public class Landmark implements ILandmark 
{

	private Color m_color;
	
	protected Landmark(int red, int green, int blue)
	{
		m_color = new Color(red, green, blue);
	}
	
	public Color getColor() 
	{
		return m_color;
	}

	/**
	 * @return The act's string representation
	 */
	public String getLabel()
	{
		return "(" + m_color.getRed() + "," + m_color.getGreen()  + "," + m_color.getBlue() + ")" ;
	}
	
	/**
	 * Landmarks are equal if they have the same color. 
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
			ILandmark other = (ILandmark)o;
			ret = (other.getLabel().equals(getLabel()));
		}
		
		return ret;
	}
	
}
