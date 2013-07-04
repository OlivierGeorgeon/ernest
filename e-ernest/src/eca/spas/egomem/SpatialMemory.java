package eca.spas.egomem;

import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import tracing.ITracer;
import eca.Primitive;
import eca.construct.Appearance;
import eca.spas.Place;

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
	 * Add a place in this spatial memory
	 * @param primitive The interaction to be placed in spatial memory.
	 * @param position This place's position
	 * @return The created place
	 */
	//public Place addPlace(Primitive primitive, Point3f position);
	public void addPlace(Place place);

	/**
	 * @return A clone of this spatial memory
	 */
	public ArrayList<Place> clonePlaceList();
	
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
	public ArrayList<Place> getPlaceList();
 	
	/**
	 * Set the list of places 
	 * @param places The list of places
	 */
	public void setPlaceList(ArrayList<Place> places);

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
	 * @return The last appearance in spatial memory.
	 */
	public Appearance getLastAppearance();
	//public Place getPreviousPlace();
	
}
