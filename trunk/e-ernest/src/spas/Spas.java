package spas;

import imos2.IAct;
import imos2.IEnaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ernest.IPrimitive;
import ernest.ITracer;

/**
 * The spatial system.
 * Maintains the local space map and the persistence memory.
 * @author Olivier
 */
public class Spas implements ISpas 
{
	
	/** The Tracer. */
	private ITracer m_tracer = null; 
	
	/** The clock of the spatial system. (updated on each update cycle as opposed to IMOS) */
	private int m_clock;

	/** A list of all the bundles ever identified. */
	public List<IBundle> m_bundles = new ArrayList<IBundle>(10);
	
	/** Ernest's local space memory  */
	private ISpatialMemory m_localSpaceMemory = new LocalSpaceMemory();
	
	/** The transformation used to keep track of simulation. */
	Transform3D m_transform = new Transform3D();

	/** The slicer. */
	IPositionCategorizer positionCategorizer = new PositionCategorizer();

	public void setTracer(ITracer tracer) 
	{
		m_tracer = tracer;
	}
	
	public IArea categorizePosition(Point3f point)
	{
		return this.positionCategorizer.categorize(point);
	}

	/**
	 * The main method of the Spatial System that is called on each interaction cycle.
	 * Maintain the local space memory.
	 * Construct compresences.
	 * @param enaction The current enaction.
	 */
	public void track(IEnaction enaction) 
	{
		tick();
		
		m_localSpaceMemory.transform(enaction.getEffect().getTransformation());		
		m_localSpaceMemory.decay();
		
		if (enaction.getEffect().getLocation() != null && enaction.getEnactedPrimitiveInteraction() != null)
		{
			addPlace(enaction.getEffect().getLocation(), Place.ENACTION_PLACE, enaction.getEffect().getColor(), enaction.getEnactedPrimitiveInteraction());			
			//constructCopresence(enaction);
			//evokePlaces(enaction);
		}
		
		if (m_tracer != null) m_localSpaceMemory.trace(m_tracer);
	}

	/**
	 * TODO Use it to track actual enactions
	 */
//	private void simulatePrimitiveAct(IEnaction enaction)
//	{
//		boolean affordIntention = false;
//		boolean affordMove = false;
//		boolean affordDisplacement = false;
//		
//		int simulationStatus = Place.INCONSISTENT;
//		IAct intendedAct = enaction.getIntendedPrimitiveAct();
//		Point3f concernedPosition = new Point3f(intendedAct.getPosition());
//		
//		IAct enactedAct = intendedAct;
//		
//		// Compute the expected effect depending on what is known about the place concerned by the intention ====
//		
//		for (IPlace concernedPlace : m_localSpaceMemory.getPlaceList())
//		{
//			if (concernedPlace.isInCell(concernedPosition) && (concernedPlace.getType() == Place.ENACTION_PLACE || concernedPlace.getType() == Place.EVOKED_PLACE))
//			{
//				// There is a place in the concerned position
//				if (!affordIntention)
//				{
//					// Not already an affordance at the same place
//					if (concernedPlace.getAct().equals(intendedAct))
//					{
//						// This place affords the current intention
//						affordDisplacement = true;
//						affordMove = true;
//						affordIntention = true;
//					}
//					
//					if (!affordMove)
//					{
//						// Not already the same move 
//						if (concernedPlace.getAct().getSchema().equals(intendedAct.getSchema()))
//						{
//							// This move has already been experienced concerning this place
//							affordMove = true;								
//							enactedAct = concernedPlace.getAct();
//						
//							if (concernedPlace.getAct().getTransform().epsilonEquals(intendedAct.getTransform(), .1f) &&
//								!intendedAct.getTransform().epsilonEquals(new Transform3D(), .1f))
//							{
//								// This place affords the intended displacement but not the intention
//								affordDisplacement = true;
//							}
//						}						
//					}
//				}	
//			}
//		}
//
//		// Compute the status of the simulation
//		
//		if (affordIntention)
//			simulationStatus = Place.AFFORD;
//		else if (affordDisplacement)
//			simulationStatus = Place.DISPLACEMENT;
//		else if (affordMove)
//			simulationStatus = Place.INCONSISTENT;			
//		else
//		{
//			if (intendedAct.getColor() == 0xFFFFFF)
//				simulationStatus = Place.UNKNOWN;
//			else
//				simulationStatus = Place.INCONSISTENT;			
//		}
//		
//		// Update the enaction
//		enaction.setSimulationStatus(simulationStatus);
//		enaction.setEnactedPrimitiveAct(enactedAct);
//		
//		// TODO the spatial effect may involve more spatial simulation.
//		enaction.getEffect().setLabel(enactedAct.getEffectLabel());
//		enaction.getEffect().setLocation(concernedPosition);
//		enaction.getEffect().setTransformation(enactedAct.getTransform());
//		
//		// Mark the simulated place in spatial memory
//		if (simulationStatus != Place.INCONSISTENT)
//			addPlace(concernedPosition, simulationStatus, enactedAct.getColor(), enactedAct);
//
//		// Apply this act's transformation to spatial memory
//		m_localSpaceMemory.transform(enactedAct.getTransform());
//		
//		// accumulate the transformation of this act to reverse the transformation after the simulation
//		Transform3D tf = new Transform3D(enactedAct.getTransform());
//		m_transform.mul(tf, m_transform);
//	}

//	private IEnaction simulate(IAct act)
//	{
//		IEnaction enaction = new Enaction();
//		
//		if (act.getSchema().isPrimitive())
//		{
//			enaction.setIntendedPrimitiveAct(act);
//			simulatePrimitiveAct(enaction);
//		}
//		else 
//		{
//			int simulationStatus = simulate(act.getSchema().getContextAct()).getSimulationStatus();
//			if (simulationStatus != Place.INCONSISTENT)
//			{
//				int status2 = simulate(act.getSchema().getIntentionAct()).getSimulationStatus();
//				if (status2 == Place.INCONSISTENT)
//					simulationStatus = Place.INCONSISTENT;
//				else
//				{
//					if (simulationStatus == Place.AFFORD && (status2 == Place.DISPLACEMENT || status2 == Place.UNKNOWN))
//						simulationStatus = status2;
//				}
//			}
//			enaction.setSimulationStatus(simulationStatus);
//		}
//		return enaction;
//	}

//	public IActProposition runSimulation(IAct act)
//	{
//		// Intialize the simulation
//		m_transform.setIdentity();
//		
//		// Run the simulation
//		int status = simulate(act).getSimulationStatus();
//		
//		// Test if the resulting situation leads to an affordance
//		
//		IAct subsequentAct = null;
//		if (status != Place.INCONSISTENT && !act.getTransform().epsilonEquals(new Transform3D(), .1f))
//		{
//			for (IPlace p : m_localSpaceMemory.getPlaceList())
//			{
//				if (p.getType() == Place.ENACTION_PLACE || p.getType() == Place.EVOKED_PLACE )
//				{
//					if (p.getAct().getPosition().epsilonEquals(p.getPosition(), .1f))
//					{
//						if (subsequentAct==null || subsequentAct.getSatisfaction() < p.getAct().getSatisfaction())
//							subsequentAct = p.getAct();
//					}					
//				}
//			}
//		}
//		
//		int subsequentSatisfaction = 0;
//		if (subsequentAct != null && subsequentAct.getSatisfaction() > 0)
//		{
//			subsequentSatisfaction = subsequentAct.getSatisfaction();
//			status = Place.REACH;
//		}		
//
//		//Revert the transformation in spatial memory 
//		m_transform.invert();
//		m_localSpaceMemory.transform(m_transform);	
//		
//		// If this act creates a new copresences then propose it
//		boolean newCopresence = false;
//		if (status != Place.INCONSISTENT)
//		{
//			for (IPlace pl : m_localSpaceMemory.getPlaceList())
//			{
//				if (pl.getType() == Place.ENACTION_PLACE)
//				{
//					if (act.getPosition().epsilonEquals(pl.getPosition(), .1f) && !act.equals(pl.getAct()) && act.getColor() == 0x73E600)
//					{
//						newCopresence = true;
//						// Test if the copresence already exists
//						for (IBundle b : evokeCompresences(act))
//						{
//							if (b.afford(pl.getAct()))
//								newCopresence = false;
//						}
//					}
//				}
//			}
//		}
//		if (newCopresence)
//			status = LocalSpaceMemory.SIMULATION_NEWCOMPRESENCE;
//
//		// Generate the proposition
//		
//		IActProposition p = new ActProposition(act, 0, 0);
//		final int SPATIAL_AFFORDANCE_WEIGHT = 10;
//		final int UNKNOWN_SATISFACTION = 1000;
//		
//		// If this act is afforded by the spatial situation then propose it.
//		if (status == Place.AFFORD)
//		{
//			int w = SPATIAL_AFFORDANCE_WEIGHT ;//* a.getSatisfaction();
//			p = new ActProposition(act, w, 0);
//		}
//
//		// If this act informs the spatial situation then propose it.
//		if (status == Place.UNKNOWN)
//		{
//			if (act.getSchema().getLabel().equals("-") || act.getSchema().getLabel().equals("/") || act.getSchema().getLabel().equals("\\"))
//			{
//				p = new ActProposition(act, 1, UNKNOWN_SATISFACTION);
//			}
//		}
//		
//		// If this act reaches a situation where another act is afforded then propose it.
//		if (status == Place.REACH)
//		{
//			int w = SPATIAL_AFFORDANCE_WEIGHT ;
//			p = new ActProposition(act, SPATIAL_AFFORDANCE_WEIGHT, subsequentSatisfaction);
//		}
//		
//		// If this act reaches a situation where another act is afforded then propose it.
//		if (status == LocalSpaceMemory.SIMULATION_NEWCOMPRESENCE)
//		{
//			int w = SPATIAL_AFFORDANCE_WEIGHT ;
//			p = new ActProposition(act, SPATIAL_AFFORDANCE_WEIGHT, UNKNOWN_SATISFACTION * 10);
//		}
//		
//		p.setStatus(status);
//		
//		return p;
//	}
	
	public int getValue(int i, int j)
	{
		Point3f position = new Point3f(1 - j, 1 - i, 0);
		if (m_localSpaceMemory != null)
			return m_localSpaceMemory.getValue(position);
		else
			return 0xFFFFFF;
	}
	
//	/**
//	 * Construct new copresence bundles.
//	 * (Do not create copresences among the same interactions)
//	 */
//	private void constructCopresence()
//	{
//		// Get the list of interaction places (that can evoke phenomena).
//		ArrayList<IPlace> interactionPlaces = new ArrayList<IPlace>();
//		for (IPlace p : m_localSpaceMemory.getPlaceList())
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
//							addBundle(interactionPlace.getAct(), secondPlace.getAct());
//						}
//					}
//				}
//			}
//		}	
//	}
	
	/**
	 * Construct new copresence bundles.
	 * (Do not create copresences among the same interactions)
	 */
//	private void constructCopresence(IEnaction enaction)
//	{
//		IAct enactedAct = enaction.getEnactedPrimitiveAct();
//		for (IPlace place : m_localSpaceMemory.getPlaceList())
//			if (place.getType() == Place.ENACTION_PLACE && place.getAct().concernOnePlace() && place.isInCell(enaction.getEffect().getLocation()))
//				if (!place.getAct().getSchema().equals(enactedAct.getSchema()))
//					addBundle(place.getAct(), enactedAct);						
//	}
	
	/**
	 * Add evoked places in spatial memory
	 */
//	private void evokePlaces(IEnaction enaction)
//	{
//		for (IBundle b : evokeCompresences(enaction.getEnactedPrimitiveAct()))
//		{
//			if (!b.getFirstAct().equals(enaction.getEnactedPrimitiveAct()))
//				addPlace(new Point3f(enaction.getEffect().getLocation()), Place.EVOKED_PLACE, enaction.getEffect().getColor(), b.getFirstAct());
//			if (!b.getSecondAct().equals(enaction.getEnactedPrimitiveAct()))
//				addPlace(new Point3f(enaction.getEffect().getLocation()), Place.EVOKED_PLACE, enaction.getEffect().getColor(), b.getSecondAct());
//		}
//	}
	
	public ArrayList<IPlace> getPlaceList()
	{
		return m_localSpaceMemory.getPlaceList();
	}

	//private IPlace addPlace(Point3f position, int type, int value, IAct act) 
	private IPlace addPlace(Point3f position, int type, int value, IAct act) 
	{
		IPlace place = m_localSpaceMemory.addPlace(act, position);
		place.setValue(value);
		//place.setAct(act);
//		place.setType(type);
		//place.setClock(m_clock);
//		place.setClock(0);
		
		return place;
	}

//	public IBundle addBundle(IAct firstAct, IAct secondAct) 
//	{
//		IBundle bundle = new Bundle(firstAct, secondAct);
//		bundle.setValue(firstAct.getColor());
//		
//		int i = m_bundles.indexOf(bundle);
//		if (i == -1)
//		{
//			m_bundles.add(bundle);
//			if (m_tracer != null) {
//				bundle.trace(m_tracer, "bundle");
//			}
//		}
//		else 
//			// The bundle already exists: return a pointer to it.
//			bundle =  m_bundles.get(i);
//		
//		return bundle;
//	}

	public int getClock() 
	{
		return m_clock;
	}

	public void tick() 
	{
		m_clock++;
		m_localSpaceMemory.tick();
	}

	public int getValue(Point3f position) 
	{
		return m_localSpaceMemory.getValue(position);
	}

//	public IPlace getPlace(Vector3f position) 
//	{
//		return m_localSpaceMemory.getPlace(position);
//	}
	
	public ISpatialMemory getSpatialMemory()
	{
		return m_localSpaceMemory;
	}

	/**
	 * Returns the list of compresences that afford this act.
	 * @param act The act to check.
	 * @return The list of compresences that match this act.
	 */
//	public ArrayList<IBundle> evokeCompresences(IAct act)
//	{
//		ArrayList<IBundle> compresences = new ArrayList<IBundle>();
//
//		for (IBundle bundle : m_bundles)
//			if (bundle.afford(act))
//				compresences.add(bundle);
//
//		return compresences;
//	}
}
