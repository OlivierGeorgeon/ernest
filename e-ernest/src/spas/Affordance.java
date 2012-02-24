package spas;

import javax.vecmath.Vector3f;

/**
 * An affordance is an interaction afforded by a bundle.
 * The orientation and speeds are in a relative referential.
 * @author Olivier
 */
public class Affordance implements IAffordance 
{
	private String m_label;
	private float m_distance;
	private float m_orientation;
	private Vector3f m_agentSpeed;
	private Vector3f m_bundleSpeed;
	private int m_proclivity;
	
	Affordance(String label, float distance, float orientation, Vector3f agentSpeed, Vector3f bundleSpeed, int proclivity)
	{
		m_label = label;
		m_distance = distance;
		m_orientation = orientation;
		m_agentSpeed.set(agentSpeed);
		m_bundleSpeed.set(bundleSpeed);
		m_proclivity = proclivity;
	}

	public String getLabel()
	{
		return m_label;
	}
	
	public float getDistance() 
	{
		return m_distance;
	}

	public float getOrientation() 
	{
		return m_orientation;
	}

	public Vector3f getAgentSpeed() 
	{
		return m_agentSpeed;
	}

	public Vector3f getBundleSpeed() 
	{
		return m_bundleSpeed;
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
		float epsilon = .5f;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			IAffordance other = (IAffordance)o;
			ret = other.getLabel().equals(m_label) && 	
				  other.getDistance() > m_distance - epsilon && other.getDistance() < m_distance + epsilon &&
				  other.getOrientation() > m_orientation - epsilon && other.getOrientation() < m_orientation + epsilon &&
				  other.getAgentSpeed().epsilonEquals(m_agentSpeed,epsilon) &&
				  other.getBundleSpeed().epsilonEquals(m_bundleSpeed, epsilon);
		}
		return ret;
	}


}
