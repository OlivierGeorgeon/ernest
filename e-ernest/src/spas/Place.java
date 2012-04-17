package spas;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import utils.ErnestUtils;

import ernest.Ernest;

/**
 * A place is a location in the local space that marks someting.
 * @author Olivier
 */
public class Place implements IPlace 
{

	IBundle m_bundle;
	Vector3f m_position;
	Vector3f m_speed;
	float m_span;
	int m_attractiveness;
	
	Vector3f m_firstPosition;
	Vector3f m_secondPosition;
	
	int m_type = Spas.PLACE_SEE;
	
	int m_shape = Spas.SHAPE_CIRCLE;
	float m_orientation = 0;
	
	int m_clock = 0;
	
	int m_stick;
	
	int m_value;
	
	String m_label;

	Vector3f m_simulatedPosition = new Vector3f();

	/**
	 * Create a new place 
	 * (The provided position is cloned so the place can be moved without changing the provided position).
	 * @param bundle The bundle at this place.
	 * @param position This place's position.
	 */
	public Place(IBundle bundle, Vector3f position)
	{
		m_bundle = bundle;
		m_position = new Vector3f(position);
	}
	
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
		
		oldPosition = m_firstPosition;
		rot.transform(oldPosition, m_firstPosition); 
		
		oldPosition = m_secondPosition;
		rot.transform(oldPosition, m_secondPosition); 
		
		// Rotate the orientation (used for display when the place has a shape)
		m_orientation += angle;
		if (m_orientation > Math.PI)
			m_orientation -= 2*Math.PI;		
		if (m_orientation < Math.PI)
			m_orientation += 2*Math.PI;		
	}

	public void translate(Vector3f translation) 
	{
		m_position.add(translation);
		m_firstPosition.add(translation);
		m_secondPosition.add(translation);
	}
	
	public boolean isInCell(Vector3f position)
	{
		boolean ret;
		// Is in the same cell.
		ret = (Math.round(m_position.x) == Math.round(position.x)) && (Math.round(m_position.y) == Math.round(position.y)); 
		
		// Is in the same cell in egocentric polar referential.
		
		// Does not work for the cell behind !!
//		if (m_position.length() < .5f && position.length() < .5f)
//			ret = true;
//		else if (Math.round(ErnestUtils.polarAngle(m_position) / (float)Math.PI * 4) ==
// 			     Math.round(ErnestUtils.polarAngle(  position) / (float)Math.PI * 4) &&
// 			     (Math.round(m_position.length()) == Math.round(position.length())))
//			ret = true;
//		else 
//			ret = false;
		
		return ret;		
	}

	/**
	 * Places are equal if they have the same position modulo the LOCATION_RADIUS.
	 * and the same updateCount 
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
			ret = isInCell(other.getPosition()) && other.getUpdateCount() == getUpdateCount() && other.getType() == getType();
		}		
		return ret;
	}

	public float getDirection() 
	{
		return ErnestUtils.polarAngle(m_position);
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
		if (m_bundle != null)
		{
			// Attractiveness of objects in the extrapersonal space.
			if (getDistance() > LocalSpaceMemory.EXTRAPERSONAL_DISTANCE)
				//return (m_bundle.getExtrapersonalAttractiveness(clock) + (int)(5 * m_span / ((float)Math.PI / 12)));
				return m_bundle.getExtrapersonalAttractiveness(clock) + (int)(20 - getDistance()) + m_stick;
			
			// Attractiveness of wall in front in peripersonal space.
			else if (isFrontal() && m_bundle.getTactileValue() == Ernest.STIMULATION_TOUCH_WALL)
				return m_bundle.getPeripersonalAttractiveness(clock) + m_stick;
	
			// Attractiveness of other things than wall in  peripersonal space. 
			//else if (m_bundle.getTactileValue() == Ernest.STIMULATION_TOUCH_FISH 
			//		|| m_bundle.getTactileValue() == Ernest.STIMULATION_TOUCH_AGENT)// != Ernest.STIMULATION_TOUCH_EMPTY.getValue())
			else if (m_bundle.getTactileValue() != Ernest.STIMULATION_TOUCH_WALL)
				return m_bundle.getPeripersonalAttractiveness(clock) + (int)(20 - getDistance()) + m_stick;
		}

		return 0;
	}
	
	public boolean isFrontal()
	{
		return (ErnestUtils.polarAngle(m_firstPosition) < 0 && ErnestUtils.polarAngle(m_secondPosition) > 0);
		
		// Covers at least a pixel to the right and a pixel to the left of Ernest's axis.
		//return (getDirection() - m_span / 2 < - Math.PI/12 + 0.1 && getDirection() + m_span / 2 > Math.PI/12 - 0.1 );
	}

	public void setPosition(Vector3f position) 
	{
		m_position = position;
	}

	public void setType(int type) 
	{
		m_type = type;
	}

	public int getType() 
	{
		return m_type;
	}

	public void setSpeed(Vector3f speed) 
	{
		m_speed = speed;
	}

	public Vector3f getSpeed() 
	{
		return m_speed;
	}

	public void setFirstPosition(Vector3f position) 
	{
		m_firstPosition = new Vector3f(position);
	}

	public void setSecondPosition(Vector3f position) 
	{
		m_secondPosition = new Vector3f(position);		
	}

	public Vector3f getFirstPosition() 
	{
		return m_firstPosition;
	}

	public Vector3f getSecondPosition() 
	{
		return m_secondPosition;
	}

	public void setUpdateCount(int clock) 
	{
		m_clock = clock;
	}

	public int getUpdateCount() 
	{
		return m_clock;
	}

	public boolean evokePhenomenon(int clock) 
	{
		boolean evokePhenomenon = true;
		
		if (m_bundle == null)  evokePhenomenon = false;
		if (m_type == Spas.PLACE_BUMP) evokePhenomenon = false; 	
		if (m_type == Spas.PLACE_EAT) evokePhenomenon = false; 	
		if (m_type == Spas.PLACE_CUDDLE) evokePhenomenon = false; 	
		//if (m_type == Spas.PLACE_BACKGROUND) attractFocus = false; 	
		//if (m_type == Spas.PLACE_PERSISTENT) attractFocus = false; 	
		
		if (m_type == Spas.PLACE_EVOKE_PHENOMENON) evokePhenomenon = true; 	
		if (m_type == Spas.PLACE_PHENOMENON)  evokePhenomenon = false;
		if (m_clock != clock) evokePhenomenon = false;
		
		return evokePhenomenon;
	}
	
	public boolean isPhenomenon()
	{
		boolean isPhenomenon = true;
		
		if (m_bundle == null)  isPhenomenon = false;
		if (m_type == Spas.PLACE_BUMP) isPhenomenon = false; 	
		if (m_type == Spas.PLACE_EAT) isPhenomenon = false; 	
		if (m_type == Spas.PLACE_CUDDLE) isPhenomenon = false; 	
		if (m_type == Spas.PLACE_BACKGROUND) isPhenomenon = false; 	
		//if (m_type == Spas.PLACE_BACKGROUND) attractFocus = false; 	
		//if (m_type == Spas.PLACE_PERSISTENT) attractFocus = false; 	
		if (m_type == Spas.PLACE_EVOKE_PHENOMENON)  isPhenomenon = false;
		
		if (m_type == Spas.PLACE_PHENOMENON)  isPhenomenon = true;
		
		return isPhenomenon;
	}

	public boolean from(Vector3f position) 
	{
		boolean from = false;
		Vector3f compare = new Vector3f(m_position);
		
		// TODO Should take the differential speed of mobile objects into account.
		// (not the speed due to Ernest's movement but the part that is due to the object's movement)
		//if (m_speed != null) compare.sub(m_speed);
		
		compare.sub(position);
		if (compare.length() < .2f) 
			from = true;
		
		return from;
	}

	public boolean from(IPlace previousPlace) 
	{
		boolean from = false;
		
		if (m_type == Spas.PLACE_SEE)
		{
			if (previousPlace.getType() == Spas.PLACE_SEE)
			{
				Vector3f compare = new Vector3f(m_position);
				compare.sub(previousPlace.getPosition());
				if (compare.length() < .2f) 
					from = true;				
			}
		}
		else if (m_type == Spas.PLACE_TOUCH)
		{
			if (previousPlace.getType() == Spas.PLACE_TOUCH)
			{
				if (Math.abs(getDirection() - previousPlace.getDirection()) < Math.PI/4)
					from = true;
			}
		}
		
		return from;
	}

	public void setStick(int stick) 
	{
		m_stick = stick;
	}

	public int getStick() 
	{
		return m_stick;
	}

	public void setShape(int shape) 
	{
		m_shape = shape;
	}

	public int getShape() 
	{
		return m_shape;
	}

	public void setOrientation(float orientation) 
	{
		m_orientation = orientation;
	}

	public float getOrientation() 
	{
		return m_orientation;
	}
	
	public float getFrontDistance()
	{
		return m_firstPosition.x - m_firstPosition.y * (m_firstPosition.x - m_secondPosition.x)/(m_firstPosition.y - m_secondPosition.y);
		
	}

	public void setValue(int value) 
	{
		m_value = value;
	}

	public int getValue() 
	{
		return m_value;
	}

	public void initSimulation() 
	{
		m_simulatedPosition.set(m_position);
	}

	public void translateSimulation(Vector3f translation) 
	{
		m_simulatedPosition.add(translation);
	}

	public void rotateSimulation(float angle) 
	{
		Matrix3f rot = new Matrix3f();
		rot.rotZ(angle);
		
		Vector3f oldPosition = m_simulatedPosition;
		rot.transform(oldPosition, m_simulatedPosition); // (rot * m_position) is placed into m_position
	}

	public boolean isInCellSimulation(Vector3f position) 
	{
		boolean ret;
		// Is in the same cell in egocentric Cartesian referential.
		ret = (Math.round(m_simulatedPosition.x) == Math.round(position.x)) && (Math.round(m_simulatedPosition.y) == Math.round(position.y)); 
		
		// Is in the same cell in egocentric polar referential.
//		if (m_position.length() < .5f && position.length() < .5f)
//			ret = true;
//		else if (Math.round(ErnestUtils.polarAngle(m_position) / (float)Math.PI * 4) ==
// 			     Math.round(ErnestUtils.polarAngle(  position) / (float)Math.PI * 4) &&
// 			     (Math.round(m_position.length()) == Math.round(position.length())))
//			ret = true;
//		else 
//			ret = false;
		
		return ret;		
	}

	public void setLabel(String label) 
	{
		m_label = label;
	}

	public String getLabel() 
	{
		return m_label;
	}
}
