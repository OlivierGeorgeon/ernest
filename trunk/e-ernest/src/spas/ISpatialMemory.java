package spas;

import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import ernest.ITracer;
import imos2.Act;


/**
 * A Spatial Memory is a memory of a spatial configuration
 * @author Olivier
 */
public interface ISpatialMemory 
{
	/**
	 * Tick this spatial memory's clock (to compute decay)
	 */
	public void tick();
	
	/**
	 * Add a place in this spatial memory
	 * @param act The act to be placed in spatial memory.
	 * @param position This place's position
	 * @return The created place
	 */
	public IPlace addPlace(Act act, Point3f position);

	/**
	 * @return A clone of this spatial memory
	 */
	public ArrayList<IPlace> clonePlaceList();
	
	/**
	 * @param transform The transformation
	 */
	public void transform(Transform3D transform);
	
	/**
	 * Remove places that are older than the decay laps
	 */
	public void forgetOldPlaces();

	/**
	 * @return The list of places in this spatial memory
	 */
	public ArrayList<IPlace> getPlaceList();
 	
	/**
	 * Set the list of places 
	 * @param places The list of places
	 */
	public void setPlaceList(ArrayList<IPlace> places);

	/**
	 * @param position The position.
	 * @return The value of a place for display.
	 */
	public int getDisplayCode(Point3f position);

	/**
	 * Trace the content of this spatial memory
	 * @param tracer The tracer
	 */
	public void trace(ITracer tracer);

	/**
	 * @return The place that was recorded during the last decision cycle.
	 */
	public IPlace getPreviousPlace();
}
