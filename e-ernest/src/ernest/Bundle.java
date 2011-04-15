package ernest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olivier
 * TODO bundles with more than 3 stimulations
 */
public class Bundle implements IBundle {

	IStimulation m_visualStimulation;
	IStimulation m_tactileStimulation;
	IStimulation m_gustatoryStimulation;

	int m_lastTimeBundled;
	
	Bundle(IStimulation visualStimulation, IStimulation tactileStimulation, IStimulation gustatoryStimulation)
	{
		m_visualStimulation = visualStimulation;
		m_tactileStimulation = tactileStimulation;
		m_gustatoryStimulation = gustatoryStimulation;
	}
	
	public IStimulation getVisualStimulation() 
	{
		return m_visualStimulation;
	}
	
	public IStimulation getTactileStimulation() 
	{
		return m_tactileStimulation;
	}
	
	public IStimulation getGustatoryStimulation() 
	{
		return m_gustatoryStimulation;
	}
	
	public void setLastTimeBundled(int clock)
	{
		m_lastTimeBundled = clock;
	}

	public int getAttractiveness(int clock) 
	{
		if (m_gustatoryStimulation.getValue() == Ernest.STIMULATION_TASTE_FISH)
			return Ernest.TOP_MOTIVATION;
		else if (clock - m_lastTimeBundled > Ernest.PERSISTENCE)// && !m_visualStimulation.getColor().equals(Ernest.COLOR_WALL))
			return Ernest.BASE_MOTIVATION;
		else
			return 0;
	}

	/**
	 * Bundles are equal if they have the same stimulations. 
	 * TODO More than 3 stimulations
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
			IBundle other = (IBundle)o;
			ret = other.getVisualStimulation().equals(m_visualStimulation) && 	
				  other.getTactileStimulation().equals(m_tactileStimulation) &&
				  other.getGustatoryStimulation().equals(m_gustatoryStimulation);
		}
		return ret;
	}
}
