package eca.spas.egomem;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import eca.Primitive;
import eca.construct.PhenomenonType;
import eca.ss.enaction.Act;

/**
 * A place is a location in spatial memory.
 * @author Olivier
 */
public interface Place extends Cloneable
{	
	/**
	 * @param transform The transformation applied to spatial memory.
	 */
	public void transform(Transform3D transform);

	/**
	 * @param clock The timestamp of this place.
	 */
	public void setClock(int clock);
	
	/**
	 * @return The timestamp of this place.
	 */
	public int getClock();
	
	/**
	 * @return A clone of this place
	 */
	public Place clone();
	
	/**
	 * @param position The place's position.
	 */
	public void setPosition(Point3f position);
	
	/**
	 * @return The location's position.
	 */
	public Point3f getPosition();
	
	/**
	 * @return The act constructed from this place.
	 */
	public Act getAct(); 
	
	/**
	 * @return This place's primitive interaction.
	 */
	public Primitive getPrimitive();
	
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
	 * @param value The place's value, corresponds to a color to display.
	 */
	public void setValue(int value);
	
	/**
	 * @return The place's value, corresponds to the color to display.
	 */
	public int getValue();

	/**
	 * @return The direction of this place.
	 */
	public float getDirection();
	
	/**
	 * @return The place's distance.
	 */
	public float getDistance();
	
	/**
	 * Increment this place's clock
	 */
	public void incClock();
	
	/**
	 * @return The orientation angle of this place
	 */
	public float getOrientationAngle();
	
	//public PhenomenonType getPhenomenonType();
	
	//public void setPhenomenonType(PhenomenonType phenomenonType);
	
	public void normalize(float scale);
	
}
