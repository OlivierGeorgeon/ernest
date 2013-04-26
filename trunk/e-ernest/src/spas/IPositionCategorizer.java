package spas;

import javax.vecmath.Point3f;

/**
 * A slicer slices the surrounding space into areas of interest.
 * @author Olivier
 */
public interface IPositionCategorizer 
{
	/**
	 * Gives the area to which a point belongs.
	 * @param point The point
	 * @return The area of interest
	 */
	public IArea categorize(Point3f point);
	
}
