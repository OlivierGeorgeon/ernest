package ernest;

import org.w3c.dom.Element;

public class Observation implements IObservation {

	private ILandmark m_landmark = null;
	private int m_distance = Ernest.INFINITE;
	private int m_direction = 0;
	private String m_dynamicFeature  = "";
	private int m_satisfaction = 0;
	
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

	public void setDynamicFeature(String dynamicFeature) 
	{
		m_dynamicFeature = dynamicFeature; 
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

	public void trace(ITracer tracer, String element) 
	{
		Element e = tracer.addEventElement(element, "");
		tracer.addSubelement(e, "color", m_landmark.getHexColor());
		tracer.addSubelement(e, "time_to_water", m_landmark.getDistanceToWater() + "");
		tracer.addSubelement(e, "time_to_food", m_landmark.getDistanceToFood() + "");
		tracer.addSubelement(e, "last_checked", m_landmark.getLastTimeChecked() + "");
		tracer.addSubelement(e, "distance", m_distance + "");
		tracer.addSubelement(e, "dynamic_feature", m_dynamicFeature);
		tracer.addSubelement(e, "satisfaction", m_satisfaction + "");
		tracer.addSubelement(e, "retinotopic_direction", m_direction + "");
	}

}
