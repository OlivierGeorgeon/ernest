package ernest;

import java.awt.Color;

import org.w3c.dom.Element;

import tracing.ITracer;

/**
 * An element of Ernest's visual system.
 * @author Olivier
 */
public class Observation implements IObservation {

	//private int m_distance = Ernest.INFINITE;
	//private int m_direction = 0;
	private String m_dynamicFeature  = "";
	private int m_satisfaction = 0;
	//private int m_attractiveness = 0;
	private IStimulation m_kinematicStimulation;
	private IStimulation m_gustatoryStimulation;
	private String m_label = "no";
	//private int m_span;
	private boolean m_confirmation;
	private IIcon m_icon;

	// The map of tactile stimulations
	IStimulation[][] m_tactileMatrix = new IStimulation[3][3];
	
	// The map of surrounding bundles 
	IBundle[][] m_bundle = new IBundle[3][3];
	
	public String getHexColor() 
	{
		if (m_icon != null)
			return m_icon.getHexColor();
		else
			return "008000";
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
	
	public void setKinematic(IStimulation kinematicStimulation)
	{
		m_kinematicStimulation = kinematicStimulation;
	}

	public IStimulation getKinematic()
	{
		return m_kinematicStimulation;
	}

	public void taste(IStimulation gustatoryStimulation)
	{
		m_gustatoryStimulation = gustatoryStimulation;
	}

	public String getLabel()
	{
		return m_label;
	}

//	public int getSpan() 
//	{
//		return m_span;
//	}
//
//	public void setSpan(int span) 
//	{
//		m_span = span;
//	}

	public void trace(ITracer tracer, String element) 
	{
		Object e = tracer.addEventElement(element);

		tracer.addSubelement(e, "color", getHexColor());
		tracer.addSubelement(e, "dynamic_feature", m_dynamicFeature);
		tracer.addSubelement(e, "satisfaction", m_satisfaction + "");
		if (m_kinematicStimulation != null)
			tracer.addSubelement(e, "kinematic", m_kinematicStimulation.getValue() + "");
		if (m_gustatoryStimulation != null)
			tracer.addSubelement(e, "gustatory", m_gustatoryStimulation.getValue() + "");
		if (m_icon != null)
		{
			tracer.addSubelement(e, "distance", m_icon.getDistance() + "");
			tracer.addSubelement(e, "attractiveness", m_icon.getAttractiveness() + "");
			tracer.addSubelement(e, "direction", m_icon.getDirection() + "");
			tracer.addSubelement(e, "span", m_icon.getSpan() + "");
		}
}
	
	public void setDynamicFeature(IAct act, IObservation previousObservation)
	{
        // Transform the bundle area
//        if (act != null)
//        {
//                if (act.getSchema().getLabel().equals(">"))
//                {
//                        if (m_kinematicStimulation.getValue() == Ernest.STIMULATION_KINEMATIC_FAIL)
//                                copy(previousObservation);
//                        else
//                                forward(previousObservation);
//                }
//                if (act.getSchema().getLabel().equals("^"))
//                        turnLeft(previousObservation);
//                if (act.getSchema().getLabel().equals("v"))
//                        turnRight(previousObservation);
//        }               

		
		// Taste
		if (m_gustatoryStimulation.getValue() == Ernest.STIMULATION_TASTE_FISH && m_bundle[1][1] != null)
		{
			m_bundle[1][1].setGustatoryStimulation(m_gustatoryStimulation);
			m_bundle[1][1] = null; // The fish disappears from the local map (into Ernest's stomach) !
		}
			
		String dynamicFeature = "";
		
		int minFovea = Ernest.CENTER_RETINA - 30; // 25;
		int maxFovea = Ernest.CENTER_RETINA + 30; // 85;
		
		int previousAttractiveness = 0;
		int previousDirection = Ernest.CENTER_RETINA;
		if (previousObservation != null && previousObservation.getIcon() != null)
		{
			previousAttractiveness = previousObservation.getIcon().getAttractiveness();
			previousDirection = previousObservation.getIcon().getDirection();
		}
		
		int currentAttractiveness = 0;
		int currentDirection = Ernest.CENTER_RETINA;
		if (m_icon != null)
		{
			currentAttractiveness = m_icon.getAttractiveness();
			currentDirection = m_icon.getDirection();
		}
			
		// Attractiveness feature
		if (previousAttractiveness > currentAttractiveness)
			// Farther
			dynamicFeature = "-";		
		else if (previousAttractiveness < currentAttractiveness)
			// Closer
			dynamicFeature = "+";
		else if (Math.abs(previousDirection - Ernest.CENTER_RETINA ) < Math.abs(currentDirection - Ernest.CENTER_RETINA))
			// More outward
			dynamicFeature = "-";
		else if (Math.abs(previousDirection - Ernest.CENTER_RETINA ) > Math.abs(currentDirection - Ernest.CENTER_RETINA))
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
			if (minFovea >= m_icon.getDirection())
				dynamicFeature = "|" + dynamicFeature;
			else if (m_icon.getDirection() >= maxFovea )
				dynamicFeature = dynamicFeature + "|";
		}		
		
		// Gustatory
		
		if (m_gustatoryStimulation.getValue() == Ernest.STIMULATION_TASTE_FISH)
		{
			dynamicFeature = "*";
			satisfaction = 200;
		}
		
		String label = dynamicFeature;
		if (act != null)
		{
			satisfaction = satisfaction + act.getSchema().resultingAct(m_kinematicStimulation.getValue() == Ernest.STIMULATION_KINEMATIC_SUCCEED).getSatisfaction();
			label = act.getSchema().getLabel() + dynamicFeature;
		}
		
		// Label
		
		boolean status = (m_kinematicStimulation.getValue() == Ernest.STIMULATION_KINEMATIC_SUCCEED);
		if (status)
			m_label = "(" + label + m_dynamicFeature + ")";
		else 
			m_label = "[" + label + m_dynamicFeature + "]";
		
		m_dynamicFeature = dynamicFeature;
		m_satisfaction = satisfaction;
		
		if (act != null) m_confirmation = (status == act.getStatus());
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
			c = m_bundle[x][y].getVisualIcon().getColor();
		
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
		m_bundle[1][0] = previousObservation.getBundle(1,0); // The front cell is updated when creating or recognizing a bundle
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
	 * Rotate the observation counterclockwise when Ernest turns clockwise
	 * TODO the observation transformations should be learned rather than hard coded  (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	private void turnRight(IObservation previousObservation)
	{
		// Tactile 
		m_bundle[0][0] = previousObservation.getBundle(1,0);
		m_bundle[1][0] = previousObservation.getBundle(2,0);  // The front cell is updated when creating or recognizing a bundle
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
	 * Rotate the observation clockwise when Ernest turns counterclockwise
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
		m_bundle[1][0] = previousObservation.getBundle(0,0); // The front cell is updated when creating or recognizing a bundle

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
	
	public void anticipate(IObservation previousObservation, IAct act)
	{
		if (act != null)
		{
			// Local map
			if (act.getSchema().getLabel().equals(">"))
			{
				if (act.getStatus())
				{
					// Move forward
					forward(previousObservation);				
				}
				else
				{
					// No change but bump
					copy(previousObservation);
				}
			}
			if (act.getSchema().getLabel().equals("^"))
			{
				// Turn left
				turnLeft(previousObservation);
			}
			if (act.getSchema().getLabel().equals("v"))
			{
				// Turn right 
				turnRight(previousObservation);
			}
			
		}		
	}

	public boolean anticipate(IObservation previousObservation, ISchema schema)
	{
		boolean anticipate = true;
		if (schema != null)
		{
			// Local map
			if (schema.getLabel().equals(">"))
			{
				if (m_bundle[1][0] == null || m_bundle[1][0].getKinematicStimulation() == null ||
                    m_bundle[1][0].getKinematicStimulation().getValue() == Ernest.STIMULATION_KINEMATIC_SUCCEED)
				{
					// Move forward
					forward(previousObservation);
				}
				else
				{
					// No change but bump
					copy(previousObservation);
					anticipate = false;
				}
			}
			if (schema.getLabel().equals("^"))
			{
				// Turn left
				turnLeft(previousObservation);
				
				if (m_bundle[1][0] != null && m_bundle[1][0].getKinematicStimulation() != null &&
	                    m_bundle[1][0].getKinematicStimulation().getValue() == Ernest.STIMULATION_KINEMATIC_FAIL)
					anticipate = false;
			}
			if (schema.getLabel().equals("v"))
			{
				// Turn right 
				turnRight(previousObservation);
				
				if (m_bundle[1][0] != null && m_bundle[1][0].getKinematicStimulation() != null &&
	                    m_bundle[1][0].getKinematicStimulation().getValue() == Ernest.STIMULATION_KINEMATIC_FAIL)
					anticipate = false;
			}
			
		}		
		return anticipate;
	}

	public boolean getConfirmation()
	{
		return m_confirmation;
	}
	public void setIcon(IIcon icon)
	{
		m_icon = icon;
	}
	
	public IIcon getIcon()
	{
		return m_icon;
	}

}
