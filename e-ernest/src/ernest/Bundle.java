package ernest;

import java.util.ArrayList;
import java.util.List;

/**
 * A bundle of sensory stimulations. 
 * So far, a bundle is defined by its visual and its tactile stimulation. 
 * Other stimulations are optional.
 * A bundle may correspond to a physical object according to Hume's bundle theory (http://en.wikipedia.org/wiki/Bundle_theory)
 * @author Olivier
 */
public class Bundle implements IBundle {

	IStimulation m_visualStimulation;
	IStimulation m_tactileStimulation;
	IStimulation m_gustatoryStimulation;

	int m_lastTimeBundled;
	
	Bundle(IStimulation visualStimulation, IStimulation tactileStimulation)
	{
		m_visualStimulation = visualStimulation;
		m_tactileStimulation = tactileStimulation;
	}
	
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
	
	public void setGustatoryStimulation(IStimulation  gustatoryStimulation) 
	{
		m_gustatoryStimulation = gustatoryStimulation;
	}
	
	public IStimulation getGustatoryStimulation() 
	{
		return m_gustatoryStimulation;
	}
	
	public void setLastTimeBundled(int clock)
	{
		m_lastTimeBundled = clock;
	}

	/**
	 * TOP_MOTIVATION (400) if this bundle's gustatory motivation is STIMULATION_TASTE_FISH.
	 * Otherwise BASE_MOTIVATION (200) if this bundle has been forgotten,
	 * or 0 if this bundle has just been visited.
	 * @param clock Ernest's current clock value.
	 * @return This bundle's attractiveness at the given time.
	 */
	public int getAttractiveness(int clock) 
	{
		if (m_gustatoryStimulation != null && m_gustatoryStimulation.getValue() == Ernest.STIMULATION_TASTE_FISH)
			return Ernest.TOP_MOTIVATION;
		else if (clock - m_lastTimeBundled > Ernest.PERSISTENCE)// && !m_visualStimulation.getColor().equals(Ernest.COLOR_WALL))
			return Ernest.BASE_MOTIVATION;
		else
			return 0;
	}

	/**
	 * Bundles are equal if they have the same visual and tactile stimulations. 
	 * TODO More than 3 stimulations.
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
				  other.getTactileStimulation().equals(m_tactileStimulation);
		}
		return ret;
	}
}
