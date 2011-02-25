package ernest;

import java.awt.Color;

/**
 * A landmark is a pattern in Ernest's visual scene that Ernest can memorize and recognize.
 * Ernest can learn associations between landmarks and behaviors such as drink, eat, and bump.
 * Ernest 9.0 recognize landmarks by their color.
 * @author Olivier
 */
public class Landmark implements ILandmark 
{

	private Color m_color;
	private int m_lastTimeSeen;
	private boolean m_edible;
	private boolean m_drinkable;
	private boolean m_bumpable;
	
	private int m_lastTimeThirsty = 0;
	private int m_lastTimeHungry = 0;
	private int m_distanceToWater;
	private int m_distanceToFood;
	
	protected Landmark(int red, int green, int blue)
	{
		m_color = new Color(red, green, blue);
	}
	
	public Color getColor() 
	{
		return m_color;
	}

	/**
	 * @return The landmark's string representation
	 */
	public String getLabel()
	{
		return "(" + m_color.getRed() + "," + m_color.getGreen()  + "," + m_color.getBlue() + ")" ;
	}
	
	/**
	 * Landmarks are equal if they have the same label (i.e. the same color). 
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
	 * @return true if the landmark is water.
	 */
	public boolean isDrinkable()
	{
		//return (m_color.equals(Ernest.WATER_COLOR)) ;
		return m_drinkable;
	}
	
	public void setDrinkable()
	{
		m_drinkable = true;
	}

	/**
	 * @return true if the landmark is food.
	 */
	public boolean isEdible()
	{
		return m_edible;
	}
	
	public void setEdible()
	{
		m_edible = true;
	}

	public void setLastTimeChecked(int t)
	{
		m_lastTimeSeen = t;
	}
	
	public int getLastTimeChecked()
	{
		return m_lastTimeSeen;
	}

	public boolean isBumpable()
	{
		return m_bumpable;
	}
	
	public void setBumpable()
	{
		m_bumpable = true;
	}

	public void setLastTimeThirsty(int t) 
	{
		m_lastTimeThirsty = t;
	}

	public void setLastTimeHungry(int t) 
	{
		m_lastTimeHungry = t;
	}

	public void setDistanceToWater(int t) 
	{
		if (m_lastTimeThirsty > 0)
		{
			m_distanceToWater = t - m_lastTimeThirsty;
			m_lastTimeThirsty = 0;
		}
	}

	public void setDistanceToFood(int t) 
	{
		if (m_lastTimeHungry > 0)
		{
			m_distanceToFood = t - m_lastTimeHungry;
			m_lastTimeHungry = 0;
		}
	}

	public int getDistanceToWater() 
	{
		return m_distanceToWater;
	}

	public int getDistanceToFood() 
	{
		return m_distanceToFood;
	}
	
	public String getHexColor()
	{
		return Integer.toHexString( m_color.getRGB() & 0x00ffffff ); 
	}

}
