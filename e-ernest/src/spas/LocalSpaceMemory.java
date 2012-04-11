package spas;

import imos.IAct;

import java.util.ArrayList;
import java.util.Iterator;
import javax.vecmath.Vector3f;
import utils.ErnestUtils;
import ernest.Ernest;
import ernest.ITracer;

/**
 * The local space structure maintains an awareness of the bundle surrounding Ernest. 
 * @author Olivier
 */
public class LocalSpaceMemory 
{
	
	/** The radius of a location. */
	public static float LOCATION_RADIUS = 0.5f;
	public static float LOCAL_SPACE_MEMORY_RADIUS = 20f;//4f;
	public static float DISTANCE_VISUAL_BACKGROUND = 10f;
	public static float EXTRAPERSONAL_DISTANCE = 1.5f;
	
	/** The duration of persistence in local space memory. */
	public static int PERSISTENCE_DURATION = 50;//20;
	
	/** The Local space structure. */
	private ArrayList<IPlace> m_places = new ArrayList<IPlace>();
	
	IPlace m_focusPlace = null;

	/** The persistence memory. */
	ISpas m_spas;
	
	/** The tracer. */
	ITracer m_tracer;
	
	public final static float DIAG2D_PROJ = (float) (1/Math.sqrt(2));

	public final static Vector3f DIRECTION_HERE         = new Vector3f(0, 0, 0);
	public final static Vector3f DIRECTION_AHEAD        = new Vector3f(1, 0, 0);
	public final static Vector3f DIRECTION_BEHIND       = new Vector3f(-1, 0, 0);
	public final static Vector3f DIRECTION_LEFT         = new Vector3f(0, 1, 0);
	public final static Vector3f DIRECTION_RIGHT        = new Vector3f(0, -1, 0);
	public final static Vector3f DIRECTION_AHEAD_LEFT   = new Vector3f(DIAG2D_PROJ, DIAG2D_PROJ, 0);
	public final static Vector3f DIRECTION_AHEAD_RIGHT  = new Vector3f(DIAG2D_PROJ, -DIAG2D_PROJ, 0);
	public final static Vector3f DIRECTION_BEHIND_LEFT  = new Vector3f(-DIAG2D_PROJ, DIAG2D_PROJ, 0);
	public final static Vector3f DIRECTION_BEHIND_RIGHT = new Vector3f(-DIAG2D_PROJ, -DIAG2D_PROJ, 0);	
	public final static float    SOMATO_RADIUS = 1f;
	
	LocalSpaceMemory(ISpas spas, ITracer tracer)
	{
		m_spas = spas;
		m_tracer = tracer;
	}

	public void trace()
	{
		if (m_tracer != null && !m_places.isEmpty())
		{
			Object localSpace = m_tracer.addEventElement("local_space");
			m_tracer.addSubelement(localSpace, "position_8", ErnestUtils.hexColor(getValue(DIRECTION_HERE)));
			m_tracer.addSubelement(localSpace, "position_7", ErnestUtils.hexColor(getValue(DIRECTION_BEHIND)));
			m_tracer.addSubelement(localSpace, "position_6", ErnestUtils.hexColor(getValue(DIRECTION_BEHIND_LEFT)));
			m_tracer.addSubelement(localSpace, "position_5", ErnestUtils.hexColor(getValue(DIRECTION_LEFT)));
			m_tracer.addSubelement(localSpace, "position_4", ErnestUtils.hexColor(getValue(DIRECTION_AHEAD_LEFT)));
			m_tracer.addSubelement(localSpace, "position_3", ErnestUtils.hexColor(getValue(DIRECTION_AHEAD)));
			m_tracer.addSubelement(localSpace, "position_2", ErnestUtils.hexColor(getValue(DIRECTION_AHEAD_RIGHT)));
			m_tracer.addSubelement(localSpace, "position_1", ErnestUtils.hexColor(getValue(DIRECTION_RIGHT)));
			m_tracer.addSubelement(localSpace, "position_0", ErnestUtils.hexColor(getValue(DIRECTION_BEHIND_RIGHT)));
		}
	}
	
	/**
	 * Add a new place to the local space memory.
	 * Replace the bundle if it already exists.
	 * @param bundle The bundle in this location.
	 * @param position The position of this place.
	 * @return The new or already existing location.
	 */
	public IPlace addPlace(IBundle bundle, Vector3f position)
	{
		IPlace place = new Place(bundle, position);	
		if (bundle != null)
			place.setValue(bundle.getValue());
		m_places.add(place);
		return place;
	}
	
	/**
	 * Update the local space memory according to the agent's moves.
	 * @param translation The translation vector in egocentric referential (provide the opposite vector from the agent's movement).
	 * @param rotation The rotation value (provide the opposite value from the agent's movement).
	 */
	public void update(Vector3f translation, float rotation)
	{
		rotate(rotation);
		translate(translation);
	}
	
	/**
	 * Rotate all the places of the given angle.
	 * @param angle The angle (provide the opposite angle from the agent's movement).
	 */
	private void rotate(float angle)
	{
		for (IPlace l : m_places)
			l.rotate(angle);
	}

	/**
	 * Simulate the rotation of all the places of the given angle.
	 * @param angle The angle (provide the opposite angle from the agent's movement).
	 */
	public void rotateSimulation(float angle)
	{
		for (IPlace l : m_places)
			l.rotateSimulation(angle);
	}

	/**
	 * Translate all the places of the given vector.
	 * Remove places that are outside the local space memory radius.
	 * @param translation The translation vector (provide the opposite vector from the agent's movement).
	 */
	private void translate(Vector3f translation)
	{
		for (IPlace p : m_places)
			p.translate(translation);
			
		for (Iterator it = m_places.iterator(); it.hasNext();)
		{
			IPlace l = (IPlace)it.next();
			if (l.getPosition().length() > LOCAL_SPACE_MEMORY_RADIUS)
			//if (l.getPosition().x < - LOCAL_SPACE_MEMORY_RADIUS)
				it.remove();
		}		
	}
	
	/**
	 * Simulate the translation of all the places of the given vector.
	 * Remove places that are outside the local space memory radius.
	 * @param translation The translation vector (provide the opposite vector from the agent's movement).
	 */
	public void translateSimulation(Vector3f translation)
	{
		for (IPlace p : m_places)
			p.translateSimulation(translation);			
	}
	
	/**
	 * Rotate all the places of the given angle.
	 * @param angle The angle (provide the opposite angle from the agent's movement).
	 */
	public void initSimulation()
	{
		for (IPlace p : m_places)
			p.initSimulation();
	}

	/**
	 * Get the bundle at a given position.
	 * (The last bundle found in the list of places that match this position)
	 * @param position The position of the location.
	 * @return The bundle.
	 */
	public IBundle getBundle(Vector3f position)
	{
		IBundle b = null;
		for (IPlace p : m_places)
		{
			if (p.isInCell(position) && p.isPhenomenon())
				b = p.getBundle();
		}	
		return b;
	}

	/**
	 * Get the phenomena value at a given position.
	 * (The last bundle found in the list of places that match this position)
	 * @param position The position of the location.
	 * @return The bundle.
	 */
	public int getValue(Vector3f position)
	{
		int value = Ernest.UNANIMATED_COLOR;
		for (IPlace p : m_places)
		{
			if (p.isInCell(position) && p.isPhenomenon())
				value = p.getValue();
		}	
		return value;
	}

	/**
	 * Get the phenomena value at a given position in the simulation.
	 * (The last bundle found in the list of places that match this position)
	 * @param position The position of the location.
	 * @return The bundle.
	 */
	public int getValueSimulation(Vector3f position)
	{
		int value = Ernest.UNANIMATED_COLOR;
		for (IPlace p : m_places)
		{
			if (p.isInCellSimulation(position) && p.isPhenomenon())
				value = p.getValue();
		}	
		return value;
	}

	/**
	 * Get the last place found at a given position.
	 * @param position The position of the location.
	 * @return The place.
	 */
	public IPlace getPlace(Vector3f position)
	{
		IPlace place = null;
		for (IPlace p : m_places)
		{
			if (p.isInCell(position) && p.evokePhenomenon(m_spas.getClock()))
				place = p;
		}
		return place;
	}

	/**
	 * Clear a position in the local space memory.
	 * @param position The position to clear.
	 */
	public void clearPlace(Vector3f position)
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
			if (p.getType() == Spas.PLACE_SEE || p.getType() == Spas.PLACE_TOUCH)
			{
				if (p.getUpdateCount() < m_spas.getClock() - 1)
					it.remove();
			}
			else
			{
				if (p.getUpdateCount() < m_spas.getClock() - PERSISTENCE_DURATION)
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
	
	public void setFocusPlace(IPlace focusPlace)
	{
		m_focusPlace = focusPlace;
	}

	public IPlace getFocusPlace()
	{
		return m_focusPlace;
	}
	
	public void phenomenon(ArrayList<IPlace> places, IObservation observation, int clock)
	{
		// Clear the old traces in persistence memory
		clear();
		
		// Confirm or create persistent places in local space memory 
		
		for (IPlace interactionPlace : places)
		{
			if (interactionPlace.evokePhenomenon(m_spas.getClock()))
			{
				boolean newPlace = true;
				// Look for a corresponding existing persistent place in local space memory.
				for (IPlace phenomenonPlace :  m_places)
				{
					if (phenomenonPlace.isPhenomenon()
							&& phenomenonPlace.getValue() == interactionPlace.getValue() // Generates some confusion with walls in Ernest11
							//&& bundlePlace.getBundle().equals(interactionPlace.getBundle()) // This version works wih Ernest11
							&& interactionPlace.from(phenomenonPlace))
					{
						phenomenonPlace.setPosition(interactionPlace.getPosition());
						phenomenonPlace.setFirstPosition(interactionPlace.getFirstPosition());
						phenomenonPlace.setSecondPosition(interactionPlace.getSecondPosition());
						phenomenonPlace.setSpeed(interactionPlace.getSpeed());
						phenomenonPlace.setSpan(interactionPlace.getSpan());
						phenomenonPlace.setOrientation(interactionPlace.getOrientation());
						phenomenonPlace.setUpdateCount(m_spas.getClock());
						newPlace = false;
					}
				}
				if (newPlace)
				{
					// Add a new persistent place
					IPlace k = addPlace(interactionPlace.getBundle(),interactionPlace.getPosition()); 
					k.setSpeed(interactionPlace.getSpeed());
					k.setSpan(interactionPlace.getSpan());
					k.setFirstPosition(interactionPlace.getFirstPosition()); 
					k.setSecondPosition(interactionPlace.getSecondPosition());
					k.setOrientation(interactionPlace.getOrientation());
					k.setUpdateCount(m_spas.getClock());
					//k.setType(interactionPlace.getType());
					k.setType(Spas.PLACE_PHENOMENON);
					k.setValue(interactionPlace.getValue());
				}
			}
		}
		
		// The most attractive place in local space memory gets the focus (abs value) 
		
		int maxAttractiveness = 0;
		IPlace focusPlace = null;
		boolean newFocus = false;
		for (IPlace place : m_places)
		{
            if (place.isPhenomenon())
			{
				int attractiveness =  place.getAttractiveness(clock);
				if (Math.abs(attractiveness) >= Math.abs(maxAttractiveness))
				{
					maxAttractiveness = attractiveness;
					focusPlace = place;
				}				
			}
		}
		
		// Test if the focus has changed
		
		if (focusPlace != null && focusPlace != m_focusPlace)
		{
			// Reset the previous stick
			if (m_focusPlace != null) m_focusPlace.setStick(0);
			// Set the new stick
			focusPlace.setStick(20);
			m_focusPlace = focusPlace;
			//m_localSpaceMemory.setFocusPlace(focusPlace);
			newFocus = true;
			
			//try { Thread.sleep(500);
			//} catch (InterruptedException e) {e.printStackTrace();}
		}
		// The new observation.
		
		observation.setFocusPlace(m_focusPlace);
		observation.setAttractiveness(maxAttractiveness);
		observation.setNewFocus(newFocus);
		
		if (focusPlace == null || focusPlace.getBundle() == null)
		{
			observation.setBundle(m_spas.addBundle(Ernest.STIMULATION_VISUAL_UNSEEN, Ernest.STIMULATION_TOUCH_EMPTY));
			observation.setPosition(new Vector3f(1,0,0));
			observation.setSpan(0);
			observation.setSpeed(new Vector3f());
			observation.setUpdateCount(-1);
		}
		else
		{
			observation.setBundle(focusPlace.getBundle());
			observation.setPosition(focusPlace.getPosition());
			observation.setSpan(focusPlace.getSpan());
			observation.setSpeed(focusPlace.getSpeed());
			observation.setType(focusPlace.getType());
			observation.setUpdateCount(focusPlace.getUpdateCount());
			
			IAct act = focusPlace.getBundle().activateAffordance(focusPlace.getPosition());
			observation.setAffordanceAct(act);
		}		
	}
	
	/**
	 * @return the list of phenomena in local space memory
	 */
	public ArrayList<IPlace> getPhenomena() 
	{
		ArrayList<IPlace> phenomena = new ArrayList<IPlace>();
		
		for (IPlace place : m_places)
		{
			if (place.getPosition().length() < 1.9 && place.getPosition().length() > .1 && place.isPhenomenon())
				phenomena.add(place);
		}
		
		trace();
		return phenomena;
	}	

}
