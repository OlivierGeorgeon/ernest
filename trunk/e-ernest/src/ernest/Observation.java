package ernest;

import org.w3c.dom.Element;

/**
 * An element of Ernest's visual system.
 * @author Olivier
 */
public class Observation implements IObservation {

	private ILandmark m_landmark = null;
	private int m_distance = Ernest.INFINITE;
	private int m_direction = 0;
	private String m_dynamicFeature  = "";
	private int m_satisfaction = 0;
	private int m_motivation = 0;
	
	public void setLandmark(ILandmark landmark) 
	{
		m_landmark = landmark;
	}

	public ILandmark getLandmark() 
	{
		return m_landmark;
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
		Element e = tracer.addEventElement(element, "");
		if (m_landmark != null)
		{
			tracer.addSubelement(e, "color", m_landmark.getHexColor());
			tracer.addSubelement(e, "time_to_water", m_landmark.getDistanceToWater() + "");
			tracer.addSubelement(e, "time_to_food", m_landmark.getDistanceToFood() + "");
			tracer.addSubelement(e, "last_checked", m_landmark.getLastTimeChecked() + "");
			tracer.addSubelement(e, "distance", m_distance + "");
			tracer.addSubelement(e, "motivation", m_motivation + "");
			tracer.addSubelement(e, "direction", m_direction + "");
		}
		// There can be a disappear dynamic feature from the previous landmark
		tracer.addSubelement(e, "dynamic_feature", m_dynamicFeature);
		tracer.addSubelement(e, "satisfaction", m_satisfaction + "");
	}

	
	public int getMotivation()
	{
		return m_motivation;
	}

	public void setMotivation(int motivation)
	{
		m_motivation = motivation;
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
			if (previousObservation.getMotivation() > m_motivation)
				// Less motivating
				dynamicFeature = ".-";
			else if (previousObservation.getMotivation() == m_motivation)
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
			// The landmark is on now the left side
			if (previousObservation.getMotivation() > m_motivation)
				// Less motivating
				dynamicFeature = "-.";
			else if (previousObservation.getMotivation() == m_motivation)
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
			if (previousObservation.getMotivation() > m_motivation)
				// Less motivating
				dynamicFeature = "-";
			else if (previousObservation.getMotivation() == m_motivation)
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
