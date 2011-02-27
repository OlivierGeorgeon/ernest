package ernest;

import java.awt.Color;

/**
 * A landmark is an item that can be identified by its visual features.
 * Ernest 9.0 identifies landmarks by their color.
 * @author Olivier
 */
public interface ILandmark 
{
	/**
	 * @return the landmark's color
	 */
	Color getColor();	
	
	/**
	 * @return a string that uniquely represents the landmark
	 */
	String getLabel();
	
	/**
	 * Mark this landmark as drinkable
	 */
	void setDrinkable();

	/**
	 * Mark this landmark as edible.
	 */
	void setEdible();

	/**
	 * @return True if the landmark is a wall that would be bumped.
	 */
	boolean isVisited();
	
	/**
	 * Mark this landmark as a wall that would be bumped.
	 */
	void setVisited();

	/**
	 * @return the timestamp when this landmark was last checked.
	 */
	int getLastTimeChecked();
	
	/**
	 * @param t the timestamp when this landmark is checked. 
	 */
	void setLastTimeChecked(int t);
	
	/**
	 * This landmark is checked while being thirsty at the current time.
	 * @param t current time
	 */
	void setLastTimeThirsty(int t);

	/**
	 * This landmark is checked while being hungry at the current time.
	 * @param t current time
	 */
	void setLastTimeHungry(int t);
	
	/**
	 * The distance to water is set equal to the difference between the current time and the last time checked when thirsty.
	 * @param t current time
	 */
	void setDistanceToWater(int t);
	
	/**
	 * The distance to food is set equal to the difference between the current time and the last time checked when hungry.
	 * @param t current time
	 */
	void setDistanceToFood(int t);

	/**
	 * @return the distance to water as currently assessed
	 */
	int getDistanceToWater();

	/**
	 * @return the distance to food as currently assessed
	 */
	int getDistanceToFood();

	String getHexColor();
}


