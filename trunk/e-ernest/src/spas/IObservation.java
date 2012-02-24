package spas;

import javax.vecmath.Vector3f;


/**
 * An observation holds the significant consequences that the enacted interaction had on the spatial system.
 * It is the structure that supports the interaction between the spatial system (spas) 
 * and the intrinsic motivation system (imos).
 * @author Olivier
 */
public interface IObservation 
{
	/**
	 * @param position The position of the focus place.
	 */
	public void setPosition(Vector3f position);
	public void setSpan(float span);
	
	/**
	 * @return The direction of the focus place.
	 */
	public Vector3f getPosition(); 
	public float getDirection();
	public float getDistance();
	public float getSpan();
	public void setBundle(IBundle bundle);
	public IBundle getBundle();
	public void setSpeed(Vector3f speed);
	public Vector3f getSpeed();
	
	public void setInstantaneousFeedback(String instantaneousFeedback);
	public void setStimuli(String stimuli);
	
	public String getInstantaneousFeedback();
	public String getStimuli();
	
	/**
	 * @param attractiveness The attractiveness of the salience of current attention.
	 */
	public void setAttractiveness(int attractiveness);
	
	/**
	 * @return The attractiveness of the salience of current attention.
	 */
	public int getAttractiveness();
	
	/**
	 * @param kinematicStimulation
	 */
	public void setKinematicValue(int value);
	
	/**
	 * @return This observation's kinematic stimulation
	 */
	public int getKinematicValue();
	
	/**
	 * @param gustatoryStimulation The gustatory stimulation;
	 */
	public void setGustatoryValue(int value);
	
	/**
	 * @return The gustatory stimulation;
	 */
	public int getGustatoryValue();
	
	public void setType(int type);
	public int getType();
	
	public void setUpdateCount(int updateCount);
	public int getUpdateCount();
	
	public void setNewFocus(boolean newFocus);
	public boolean getNewFocus();
	
	public void setTranslation(Vector3f translation);
	public Vector3f getTranslation();
	
	public void setRotation(float rotation);
	public float getRotation();
}
