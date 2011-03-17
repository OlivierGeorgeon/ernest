package ernest;

import java.awt.Color;

/**
 * A landmark is an item that can be identified by its visual features.
 * Ernest 9 identifies landmarks by their color.
 * @author Olivier
 */
public interface ILandmark 
{
	/**
	 * @return the landmark's color
	 */
	Color getColor();	
	
	/**
	 * @return The landmark's color in hexadecimal code
	 */
	String getHexColor();
	
	/**
	 * @return a string that uniquely represents the landmark
	 */
	String getLabel();
	
	/**
	 * Refresh this landmark in Ernest's static memory
	 * (Set the time-stamp when this landmark is seen in the vicinity.)
	 * @param t Current time at Ernest's clock 
	 */
	void setLastTimeChecked(int t);
	
	/**
	 * Get the degree of "freshness" of this landmark in Ernest's static memory
	 * @return the time-stamp when this landmark was last seen in the vicinity.
	 */
	int getLastTimeChecked();
	
	/**
	 * Refresh this landmark with the hope for water.
	 * Ernest memorizes that he passed in this landmark's vicinity when hoping for water.
	 * @param t Current time at Ernest's clock.
	 */
	void setLastTimeThirsty(int t);

	/**
	 * Refresh this landmark with the hope for food.
	 * Ernest memorizes that he passed in this landmark's vicinity when hoping for food.
	 * @param t Current time at Ernest's clock.
	 */
	void setLastTimeHungry(int t);
	
	/**
	 * Associate an estimated distance to water to this landmark.
	 * The distance to water is set equal to the difference between the current time and the last time checked when thirsty.
	 * @param t Current time at Ernest's clock.
	 */
	void setDistanceToWater(int t);
	
	/**
	 * Associate an estimated distance to food to this landmark.
	 * The distance to food is set equal to the difference between the current time and the last time checked when hungry.
	 * @param t current time
	 */
	void setDistanceToFood(int t);

	/**
	 * @return This landmark's distance to water as currently assessed.
	 */
	int getDistanceToWater();

	/**
	 * @return This landmark's distance to food as currently assessed
	 */
	int getDistanceToFood();

	/**
	 * Updates the time from hive on the way out.
	 * @param t The time from hive (or food).
	 */
	public void updateTimeFromHive(int t);

}


