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
	private int m_lastTimeChecked = - Ernest.INFINITE; // Landmarks must not be inhibited at start
	
	private int m_lastTimeThirsty = 0;
	private int m_lastTimeHungry = 0;
	private int m_distanceToWater = Ernest.INFINITE; // must be lower than Ernest's initial distance to water.
	private int m_distanceToFood = Ernest.INFINITE;
	
	protected Landmark(int red, int green, int blue)
	{
		m_color = new Color(red, green, blue);
	}
	
	public Color getColor() 
	{
		return m_color;
	}

	public String getHexColor()
	{
		String s = String.format("%06X", m_color.getRGB()  & 0x00ffffff); 
		return s;
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
	
	public void setLastTimeChecked(int t)
	{
		m_lastTimeChecked = t;
	}
	
	public int getLastTimeChecked()
	{
		return m_lastTimeChecked;
	}

	public void setLastTimeThirsty(int t) 
	{
		// When mature, Ernest considers all landmarks as visited. This reduces ugly chaotic behavior in Ernest 9.0 demo.
		if (t > Ernest.MATURITY)
			m_lastTimeChecked = t;
		m_lastTimeThirsty = t;
	}

	public void setLastTimeHungry(int t) 
	{
		if (t > Ernest.MATURITY)
			m_lastTimeChecked = t;
		m_lastTimeHungry = t;
	}

	public void setDistanceToWater(int t) 
	{
		if (m_lastTimeThirsty > 0)
		{
			//if (m_distanceToWater == Ernest.INFINITE)
			//	m_distanceToWater = t - m_lastTimeThirsty;
			//m_distanceToWater = (m_distanceToWater + t - m_lastTimeThirsty) / 2;
			m_distanceToWater = t - m_lastTimeThirsty;
			m_lastTimeThirsty = 0;
		}
	}

	public void setDistanceToFood(int t) 
	{
		if (m_lastTimeHungry > 0)
		{
			int d = t - m_lastTimeHungry;
			// update if the distance is shorter assuming that the landmarks and the hive don't move.
			if (m_distanceToFood > d)
				m_distanceToFood = d;//(m_distanceToFood + t - m_lastTimeHungry) / 2;
			//m_distanceToFood =  t - m_lastTimeHungry;
			m_lastTimeHungry = 0;
		}
	}

	public void updateTimeFromHive(int t)
	{
		// update if the distance is shorter assuming that the landmarks and the hive don't move.
		if (m_distanceToFood > t)
			m_distanceToFood = t;//(m_distanceToFood + t - m_lastTimeHungry) / 2;	
		System.out.println(getHexColor() + "distance to food: " + t);
	}
	
	public int getDistanceToWater() 
	{
		return m_distanceToWater;
	}

	public int getDistanceToFood() 
	{
		return m_distanceToFood;
	}
	
}
