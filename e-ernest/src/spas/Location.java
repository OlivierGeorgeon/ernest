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
		return (Math.abs(m_position.x - position.x ) < .5f && Math.abs(m_position.y - position.y ) < .5f);		
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

}
