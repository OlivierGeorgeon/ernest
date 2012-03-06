package spas;

import imos.IAct;

import javax.vecmath.Vector3f;

/**
 * An affordance is an interaction afforded by a bundle.
 * The orientation and speeds are in a relative referential.
 * @author Olivier
 */
public interface IAffordance 
{
	IAct getAct();
	
	IPlace getPlace();
	
	int getProclivity();
	
	public void setValue(int value);
	
	public int getValue();
	
	//float getDistance();
	
	//float getOrientation();
	
	//Vector3f getAgentSpeed();
	
	//Vector3f getBundleSpeed();
	
}
