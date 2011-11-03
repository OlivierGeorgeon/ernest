package spas;


/**
 * An observation holds the significant consequences that the enacted interaction had on the spatial system.
 * It is the structure that supports the interaction between the spatial system (spas) 
 * and the intrinsic motivation system (imos).
 * @author Olivier
 */
public interface IObservation 
{
	/**
	 * @param direction The direction of the salience of current attention.
	 * Radian trigonometric in egocentric referential.
	 */
	public void setDirection(float direction);
	
	/**
	 * @return The direction of the salience of current attention.
	 * Radian trigonometric in egocentric referential.
	 */
	public float getDirection();
	
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
	void setKinematic(IStimulation kinematicStimulation);
	
	/**
	 * @return This observation's kinematic stimulation
	 */
	IStimulation getKinematicStimulation();
	
	/**
	 * @param gustatoryStimulation The gustatory stimulation;
	 */
	public void setGustatory(IStimulation gustatoryStimulation);
	
	/**
	 * @return The gustatory stimulation;
	 */
	public IStimulation getGustatoryStimulation();
	
}
