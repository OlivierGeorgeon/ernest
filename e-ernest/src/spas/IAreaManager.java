package spas;

import javax.vecmath.Point3f;

import ernest.Action;
import ernest.ITracer;
import ernest.Observation;

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
	public Area categorize(Point3f point);
	
	public void clearAll();
	
	public Observation predict(Action action);
	public Observation simulateShiftLef();
	public Observation simulateShiftRight();
	public Observation simulateShiftForward();
	
	public void trace(ITracer tracer);

	public void shiftLef();
	public void shiftRight();
}
