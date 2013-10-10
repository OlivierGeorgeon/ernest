package eca.spas;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import eca.construct.Area;

/**
 * A place in egocentric spatial memory where an ActInstance or a PhenomenonInstance is located.
 * @author Olivier
 */
public interface Place extends Cloneable{
	
	/**
	 * @param transform The transformation applied to spatial memory.
	 */
	public void transform(Transform3D transform);

	/**
	 * @return A clone of this place
	 */
	public PlaceImpl clone();
	
	/**
	 * @param position The place's position.
	 */
	public void setPosition(Point3f position);
	
	/**
	 * @return The location's position.
	 */
	public Point3f getPosition();
	
	/**
	 * Test if this place is at this position.
	 * @param position The position to test
	 * @return true if this place is in the same cell as thi position.
	 */
	public boolean isInCell(Point3f position);
	
	/**
	 * @param orientation This place's orientation.
	 */
	public void setOrientation(Vector3f orientation);
	
	/**
	 * @return This place's orientation.
	 */
	public Vector3f getOrientation();
	
	/**
	 * @return The direction of this place.
	 */
	public float getDirection();
	
	/**
	 * @return The place's distance.
	 */
	public float getDistance();
	
	/**
	 * @return The orientation angle of this place
	 */
	public float getOrientationAngle();
	
	/**
	 * @param scale The unity value for normalization.
	 */
	public void normalize(float scale);
	
	/**
	 * @return The are of this place
	 */
	public Area getArea();
	
	public void fade();
	
}
