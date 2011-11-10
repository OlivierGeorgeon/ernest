package spas;

import java.util.ArrayList;
import java.util.List;
import ernest.Ernest;
import ernest.ITracer;

/**
 * A bundle of sensory stimulations. 
 * So far, a bundle is defined by its visual and its tactile stimulation. 
 * Other stimulations are optional.
 * A bundle may correspond to a physical object according to Hume's bundle theory (http://en.wikipedia.org/wiki/Bundle_theory)
 * @author Olivier
 */
public class Bundle implements IBundle {

	//ISalience m_visualSalience;
	int m_visualValue;
	int m_tactileValue;
	int m_kinematicValue;
	int m_gustatoryValue;

	int m_lastTimeBundled;
	
	Bundle(int visualValue, int tactileValue, int kinematicValue, int gustatoryValue)
	{
		m_visualValue = visualValue;
		m_tactileValue = tactileValue;
		m_kinematicValue = kinematicValue;
		m_gustatoryValue = gustatoryValue;
	}
	
	public int getValue()
	{
		int value = 0;
		if (m_visualValue == Ernest.STIMULATION_VISUAL_UNSEEN.getValue())
			value = m_tactileValue;
		else 
			value = m_visualValue;
		
		return value;
	}
	
//	public String getHexColor() 
//	{
//		if (m_visualValue == Ernest.STIMULATION_VISUAL_UNSEEN.getValue())
//			return hexColor(m_tactileValue);
//		else 
//			return hexColor(m_visualValue);
//	}
	
	public int getVisualValue() 
	{
		return m_visualValue;
	}
	
	public void setVisualValue(int visualValue) 
	{
		m_visualValue = visualValue;
	}
	
	public int getTactileValue() 
	{
		return m_tactileValue;
	}
	public void setGustatoryValue(int  gustatoryValue) 
	{
		m_gustatoryValue = gustatoryValue;
	}
	
	public int getGustatoryValue() 
	{
		return m_gustatoryValue;
	}
	
	public void setKinematicValue(int kinematicValue)
	{
		m_kinematicValue = kinematicValue;
	}
	
	public int getKinematicValue()
	{
		return m_kinematicValue;
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
		// If the bundle has a kinematic stimulation of bump.
		if (m_kinematicValue == Ernest.STIMULATION_KINEMATIC_BUMP.getValue())
			return Ernest.ATTRACTIVENESS_OF_BUMP;

		// If the bundle has a tactile stimulation of hard.
		if (m_tactileValue == Ernest.STIMULATION_TOUCH_WALL.getValue())
			return Ernest.ATTRACTIVENESS_OF_HARD - 10; // prefer a bundle salience than a mere touch salience.

		// The bundle of touching a fish
		//if (m_visualStimulation.equals(PersistenceSystem.BUNDLE_GRAY_FISH.getVisualStimulation()))
		//	return Ernest.ATTRACTIVENESS_OF_FISH + 10;
		
		// If the bundle has a gustatory stimulation of fish 
		else if (m_gustatoryValue == Ernest.STIMULATION_GUSTATORY_FISH.getValue())
		{
			if (m_visualValue == Ernest.STIMULATION_VISUAL_UNSEEN.getValue())
				// Fish that are touched are more attractive 
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
	 * Bundles are equal if they have the same visual, tactile, kinematic, and gustatory values. 
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
			ret = (other.getVisualValue() == m_visualValue) && 	
				  (other.getTactileValue() == m_tactileValue);
				  //(other.getKinematicValue() == m_kinematicValue) &&
				  //(other.getGustatoryValue() == m_gustatoryValue);
		}
		return ret;
	}

	/**
	 * Traces the bundle 
	 */
	public void trace(ITracer tracer, String label)
	{
		Object element = tracer.addEventElement(label);
		
		// Visual stimulation
		tracer.addSubelement(element, "visual", hexColor(m_visualValue));
		
		// Only trace fish gustatory stimulations.
		if (m_gustatoryValue == Ernest.STIMULATION_GUSTATORY_FISH.getValue())
			tracer.addSubelement(element, "gustatory", hexColor(m_gustatoryValue));
		else
			tracer.addSubelement(element, "gustatory", hexColor(m_visualValue));
		
		// Tactile stimulation
		tracer.addSubelement(element, "tactile", hexColor(m_tactileValue));

		// Only trace bump kinematic stimulation.
		if (m_kinematicValue == Ernest.STIMULATION_KINEMATIC_BUMP.getValue())
			tracer.addSubelement(element, "kinematic", hexColor(m_kinematicValue));
		else
			tracer.addSubelement(element, "kinematic", hexColor(m_tactileValue));
		}

	private String hexColor(int value) 
	{
		int r = value/65536;
		int g = (value - r * 65536)/256;
		int b = value - r * 65536 - g * 256;
		String s = format(r) + format(g) + format(b);

		return s;
	}
	
	private String format(int i)
	{
		if (i == 0)
			return "00";
		else if (i < 16)
			return "0" + Integer.toString(i, 16).toUpperCase();
		else
			return Integer.toString(i, 16).toUpperCase();
	}
}
