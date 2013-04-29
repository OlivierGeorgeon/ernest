package spas;

import javax.vecmath.Point3f;

/**
 * A slicer slices the surrounding space into areas of interest.
 * @author Olivier
 */
public interface IAreaManager 
{
	/**
	 * Gives the area to which a point belongs.
	 * @param point The point
	 * @return The area of interest
	 */
	public IArea categorize(Point3f point);
	
	public IArea getArea(String areaLabel);
	
	public void clearAll();
	public String simulateShiftLef();
	public String simulateShiftRight();
	public String simulateShiftForward();

	public void shiftLef();
	public void shiftRight();
}
