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
	 * @return True if the landmark is drinkable.
	 */
	boolean isDrinkable();
	
	/**
	 * Mark this landmark as drinkable
	 */
	void setDrinkable();

	/**
	 * @return True if the landmark is edible.
	 */
	boolean isEdible();
	
	/**
	 * Mark this landmark as edible.
	 */
	void setEdible();

	/**
	 * @return True if the landmark is a wall that would be bumped.
	 */
	boolean isBumpable();
	
	/**
	 * Mark this landmark as a wall that would be bumped.
	 */
	void setBumpable();

	/**
	 * @return the timestamp when this landmark was last checked.
	 */
	int getLastTimeChecked();
	
	/**
	 * @param t the timestamp when this landmark is checked. 
	 */
	void setLastTimeChecked(int t);

}


