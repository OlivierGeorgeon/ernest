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
	public static int PERSISTENCE_DURATION = 500;//20;
	
	/** The Local space structure. */
	private ArrayList<IPlace> m_places = new ArrayList<IPlace>();
	
	IPlace m_focusPlace = null;

	/** The persistence memory. */
	PersistenceMemory m_persistenceMemory;
	
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
	
	LocalSpaceMemory(PersistenceMemory persistenceMemory, ITracer tracer)
	{
		m_persistenceMemory = persistenceMemory;
		m_tracer = tracer;
	}

	public void Trace()
	{
		if (m_tracer != null && !m_places.isEmpty())
		{
			Object localSpace = m_tracer.addEventElement("local_space");
			m_tracer.addSubelement(localSpace, "position_8", getHexColor(DIRECTION_HERE));
			m_tracer.addSubelement(localSpace, "position_7", getHexColor(DIRECTION_BEHIND));
			m_tracer.addSubelement(localSpace, "position_6", getHexColor(DIRECTION_BEHIND_LEFT));
			m_tracer.addSubelement(localSpace, "position_5", getHexColor(DIRECTION_LEFT));
			m_tracer.addSubelement(localSpace, "position_4", getHexColor(DIRECTION_AHEAD_LEFT));
			m_tracer.addSubelement(localSpace, "position_3", getHexColor(DIRECTION_AHEAD));
			m_tracer.addSubelement(localSpace, "position_2", getHexColor(DIRECTION_AHEAD_RIGHT));
			m_tracer.addSubelement(localSpace, "position_1", getHexColor(DIRECTION_RIGHT));
			m_tracer.addSubelement(localSpace, "position_0", getHexColor(DIRECTION_BEHIND_RIGHT));
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
			if (p.isInCell(position) && p.attractFocus( m_persistenceMemory.getUpdateCount()))
				b = p.getBundle();
		}	
		return b;
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
			if (p.isInCell(position) && p.attractFocus(m_persistenceMemory.getUpdateCount()))
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
				if (p.getUpdateCount() < m_persistenceMemory.getUpdateCount() - 1)
					it.remove();
			}
			else
			{
				if (p.getUpdateCount() < m_persistenceMemory.getUpdateCount() - PERSISTENCE_DURATION)
					it.remove();
			}
		}
		//m_places.clear();
	}
	
	/**
	 * Get the color of a given position.
	 * @param position The position.
	 * @return The Hexadecimal color code.
	 */
	public String getHexColor(Vector3f position) 
	{
		int value = getValue(position);
		if (value == Ernest.STIMULATION_VISUAL_UNSEEN)
		{
			Vector3f farPosition = new Vector3f(position);
			farPosition.scale(2f);
			//Vector3f farPosition = new Vector3f();
			//farPosition.scale(2f, position);
			value = getValue(farPosition);
		}
		return ErnestUtils.hexColor(value);
	}

	/**
	 * Get the value of the first bundle found in a given position.
	 * or STIMULATION_VISUAL_UNSEEN if no bundle found in that position.
	 * @param position The position.
	 * @return The value.
	 */
	public int getValue(Vector3f position)
	{
		int value = Ernest.STIMULATION_VISUAL_UNSEEN;

		IBundle b = getBundle(position);
		if (b != null)
			value = b.getValue();
		return value;
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
	
	public void refresh(ArrayList<IPlace> places, IObservation observation)
	{
		// Clear the old traces in persistence memory
		clear();
		
		// Confirm or create persistent places in local space memory 
		
		for (IPlace place : places)
		{
			if (place.attractFocus(m_persistenceMemory.getUpdateCount()))
			{
				boolean newPlace = true;
				// Look for a corresponding persistent place in local space memory.
				for (IPlace p :  m_places)
				{
					if (p.attractFocus(m_persistenceMemory.getUpdateCount()-1) 
							&& p.getBundle().equals(place.getBundle())
							&& place.from(p))
					{
						p.setPosition(place.getPosition());
						p.setFirstPosition(place.getFirstPosition());
						p.setSecondPosition(place.getSecondPosition());
						p.setSpeed(place.getSpeed());
						p.setSpan(place.getSpan());
						p.setOrientation(place.getOrientation());
						p.setUpdateCount(m_persistenceMemory.getUpdateCount());
						newPlace = false;
					}
				}
				if (newPlace)
				{
					// Add a new persistent place
					IPlace k = addPlace(place.getBundle(),place.getPosition()); 
					k.setSpeed(place.getSpeed());
					k.setSpan(place.getSpan());
					k.setFirstPosition(place.getFirstPosition()); // somehow inverted
					k.setSecondPosition(place.getSecondPosition());
					k.setOrientation(place.getOrientation());
					k.setUpdateCount(m_persistenceMemory.getUpdateCount());
					k.setType(place.getType());
				}
			}
		}
		
		// The most attractive place in local space memory gets the focus (abs value) 
		
		int maxAttractiveness = 0;
		IPlace focusPlace = null;
		boolean newFocus = false;
		for (IPlace place : m_places)
		{
			if (place.attractFocus(m_persistenceMemory.getUpdateCount()) && place.getType() != Spas.PLACE_BACKGROUND)
			{
				int attractiveness =  place.getAttractiveness(m_persistenceMemory.getClock());
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
		
		if (focusPlace == null)
		{
			observation.setBundle(m_persistenceMemory.addBundle(Ernest.STIMULATION_VISUAL_UNSEEN, Ernest.STIMULATION_TOUCH_EMPTY));
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
}
