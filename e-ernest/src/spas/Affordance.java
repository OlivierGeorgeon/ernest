package spas;

import imos.IAct;

import javax.vecmath.Vector3f;

/**
 * An affordance is an interaction afforded by a bundle.
 * The orientation and speeds are in a relative referential.
 * @author Olivier
 */
public class Affordance implements IAffordance 
{
	private IAct m_act;
	private IPlace m_place;
	private int m_proclivity;
	private int m_value;
	
	Affordance(IAct act, IPlace place, int proclivity, int value)
	{
		m_act = act;
		m_place = place;
		m_proclivity = proclivity;
		m_value = value;
	}

	public IAct getAct()
	{
		return m_act;
	}
	
	public IPlace getPlace()
	{
		return m_place;
	}
	
	public int getProclivity() 
	{
		return m_proclivity;
	}

	/**
	 * Defines the affordance equality measure
	 * TODO: This should be learned. 
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		float epsilon = .1f;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			IAffordance other = (IAffordance)o;
			ret = other.getAct().getSchema().equals(m_act.getSchema()) &&
				  other.getPlace().getPosition().epsilonEquals(m_place.getPosition(), epsilon);
				  //other.getDistance() > m_distance - epsilon && other.getDistance() < m_distance + epsilon &&
				  //other.getOrientation() > m_orientation - epsilon && other.getOrientation() < m_orientation + epsilon &&
				  //other.getAgentSpeed().epsilonEquals(m_agentSpeed,epsilon) &&
				  //other.getBundleSpeed().epsilonEquals(m_bundleSpeed, epsilon);
		}
		return ret;
	}

	public void setValue(int value) 
	{
		m_value = value;
	}

	public int getValue() 
	{
		return m_value;
	}

}
