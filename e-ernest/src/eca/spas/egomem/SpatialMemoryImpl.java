package eca.spas.egomem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import tracing.ITracer;
import utils.ErnestUtils;
import ernest.Ernest;


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
	private ArrayList<Place> m_places = new ArrayList<Place>();
	
	private List<PhenomenonInstance> phenomenonInstances = new ArrayList<PhenomenonInstance>();
	
	/**
	 * Clone spatial memory to perform simulations
	 * TODO clone the places 
	 * From tutorial here: http://ydisanto.developpez.com/tutoriels/java/cloneable/ 
	 * @return The cloned spatial memory
	 */
	public ArrayList<Place> clonePlaceList() 
	{
		ArrayList<Place> clonePlaces = new ArrayList<Place>();
		for (Place place : m_places)
			clonePlaces.add(place.clone());
		
		return clonePlaces;
	}

	public void tick()
	{
		for (Place p : m_places)
			p.incClock();
	}

	public void addPlace(Place place){
		m_places.add(place);
	}
	
	public void addPhenomenonInstance(PhenomenonInstance phenomenonInstance){
		this.phenomenonInstances.add(phenomenonInstance);
	}
	
	public void removePhenomenonInstance(PhenomenonInstance phenomenonInstance){
		this.phenomenonInstances.remove(phenomenonInstance);
	}
	
	public void transform(Transform3D transform)
	{
		//if (transform != null)
		for (Place p : m_places)
			p.transform(transform);
		
		for (PhenomenonInstance pi : this.phenomenonInstances)
			pi.getPlace().transform(transform);
	}
	
	/**
	 * Get the value at a given position.
	 * (The last place found in the list of places that match this position)
	 * (Used to display in the trace)
	 * @param position The position of the location.
	 * @return The bundle.
	 */
	public int getDisplayCode(Point3f position)
	{
		int value = Ernest.UNANIMATED_COLOR;
		for (Place p : m_places)
		{
			if (p.isInCell(position))
				if (value != 0x73E600 && value != 0x00E6A0)
				value = p.getValue();
		}	
		return value;
	}

	/**
	 * Clear a position in the local space memory.
	 * @param position The position to clear.
	 */
	public void clearPlace(Point3f position)
	{
		for (Iterator<Place> it = m_places.iterator(); it.hasNext();)
		{
			Place l = (Place)it.next();
			if (l.isInCell(position))
				it.remove();
		}		
	}
	
	/**
	 * Clear the places farther than DISTANCE_VISUAL_BACKGROUND.
	 */
	public void clearBackground()
	{
		for (Iterator<Place> it = m_places.iterator(); it.hasNext();)
		{
			Place l = (Place)it.next();
			if (l.getDistance() > DISTANCE_VISUAL_BACKGROUND - 1)
				it.remove();
		}
	}
	
	/**
	 * Clear all the places older than PERSISTENCE_DURATION.
	 */
	public void forgetOldPlaces()
	{
		for (Iterator<Place> it = m_places.iterator(); it.hasNext();)
		{
			Place p = (Place)it.next();
			if (p.getClock() > PERSISTENCE_DURATION )//|| p.getPosition().x < -.1) 
				it.remove();
		}
	}
		
	public void trace(ITracer tracer)
	{
		if (tracer != null && !m_places.isEmpty())
		{
			Object localSpace = tracer.addEventElement("local_space");
			tracer.addSubelement(localSpace, "position_8", ErnestUtils.hexColor(getDisplayCode(DIRECTION_HERE)));
			tracer.addSubelement(localSpace, "position_7", ErnestUtils.hexColor(getDisplayCode(DIRECTION_BEHIND)));
			tracer.addSubelement(localSpace, "position_6", ErnestUtils.hexColor(getDisplayCode(DIRECTION_BEHIND_LEFT)));
			tracer.addSubelement(localSpace, "position_5", ErnestUtils.hexColor(getDisplayCode(DIRECTION_LEFT)));
			tracer.addSubelement(localSpace, "position_4", ErnestUtils.hexColor(getDisplayCode(DIRECTION_AHEAD_LEFT)));
			tracer.addSubelement(localSpace, "position_3", ErnestUtils.hexColor(getDisplayCode(DIRECTION_AHEAD)));
			tracer.addSubelement(localSpace, "position_2", ErnestUtils.hexColor(getDisplayCode(DIRECTION_AHEAD_RIGHT)));
			tracer.addSubelement(localSpace, "position_1", ErnestUtils.hexColor(getDisplayCode(DIRECTION_RIGHT)));
			tracer.addSubelement(localSpace, "position_0", ErnestUtils.hexColor(getDisplayCode(DIRECTION_BEHIND_RIGHT)));
		}
	}
	
}
