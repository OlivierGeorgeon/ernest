package persistence;

import java.util.ArrayList;
import java.util.List;
import ernest.Ernest;
import ernest.ISalience;
import ernest.ITracer;

/**
 * A bundle of sensory stimulations. 
 * So far, a bundle is defined by its visual and its tactile stimulation. 
 * Other stimulations are optional.
 * A bundle may correspond to a physical object according to Hume's bundle theory (http://en.wikipedia.org/wiki/Bundle_theory)
 * @author Olivier
 */
public class Bundle implements IBundle {

	ISalience m_visualSalience;
	IStimulation m_visualStimulation;
	IStimulation m_tactileStimulation;
	IStimulation m_gustatoryStimulation;
	IStimulation m_kinematicStimulation;

	int m_lastTimeBundled;
	
	/**
	 * Create a bundle with a visual and a tactile stimulation.
	 * @param visualStimulation The visual stimulation.
	 * @param tactileStimulation The tactile stimulation.
	 * @return The bundle.
	 */
	public static IBundle createVisioTactileBundle(IStimulation visualStimulation, IStimulation tactileStimulation)
	{
		return new Bundle(visualStimulation, tactileStimulation, Ernest.STIMULATION_KINEMATIC_FORWARD, Ernest.STIMULATION_GUSTATORY_NOTHING);
	}
	
	/**
	 * Create a bundle with a tactile and a gustatory stimulation.
	 * @param tactileStimulation The tactile stimulation.
	 * @param gustatoryStimulation The gustatory stimulation.
	 * @return The bundle
	 */
	public static IBundle createTactoGustatoryBundle(IStimulation tactileStimulation, IStimulation gustatoryStimulation)
	{
		return new Bundle(Ernest.STIMULATION_VISUAL_UNSEEN, tactileStimulation, Ernest.STIMULATION_KINEMATIC_FORWARD, gustatoryStimulation);
	}
	
	Bundle(IStimulation visualStimulation, IStimulation tactileStimulation, IStimulation kinematicStimulation, IStimulation gustatoryStimulation)
	{
		m_visualStimulation = visualStimulation;
		m_tactileStimulation = tactileStimulation;
		m_kinematicStimulation = kinematicStimulation;//Ernest.STIMULATION_KINEMATIC_FORWARD;
		m_gustatoryStimulation = gustatoryStimulation; //Ernest.STIMULATION_GUSTATORY_NOTHING;
	}
	
	public int getValue()
	{
		int value = 0;
		if (m_visualStimulation.equals(Ernest.STIMULATION_VISUAL_UNSEEN))
			value = m_tactileStimulation.getValue();
		else 
			value = m_visualStimulation.getValue();
		
		return value;
	}
	
	public String getHexColor() 
	{
		String s = "";
		if (m_visualStimulation.equals(Ernest.STIMULATION_VISUAL_UNSEEN))
			s = m_tactileStimulation.getHexColor();
		else 
			s = m_visualStimulation.getHexColor();
			
		return s;
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
		// Bundles where Ernest bumps are not attractive.
		if (Ernest.STIMULATION_KINEMATIC_BUMP.equals(m_kinematicStimulation))
			return 0;

		// The bundle of touching a fish
		//if (m_visualStimulation.equals(PersistenceSystem.BUNDLE_GRAY_FISH.getVisualStimulation()))
		//	return Ernest.ATTRACTIVENESS_OF_FISH + 10;
		
		// The bundle 
		else if (Ernest.STIMULATION_GUSTATORY_FISH.equals(m_gustatoryStimulation))
		{
			if (m_visualStimulation.equals(Ernest.STIMULATION_VISUAL_UNSEEN))
				// Fish that are touched are more attractive 
				// TODO see how to change that.
				return Ernest.ATTRACTIVENESS_OF_FISH + 10;
			else
				return Ernest.ATTRACTIVENESS_OF_FISH;
		}
		
		else if (clock - m_lastTimeBundled > Ernest.PERSISTENCE)// && !m_visualStimulation.getColor().equals(Ernest.COLOR_WALL))
			return Ernest.ATTRACTIVENESS_OF_UNKNOWN ;
		
		else
			return 0;
	}

	/**
	 * Bundles are equal if they have the same visual and tactile stimulations. 
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
			ret = other.getVisualStimulation().equals(m_visualStimulation) && 	
				  other.getTactileStimulation().equals(m_tactileStimulation);
		}
		return ret;
	}

	/**
	 * Traces the bundle 
	 */
	public void trace(ITracer tracer, String label)
	{
		Object element = tracer.addEventElement(label);
		tracer.addSubelement(element, "color", getHexColor());
		tracer.addSubelement(element, "tactile", getTactileStimulation().getValue() + "");
		if (getKinematicStimulation() != null)
			tracer.addSubelement(element, "kinematic", getKinematicStimulation().getValue() + "");
		if (getGustatoryStimulation() != null)
			tracer.addSubelement(element, "gustatory", getGustatoryStimulation().getValue() + "");
		
	}

}
