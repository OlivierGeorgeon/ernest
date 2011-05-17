package ernest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import tracing.ITracer;

/**
 * An element of Ernest's visual system.
 * @author Olivier
 */
public class Observation implements IObservation 
{

	private int m_clock;
	private int m_direction = Ernest.CENTER_RETINA;
	private int m_previousDirection = Ernest.CENTER_RETINA;
	private int m_attractiveness = 0;
	private int m_previousAttractiveness = 0;

	private String m_dynamicFeature  = "";
	private int m_satisfaction = 0;
	private IStimulation m_kinematicStimulation;
	private IStimulation m_gustatoryStimulation;
	private String m_label = "no";

	private boolean m_confirmation;
	private ISalience m_salience;
	
	private boolean m_peripersonal = false;
	private boolean m_previousPeripersonal = false;
	
	private boolean m_status;

	// The map of tactile stimulations
	IStimulation[][] m_tactileMap = new IStimulation[3][3];
	
	// The map of surrounding bundles 
	IBundle[][] m_bundle = new IBundle[3][3];
	
	private String getHexColor() 
	{
		if (m_salience != null)
			return String.format("%06X", m_salience.getColor().getRGB()  & 0x00ffffff);
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

	public void setGustatory(IStimulation gustatoryStimulation)
	{
		m_gustatoryStimulation = gustatoryStimulation;
	}

	public String getLabel()
	{
		return m_label;
	}

	public boolean getStatus()
	{
		return m_status;
	}
	
	public void trace(ITracer tracer, String element) 
	{
		Object e = tracer.addEventElement(element);

		tracer.addSubelement(e, "space", (m_peripersonal ? "peripersonal" : "ambient"));
		tracer.addSubelement(e, "previous_space", (m_previousPeripersonal ? "peripersonal" : "ambient"));

		tracer.addSubelement(e, "color", getHexColor());
		tracer.addSubelement(e, "dynamic_feature", m_dynamicFeature);
		tracer.addSubelement(e, "satisfaction", m_satisfaction + "");
		tracer.addSubelement(e, "direction", m_direction + "");
		if (m_kinematicStimulation != null)
			tracer.addSubelement(e, "kinematic", m_kinematicStimulation.getValue() + "");
		if (m_gustatoryStimulation != null)
			tracer.addSubelement(e, "gustatory", m_gustatoryStimulation.getValue() + "");
		if (m_salience != null)
		{
			tracer.addSubelement(e, "distance", m_salience.getDistance() + "");
			tracer.addSubelement(e, "attractiveness", m_salience.getAttractiveness() + "");
			tracer.addSubelement(e, "span", m_salience.getSpan() + "");
		}
		
		// Local map
		
		Object localMap = tracer.addSubelement(e, "local_map");
		if (m_bundle[0][2] != null)
			tracer.addSubelement(localMap, "position_0_2", m_bundle[0][2].getHexColor());
		else 
			tracer.addSubelement(localMap, "position_0_2", "808080");
			
		if (m_bundle[0][1] != null)
			tracer.addSubelement(localMap, "position_0_1", m_bundle[0][1].getHexColor());
		else 
			tracer.addSubelement(localMap, "position_0_1", "808080");
		
		if (m_bundle[0][0] != null)
			tracer.addSubelement(localMap, "position_0_0", m_bundle[0][0].getHexColor());
		else
			tracer.addSubelement(localMap, "position_0_0", "808080");
			
		if (m_bundle[1][0] != null)
			tracer.addSubelement(localMap, "position_1_0", m_bundle[1][0].getHexColor());
		else
			tracer.addSubelement(localMap, "position_1_0", "808080");

		if (m_bundle[2][0] != null)
			tracer.addSubelement(localMap, "position_2_0", m_bundle[2][0].getHexColor());
		else
			tracer.addSubelement(localMap, "position_2_0", "808080");

		if (m_bundle[2][1] != null)
			tracer.addSubelement(localMap, "position_2_1", m_bundle[2][1].getHexColor());
		else
			tracer.addSubelement(localMap, "position_2_1", "808080");

		if (m_bundle[2][2] != null)
			tracer.addSubelement(localMap, "position_2_2", m_bundle[2][2].getHexColor());
		else
			tracer.addSubelement(localMap, "position_2_2", "808080");

		if (m_bundle[1][2] != null)
			tracer.addSubelement(localMap, "position_1_2", m_bundle[1][2].getHexColor());
		else
			tracer.addSubelement(localMap, "position_1_2", "808080");

		if (m_bundle[1][1] != null)
			tracer.addSubelement(localMap, "position_1_1", m_bundle[1][1].getHexColor());
		else
			tracer.addSubelement(localMap, "position_1_1", "808080");
	}
	
	public void setDynamicFeature(IAct act)
	{
		
		String dynamicFeature = "";
		
		int minFovea = Ernest.CENTER_RETINA - 30; // 25;
		int maxFovea = Ernest.CENTER_RETINA + 30; // 85;
		
		int satisfaction = 0;

		if (!m_peripersonal)
		{
			// Attractiveness feature
			if (m_previousAttractiveness > m_attractiveness)
				// Farther
				dynamicFeature = "-";		
			else if (m_previousAttractiveness < m_attractiveness)
				// Closer
				dynamicFeature = "+";
			else if (Math.abs(m_previousDirection - Ernest.CENTER_RETINA ) <= Math.abs(m_direction - Ernest.CENTER_RETINA))
				// More outward (or same direction, therefore another salience)
				dynamicFeature = "-";
			else if (Math.abs(m_previousDirection - Ernest.CENTER_RETINA ) > Math.abs(m_direction - Ernest.CENTER_RETINA))
				// More inward
				dynamicFeature = "+";
			//else 
				// Same attractiveness, same direction, then it is necessarily a different salience
				//dynamicFeature = "-";
	
			if (dynamicFeature.equals("-"))
				satisfaction = -100;
			if (dynamicFeature.equals("+"))
				satisfaction = 20;
		}
		else
		{
			if (!m_previousPeripersonal)
				// Entering peripersonal space
				m_previousAttractiveness = 0;
			
			// Attractiveness feature
			if (m_previousAttractiveness > m_attractiveness)
				// Farther
				dynamicFeature = "_";		
			else if (m_previousAttractiveness < m_attractiveness)
				// Closer
				dynamicFeature = "*";
			else if (Math.abs(m_previousDirection - Ernest.CENTER_RETINA ) < Math.abs(m_direction - Ernest.CENTER_RETINA))
				// More outward
				dynamicFeature = "_";
			else if (Math.abs(m_previousDirection - Ernest.CENTER_RETINA ) > Math.abs(m_direction - Ernest.CENTER_RETINA))
				// More inward
				dynamicFeature = "*";
	
			if (dynamicFeature.equals("_"))
				satisfaction = -100;
			if (dynamicFeature.equals("*"))
				satisfaction = 20;
			
		}
		// Direction feature
		
		if (!dynamicFeature.equals(""))
		{
			if (minFovea >= m_direction)
				dynamicFeature = "|" + dynamicFeature;
			else if (m_direction >= maxFovea )
				dynamicFeature = dynamicFeature + "|";
		}		
		
		// Gustatory
		
		if (m_gustatoryStimulation.equals(Ernest.STIMULATION_GUSTATORY_FISH))
		{
			if (m_bundle[1][1] != null)
			{
				m_bundle[1][1].setGustatoryStimulation(m_gustatoryStimulation);
				m_bundle[1][1] = null; // The fish disappears from the local map (into Ernest's stomach) !
			}
			dynamicFeature = "e";
			satisfaction = 200;
		}
		
		// White square
		
		if (m_bundle[1][1] != null && m_bundle[1][1].equals(Ernest.BUNDLE_WHITE))
		{
			m_bundle[1][1] = null;
			//m_peripersonal = false;
			dynamicFeature = "*";
			satisfaction = 20;
		}
		
		// Label
		
		// boolean status = (m_kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_FORWARD)
		//		       || m_kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_LEFT_EMPTY)
       	//               || m_kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_RIGHT_EMPTY));

		boolean status = true;
		if (m_kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_BUMP)) status = false;
		
		String label = dynamicFeature;
		if (act != null)
		{
			satisfaction = satisfaction + act.getSchema().resultingAct(status).getSatisfaction();
			label = act.getSchema().getLabel() + dynamicFeature;
		}
		
		// Label
		
		if (status)
			m_label = "(" + label + ")";
		else 
			m_label = "[" + label + "]";
		
		m_dynamicFeature = dynamicFeature;
		m_satisfaction = satisfaction;
		
		if (act != null) m_confirmation = (status == act.getStatus());
		
	}
	
	public void setMap(IStimulation[][] tactileMatrix)
	{
		for (int i = 0 ; i < 3; i++)
			for (int j = 0 ; j < 3; j++)	
				m_tactileMap[i][j] = tactileMatrix[i][j];
	}
	
	public Color getColor(int x, int y)
	{
		Color c = null;
		if (m_bundle[x][y] == null)
		{
			if (m_tactileMap[x][y] != null)
			{
				int value = 140 - 70 * m_tactileMap[x][y].getValue();
				c = new Color(value, value, value);
			}
			else c = Ernest.COLOR_TOUCH_EMPTY;//Color.WHITE;
		}
		else
			c = m_bundle[x][y].getColor();
		
		//if (x == 1 && y == 0 && Ernest.STIMULATION_KINEMATIC_FAIL.equals(m_kinematicStimulation))
		//	c = Color.RED;
		
		return c;
	}
	
	public IBundle getBundle(int x, int y)
	{
		return m_bundle[x][y];
	}
	
	public void clearMap()
	{
		m_bundle[0][2] = null;
		m_bundle[1][2] = null;
		m_bundle[2][2] = null;
		
		m_bundle[0][1] = null;
		m_bundle[1][1] = null;
		m_bundle[2][1] = null;
	
		m_bundle[0][0] = null;
		m_bundle[1][0] = null; // The front cell is updated when creating or recognizing a bundle
		m_bundle[2][0] = null;
	}

	public void setFrontBundle(IBundle bundle)
	{
		m_bundle[1][0] = bundle;
	}
	
	public void anticipate(IObservation previousObservation, IAct act)
	{
		if (act != null)
		{
			ISchema schema = act.getSchema();
			// Local map
			if (schema.getLabel().equals(">"))
			{
				//if (previousObservation.getBundle(1,0) == null || previousObservation.getBundle(1,0).getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_FORWARD))
				if (previousObservation.getBundle(1,0) == null || !previousObservation.getBundle(1,0).getTactileStimulation().equals(Ernest.STIMULATION_TOUCH_WALL))
				{
					// Move forward
					m_bundle[0][2] = previousObservation.getBundle(0,1);
					m_bundle[1][2] = previousObservation.getBundle(1,1);
					m_bundle[2][2] = previousObservation.getBundle(2,1);
					
					m_bundle[0][1] = previousObservation.getBundle(0,0);
					m_bundle[1][1] = previousObservation.getBundle(1,0);
					m_bundle[2][1] = previousObservation.getBundle(2,0);
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_FORWARD;
					m_status = true;
				}
				else
				{
					// No change but bump
					m_bundle[0][2] = previousObservation.getBundle(0,2);
					m_bundle[1][2] = previousObservation.getBundle(1,2);
					m_bundle[2][2] = previousObservation.getBundle(2,2);
					
					m_bundle[0][1] = previousObservation.getBundle(0,1);
					m_bundle[1][1] = previousObservation.getBundle(1,1);
					m_bundle[2][1] = previousObservation.getBundle(2,1);
				
					m_bundle[0][0] = previousObservation.getBundle(0,0);
					m_bundle[1][0] = previousObservation.getBundle(1,0); // The front cell is updated when creating or recognizing a bundle
					m_bundle[2][0] = previousObservation.getBundle(2,0);	
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_BUMP;
					m_status = false;
				}
			}
			if (schema.getLabel().equals("^"))
			{
				// Turn left
				m_bundle[0][0] = previousObservation.getBundle(0,1);
				m_bundle[0][1] = previousObservation.getBundle(0,2);
				m_bundle[0][2] = previousObservation.getBundle(1,2);
				m_bundle[1][2] = previousObservation.getBundle(2,2);
				m_bundle[2][2] = previousObservation.getBundle(2,1);
				m_bundle[2][1] = previousObservation.getBundle(2,0);
				m_bundle[2][0] = previousObservation.getBundle(1,0);
				m_bundle[1][0] = previousObservation.getBundle(0,0); // The front cell is updated when adjusting the observation

				m_bundle[1][1] = previousObservation.getBundle(1,1);
				
				if (m_bundle[1][0] == null || m_bundle[1][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_FORWARD))
				{
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_LEFT_EMPTY;
					m_status = true;
				}
				else
				{
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_LEFT_WALL;
					m_status = false;
				}
			}
			if (schema.getLabel().equals("v"))
			{
				// Turn right 
				m_bundle[0][0] = previousObservation.getBundle(1,0);
				m_bundle[1][0] = previousObservation.getBundle(2,0);  // The front cell is updated when adjusting the observation
				m_bundle[2][0] = previousObservation.getBundle(2,1);
				m_bundle[2][1] = previousObservation.getBundle(2,2);
				m_bundle[2][2] = previousObservation.getBundle(1,2);
				m_bundle[1][2] = previousObservation.getBundle(0,2);
				m_bundle[0][2] = previousObservation.getBundle(0,1);
				m_bundle[0][1] = previousObservation.getBundle(0,0);

				m_bundle[1][1] = previousObservation.getBundle(1,1);
				
				if (m_bundle[1][0] == null || m_bundle[1][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_FORWARD)) 
				{
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_RIGHT_EMPTY;
					m_status = true;
				}
				else
				{
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_RIGHT_WALL;
					m_status = false;
				}
			}
			
		m_previousDirection = previousObservation.getDirection();
		m_previousAttractiveness = previousObservation.getAttractiveness();
		m_previousPeripersonal = previousObservation.getPeripersonal();
		m_confirmation = (m_status == act.getStatus()); 
		}		
		
	}

	public boolean getConfirmation()
	{
		return m_confirmation;
	}
	
	public void setConfirmation(boolean confirmation)
	{
		m_confirmation = confirmation;
	}

	public void setSalience(ISalience salience)
	{
		m_salience = salience;
	}
	
	public ISalience getSalience()
	{
		return m_salience;
	}

	public void setDirection(int direction) 
	{
		m_direction = direction;
	}

	public int getDirection() 
	{
		return m_direction;
	}

	public void setPreviousDirection(int direction) 
	{
		m_previousDirection = direction;		
	}

	public int getPreviousDirection() 
	{
		return m_previousDirection;
	}

	public void setAttractiveness(int attractiveness) 
	{
		m_attractiveness = attractiveness;
	}

	public int getAttractiveness() 
	{
		return m_attractiveness;
	}

//	public void setPreviousAttractiveness(int attractiveness) 
//	{
//		m_previousAttractiveness = attractiveness;
//	}

	public int getPreviousAttractiveness() 
	{
		return m_previousAttractiveness;
	}

	public void setTactileAttractiveness()
	{
		
		// If Ernest is facing a wall
		if (m_bundle[1][0] != null && m_bundle[1][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_BUMP))
		{
			// If there is no wall on the left then attracted to the left
			if (m_bundle[0][0] == null || !m_bundle[0][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_BUMP))
			{
				m_direction = 90;
				m_attractiveness = Ernest.BASE_MOTIVATION;
			}
			else if (m_bundle[2][0] == null || !m_bundle[2][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_BUMP))
			{
				// If there is no wall on the right then attracted to the right
				m_direction = 20;
				m_attractiveness = Ernest.BASE_MOTIVATION;
			}
			else if (m_bundle[0][1] == null || !m_bundle[0][1].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_BUMP))
			{
				// if there is no wall on the left side then attracted to the left side
				m_direction = 110;
				m_attractiveness = Ernest.BASE_MOTIVATION;
			}
			else
			{
				// else attracted to the right side
				m_direction = 0;
				m_attractiveness = Ernest.BASE_MOTIVATION;
			}				
		}
	}

	public void setTactileMap()
	{
		
		// If Ernest is facing a wall then set a white bundle as a local target.
		boolean whiteBundle = false;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (m_bundle[i][j] != null && m_bundle[i][j].equals(Ernest.BUNDLE_WHITE)) 
						whiteBundle = true;
		
		if (!whiteBundle && m_tactileMap[1][0].equals(Ernest.STIMULATION_TOUCH_WALL))
		{
			if (m_tactileMap[0][0].equals(Ernest.STIMULATION_TOUCH_EMPTY))
				// If there is no wall on the left then attracted to the left
				m_bundle[0][0] = Ernest.BUNDLE_WHITE;
			else if (m_tactileMap[2][0].equals(Ernest.STIMULATION_TOUCH_EMPTY))
				// If there is no wall on the right then attracted to the right
				m_bundle[2][0] = Ernest.BUNDLE_WHITE;
			else if (m_tactileMap[0][1].equals(Ernest.STIMULATION_TOUCH_EMPTY))
				// if there is no wall on the left side then attracted to the left side
				m_bundle[0][1] = Ernest.BUNDLE_WHITE;
			else if (m_tactileMap[2][1].equals(Ernest.STIMULATION_TOUCH_EMPTY))
				// if there is no wall on the right side then attracted to the right side
				m_bundle[2][1] = Ernest.BUNDLE_WHITE;
		}
		
		// Gray bundles will generate attractiveness due to curiosity.
		boolean grayBundle = false;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (m_bundle[i][j] != null && m_bundle[i][j].equals(Ernest.BUNDLE_GRAY)) 
					grayBundle = true;
		
		if (!grayBundle)
		{
			if (m_tactileMap[1][0].equals(Ernest.STIMULATION_TOUCH_SOFT) && m_bundle[1][0] == null)
				m_bundle[1][0] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[0][0].equals(Ernest.STIMULATION_TOUCH_SOFT) && m_bundle[0][0] == null)
				m_bundle[0][0] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[2][0].equals(Ernest.STIMULATION_TOUCH_SOFT) && m_bundle[2][0] == null)
				m_bundle[2][0] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[0][1].equals(Ernest.STIMULATION_TOUCH_SOFT) && m_bundle[0][1] == null)
				m_bundle[0][1] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[2][1].equals(Ernest.STIMULATION_TOUCH_SOFT) && m_bundle[2][1] == null)
				m_bundle[2][1] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[0][2].equals(Ernest.STIMULATION_TOUCH_SOFT) && m_bundle[0][2] == null)
				m_bundle[0][2] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[2][2].equals(Ernest.STIMULATION_TOUCH_SOFT) && m_bundle[2][2] == null)
				m_bundle[2][2] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[1][2].equals(Ernest.STIMULATION_TOUCH_SOFT) && m_bundle[1][2] == null)
				m_bundle[1][2] = Ernest.BUNDLE_GRAY;
		}
		
	}

	public void setPeripersonal(boolean peripersonal)
	{
		m_peripersonal = peripersonal;
	}
	
	public boolean getPeripersonal()
	{
		return m_peripersonal;
	}

}
