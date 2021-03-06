package eca.spas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import eca.ActInstance;
import eca.construct.egomem.PhenomenonInstance;
import eca.construct.egomem.PhenomenonInstanceImpl;
import eca.construct.egomem.PhenomenonType;
import eca.construct.egomem.PhenomenonTypeImpl;


/**
 * Ernest's spatial memory. 
 * @author Olivier
 */
public class SpatialMemoryImpl implements SpatialMemory, Cloneable
{
	
	/** The radius of a location. */
	public final static float LOCATION_RADIUS = 0.5f;
	public final static float LOCAL_SPACE_MEMORY_RADIUS = 20f;//4f;
	public final static float DISTANCE_VISUAL_BACKGROUND = 10f;
	public final static float EXTRAPERSONAL_DISTANCE = 1.5f;
	public final static float DIAG2D_PROJ = (float) (1/Math.sqrt(2));
	public final static Point3f DIRECTION_HERE         = new Point3f(0, 0, 0);
	public final static Point3f DIRECTION_AHEAD        = new Point3f(1, 0, 0);
	public final static Point3f DIRECTION_BEHIND       = new Point3f(-1, 0, 0);
	public final static Point3f DIRECTION_LEFT         = new Point3f(0, 1, 0);
	public final static Point3f DIRECTION_RIGHT        = new Point3f(0, -1, 0);
	public final static Point3f DIRECTION_AHEAD_LEFT   = new Point3f(DIAG2D_PROJ, DIAG2D_PROJ, 0);
	public final static Point3f DIRECTION_AHEAD_RIGHT  = new Point3f(DIAG2D_PROJ, -DIAG2D_PROJ, 0);
	public final static Point3f DIRECTION_BEHIND_LEFT  = new Point3f(-DIAG2D_PROJ, DIAG2D_PROJ, 0);
	public final static Point3f DIRECTION_BEHIND_RIGHT = new Point3f(-DIAG2D_PROJ, -DIAG2D_PROJ, 0);	
	public final static float    SOMATO_RADIUS = 1f;
	
	/** The duration of persistence in local space memory. */
	public static int PERSISTENCE_DURATION = 7;//50;
	
	/** The Local space structure. */
	private List<Placeable> placeables = new ArrayList<Placeable>();
	
	/**
	 * Clone spatial memory to perform simulations
	 * TODO clone the places 
	 * From tutorial here: http://ydisanto.developpez.com/tutoriels/java/cloneable/ 
	 * @return The cloned spatial memory
	 */
//	public ArrayList<Placeable> clonePlaceList() 
//	{
//		ArrayList<Placeable> clonePlaces = new ArrayList<Placeable>();
//		for (Placeable placeable : placeables)
//			clonePlaces.add(placeable.clone());
//
//		return clonePlaces;
//	}

	public void tick()
	{
		for (Placeable p : placeables)
			p.incClock();
	}

	public void addPlaceable(Placeable placeable){
		placeables.add(placeable);
	}
	
//	public void addActInstance(ActInstance actInstance){
//		placeables.add(actInstance);
//		PhenomenonInstance phenomenonInstance = getPhenomenonInstance(actInstance.getPosition());
//		if (phenomenonInstance == null){
//			// create a new phenomenon type with this act
//			PhenomenonType phenomenonType = PhenomenonTypeImpl.createNew();
//			phenomenonType.addPrimitive(actInstance.getPrimitive());
//			// create a new phenomenon instance at this place
//			phenomenonInstance = new PhenomenonInstanceImpl(phenomenonType, actInstance.getPosition());
//		}
//		else{
//			// add this act to the phenomenon type of this place
//			PhenomenonType phenomenonType = phenomenonInstance.getPhenomenonType();
//			phenomenonType.addPrimitive(actInstance.getPrimitive());
//		}
//	}
	
	public void transform(Transform3D transform)
	{
		for (Placeable p : placeables)
			p.transform(transform);
	}
	
	/**
	 * Clear a position in the local space memory.
	 * @param position The position to clear.
	 */
//	public void clearPlace(Point3f position)
//	{
//		for (Iterator<Placeable> it = placeables.iterator(); it.hasNext();)
//		{
//			ActInstance l = (ActInstance)it.next();
//			if (l.isInCell(position))
//				it.remove();
//		}		
//	}
	
	/**
	 * Clear the places farther than DISTANCE_VISUAL_BACKGROUND.
	 */
//	public void clearBackground()
//	{
//		for (Iterator<Placeable> it = placeables.iterator(); it.hasNext();)
//		{
//			Placeable l = it.next();
//			if (l.getDistance() > DISTANCE_VISUAL_BACKGROUND - 1)
//				it.remove();
//		}
//	}
	
	/**
	 * Clear all the places older than PERSISTENCE_DURATION.
	 */
	public void forgetOldPlaces()
	{
		for (Iterator<Placeable> it = placeables.iterator(); it.hasNext();)
		{
			Placeable p = it.next();
			if (p.getClock() > PERSISTENCE_DURATION )//|| p.getPosition().x < -.1) 
				it.remove();
		}
	}
		
//	public void trace(ITracer tracer)
//	{
//		if (tracer != null && !placeables.isEmpty())
//		{
//			Object localSpace = tracer.addEventElement("local_space");
//			tracer.addSubelement(localSpace, "position_8", ErnestUtils.hexColor(getDisplayCode(DIRECTION_HERE)));
//			tracer.addSubelement(localSpace, "position_7", ErnestUtils.hexColor(getDisplayCode(DIRECTION_BEHIND)));
//			tracer.addSubelement(localSpace, "position_6", ErnestUtils.hexColor(getDisplayCode(DIRECTION_BEHIND_LEFT)));
//			tracer.addSubelement(localSpace, "position_5", ErnestUtils.hexColor(getDisplayCode(DIRECTION_LEFT)));
//			tracer.addSubelement(localSpace, "position_4", ErnestUtils.hexColor(getDisplayCode(DIRECTION_AHEAD_LEFT)));
//			tracer.addSubelement(localSpace, "position_3", ErnestUtils.hexColor(getDisplayCode(DIRECTION_AHEAD)));
//			tracer.addSubelement(localSpace, "position_2", ErnestUtils.hexColor(getDisplayCode(DIRECTION_AHEAD_RIGHT)));
//			tracer.addSubelement(localSpace, "position_1", ErnestUtils.hexColor(getDisplayCode(DIRECTION_RIGHT)));
//			tracer.addSubelement(localSpace, "position_0", ErnestUtils.hexColor(getDisplayCode(DIRECTION_BEHIND_RIGHT)));
//		}
//	}

	public List<Placeable> getPlaceables() {
		return this.placeables;
	}
	
	public List<PhenomenonInstance> getPhenomenonInstances() {
		List<PhenomenonInstance> phenomenonInstances = new ArrayList<PhenomenonInstance>();
		for (Placeable placeable : this.placeables)
			if (placeable instanceof PhenomenonInstance)
				phenomenonInstances.add((PhenomenonInstance)placeable);
		return phenomenonInstances;
	}
	
	public PhenomenonInstance getPhenomenonInstance(Point3f position){
		PhenomenonInstance phenomenonInstance = null;
		for (Placeable placeable : this.placeables)
			if (placeable instanceof PhenomenonInstance)
				if (placeable.isInCell(position))
					phenomenonInstance = (PhenomenonInstance)placeable;
		
		return phenomenonInstance;
	}

	public void clearPhenomenonInstanceFront() {
		for (Iterator<Placeable> it = placeables.iterator(); it.hasNext();){
			Placeable placeable = it.next();
			if (placeable instanceof PhenomenonInstance)
				if (placeable.getPlace().getPosition().x > 0)
					it.remove();					
		}
	}
	
}
