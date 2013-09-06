package eca.spas.egomem;

import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;

import eca.construct.PhenomenonInstance;
import tracing.ITracer;

/**
 * A Spatial Memory is a memory of a spatial configuration
 * @author Olivier
 */
public interface SpatialMemory 
{
	/**
	 * Tick this spatial memory's clock (to compute decay)
	 */
	public void tick();
	
	/**
	 * Add a place in spatial memory
	 * @param actInstance The place to add in spatial memory.
	 */
	public void addPlace(ActInstance actInstance);

	public void addPhenomenonInstance(PhenomenonInstance phenomenonInstance);
	
	public void removePhenomenonInstance(PhenomenonInstance phenomenonInstance);

		/**
	 * @return A clone of this spatial memory
	 */
	public ArrayList<ActInstance> clonePlaceList();
	
	/**
	 * @param transform The transformation
	 */
	public void transform(Transform3D transform);
	
	/**
	 * Remove places that are older than the decay laps
	 */
	public void forgetOldPlaces();

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

	
}
