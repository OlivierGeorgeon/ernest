package spas;

import imos.IAct;
import imos.ISchema;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import utils.ErnestUtils;
import ernest.Ernest;
import ernest.ITracer;

/**
 * Ernest's spatial memory. 
 * @author Olivier
 */
public class LocalSpaceMemory implements ISpatialMemory, Cloneable
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
	
	public final static int SIMULATION_INCONSISTENT = -1;
	public final static int SIMULATION_UNKNOWN = 0;
	public final static int SIMULATION_CONSISTENT = 1;
	public final static int SIMULATION_AFFORD = 2;
	public final static int SIMULATION_REACH = 3;
	
	/** The duration of persistence in local space memory. */
	public static int PERSISTENCE_DURATION = 10;//50;
	
	/** The Local space structure. */
	private ArrayList<IPlace> m_places = new ArrayList<IPlace>();
	
	private int m_clock = 0;
	
	private ISpas m_spas;
	
	private Transform3D m_transform = new Transform3D();
		
	/**
	 * Clone spatial memory to perform simulations
	 * TODO clone the places 
	 * From tutorial here: http://ydisanto.developpez.com/tutoriels/java/cloneable/ 
	 * @return The cloned spatial memory
	 */
	public ISpatialMemory clone() 
	{
		LocalSpaceMemory cloneSpatialMemory = null;
		try {
			cloneSpatialMemory = (LocalSpaceMemory) super.clone();
		} catch(CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}

		// We must clone the place list because it is passed by reference by default

		ArrayList<IPlace> clonePlaces = new ArrayList<IPlace>();
		for (IPlace place : m_places)
			clonePlaces.add(place.clone());
		cloneSpatialMemory.setPlaceList(clonePlaces);

		//cloneSpatialMemory.m_places = clonePlaces;
		//cloneSpatialMemory.m_clock = m_clock;
		
		return cloneSpatialMemory;
	}

	public void tick()
	{
		m_clock++;
	}

	public void trace(ITracer tracer)
	{
		if (tracer != null && !m_places.isEmpty())
		{
			Object localSpace = tracer.addEventElement("local_space");
			tracer.addSubelement(localSpace, "position_8", ErnestUtils.hexColor(getValue(DIRECTION_HERE)));
			tracer.addSubelement(localSpace, "position_7", ErnestUtils.hexColor(getValue(DIRECTION_BEHIND)));
			tracer.addSubelement(localSpace, "position_6", ErnestUtils.hexColor(getValue(DIRECTION_BEHIND_LEFT)));
			tracer.addSubelement(localSpace, "position_5", ErnestUtils.hexColor(getValue(DIRECTION_LEFT)));
			tracer.addSubelement(localSpace, "position_4", ErnestUtils.hexColor(getValue(DIRECTION_AHEAD_LEFT)));
			tracer.addSubelement(localSpace, "position_3", ErnestUtils.hexColor(getValue(DIRECTION_AHEAD)));
			tracer.addSubelement(localSpace, "position_2", ErnestUtils.hexColor(getValue(DIRECTION_AHEAD_RIGHT)));
			tracer.addSubelement(localSpace, "position_1", ErnestUtils.hexColor(getValue(DIRECTION_RIGHT)));
			tracer.addSubelement(localSpace, "position_0", ErnestUtils.hexColor(getValue(DIRECTION_BEHIND_RIGHT)));
		}
	}
	
	public IPlace addPlace(Point3f position, int type)
	{
		IPlace place = new Place(position, type);	
		m_places.add(place);
		return place;
	}
	
	/**
	 * Update the local space memory according to the agent's moves.
	 * @param translation The translation vector in egocentric referential (provide the opposite vector from the agent's movement).
	 * @param rotation The rotation value (provide the opposite value from the agent's movement).
	 */
	public void transform(IAct act)
	{
		for (IPlace p : m_places)
		    p.transform(act.getTransform());

	}
	
	public void transform(Transform3D transform)
	{
		for (IPlace p : m_places)
		    p.transform(transform);
	}
	
	public int runSimulation(IAct act, ISpas spas)
	{
		// Intialize the simulation
		m_spas = spas;
		m_transform.setIdentity();
		
		// Run the simulation
		int status = simulate(act);
		
		// Test if the resulting situation leads to an empty square 
		Transform3D tr = new Transform3D(); tr.setIdentity();
		int clock = m_spas.getClock();

		if (status != SIMULATION_INCONSISTENT && (getValue(DIRECTION_AHEAD) == 0xFFFFFF || getValue(DIRECTION_AHEAD) == 0x73E600 || getValue(DIRECTION_RIGHT) == 0x73E600) && !act.getTransform().epsilonEquals(tr, .1f) && clock > 100)
			status = SIMULATION_REACH;
		
		//Revert the transformation in spatial memory 
		m_transform.invert();
		transform(m_transform);
		
		return status;
	}
	
	private int simulate(IAct act)
	{
		//boolean consistent = false;
		boolean unknown = true;
		boolean consistent = true;
		boolean afford = false;
		int simulationStatus = SIMULATION_INCONSISTENT;
		ISchema s = act.getSchema();
		if (s.isPrimitive())
		{
			// The start position concerned by this act
			Point3f position = new Point3f(act.getStartPosition()); 
			
			for (IPlace p : m_places)
			{
				if (p.isInCell(position) && p.getType() == Place.EVOKE_PHENOMENON)
				{
					unknown = false;
					for (IBundle bundle : m_spas.evokeCompresences(p.getAct()))
					{
						if (!bundle.isConsistent(act)) consistent = false; 
						if (bundle.afford(act)) afford = true;
					}
				}
			}	

			if (unknown)	
			{
				// No place found at this location
				simulationStatus = SIMULATION_UNKNOWN;
				// Mark an unknown interaction
				IPlace sim = addPlace(position, Place.UNKNOWN);
				sim.setAct(act);
				sim.setValue(0xB0B0FF);				
			}
			else
			{
				if (consistent)
				{
					// No place that contains an incompatible act was fond at this location
					simulationStatus = SIMULATION_CONSISTENT;
					// Mark a consistent interaction
					IPlace sim = addPlace(position, Place.UNKNOWN);
					sim.setAct(act);
					sim.setValue(act.getColor());
				}
				if (afford)
				{
					// A place that contains this act is found at this location
					simulationStatus = SIMULATION_AFFORD;
					// Mark an afforded interaction
					IPlace sim = addPlace(position, Place.AFFORD);
					sim.setAct(act);
					sim.setValue(act.getColor());
				}
			}
			
			// Apply this act's transformation to spatial memory
			transform(act);
			// accumulate the transformation of this act to reverse the transformation after the simulation
			Transform3D tf = new Transform3D(act.getTransform());
			m_transform.mul(tf, m_transform);

		}
		else 
		{
			simulationStatus = simulate(act.getSchema().getContextAct());
			if (simulationStatus > SIMULATION_INCONSISTENT)
			{
				int status2 = simulate(act.getSchema().getIntentionAct());
				if (status2 == SIMULATION_INCONSISTENT)
					simulationStatus = SIMULATION_INCONSISTENT;
				else
				{
					if (simulationStatus == SIMULATION_AFFORD && (status2 == SIMULATION_CONSISTENT || status2 == SIMULATION_UNKNOWN))
						simulationStatus = status2;
				}
			}
		}
		return simulationStatus;
	}

	/**
	 * Get the value at a given position.
	 * (The last place found in the list of places that match this position)
	 * (Used to display in the trace)
	 * @param position The position of the location.
	 * @return The bundle.
	 */
	public int getValue(Point3f position)
	{
		int value = Ernest.UNANIMATED_COLOR;
		for (IPlace p : m_places)
		{
			if (p.isInCell(position) && p.getType() == Place.EVOKE_PHENOMENON)
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
		for (Iterator it = m_places.iterator(); it.hasNext();)
		{
			IPlace l = (IPlace)it.next();
			if (l.isInCell(position))
				it.remove();
		}		
	}
	
	/**
	 * Clear the places farther than DISTANCE_VISUAL_BACKGROUND.
	 */
	public void clearBackground()
	{
		for (Iterator it = m_places.iterator(); it.hasNext();)
		{
			IPlace l = (IPlace)it.next();
			if (l.getDistance() > DISTANCE_VISUAL_BACKGROUND - 1)
				it.remove();
		}
	}
	
	/**
	 * Clear the places in front (but not below Ernest) 
	 * (will be replaced by new seen places).
	 */
	public void clearFront()
	{
		for (Iterator it = m_places.iterator(); it.hasNext();)
		{
			IPlace l = (IPlace)it.next();
			if (l.getDirection() > - Math.PI/2 && l.getDirection() < Math.PI/2 &&
				l.getDistance() > 1)
				it.remove();
		}
	}
	
	/**
	 * Clear all the places older than PERSISTENCE_DURATION.
	 */
	public void clear()
	{
		for (Iterator it = m_places.iterator(); it.hasNext();)
		{
			IPlace p = (IPlace)it.next();
			if (p.getType() == Place.EVOKE_PHENOMENON)
			{
				//if (p.getUpdateCount() < m_spas.getClock() - PERSISTENCE_DURATION +1) // -1
				if (p.getUpdateCount() < m_clock - PERSISTENCE_DURATION +1) // -1
					it.remove();
			}
			else
			{
				//if (p.getUpdateCount() < m_spas.getClock() - PERSISTENCE_DURATION)
				if (p.getUpdateCount() < m_clock - PERSISTENCE_DURATION)
					it.remove();
			}
		}
		//m_places.clear();
	}
	
	/**
	 * @return The list of places in Local Spave Memory
	 */
	public ArrayList<IPlace> getPlaceList()
	{
		return m_places;
	}
	
	public void setPlaceList(ArrayList<IPlace> places) 
	{
		m_places = places;
	}
		
	/**
	 * Construct new copresence bundles
	 * @param observation The observation 
	 * @param spas A reference to the spatial system to add bundles
	 */
	public void copresence(ISpas spas)
	{
		// Clear the places that are older than the persistence of spatial memory
		clear();
		
		// Get the list of interaction places (that can evoke phenomena).
		ArrayList<IPlace> interactionPlaces = new ArrayList<IPlace>();
		for (IPlace p : m_places)
			//if (p.evokePhenomenon(m_clock))
			if (p.getType() == Place.EVOKE_PHENOMENON)
				interactionPlaces.add(p);

		// Create new copresence bundles 
		
		for (IPlace interactionPlace : interactionPlaces)
		{
			if (interactionPlace.getAct().concernOnePlace())
			{
				for (IPlace secondPlace : interactionPlaces)
				{
					if (secondPlace.getAct().concernOnePlace())
					{
						if (!interactionPlace.getAct().equals(secondPlace.getAct()) && interactionPlace.isInCell(secondPlace.getPosition()))
						{
							spas.addBundle(interactionPlace.getAct(), secondPlace.getAct());
						}
					}
				}
			}
		}
		
//		// Create copresence places that match enacted interactions
//		for (IPlace interactionPlace : interactionPlaces)
//		{
//			//if (interactionPlace.getUpdateCount() == m_spas.getClock())
//			if (interactionPlace.getUpdateCount() == m_clock)
//			{
//				IBundle bundle = spas.evokeBundle(interactionPlace.getAct());
//
//				if (bundle != null)
//				{
//					boolean newPlace = true;
//				
//					// If the copresence place already exists then refresh it.
//					for (IPlace copresencePlace :  m_places)
//					{
//						if (copresencePlace.getType() == Spas.PLACE_COPRESENCE && copresencePlace.isInCell(interactionPlace.getPosition())
//								&& copresencePlace.getBundle().equals(bundle))
//						{
//							//copresencePlace.setUpdateCount(m_spas.getClock());
//							copresencePlace.setUpdateCount(m_clock);
//							newPlace = false;
//						}
//					}
//					if (newPlace)
//					{
//						// If the copresence place does not exist then create it.
//						
//						IPlace k = addPlace(bundle,interactionPlace.getPosition()); 
//						k.setFirstPosition(interactionPlace.getFirstPosition()); 
//						k.setSecondPosition(interactionPlace.getSecondPosition());
//						k.setOrientation(interactionPlace.getOrientation());
//						//k.setUpdateCount(m_spas.getClock());
//						k.setUpdateCount(m_clock);
//						k.setType(Spas.PLACE_COPRESENCE);
//						k.setValue(interactionPlace.getValue());
//					}
//				}
//			}
//		}
	}

	public void clearSimulation() 
	{
		for (Iterator it = m_places.iterator(); it.hasNext();)
		{
			IPlace p = (IPlace)it.next();
			if (p.getType() == Place.SIMULATION || p.getType() == Place.UNKNOWN || p.getType() == Place.AFFORD)
				it.remove();
		}
	}
	
	public void setTransform(Transform3D transform) 
	{
		m_transform = transform;
	}

	public Transform3D getTransform() 
	{
		return m_transform;
	}
}
