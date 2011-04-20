package ernest;

import org.w3c.dom.Element;

import tracing.ITracer;

/**
 * An element of Ernest's visual system.
 * @author Olivier
 */
public class Observation implements IObservation {

	private int m_distance = Ernest.INFINITE;
	private int m_direction = 0;
	private String m_dynamicFeature  = "";
	private int m_satisfaction = 0;
	private int m_attractiveness = 0;
	private String m_hexColor = "";
	private int m_kinematic = 0;
	private int m_taste = 0;
	private String m_label;
	private int m_span;
	private IStimulation m_visual;

	
	public void setHexColor(String hexColor) 
	{
		m_hexColor = hexColor;
	}

	public String getHexColor() 
	{
		return m_hexColor;
	}

	public void setDistance(int distance) 
	{
		m_distance = distance;
	}

	public int getDistance() 
	{
		return m_distance;
	}

	public void setDirection(int position) 
	{
		m_direction = position;

	}

	public int getDirection() 
	{
		return m_direction;
	}

	public void setVisual(IStimulation stimulation) 
	{
		m_visual = stimulation;
	}

	public IStimulation getVisual() 
	{
		return m_visual;
	}

	public String getDynamicFeature() 
	{
		return m_dynamicFeature;
	}

	public void setSatisfaction(int satisfaction) 
	{
		m_satisfaction = satisfaction;
	}

	public int getSatisfaction() 
	{
		return m_satisfaction;
	}
	
	public void setKinematic(int kinematic)
	{
		m_kinematic = kinematic;
	}

	public int getKinematic()
	{
		return m_kinematic;
	}

	public void setTaste(int taste)
	{
		m_taste = taste;
	}

	public int getTaste()
	{
		return m_taste;
	}

	public String getLabel()
	{
		return m_label;
	}

	public int getSpan() 
	{
		return m_span;
	}

	public void setSpan(int span) 
	{
		m_span = span;
	}

	public void trace(ITracer tracer, String element) 
	{
		Object e = tracer.addEventElement(element);

		tracer.addSubelement(e, "color", m_hexColor);
		tracer.addSubelement(e, "distance", m_distance + "");
		tracer.addSubelement(e, "attractiveness", m_attractiveness + "");
		tracer.addSubelement(e, "direction", m_direction + "");
		tracer.addSubelement(e, "dynamic_feature", m_dynamicFeature);
		tracer.addSubelement(e, "satisfaction", m_satisfaction + "");
	}
	
	public int getAttractiveness()
	{
		return m_attractiveness;
	}

	public void setAttractiveness(int attractiveness)
	{
		m_attractiveness = attractiveness;
	}

	public void setDynamicFeature(IAct act, IObservation previousObservation)
	{
		String dynamicFeature = "";
		
		int minFovea = 25;
		int centerFovea = 55;
		int maxFovea = 85;
		
		if (minFovea >= m_direction)
		{
			// The landmark is now on the right side
			if (previousObservation.getAttractiveness() > m_attractiveness)
				// Less motivating
				dynamicFeature = ".-";
			else if (previousObservation.getAttractiveness() == m_attractiveness)
			{
				// As motivating
				if ( previousObservation.getDirection() >  m_direction)
					// The landmark is now more outward
					dynamicFeature = ".-";
				if ( m_direction > previousObservation.getDirection())
					// The landmark is now more inward
					dynamicFeature = ".+";
			}
			else
				// More motivating
				dynamicFeature = ".+";
		}
		else if (m_direction >= maxFovea)
		{
			// The landmark is now on the left side
			if (previousObservation.getAttractiveness() > m_attractiveness)
				// Less motivating
				dynamicFeature = "-.";
			else if (previousObservation.getAttractiveness() == m_attractiveness)
			{
				// As motivating
				if ( m_direction > previousObservation.getDirection())
					// The landmark is now more outward
					dynamicFeature = "-.";
				if ( previousObservation.getDirection() >  m_direction)
					// The landmark is now more inward
					dynamicFeature = "+.";
			}
			else
				// More motivating
				dynamicFeature = "+.";
		}
		else 
		{
			// The landmark is now in the fovea
			if (previousObservation.getAttractiveness() > m_attractiveness)
				// Less motivating
				dynamicFeature = "-";
			else if (previousObservation.getAttractiveness() == m_attractiveness)
			{
				// As motivating
				if ( Math.abs(previousObservation.getDirection() - centerFovea) > Math.abs(m_direction - centerFovea))
					// The landmark is now more inward
					dynamicFeature = "+";
				else if (m_direction > previousObservation.getDirection())
					// The landmark is now more outward to the left
					dynamicFeature = "-.";
				else if (previousObservation.getDirection() > m_direction)
					// The landmark is now more outward to the right
					dynamicFeature = ".-";
			}
			else
				// More motivating
				dynamicFeature = "+";
		}
		
		int satisfaction = 0;
		if (dynamicFeature.equals("-.") || dynamicFeature.equals(".-"))
			satisfaction = -100;
		if (dynamicFeature.equals("+.") || dynamicFeature.equals(".+"))
			satisfaction = 100;
		if (dynamicFeature.equals("+"))
			satisfaction = 150;
		if (dynamicFeature.equals("-"))
			satisfaction = -150;
		
		
		String label = dynamicFeature;
		if (act != null)
		{
			satisfaction = satisfaction + act.getSchema().resultingAct(m_kinematic == 1).getSatisfaction();
			label = act.getSchema().getLabel() + dynamicFeature;
		}
		
		// Label

		if (m_kinematic == 1)
			m_label = "(" + label + m_dynamicFeature + ")";
		else 
			m_label = "[" + label + m_dynamicFeature + "]";
		
		m_dynamicFeature = dynamicFeature;
		m_satisfaction = satisfaction;
	}

	public void setDynamicFeature2(IAct act, IObservation previousObservation)
	{
		String dynamicFeature = "";
		
		int minFovea = Ernest.CENTER_RETINA - 30; // 25;
		int maxFovea = Ernest.CENTER_RETINA + 30; // 85;
		
		// Attractiveness feature
		if (previousObservation.getAttractiveness() > m_attractiveness)
			// Farther
			dynamicFeature = "-";		
		else if (previousObservation.getAttractiveness() < m_attractiveness)
			// Closer
			dynamicFeature = "+";
		else if (Math.abs(previousObservation.getDirection() - Ernest.CENTER_RETINA ) < Math.abs(m_direction - Ernest.CENTER_RETINA))
			// More outward
			dynamicFeature = "-";
		else if (Math.abs(previousObservation.getDirection() - Ernest.CENTER_RETINA ) > Math.abs(m_direction - Ernest.CENTER_RETINA))
			// More inward
			dynamicFeature = "+";

		int satisfaction = 0;
		if (dynamicFeature.equals("-"))
			satisfaction = -100;
		if (dynamicFeature.equals("+"))
			satisfaction = 20;
		
		// Direction feature
		
		if (!dynamicFeature.equals(""))
		{
			if (minFovea >= m_direction)
				dynamicFeature = "|" + dynamicFeature;
			else if (m_direction >= maxFovea )
				dynamicFeature = dynamicFeature + "|";
		}		
		
		// Gustatory
		
		if (m_taste == Ernest.STIMULATION_TASTE_FISH)
		{
			dynamicFeature = "*";
			satisfaction = 200;
		}
		
		String label = dynamicFeature;
		if (act != null)
		{
			satisfaction = satisfaction + act.getSchema().resultingAct(m_kinematic == 1).getSatisfaction();
			label = act.getSchema().getLabel() + dynamicFeature;
		}
		
		// Label

		if (m_kinematic == 1)
			m_label = "(" + label + m_dynamicFeature + ")";
		else 
			m_label = "[" + label + m_dynamicFeature + "]";
		
		m_dynamicFeature = dynamicFeature;
		m_satisfaction = satisfaction;
	}
}
