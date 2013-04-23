package spas;

import javax.vecmath.Point3f;

/**
 * A slicer slices the surrounding space into areas of interest.
 * @author Olivier
 */
public interface ISlicer 
{
	/**
	 * Gives the identifier of the area of interest to which a point belongs.
	 * @param point The point
	 * @return The identifier of the area of interest
	 */
	public String slice(Point3f point);
	
}
