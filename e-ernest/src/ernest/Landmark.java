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
	private int m_lastTimeSeen;
	private int m_eatability;
	private int m_drinkability;
	
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
	
	/**
	 * @return false if this landmark's color is the standard wall color or bump color. true in all other cases.
	 */
	public boolean isSingularity()
	{
		boolean regularWall = m_color.equals(Ernest.WALL_COLOR) || m_color.equals(Ernest.BUMP_COLOR);
		return !regularWall ;
	}

	/**
	 * @return true if the landmark is water.
	 */
	public int getDrinkability()
	{
		//return (m_color.equals(Ernest.WATER_COLOR)) ;
		return m_drinkability;
	}
	
	public void updateDrinkability(int t)
	{
		int elapsed = t - m_lastTimeSeen;
		
		// Drinkability is inversely proportional to the time elapsed
		// between the last time seeing this lankmark since thirsty and drinking.
		if (elapsed > 0) m_drinkability = 1000/elapsed;
	}

	/**
	 * @return true if the landmark is food.
	 */
	public int getEatability()
	{
		//return (m_color.equals(Ernest.FOOD_COLOR)) ;
		return m_eatability;
	}
	
	public void updateEatability(int t)
	{
		int elapsed = t - m_lastTimeSeen;
		
		// Eatability is inversely proportional to the time elapsed
		// between the last time seeing this lankmark since hungry and eating.
		if (elapsed > 0) m_eatability = 1000/elapsed;
	}

	public void setLastTimeSeen(int t)
	{
		m_lastTimeSeen = t;
	}
	
	public int getLastTimeSeen()
	{
		return m_lastTimeSeen;
	}

}
