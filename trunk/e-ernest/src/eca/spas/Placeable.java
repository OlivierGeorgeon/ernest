package eca.spas;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;

import eca.construct.Area;

/**
 * An object that can be placed and tracked in spatial memory
 * @author Olivier
 */
public interface Placeable {

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
	 * @return The area of this obeject
	 */
	public Area getArea();
	
}
