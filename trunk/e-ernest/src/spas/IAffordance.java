package spas;

import javax.vecmath.Vector3f;

/**
 * An affordance is an interaction afforded by a bundle.
 * The orientation and speeds are in a relative referential.
 * @author Olivier
 */
public interface IAffordance 
{
	String getLabel();
	
	float getDistance();
	
	float getOrientation();
	
	Vector3f getAgentSpeed();
	
	Vector3f getBundleSpeed();
	
	int getProclivity();
}
