package spas;

import javax.vecmath.Vector3f;

/**
 * A place is a location in the local space where a bundle is placed.
 * @author Olivier
 */
public interface IPlace 
{	
	/**
	 * @return The location's bundle.
	 */
	public IBundle getBundle();
	
	/**
	 * Allocate a bundle to this place.
	 * @param bundle The bundle at this place.
	 */
	public void setBundle(IBundle bundle);

	/**
	 * @return The location's position.
	 */
	public Vector3f getPosition();
	
	/**
	 * Rotate this location with regard to the agent's center.
	 * @param angle The rotation angle.
	 */
	public void rotate(float angle);

	/**
	 * Translate this location along the agent's axis.
	 * @param distance The translation distance.
	 */
	public void translate(Vector3f translation);

	/**
	 * Test if this place is at this position.
	 * @param position The position to test
	 * @return true if this place is in the same cell as thi position.
	 */
	public boolean isInCell(Vector3f position);
	
	/**
	 * @return The place's direction.
	 */
	public float getDirection();
	
	/**
	 * @return The place's distance.
	 */
	public float getDistance();
	
	/**
	 * @param position The place's position.
	 */
	public void setPosition(Vector3f position);
	
	/**
	 * @param value The place's value, corresponds to a color to display.
	 */
	public void setValue(int value);
	
	/**
	 * @return The place's value, corresponds to the color to display.
	 */
	public int getValue();

	/**
	 * @param clock The current time in Ernest's life.
	 * @return The salience's attractiveness.
	 */
	int getAttractiveness(int clock);

	/**
	 * @return The span of the bundle at this place.
	 */
	public float getSpan();
	
	/**
	 * @param span The span of the bundle at this place.
	 */
	public void setSpan(float span);
	
	/**
	 * @return True if this place is frontal. 
	 */
	public boolean isFrontal();
	
	public void setType(int type);
	
	public int getType();
	
	public void setShape(int shape);
	public int getShape();
	public void setOrientation(float orientation);
	public float getOrientation();
	
	public void setSpeed(Vector3f speed);
	
	public Vector3f getSpeed();
	
	public void setFirstPosition(Vector3f position);
	public void setSecondPosition(Vector3f position);
	public Vector3f getFirstPosition();
	public Vector3f getSecondPosition();
	
	public void setUpdateCount(int count);
	public int getUpdateCount();
	
	public boolean evokePhenomenon(int clock);
	public boolean isPhenomenon();
	public boolean from(Vector3f position); 
	public boolean from(IPlace previousPlace); 
	
	//public boolean anticipateTo(Vector3f position);
	
	public void setStick(int stick);
	public int getStick();
	public float getFrontDistance();
	
}
