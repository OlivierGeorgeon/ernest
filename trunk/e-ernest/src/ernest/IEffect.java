package ernest;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;

/**
 * An effect sensed by Ernest after making a move in the environment.
 * Or an effect expected after simulating a move in spatial memory.
 * @author ogeorgeon
 */
public interface IEffect 
{
	
	/**
	 * @param effect The elementary effect of the enacted primitive scheme.
	 */
	public void setLabel(String label);
	
	/**
	 * @return The elementary effect of the enacted primitive scheme.
	 */
	public String getLabel();
	
	/**
	 * @param location The location concerned by the enacted scheme.
	 */
	public void setLocation(Point3f location);
	
	/**
	 * @return The location concerned by the enacted scheme.
	 */
	public Point3f getLocation();
	
	/**
	 * @param transformation The agent's movement during the scheme enaction.
	 */
	public void setTransformation(Transform3D transformation);
	
	/**
	 * Set the transformation caused by the scheme enaction
	 * @param angle The angle of rotation.
	 * @param x The translation along the agent axis.
	 */
	public void setTransformation(float angle, float x); 

	/**
	 * @return The agent's movement during the scheme enaction.
	 */
	public Transform3D getTransformation();
	
	/**
	 * @param color The color used to represent this effect in the trace
	 */
	public void setColor(int color);
	
	/**
	 * @return The color used to represent this effect in the trace
	 */
	public int getColor();

	/**
	 * @param tracer The tracer
	 */
	public void trace(ITracer tracer);
}
