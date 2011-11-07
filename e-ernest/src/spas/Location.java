package spas;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

/**
 * A location is a place in the local space that was associated with a bundle.
 * @author Olivier
 */
public class Location implements ILocation 
{

	IBundle m_bundle;
	Vector3f m_position;
	
	/**
	 * @param bundle The location's bundle.
	 * @param position The location's position.
	 */
	public Location(IBundle bundle, Vector3f position)
	{
		m_bundle = bundle;
		m_position = position;
	}
	
	public void setBundle(IBundle bundle) 
	{
		m_bundle = bundle;
	}

	public IBundle getBundle() 
	{
		return m_bundle;
	}

	public Vector3f getPosition() 
	{
		return m_position;
	}
	
	public void rotate(float angle)
	{
		Matrix3f rot = new Matrix3f();
		rot.rotZ(angle);
		
		Vector3f oldPosition = m_position;
		rot.transform(oldPosition, m_position); // (rot * m_position) is placed into m_position
		//parentVec.add(new Vector3f(m_x, m_y, 0));
		//parentVec.add(mPosition); // now parentVec = (rotZ(mOrientation.z) * localVec) + mPosition.
		
	}

	public void translate(float distance) 
	{
		m_position.add(new Vector3f (distance, 0, 0));
	}
	
	public boolean isInCell(Vector3f position)
	{
		boolean ret;
		// Is in the same cell in egocentric Cartesian referential.
		//ret = (Math.round(m_position.x) == Math.round(position.x)) && (Math.round(m_position.y) == Math.round(position.y)); 
		
		// Is in the same cell in egocentric polar referential.
		if (m_position.length() < .5f && position.length() < .5f)
			ret = true;
		else if (Math.round(polarAngle(m_position) / (float)Math.PI * 4) ==
 			     Math.round(polarAngle(  position) / (float)Math.PI * 4) &&
 			     (Math.round(m_position.length()) == Math.round(position.length())))
			ret = true;
		else 
			ret = false;
		
		return ret;		
	}

	/**
	 * Locations are equal if they are in the same grid cell (in egocentric referential). 
	 * have the same bundle at the same position modulo the LOCATION_RADIUS. 
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			ILocation other = (ILocation)o;
			//ret = (other.getBundle() == m_bundle) && (m_position.epsilonEquals(other.getPosition(), LocalSpace.LOCATION_RADIUS));
			ret = isInCell(other.getPosition());
		}		
		return ret;
	}

	private float polarAngle(Vector3f v) 
	{
		return (float)Math.atan2((double)v.y, (double)v.x);
	}

}
