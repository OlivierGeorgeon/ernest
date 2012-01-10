package spas;


import imos.IAct;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

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
	public static float LOCAL_SPACE_MEMORY_RADIUS = 2f;
	public static float DISTANCE_VISUAL_BACKGROUND = 10f;
	
	/** The Local space structure. */
	private ArrayList<IPlace> m_places = new ArrayList<IPlace>();

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
	 * Add places in the background associated with visual bundles.
	 * @param visualStimulations The list of visual stimulations.
	 */
	public void addVisualPlaces(IStimulation[] visualStimulations)
	{
		IStimulation stimulation = visualStimulations[0];
		int span = 1;
		float theta = - 11 * (float)Math.PI / 24; 
		float sumDirection = theta;
		float spanf = (float)Math.PI / 12;
		float sumDistance = visualStimulations[0].getPosition().length();
		for (int i = 1 ; i <= Ernest.RESOLUTION_RETINA; i++)
		{
			theta += (float)Math.PI / 12;
			if ((i < Ernest.RESOLUTION_RETINA) && visualStimulations[i].equals(stimulation))
			{
				// measure the salience span and average direction
				span++;
                sumDirection += theta;
                spanf += (float)Math.PI / 12;
                sumDistance += visualStimulations[i].getPosition().length();
			}
			else 
			{	
				// Create or recognize a visual bundle.
				IBundle b = m_persistenceMemory.seeBundle(stimulation.getValue());
				if (b == null)
					b = m_persistenceMemory.addBundle(stimulation.getValue(), Ernest.STIMULATION_TOUCH_EMPTY, Ernest.STIMULATION_KINEMATIC_FORWARD, Ernest.STIMULATION_GUSTATORY_NOTHING);
				// Record the place in the visual background.
				//IPlace place = new Place(b,DISTANCE_VISUAL_BACKGROUND, sumDirection / span, spanf);
				IPlace place = new Place(b,sumDistance / span, sumDirection / span, spanf);
				m_places.add(place);
				// look for the next bundle
				
				if (i < Ernest.RESOLUTION_RETINA)
				{
					stimulation = visualStimulations[i];
					span = 1;
					spanf = (float)Math.PI / 12;
					sumDirection = theta;
					sumDistance = visualStimulations[i].getPosition().length();
				}
			}
		}
	}

	/**
	 * Add places in the peripersonal space associated with tactile bundles.
	 * @param tactileStimulations The list of visual stimulations.
	 */
	public void addTactilePlaces(IStimulation[] tactileStimulations)
	{

		IStimulation tactileStimulation = tactileStimulations[0];
		int span = 1;
		float theta = - 3 * (float)Math.PI / 4; 
		float sumDirection = theta;
		float spanf = (float)Math.PI / 4;
		
		for (int i = 1 ; i <= 7; i++)
		{
			theta += (float)Math.PI / 4;
			if ((i < 7) && tactileStimulations[i].equals(tactileStimulation))
			{
				// measure the salience span and average direction
				span++;
                sumDirection += theta;
                spanf += (float)Math.PI / 4;
			}
			else 
			{	
				if (tactileStimulation.getValue() != Ernest.STIMULATION_TOUCH_EMPTY)
				{
					// Create a tactile bundle.
					float direction = sumDirection / span;
					Vector3f position = new Vector3f((float)(Ernest.TACTILE_RADIUS * Math.cos((double)direction)), (float)(Ernest.TACTILE_RADIUS * Math.sin((double)direction)), 0f);
					// See in that direction.
					IPlace place = seePlace(direction);
					if (place == null)
					{
						// Create a place.
						IBundle b = m_persistenceMemory.addBundle(Ernest.STIMULATION_VISUAL_UNSEEN, tactileStimulation.getValue(), Ernest.STIMULATION_KINEMATIC_FORWARD, Ernest.STIMULATION_GUSTATORY_NOTHING);
						place = addPlace(b, position);
						place.setSpan(spanf);
					}
					else
					{
						if (place.getBundle().getTactileValue() == tactileStimulation.getValue())
						{
							// move the visual place to the tactile radius.
							place.getBundle().setLastTimeBundled(m_persistenceMemory.getClock());
							place.setPosition(position);
							place.setSpan(spanf);
						}
						else if (place.getBundle().getTactileValue() == Ernest.STIMULATION_TOUCH_EMPTY)
						{
							// Update the place and the bundle
							IBundle b = m_persistenceMemory.addBundle(place.getBundle().getVisualValue(), tactileStimulation.getValue(), Ernest.STIMULATION_KINEMATIC_FORWARD, Ernest.STIMULATION_GUSTATORY_NOTHING);
							place.setBundle(b);
							place.setPosition(position);							
							place.setSpan(spanf);
						}
					}
				}
				// look for the next bundle
				if (i < 7)
				{
					tactileStimulation = tactileStimulations[i];
					span = 1;
					spanf = (float)Math.PI / 4;
					sumDirection = theta;
				}
			}
		}
	}
	
	public void addKinematicPlace(int kinematicValue)
	{
		// Find the place in front of Ernest.
		IBundle frontBundle = null;
		for (IPlace place : m_places)
			if (place.isFrontal() && place.getBundle().getVisualValue() != Ernest.STIMULATION_VISUAL_UNSEEN && place.getSpan() > Math.PI/6 + 0.01f )
				frontBundle = place.getBundle();
		
		// Associate kinematic stimulation to the front bundle.

		if (kinematicValue == Ernest.STIMULATION_KINEMATIC_BUMP)
		{
			if (frontBundle != null)
			{
				m_persistenceMemory.addKinematicValue(frontBundle, kinematicValue);
			}
		}
	}
	
	public void addGustatoryPlace(int gustatoryValue)
	{
		IBundle frontBundle = getBundle(LocalSpaceMemory.DIRECTION_AHEAD);
		IBundle hereBundle = getBundle(LocalSpaceMemory.DIRECTION_HERE);

		// Associate the tactile stimulation with the fish gustatory stimulation
		
		if (gustatoryValue == Ernest.STIMULATION_GUSTATORY_FISH)
		{
			// Discrete environment. The fish bundle is the hereBundle.
			if (hereBundle != null && hereBundle.getTactileValue() == Ernest.STIMULATION_TOUCH_FISH)
			{
				m_persistenceMemory.addGustatoryValue(hereBundle, gustatoryValue);
				clearPlace(LocalSpaceMemory.DIRECTION_HERE); // The fish is eaten
			}
			
			// Continuous environment. The fish bundle is the frontBundle
			if (frontBundle != null && frontBundle.getTactileValue() == Ernest.STIMULATION_TOUCH_FISH)
			{
				m_persistenceMemory.addGustatoryValue(frontBundle, gustatoryValue);
				clearPlace(LocalSpaceMemory.DIRECTION_AHEAD); // The fish is eaten
			}
		}
		
		// Associate the tactile stimulation with the cuddle stimulation
		
		if (gustatoryValue == Ernest.STIMULATION_GUSTATORY_CUDDLE)
		{
			if (frontBundle != null && frontBundle.getTactileValue() == Ernest.STIMULATION_TOUCH_AGENT)
			{
				m_persistenceMemory.addGustatoryValue(frontBundle, gustatoryValue);
			}
		}
		
	}
	
	/**
	 * Add a new place to the local space memory if it does not yet exist.
	 * Replace the bundle if it already exists.
	 * @param bundle The bundle in this location.
	 * @param distance The distance of this place.
	 * @param direction The direction of this place.
	 * @return The new or already existing location.
	 */
	public IPlace addPlace(IBundle bundle, float distance, float direction)
	{
		Vector3f position = new Vector3f((float)(distance * Math.cos((double)direction)), (float)(distance * Math.sin((double)direction)), 0f);
		return addPlace(bundle, position);
	}
	
	public IPlace addPlace(IBundle bundle, Vector3f position)
	{
		// The initial position must be cloned so that 
		// the position can be moved without changing the position used for intialization.
		Vector3f p = new Vector3f(position);
		
		IPlace l = new Place(bundle, p);
		
		int i = m_places.indexOf(l);
		if (i == -1)
			// The place does not exist
			m_places.add(l);
		else 
		{
			// The place already exists: return a pointer to it.
			l =  m_places.get(i);
			l.setBundle(bundle);
		}
		return l;
	}
	
	/**
	 * Update the local space memory according to the enacted interaction.
	 * @param act The enacted act.
	 * @param kinematicStimulation The kinematic stimulation.
	 */
	public void update(IAct act, IStimulation kinematicStimulation)
	{
		if (act != null)
		{
			if (act.getSchema().getLabel().equals(">") && kinematicStimulation.getValue() != Ernest.STIMULATION_KINEMATIC_BUMP)
				translate(new Vector3f(-1f, 0,0));
			else if (act.getSchema().getLabel().equals("^"))
				rotate(- (float)Math.PI / 4);
			else if (act.getSchema().getLabel().equals("v"))
				rotate((float)Math.PI / 4);
		}
	}
	
	/**
	 * Update the local space memory according to the agent's moves.
	 * @param translation The translation value (provide the opposite value from the agent's movement).
	 * @param rotation The rotation value (provide the opposite value from the agent's movement).
	 */
	public void update(Vector3f translation, float rotation)
	{
		translate(translation);
		rotate(rotation);
	}
	
	/**
	 * Rotate the local space of the given angle.
	 * @param angle The angle (provide the opposite angle from the agent's movement).
	 */
	private void rotate(float angle)
	{
		for (IPlace l : m_places)
		{
			l.rotate(angle);
		}		
	}

	/**
	 * Translate the local space of the given distance.
	 * Remove locations that are left behind.
	 * @param distance The distance (provide the opposite value from the agent's movement).
	 */
	private void translate(Vector3f translation)
	{
		for (IPlace p : m_places)
			p.translate(translation);
			
		for (Iterator it = m_places.iterator(); it.hasNext();)
		{
			IPlace l = (IPlace)it.next();
			//if (l.getPosition().length() > LOCAL_SPACE_MEMORY_RADIUS)
			//if (l.getPosition().x < - LOCAL_SPACE_MEMORY_RADIUS)
			//	it.remove();
		}		
	}
	
	private IPlace seePlace(float direction)
	{
		IPlace place = null;
		for (IPlace l : m_places)
		{
			if (l.getDirection() - l.getSpan() / 2 < direction - Math.PI/12 + 0.1 && 
				l.getDirection() + l.getSpan() / 2 > direction + Math.PI/12 - 0.1 &&
				l.getBundle().getVisualValue() != Ernest.STIMULATION_VISUAL_UNSEEN)
				if (place == null || l.getDistance() < place.getDistance())
					place = l;
		}
		return place;
	}
	
	/**
	 * Get the bundle at a given position.
	 * @param position The position of the location.
	 * @return The bundle.
	 */
	public IBundle getBundle(Vector3f position)
	{
		for (IPlace l : m_places)
		{
			if (l.isInCell(position))
				return l.getBundle();
		}		

		return null;
	}

	/**
	 * Clear a location in the local space memory.
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
	 * Clear a location in the local space memory.
	 * @param position The position to clear.
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
		return getHexColor(value);
		//return getHexColor(getValue(position));
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
	
	public ArrayList<IPlace> getPlaceList()
	{
		return m_places;
	}
	
	private String getHexColor(int rgb) 
	{
		int r = rgb/65536;
		int g = (rgb - r * 65536)/256;
		int b = rgb - r * 65536 - g * 256;
		String s = format(r) + format(g) + format(b);

		return s;
	}
	
	private String format(int i)
	{
		if (i == 0)
			return "00";
		else if (i < 16)
			return "0" + Integer.toString(i, 16).toUpperCase();
		else
			return Integer.toString(i, 16).toUpperCase();
	}
}
