package spas;

import ernest.Ernest;

/**
 * An observation holds the significant consequences that the enacted interaction had on the spatial system.
 * It is the structure that supports the interaction between the spatial system (spas) 
 * and the intrinsic motivation system (imos).
 * @author Olivier
 */
public class Observation implements IObservation 
{

	/** The Direction of Ernest's interest. */
	//private float m_direction = Ernest.CENTER_RETINA;
	
	//private float m_distance = 1;
	//private float m_span = 0;
	private IPlace m_place = null;

	/** The attractiveness of Ernest's interest. */
	private int m_attractiveness = 0;

	/** The kinematic stimulation. */
	private IStimulation m_kinematicStimulation;
	
	/** The gustatory stimulation. */
	private IStimulation m_gustatoryStimulation;

	public void setAttractiveness(int attractiveness) 
	{
		m_attractiveness = attractiveness;
	}

	public int getAttractiveness() 
	{
		return m_attractiveness;
	}

	public void setKinematic(IStimulation kinematicStimulation)
	{
		m_kinematicStimulation = kinematicStimulation;
	}

	public IStimulation getKinematicStimulation()
	{
		return m_kinematicStimulation;
	}

	public void setGustatory(IStimulation gustatoryStimulation)
	{
		m_gustatoryStimulation = gustatoryStimulation;
	}
	
	public IStimulation getGustatoryStimulation()
	{
		return m_gustatoryStimulation;
	}

	public void setPlace(IPlace place) 
	{
		m_place = place;
	}

	public IPlace getPlace() 
	{
		return m_place;
	}
}
