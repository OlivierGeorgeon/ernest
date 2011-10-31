package spas;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import ernest.Ernest;

/**
 * The local space structure maintaines an awareness of the bundle surrounding Ernest. 
 * @author Olivier
 */
public class LocalSpaceMemory 
{
	
	/** The radius of any given location. */
	public static float LOCATION_RADIUS = 0.5f;
	
	/** The Local space structure. */
	private List<ILocation> m_localSpace = new ArrayList<ILocation>();
	
	/**
	 * Add a new location to the localSpace if it does not yet exist.
	 * @param bundle The bundle in this location.
	 * @param position The position of this location.
	 * @return The new or already existing location.
	 */
	public ILocation addLocation(IBundle bundle, Vector3f position)
	{
		ILocation l = new Location(bundle, position);
		int i = m_localSpace.indexOf(l);
		if (i == -1)
			// The location does not exist
			m_localSpace.add(l);
		else 
			// The location already exists: return a pointer to it.
			l =  m_localSpace.get(i);
		return l;
		
	}
	
	/**
	 * Rotate the local space of the given angle.
	 * @param angle The angle.
	 */
	public void rotate(float angle)
	{
		for (ILocation l : m_localSpace)
		{
			l.rotate(angle);
		}		
	}

	/**
	 * Translate the local space of the given distance.
	 * @param distance The distance.
	 */
	public void translate(float distance)
	{
		for (ILocation l : m_localSpace)
		{
			l.translate(distance);
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
		for (ILocation l : m_localSpace)
		{
			if (l.isInCell(position))
				return l.getBundle();
		}		

		return null;
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
