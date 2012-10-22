package spas;

import imos.ActProposition;
import imos.IAct;
import imos.IActProposition;
import imos.ISchema;

import java.util.ArrayList;
import java.util.Iterator;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import utils.ErnestUtils;
import ernest.Enaction;
import ernest.Ernest;
import ernest.IEnaction;
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
	
	//public final static int SIMULATION_INCONSISTENT = -1;
	//public final static int SIMULATION_UNKNOWN = 0;
	//public final static int SIMULATION_CONSISTENT = 1;
	//public final static int SIMULATION_AFFORD = 2;
	//public final static int SIMULATION_REACH = 10;
	//public final static int SIMULATION_REACH2 = 4;
	public final static int SIMULATION_NEWCOMPRESENCE = 11;
	
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
	
	public void transform(Transform3D transform)
	{
		if (transform != null)
			for (IPlace p : m_places)
				p.transform(transform);
	}
	
	public IActProposition runSimulation(IAct act, ISpas spas)
	{
		// Intialize the simulation
		m_spas = spas;
		m_transform.setIdentity();
		
		// Run the simulation
		int status = simulate(act).getSimulationStatus();
		
		// Test if the resulting situation leads to an affordance
		
		Vector3f trans = new Vector3f(); 
		act.getTransform().get(trans); // Get the translation part of this act's transformation
		//if (status != SIMULATION_INCONSISTENT && !trans.epsilonEquals(new Vector3f(), .1f))
		
		IAct subsequentAct = null;
		if (status != Place.INCONSISTENT && !act.getTransform().epsilonEquals(new Transform3D(), .1f))
		{
			//boolean afford = false;
			for (IPlace p : m_places)
			{
				if (p.getType() == Place.ENACTION_PLACE)
				{
					if (p.getAct().getPosition().epsilonEquals(p.getPosition(), .1f))
					{
						// This place affords itself
						if (subsequentAct==null || subsequentAct.getSatisfaction() < p.getAct().getSatisfaction())
							subsequentAct = p.getAct();
					}					
					//else
					// This place also affords its compresences
					for (IBundle b : m_spas.evokeCompresences(p.getAct()))
					{
						if (b.getFirstAct().getPosition().epsilonEquals(p.getPosition(), .1f))
							if (subsequentAct==null || subsequentAct.getSatisfaction() < b.getFirstAct().getSatisfaction())
								subsequentAct = b.getFirstAct();
						if (b.getSecondAct().getPosition().epsilonEquals(p.getPosition(), .1f))
							if (subsequentAct==null || subsequentAct.getSatisfaction() < b.getSecondAct().getSatisfaction())
								subsequentAct = b.getSecondAct();
					}
				}
			}
		}
		
		int subsequentSatisfaction = 0;
//		if (subsequentAct != null && subsequentAct.getSatisfaction() > 0)
//		{
//			subsequentSatisfaction = subsequentAct.getSatisfaction();
//			status = SIMULATION_REACH;
//		}		

		//Revert the transformation in spatial memory 
		m_transform.invert();
		transform(m_transform);	
		
		// If this act creates a new copresences then propose it
		boolean newCopresence = false;
		if (status != Place.INCONSISTENT)
		{
			for (IPlace pl : m_places)
			{
				if (pl.getType() == Place.ENACTION_PLACE)
				{
					if (act.getPosition().epsilonEquals(pl.getPosition(), .1f) && !act.equals(pl.getAct()) && act.getColor() == 0x73E600)
					{
						newCopresence = true;
						// Test if the copresence already exists
						for (IBundle b : m_spas.evokeCompresences(act))
						{
							if (b.afford(pl.getAct()))
								newCopresence = false;
						}
					}
				}
			}
		}
		if (newCopresence)
			status = SIMULATION_NEWCOMPRESENCE;

		// Generate the proposition
		
		IActProposition p = new ActProposition(act, 0, 0);
		final int SPATIAL_AFFORDANCE_WEIGHT = 10;
		final int UNKNOWN_SATISFACTION = 1000;
		
		// If this act is afforded by the spatial situation then propose it.
		if (status == Place.AFFORD)
		{
			int w = SPATIAL_AFFORDANCE_WEIGHT ;//* a.getSatisfaction();
			p = new ActProposition(act, w, 0);
		}

		// If this act informs the spatial situation then propose it.
		if (status == Place.UNKNOWN)
		{
			if (act.getSchema().getLabel().equals("-") || act.getSchema().getLabel().equals("/") || act.getSchema().getLabel().equals("\\"))
			{
				p = new ActProposition(act, 1, UNKNOWN_SATISFACTION);
			}
		}
		
		// If this act reaches a situation where another act is afforded then propose it.
//		if (status == LocalSpaceMemory.SIMULATION_REACH)
//		{
//			int w = SPATIAL_AFFORDANCE_WEIGHT ;
//			p = new ActProposition(act, SPATIAL_AFFORDANCE_WEIGHT, subsequentSatisfaction);
//		}
		
		// If this act reaches a situation where another act is afforded then propose it.
		if (status == LocalSpaceMemory.SIMULATION_NEWCOMPRESENCE)
		{
			int w = SPATIAL_AFFORDANCE_WEIGHT ;
			p = new ActProposition(act, SPATIAL_AFFORDANCE_WEIGHT, UNKNOWN_SATISFACTION * 10);
		}
		
		p.setStatus(status);
		
		return p;
	}
	
	private IEnaction simulate(IAct act)
	{
		IEnaction enaction = new Enaction();
		
		boolean unknown = true;
		boolean consistent = true;
		boolean afford = false;
		//String effectLabel ="";
		IAct enactedAct = null;
		int simulationStatus = Place.INCONSISTENT;
		ISchema s = act.getSchema();
		if (s.isPrimitive())
		{
			Point3f concernedPosition = new Point3f(act.getPosition()); 
			
			for (IPlace p : m_places)
			{
				if (p.isInCell(concernedPosition) && p.getType() == Place.ENACTION_PLACE)
				{
					unknown = false;
					if (p.getAct().equals(act))
					{
						// This place affords itself
						afford = true;
						enactedAct = act;
					}
					else
						// This place affords its compresences
						for (IBundle bundle : m_spas.evokeCompresences(p.getAct()))
						{
							if (!bundle.isConsistent(act)) consistent = false; 
							if (bundle.afford(act)) afford = true;
							IAct bundleAct = bundle.resultingAct(act); 
							if (bundleAct != null)
								enactedAct = bundleAct; 
							enaction.getEffect().setLabel(bundle.effectlabel(act));
						}
				}
			}	

			if (unknown)	
			{
				// No place found at this location
				
				if (act.getColor() == 0xFFFFFF)
				{
					simulationStatus = Place.UNKNOWN;
					// Mark an unknown interaction
					IPlace sim = addPlace(concernedPosition, Place.UNKNOWN);
					sim.setAct(act);
					sim.setValue(0xB0B0FF);
				}
				
				// acts that involve phenomena are inconsistent with unknown places.
				// TODO improve that.
			}
			else
			{
				if (consistent)
				{
					// No place that contains an incompatible act was fond at this location
					simulationStatus = Place.DISPLACEMENT;
					// Mark a consistent interaction
					IPlace sim = addPlace(concernedPosition, Place.UNKNOWN);
					sim.setAct(act);
					sim.setValue(act.getColor());
				}
				if (afford)
				{
					// A place that contains this act is found at this location
					simulationStatus = Place.AFFORD;
					// Mark an afforded interaction
					IPlace sim = addPlace(concernedPosition, Place.AFFORD);
					sim.setAct(act);
					sim.setValue(act.getColor());
					// the simulated enacted act is the intended act
					enaction.setEnactedPrimitiveAct(act);
					enaction.getEffect().setLabel(act.getEffectLabel());
				}
			}
			
			// Apply this act's transformation to spatial memory
			transform(act.getTransform());
			// accumulate the transformation of this act to reverse the transformation after the simulation
			Transform3D tf = new Transform3D(act.getTransform());
			m_transform.mul(tf, m_transform);

		}
		else 
		{
			simulationStatus = simulate(act.getSchema().getContextAct()).getSimulationStatus();
			if (simulationStatus != Place.INCONSISTENT)
			{
				int status2 = simulate(act.getSchema().getIntentionAct()).getSimulationStatus();
				if (status2 == Place.INCONSISTENT)
					simulationStatus = Place.INCONSISTENT;
				else
				{
					if (simulationStatus == Place.AFFORD && (status2 == Place.DISPLACEMENT || status2 == Place.UNKNOWN))
						simulationStatus = status2;
				}
			}
		}
		//return simulationStatus;
		enaction.setSimulationStatus(simulationStatus);
		return enaction;
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
			if (p.isInCell(position) && p.getType() == Place.ENACTION_PLACE)
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
//	public void clearFront()
//	{
//		for (Iterator it = m_places.iterator(); it.hasNext();)
//		{
//			IPlace l = (IPlace)it.next();
//			if (l.getDirection() > - Math.PI/2 && l.getDirection() < Math.PI/2 &&
//				l.getDistance() > 1)
//				it.remove();
//		}
//	}
	
	/**
	 * Clear all the places older than PERSISTENCE_DURATION.
	 */
	public void clear()
	{
		for (Iterator it = m_places.iterator(); it.hasNext();)
		{
			IPlace p = (IPlace)it.next();
			if (p.getType() == Place.ENACTION_PLACE || p.getType() == Place.EVOKED_PLACE )
			{
				//if (p.getUpdateCount() < m_spas.getClock() - PERSISTENCE_DURATION +1) // -1
				if (p.getClock() < m_clock - PERSISTENCE_DURATION +1) // -1
					it.remove();
			}
			else
			{
				//if (p.getUpdateCount() < m_spas.getClock() - PERSISTENCE_DURATION)
				if (p.getClock() < m_clock - PERSISTENCE_DURATION)
					it.remove();
				else if (p.getType() == Place.AFFORD || p.getType() == Place.UNKNOWN)// || p.getType() == Place.SIMULATION_PLACE)
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
		
//	/**
//	 * Construct new copresence bundles.
//	 * (Do not create copresences among the same schemas)
//	 * @param observation The observation 
//	 * @param spas A reference to the spatial system to add bundles
//	 */
//	public void copresence(ISpas spas)
//	{
//		// Clear the places that are older than the persistence of spatial memory
//		clear();
//		
//		// Get the list of interaction places (that can evoke phenomena).
//		ArrayList<IPlace> interactionPlaces = new ArrayList<IPlace>();
//		for (IPlace p : m_places)
//			//if (p.evokePhenomenon(m_clock))
//			if (p.getType() == Place.ENACTION_PLACE)
//				interactionPlaces.add(p);
//
//		// Create new copresence bundles 
//		
//		for (IPlace interactionPlace : interactionPlaces)
//		{
//			if (interactionPlace.getAct().concernOnePlace())
//			{
//				for (IPlace secondPlace : interactionPlaces)
//				{
//					if (secondPlace.getAct().concernOnePlace())
//					{
//						if (!interactionPlace.getAct().getSchema().equals(secondPlace.getAct().getSchema()) && interactionPlace.isInCell(secondPlace.getPosition())
//								&& interactionPlace.getAct().getColor() == secondPlace.getAct().getColor())
//						{
//							spas.addBundle(interactionPlace.getAct(), secondPlace.getAct());
//						}
//					}
//				}
//			}
//		}	
//	}

//	public void clearSimulation() 
//	{
//		for (Iterator it = m_places.iterator(); it.hasNext();)
//		{
//			IPlace p = (IPlace)it.next();
//			if (p.getType() == Place.SIMULATION_PLACE || p.getType() == Place.UNKNOWN || p.getType() == Place.AFFORD)
//				it.remove();
//		}
//	}
	
	public void setTransform(Transform3D transform) 
	{
		m_transform = transform;
	}

	public Transform3D getTransform() 
	{
		return m_transform;
	}
}
