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
	 * @return false if this landmark's color is the standard wall color. True in all other cases.
	 */
	boolean isSingularity();

	/**
	 * @return True if the landmark is food.
	 */
	boolean isFood();

	/**
	 * @return True if the landmark is water.
	 */
	boolean isWater();
}
