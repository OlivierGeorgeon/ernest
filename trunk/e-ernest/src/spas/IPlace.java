package spas;

//import imos.IAct;
import imos2.IInteraction;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * A place is a location in spatial memory.
 * @author Olivier
 */
public interface IPlace extends Cloneable
{	
	/**
	 * @param transform The transformation applied to spatial memory.
	 */
	public void transform(Transform3D transform);

	/**
	 * @param type This place's type.
	 */
	public void setType(int type);
	
	/**
	 * @return This place's type.
	 */
	public int getType();
	
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
	public IPlace clone();
	
	/**
	 * @param position The place's position.
	 */
	public void setPosition(Point3f position);
	
	/**
	 * @return The location's position.
	 */
	public Point3f getPosition();
	
	/**
	 * @param act This place's act.
	 */
	//public void setAct(IAct act); 
	
	/**
	 * @return This place's act.
	 */
	//public IAct getAct(); 
	
	/**
	 * @param interaction This place's interaction.
	 */
	public void setInteraction(IInteraction interaction); 
	
	/**
	 * @return This place's act.
	 */
	public IInteraction getInteraction(); 
	
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
	 * @return The plac's bundle.
	 */
	public IBundle getBundle();
	
	/**
	 * @param bundle The bundle at this place.
	 */
	public void setBundle(IBundle bundle);

	/**
	 * @param value The place's value, corresponds to a color to display.
	 */
	public void setValue(int value);
	
	/**
	 * @return The place's value, corresponds to the color to display.
	 */
	public int getValue();

	/**
	 * @return The place's direction.
	 */
	//public float getDirection();
	
	/**
	 * @return The place's distance.
	 */
	public float getDistance();
	
	public void incClock();
	
	public void setOrientation(float orientation);
	public float getOrientationAngle();
	
	//public void setSpeed(Vector3f speed);	
	//public Vector3f getSpeed();
	
//	public void setFirstPosition(Vector3f position);
//	public void setSecondPosition(Vector3f position);
//	public Vector3f getFirstPosition();
//	public Vector3f getSecondPosition();
//	
	//public float getSpan();	
	//public void setSpan(float span);
	
	//public boolean evokePhenomenon(int clock);
	//public boolean isPhenomenon();
	public boolean from(Vector3f position); 
	//public boolean from(IPlace previousPlace); 
	
	//public boolean anticipateTo(Vector3f position);
	
	//public void setStick(int stick);
	//public int getStick();
	//public float getFrontDistance();
	
}
