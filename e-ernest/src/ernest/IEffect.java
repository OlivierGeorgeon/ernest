package ernest;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

/**
 * An effect sensed by Ernest after making a move in the environment.
 * Or an effect expected after simulating a move in spatial memory.
 * @author ogeorgeon
 */
public interface IEffect 
{
	
	public void setSimulationStatus(int simulationStatus);
	
	/**
	 * @param effect The elementary effect of the enacted primitive scheme.
	 */
	public void setEffect(String effect);
	
	/**
	 * @param location The location concerned by the enacted scheme.
	 */
	public void setLocation(Point3d location);
	
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

	public int getSimulationStatus();

	/**
	 * @return The elementary effect of the enacted primitive scheme.
	 */
	public String getEffect();
	
	/**
	 * @return The location concerned by the enacted scheme.
	 */
	public Point3d getLocation();
	
	/**
	 * @return The agent's movement during the scheme enaction.
	 */
	public Transform3D getTransformation();
	
	public void trace(ITracer tracer);
}
