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

//	public void setDynamicFeature(String dynamicFeature) 
//	{
//		m_dynamicFeature = dynamicFeature; 
//	}

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

	public void setDynamicFeature(IObservation previousObservation)
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
		
		m_dynamicFeature = dynamicFeature;
		m_satisfaction = satisfaction;
	}

}
