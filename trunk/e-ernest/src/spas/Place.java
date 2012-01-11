package spas;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import ernest.Ernest;

/**
 * A place is a location in the local space that holds a bundle.
 * @author Olivier
 */
public class Place implements IPlace 
{

	IBundle m_bundle;
	Vector3f m_position;
	float m_span;
	int m_attractiveness;
	
	/**
	 * @param bundle This place's bundle.
	 * @param distance This place's distance.
	 * @param direction This place's direction. 
	 * @param span The span of this bundle at this place.
	 */
	public Place(IBundle bundle, float distance, float direction, float span)
	{
		m_bundle = bundle;
		m_position = new Vector3f((float)(distance * Math.cos((double)direction)), (float)(distance * Math.sin((double)direction)), 0f);
		m_span = span;
	}
	
	/**
	 * @param bundle The bundle at this place.
	 * @param position This place's position.
	 */
	public Place(IBundle bundle, Vector3f position)
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

	public void translate(Vector3f translation) 
	{
		m_position.add(translation);
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
			IPlace other = (IPlace)o;
			//ret = (other.getBundle() == m_bundle) && (m_position.epsilonEquals(other.getPosition(), LocalSpace.LOCATION_RADIUS));
			ret = isInCell(other.getPosition());
		}		
		return ret;
	}

	private float polarAngle(Vector3f v) 
	{
		return (float)Math.atan2((double)v.y, (double)v.x);
	}

	public float getDirection() 
	{
		return (float)Math.atan2((double)m_position.y, (double)m_position.x);
	}

	public float getDistance() 
	{
		return m_position.length();
	}
	public float getSpan() 
	{
		return m_span;
	}

	public void setSpan(float span) 
	{
		m_span = span;
	}
	
	public int getAttractiveness(int clock) 
	{
		// If it is in the background (visual place).
		if (getDistance() > LocalSpaceMemory.DISTANCE_VISUAL_BACKGROUND - 1)
			return (m_bundle.getExtrapersonalAttractiveness(clock) + (int)(5 * m_span / ((float)Math.PI / 12)));
		
		// Attractiveness of wall in front
		else if (isFrontal() && m_bundle.getTactileValue() == Ernest.STIMULATION_TOUCH_WALL)
			return m_bundle.getPeripersonalAttractiveness(clock);
		
		// Attractiveness of fish 
		else if (m_bundle.getTactileValue() == Ernest.STIMULATION_TOUCH_FISH 
				|| m_bundle.getTactileValue() == Ernest.STIMULATION_TOUCH_AGENT)// != Ernest.STIMULATION_TOUCH_EMPTY.getValue())
			return m_bundle.getPeripersonalAttractiveness(clock);

		return 0;
	}
	
	public boolean isFrontal()
	{
		// Covers at least a pixel to the right and a pixel to the left of Ernest's axis.
		return (getDirection() - m_span / 2 < - Math.PI/12 + 0.1 && getDirection() + m_span / 2 > Math.PI/12 - 0.1 );
	}

	public void setDistance(float distance)
	{
		m_position.normalize();
		m_position.scale(distance);
	}

	public void setPosition(Vector3f position) 
	{
		m_position = position;
	}

}
