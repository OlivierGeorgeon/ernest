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

	EColor m_color;
	ISalience m_visualSalience;
	IStimulation m_tactileStimulation;
	IStimulation m_gustatoryStimulation;
	IStimulation m_kinematicStimulation;

	int m_lastTimeBundled;
	
	Bundle(EColor color, IStimulation tactileStimulation)
	{
		m_color = color;
		m_tactileStimulation = tactileStimulation;
		m_gustatoryStimulation = Ernest.STIMULATION_GUSTATORY_NOTHING;
		m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_FORWARD;
	}
	
	public EColor getColor() 
	{
		return m_color;
	}
	
	public String getHexColor() 
	{
//		String s = String.format("%06X", m_color.getRGB()  & 0x00ffffff); 
		String s = m_color.getHexCode();
		return s;
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
	
	public void setKinematicStimulation(IStimulation kinematicStimulation)
	{
		m_kinematicStimulation = kinematicStimulation;
	}
	
	public IStimulation getKinematicStimulation()
	{
		return m_kinematicStimulation;
	}
	
	public void setLastTimeBundled(int clock)
	{
		m_lastTimeBundled = clock;
	}

	/**
	 * ATTRACTIVENESS_OF_FISH (400) if this bundle's gustatory stimulation is STIMULATION_TASTE_FISH.
	 * ATTRACTIVENESS_OF_FISH + 10 (410) if the fish is touched.
	 * Otherwise ATTRACTIVENESS_OF_UNKNOWN (200) if this bundle has been forgotten,
	 * or 0 if this bundle has just been visited.
	 * @param clock Ernest's current clock value.
	 * @return This bundle's attractiveness at the given time.
	 */
	public int getAttractiveness(int clock) 
	{
		if (m_color.equals(Ernest.BUNDLE_GRAY.getColor()))
			return Ernest.ATTRACTIVENESS_OF_FISH + 10;
		
		else if (m_gustatoryStimulation != null && m_gustatoryStimulation.equals(Ernest.STIMULATION_GUSTATORY_FISH))
			return Ernest.ATTRACTIVENESS_OF_FISH;
		else if (clock - m_lastTimeBundled > Ernest.PERSISTENCE)// && !m_visualStimulation.getColor().equals(Ernest.COLOR_WALL))
			return Ernest.ATTRACTIVENESS_OF_UNKNOWN;
		else
			return 0;
	}

	/**
	 * Bundles are equal if they have the same color and tactile stimulations. 
	 * TODO also test other stimulations.
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
			ret = other.getColor().equals(m_color) && 	
				  other.getTactileStimulation().equals(m_tactileStimulation);
		}
		return ret;
	}

}
