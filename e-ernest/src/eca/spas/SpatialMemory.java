package eca.spas;

import java.util.ArrayList;
import java.util.List;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;

import eca.ActInstance;
import eca.construct.egomem.PhenomenonInstance;

/**
 * A Spatial Memory is a set of placeable objects to which a general spatial transformation apply.
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
	public void addPlaceable(Placeable placeable);
	
	//public void addActInstance(ActInstance actInstance);

		/**
	 * @return A clone of this spatial memory
	 */
	//public ArrayList<Placeable> clonePlaceList();
	
	/**
	 * @return The list of Placeable objects.
	 */
	public List<Placeable> getPlaceables();
	
	/**
	 * @param transform The transformation
	 */
	public void transform(Transform3D transform);
	
	/**
	 * Remove places that are older than the decay laps
	 */
	public void forgetOldPlaces();
	
	public List<PhenomenonInstance> getPhenomenonInstances();

	public PhenomenonInstance getPhenomenonInstance(Point3f position);

	public void clearPhenomenonInstanceFront();
}
