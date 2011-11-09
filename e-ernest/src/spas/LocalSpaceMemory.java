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
 * The local space structure maintaines an awareness of the bundle surrounding Ernest. 
 * @author Olivier
 */
public class LocalSpaceMemory 
{
	
	/** The radius of a location. */
	public static float LOCATION_RADIUS = 0.5f;
	
	/** The Local space structure. */
	private List<IPlace> m_localSpace = new ArrayList<IPlace>();
	
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

	public void Trace(ITracer tracer)
	{
		if (tracer != null && !m_localSpace.isEmpty())
		{
			Object localSpace = tracer.addEventElement("local_space");
			tracer.addSubelement(localSpace, "position_8", getHexColor(DIRECTION_HERE));
			tracer.addSubelement(localSpace, "position_7", getHexColor(DIRECTION_BEHIND));
			tracer.addSubelement(localSpace, "position_6", getHexColor(DIRECTION_BEHIND_LEFT));
			tracer.addSubelement(localSpace, "position_5", getHexColor(DIRECTION_LEFT));
			tracer.addSubelement(localSpace, "position_4", getHexColor(DIRECTION_AHEAD_LEFT));
			tracer.addSubelement(localSpace, "position_3", getHexColor(DIRECTION_AHEAD));
			tracer.addSubelement(localSpace, "position_2", getHexColor(DIRECTION_AHEAD_RIGHT));
			tracer.addSubelement(localSpace, "position_1", getHexColor(DIRECTION_RIGHT));
			tracer.addSubelement(localSpace, "position_0", getHexColor(DIRECTION_BEHIND_RIGHT));
		}

	}
	
	/**
	 * Add a new location to the localSpace if it does not yet exist.
	 * Replace the bundle if it already exists.
	 * @param bundle The bundle in this location.
	 * @param position The initial position of this location.
	 * @return The new or already existing location.
	 */
	public IPlace addLocation(IBundle bundle, Vector3f position)
	{
		// The initial position must be cloned so that 
		// the position can be moved without changing the position used for intialization.
		Vector3f p = new Vector3f(position);
		
		IPlace l = new place(bundle, p);
		
		int i = m_localSpace.indexOf(l);
		if (i == -1)
			// The location does not exist
			m_localSpace.add(l);
		else 
		{
			// The location already exists: return a pointer to it.
			l =  m_localSpace.get(i);
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
			if (act.getSchema().getLabel().equals(">") && !Ernest.STIMULATION_KINEMATIC_BUMP.equals(kinematicStimulation))
				translate(-1f);
			else if (act.getSchema().getLabel().equals("^"))
				rotate(- (float)Math.PI / 4);
			else if (act.getSchema().getLabel().equals("v"))
				rotate((float)Math.PI / 4);
		}
	}
	
	/**
	 * Rotate the local space of the given angle.
	 * @param angle The angle (provide the oposite angle from the agent's movement).
	 */
	private void rotate(float angle)
	{
		for (IPlace l : m_localSpace)
		{
			l.rotate(angle);
		}		
	}

	/**
	 * Translate the local space of the given distance.
	 * Remove locations that are left behind.
	 * @param distance The distance (provide a negative value to translate backwards).
	 */
	private void translate(float distance)
	{
		for (IPlace l : m_localSpace)
			l.translate(distance);
			
		for (Iterator it = m_localSpace.iterator(); it.hasNext();)
		{
			IPlace l = (IPlace)it.next();
			if (l.getPosition().x < - 1.5f)
				it.remove();
		}		
	}
	
	/**
	 * Remove all locations from the local space.
	 */
	public void clear()
	{
		m_localSpace.clear();
	}

	/**
	 * Get the bundle at a given position.
	 * @param position The position of the location.
	 * @return The bundle.
	 */
	public IBundle getBundle(Vector3f position)
	{
		for (IPlace l : m_localSpace)
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
	public void clearLocation(Vector3f position)
	{
		for (Iterator it = m_localSpace.iterator(); it.hasNext();)
		{
			IPlace l = (IPlace)it.next();
			if (l.isInCell(position))
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
		return getHexColor(getValue(position));
	}

	/**
	 * Get the value of the main bundle in a given position.
	 * or STIMULATION_VISUAL_UNSEEN in no bundle.
	 * @param position The position.
	 * @return The value.
	 */
	public int getValue(Vector3f position)
	{
		int c = Ernest.STIMULATION_VISUAL_UNSEEN.getValue();

		IBundle b = getBundle(position);
		if (b != null)
			c = b.getValue();
		return c;
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
