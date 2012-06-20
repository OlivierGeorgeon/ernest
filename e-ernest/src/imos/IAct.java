package imos;

import javax.vecmath.Vector3f;




/**
 * An act is the association of a schema with the feedback the agent receives 
 * when trying to enact that schema.    
 * The term Act and the term interaction are used indifferently.  
 * @author mcohen
 * @author ogeorgeon
 */
public interface IAct  extends Comparable<IAct>
{
	/**
	 * @return The act's enaction status. True for success, false for failure.
	 */
	public boolean getStatus();
	
	/**
	 * @return The act's string representation.
	 */
	public String getLabel();
	
	/**
	 * @return The act's satisfaction value.
	 */
	public int getSatisfaction();
	
	/**
	 * @param s The act's satisfaction value.
	 */
	public void setSatisfaction(int s);
	
	/**
	 * @param c The act's confidence status.
	 */
	public void setConfidence(int c);
	
	/**
	 * @param s The schema that prescribes this act during enaction.
	 */
	public void setPrescriberSchema(ISchema s);
	
	/**
	 * @param a The act's activation value
	 */
	public void setActivation(int a);
	
	/**
	 * @return The schema that prescribed this act during enaction.
	 */
	public ISchema getPrescriberSchema();
	
	/**
	 * @return the act's schema.
	 */
	public ISchema getSchema();

	/**
	 * @return The confidence status .
	 */
	public int getConfidence();

	/**
	 * @return The act's activation value.
	 */
	public int getActivation();

	/**
	 * @return The act's length (eigher its schema's length or 1 if the act has no schema).
	 */
	public int getLength();
	
	/**
	 * Methods related to space.
	 * @param bundleCode The phenomenon's code.
	 */	
	public void setPhenomenon(int bundleCode);
	
	/**
	 * @return the phenomenon's code.
	 */
	public int getPhenomenon();
	
	/**
	 * @param position The position of the phenomena that is concerned by the enaction of this act.
	 */
	public void setEndPosition(Vector3f position);
	
	/**
	 * @return The position of the phenomena that is concerned by the enaction of this act.
	 */
	public Vector3f getEndPosition();
	
	public void setStartPosition(Vector3f position);
	public Vector3f getStartPosition();
	
	//public void setTranslation(Vector3f translation);
	public Vector3f getTranslation();
	
	public void setRotation(float rotation);
	public float getRotation();
	
}
