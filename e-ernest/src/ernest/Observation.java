package ernest;

import java.awt.Color;

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
	private int m_kinematic = Ernest.STIMULATION_KINEMATIC_SUCCEED;
	private IStimulation m_taste;
	private String m_label;
	private int m_span;
	private IStimulation m_visual;

	// The map of tactile stimulations
	IStimulation[][] m_tactileMatrix = new IStimulation[3][3];
	
	// The map of surrounding bundles 
	IBundle[][] m_bundle = new IBundle[3][3];
	
	public String getHexColor() 
	{
		return m_visual.getHexColor();
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

	public void taste(IStimulation gustatoryStimulation)
	{
		m_taste = gustatoryStimulation;
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

		tracer.addSubelement(e, "color", m_visual.getHexColor());
		tracer.addSubelement(e, "distance", m_distance + "");
		tracer.addSubelement(e, "attractiveness", m_attractiveness + "");
		tracer.addSubelement(e, "direction", m_direction + "");
		tracer.addSubelement(e, "dynamic_feature", m_dynamicFeature);
		tracer.addSubelement(e, "satisfaction", m_satisfaction + "");
		tracer.addSubelement(e, "span", m_span + "");
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
		// Transform the bundle area
		if (act != null)
		{
			if (act.getSchema().getLabel().equals(">"))
			{
				if (m_kinematic == Ernest.STIMULATION_KINEMATIC_FAIL)
					copy(previousObservation);
				else
					forward(previousObservation);
			}
			if (act.getSchema().getLabel().equals("^"))
				turnLeft(previousObservation);
			if (act.getSchema().getLabel().equals("v"))
				turnRight(previousObservation);
		}		
		
		// Taste
		if (m_taste.getValue() == Ernest.STIMULATION_TASTE_FISH && m_bundle[1][1] != null)
		{
			m_bundle[1][1].setGustatoryStimulation(m_taste);
			m_bundle[1][1] = null;
		}
			
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
		
		if (m_taste.getValue() == Ernest.STIMULATION_TASTE_FISH)
		{
			dynamicFeature = "*";
			satisfaction = 200;
		}
		
		String label = dynamicFeature;
		if (act != null)
		{
			satisfaction = satisfaction + act.getSchema().resultingAct(m_kinematic == Ernest.STIMULATION_KINEMATIC_SUCCEED).getSatisfaction();
			label = act.getSchema().getLabel() + dynamicFeature;
		}
		
		// Label

		if (m_kinematic == Ernest.STIMULATION_KINEMATIC_SUCCEED)
			m_label = "(" + label + m_dynamicFeature + ")";
		else 
			m_label = "[" + label + m_dynamicFeature + "]";
		
		m_dynamicFeature = dynamicFeature;
		m_satisfaction = satisfaction;
	}
	
	public void setMap(IStimulation[][] tactileMatrix)
	{
		for (int i = 0 ; i < 3; i++)
			for (int j = 0 ; j < 3; j++)	
				m_tactileMatrix[i][j] = tactileMatrix[i][j];
	}
	
	public int getTactile(int x, int y)
	{
		return m_tactileMatrix[x][y].getValue();
	}

	public Color getColor(int x, int y)
	{
		Color c = null;
		if (m_bundle[x][y] == null)
		{
			if (m_tactileMatrix[x][y] != null)
			{
				int value = 140 - 70 * m_tactileMatrix[x][y].getValue();
				c = new Color(value, value, value);
			}
		}
		else
			c = m_bundle[x][y].getVisualStimulation().getColor();
		
		return c;
	}
	
	public IBundle getBundle(int x, int y)
	{
		return m_bundle[x][y];
	}
	
	/**
	 * Duplicate the observation
	 * TODO the observation transformations should be learned rather than hard coded (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	private void copy(IObservation previousObservation)
	{
		// Tactile
		
		m_bundle[0][2] = previousObservation.getBundle(0,2);
		m_bundle[1][2] = previousObservation.getBundle(1,2);
		m_bundle[2][2] = previousObservation.getBundle(2,2);
		
		m_bundle[0][1] = previousObservation.getBundle(0,1);
		m_bundle[1][1] = previousObservation.getBundle(1,1);
		m_bundle[2][1] = previousObservation.getBundle(2,1);
	
		m_bundle[0][0] = previousObservation.getBundle(0,0);
		//m_bundle[1][0] = previousObservation.getBundle(1,1);
		m_bundle[2][0] = previousObservation.getBundle(2,0);
	
	}

	/**
	 * Translate the observation
	 * TODO the observation transformations should be learned rather than hard coded (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	private void forward(IObservation previousObservation)
	{
		// Tactile
		
		m_bundle[0][2] = previousObservation.getBundle(0,1);
		m_bundle[1][2] = previousObservation.getBundle(1,1);
		m_bundle[2][2] = previousObservation.getBundle(2,1);
		
		m_bundle[0][1] = previousObservation.getBundle(0,0);
		m_bundle[1][1] = previousObservation.getBundle(1,0);
		m_bundle[2][1] = previousObservation.getBundle(2,0);
	
		// Gustatory
		
//		if (m_tactileMatrix[1][1].getValue() == Ernest.STIMULATION_TOUCH_FISH)
//		{
//			m_taste = Ernest.STIMULATION_TASTE_FISH;
//			// eat
//			//m_tactileMatrix[1][1] = Ernest.STIMULATION_TOUCH_EMPTY;
//		}
//		else 
//			m_taste = Ernest.STIMULATION_TASTE_NOTHING;
	}
	
	/**
	 * Rotate the observation
	 * TODO the observation transformations should be learned rather than hard coded  (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	private void turnRight(IObservation previousObservation)
	{
		// Tactile 
		m_bundle[0][0] = previousObservation.getBundle(1,0);
		//m_bundle[1][0] = previousObservation.getBundle(2,0);
		m_bundle[2][0] = previousObservation.getBundle(2,1);
		m_bundle[2][1] = previousObservation.getBundle(2,2);
		m_bundle[2][2] = previousObservation.getBundle(1,2);
		m_bundle[1][2] = previousObservation.getBundle(0,2);
		m_bundle[0][2] = previousObservation.getBundle(0,1);
		m_bundle[0][1] = previousObservation.getBundle(0,0);

		m_bundle[1][1] = previousObservation.getBundle(1,1);

		//		// Kinematic
//		if (m_tactileMatrix[1][0].getValue() == Ernest.STIMULATION_TOUCH_WALL)
//			m_kinematic = Ernest.STIMULATION_KINEMATIC_FAIL;
//		else 
//			m_kinematic = Ernest.STIMULATION_KINEMATIC_SUCCEED;
	}
	
	/**
	 * Rotate the observation
	 * TODO the observation transformations should be learned rather than hard coded  (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	private void turnLeft(IObservation previousObservation)
	{
		// Tactile counterclockwise
		m_bundle[0][0] = previousObservation.getBundle(0,1);
		m_bundle[0][1] = previousObservation.getBundle(0,2);
		m_bundle[0][2] = previousObservation.getBundle(1,2);
		m_bundle[1][2] = previousObservation.getBundle(2,2);
		m_bundle[2][2] = previousObservation.getBundle(2,1);
		m_bundle[2][1] = previousObservation.getBundle(2,0);
		m_bundle[2][0] = previousObservation.getBundle(1,0);
		//m_bundle[1][0] = previousObservation.getBundle(0,0);

		m_bundle[1][1] = previousObservation.getBundle(1,1);

		// Kinematic
//		if (m_tactileMatrix[1][0].getValue() == Ernest.STIMULATION_TOUCH_WALL)
//			m_kinematic = Ernest.STIMULATION_KINEMATIC_FAIL;
//		else 
//			m_kinematic = Ernest.STIMULATION_KINEMATIC_SUCCEED;
	}

	public void setFrontBundle(IBundle bundle)
	{
		m_bundle[1][0] = bundle;
	}
}
