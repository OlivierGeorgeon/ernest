package eca.spas;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import eca.construct.Area;

/**
 * An object that can be placed and tracked in spatial memory
 * @author Olivier
 */
public interface Placeable extends Cloneable {

	/**
	 * @return A clone of this Placeable
	 */
	public Placeable clone();
	
	/**
	 * @param position The new position of this object.
	 */
	public void setPosition(Point3f position);
	
	/**
	 * @return The place of this object.
	 */
	public Place getPlace();
	
	/**
	 * @param transform The transformation to move this object in spatial memory.
	 */
	public void transform(Transform3D transform);

	/**
	 * @return The area of this object
	 */
	public Area getArea();
	
	public Point3f getPosition();
	
	public int getDisplayCode();
	
	public int getClock();
	
	public String getDisplayLabel();
	
	public float getOrientationAngle();
	
	public void incClock();
	
	public boolean isInCell(Point3f position);
	
	/**
	 * @return The place's distance.
	 */
	public float getDistance();
	

}
