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
public class Observation implements IObservation {

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
	private ISalience m_icon;
	
	private ISalience m_tactileSalience;
	private int m_tactileDirection = Ernest.CENTER_RETINA;
	private int m_previousTactileDirection = Ernest.CENTER_RETINA;
	private int m_tactileAttractiveness = 0;
	private int m_previousTactileAttractiveness = 0;
	
	private boolean m_status;

	// The map of tactile stimulations
	IStimulation[][] m_tactileMatrix = new IStimulation[3][3];
	
	// The map of surrounding bundles 
	IBundle[][] m_bundle = new IBundle[3][3];
	
	private String getHexColor() 
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

		tracer.addSubelement(e, "color", getHexColor());
		tracer.addSubelement(e, "dynamic_feature", m_dynamicFeature);
		tracer.addSubelement(e, "satisfaction", m_satisfaction + "");
		tracer.addSubelement(e, "direction", m_direction + "");
		tracer.addSubelement(e, "tactile_attractiveness", m_tactileAttractiveness + "");
		tracer.addSubelement(e, "tactile_direction", m_tactileDirection + "");
		if (m_kinematicStimulation != null)
			tracer.addSubelement(e, "kinematic", m_kinematicStimulation.getValue() + "");
		if (m_gustatoryStimulation != null)
			tracer.addSubelement(e, "gustatory", m_gustatoryStimulation.getValue() + "");
		if (m_icon != null)
		{
			tracer.addSubelement(e, "distance", m_icon.getDistance() + "");
			tracer.addSubelement(e, "attractiveness", m_icon.getAttractiveness() + "");
			tracer.addSubelement(e, "span", m_icon.getSpan() + "");
		}
		if (m_tactileSalience != null)
		{
			tracer.addSubelement(e, "tactile_span", m_tactileSalience.getSpan() + "");
		}
	}
	
	public void setDynamicFeature(IAct act)
	{
		
		// Taste
		if (m_gustatoryStimulation.equals(Ernest.STIMULATION_GUSTATORY_FISH) && m_bundle[1][1] != null)
		{
			m_bundle[1][1].setGustatoryStimulation(m_gustatoryStimulation);
			m_bundle[1][1] = null; // The fish disappears from the local map (into Ernest's stomach) !
		}
			
		String dynamicFeature = "";
		
		int minFovea = Ernest.CENTER_RETINA - 30; // 25;
		int maxFovea = Ernest.CENTER_RETINA + 30; // 85;
		
		// Attractiveness feature
		if (m_previousAttractiveness > m_attractiveness)
			// Farther
			dynamicFeature = "-";		
		else if (m_previousAttractiveness < m_attractiveness)
			// Closer
			dynamicFeature = "+";
		else if (Math.abs(m_previousDirection - Ernest.CENTER_RETINA ) < Math.abs(m_direction - Ernest.CENTER_RETINA))
			// More outward
			dynamicFeature = "-";
		else if (Math.abs(m_previousDirection - Ernest.CENTER_RETINA ) > Math.abs(m_direction - Ernest.CENTER_RETINA))
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
		
		// Dynamic feature from the local map would override dynamic feature from vision.
		if (m_bundle[1][0] != null && m_bundle[1][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_BUMP))
		{
			// Attractiveness feature
			if (m_previousTactileAttractiveness > m_tactileAttractiveness)
				// more wall
				dynamicFeature = "-";		
			else if (m_previousTactileAttractiveness < m_tactileAttractiveness)
				// less wall
				dynamicFeature = "+";
			else if (Math.abs(m_previousTactileDirection - Ernest.CENTER_RETINA ) < Math.abs(m_tactileDirection - Ernest.CENTER_RETINA))
				// More outward
				dynamicFeature = "+";
			else if (Math.abs(m_previousTactileDirection - Ernest.CENTER_RETINA ) > Math.abs(m_tactileDirection - Ernest.CENTER_RETINA))
				// More inward
				dynamicFeature = "-";

			satisfaction = 0;
			if (dynamicFeature.equals("-"))
				satisfaction = -100;
			if (dynamicFeature.equals("+"))
				satisfaction = 20;
			
			// Direction feature
			
			if (!dynamicFeature.equals(""))
			{
				if (minFovea >= m_tactileDirection)
					dynamicFeature = "|" + dynamicFeature;
				else if (m_tactileDirection >= maxFovea )
					dynamicFeature = dynamicFeature + "|";
			}		

		}
		
		if (m_bundle[1][0] != null && m_bundle[1][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_BUMP))
		{
			//dynamicFeature = "w";
			//satisfaction = -20;			
		}
		

		// Gustatory
		
		if (m_gustatoryStimulation.equals(Ernest.STIMULATION_GUSTATORY_FISH))
		{
			dynamicFeature = "*";
			satisfaction = 200;
		}
		
		// Label
		
		boolean status = (m_kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_FORWARD)
				       || m_kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_LEFT_EMPTY)
       	               || m_kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_RIGHT_EMPTY));

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
				m_tactileMatrix[i][j] = tactileMatrix[i][j];
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
			else c = Color.WHITE;
		}
		else
			c = m_bundle[x][y].getVisualIcon().getColor();
		
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

	/**
	 * Duplicate the observation
	 * TODO the observation transformations should be learned rather than hard coded (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	private void copy(IObservation previousObservation)
	{
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
		m_bundle[0][2] = previousObservation.getBundle(0,1);
		m_bundle[1][2] = previousObservation.getBundle(1,1);
		m_bundle[2][2] = previousObservation.getBundle(2,1);
		
		m_bundle[0][1] = previousObservation.getBundle(0,0);
		m_bundle[1][1] = previousObservation.getBundle(1,0);
		m_bundle[2][1] = previousObservation.getBundle(2,0);
	}
	
	/**
	 * Rotate the observation counterclockwise when Ernest turns clockwise
	 * TODO the observation transformations should be learned rather than hard coded  (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	private void turnRight(IObservation previousObservation)
	{
		m_bundle[0][0] = previousObservation.getBundle(1,0);
		m_bundle[1][0] = previousObservation.getBundle(2,0);  // The front cell is updated when adjusting the observation
		m_bundle[2][0] = previousObservation.getBundle(2,1);
		m_bundle[2][1] = previousObservation.getBundle(2,2);
		m_bundle[2][2] = previousObservation.getBundle(1,2);
		m_bundle[1][2] = previousObservation.getBundle(0,2);
		m_bundle[0][2] = previousObservation.getBundle(0,1);
		m_bundle[0][1] = previousObservation.getBundle(0,0);

		m_bundle[1][1] = previousObservation.getBundle(1,1);
	}
	
	/**
	 * Rotate the observation clockwise when Ernest turns counterclockwise
	 * TODO the observation transformations should be learned rather than hard coded  (at least with the assumption that it is linear).
	 * @param previousObservation The previous observation
	 */
	private void turnLeft(IObservation previousObservation)
	{
		m_bundle[0][0] = previousObservation.getBundle(0,1);
		m_bundle[0][1] = previousObservation.getBundle(0,2);
		m_bundle[0][2] = previousObservation.getBundle(1,2);
		m_bundle[1][2] = previousObservation.getBundle(2,2);
		m_bundle[2][2] = previousObservation.getBundle(2,1);
		m_bundle[2][1] = previousObservation.getBundle(2,0);
		m_bundle[2][0] = previousObservation.getBundle(1,0);
		m_bundle[1][0] = previousObservation.getBundle(0,0); // The front cell is updated when adjusting the observation

		m_bundle[1][1] = previousObservation.getBundle(1,1);
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
				if (previousObservation.getBundle(1,0) == null || previousObservation.getBundle(1,0).getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_FORWARD))
				{
					// Move forward
					forward(previousObservation);
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_FORWARD;
					m_status = true;
				}
				else
				{
					// No change but bump
					copy(previousObservation);
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_BUMP;
					m_status = false;
				}
			}
			if (schema.getLabel().equals("^"))
			{
				// Turn left
				turnLeft(previousObservation);
				
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
				turnRight(previousObservation);
				
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
		m_previousTactileDirection = previousObservation.getTactileDirection();
		m_previousTactileAttractiveness = previousObservation.getTactileAttractiveness();
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

	public void setVisualSalience(ISalience salience)
	{
		m_icon = salience;
	}
	
	public ISalience getVisualSalience()
	{
		return m_icon;
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

	public void setPreviousAttractiveness(int attractiveness) 
	{
		m_previousAttractiveness = attractiveness;
	}

	public int getPreviousAttractiveness() 
	{
		return m_previousAttractiveness;
	}

	public void setTactileSalience()
	{
		
		IStimulation[] tactileStimulations = new Stimulation[5];
		tactileStimulations[0] = m_tactileMatrix[2][1];
		tactileStimulations[1] = m_tactileMatrix[2][0];
		tactileStimulations[2] = m_tactileMatrix[1][0];
		tactileStimulations[3] = m_tactileMatrix[0][0];
		tactileStimulations[4] = m_tactileMatrix[0][1];

		List<ISalience> saliencies = new ArrayList<ISalience>(5);

		// Create a List of the various saliences in the visual field

		for (int i = 0 ; i < 5; i++)
		{
			int nbDirection = 1;
			int sumDirection = i * 25 + 5;
			int j = i + 1;
			while ( j < 5 && tactileStimulations[i].equals(tactileStimulations[j]))
			{
				nbDirection++;
				sumDirection += j * 25;
				j++;
			}	
			ISalience salience = new Salience();
			salience.setDirection((int) (sumDirection / nbDirection + .5));
			salience.setSpan(nbDirection);
			salience.setColor(tactileStimulations[i].getColor());
			int attractiveness = 0;
			if (tactileStimulations[i].equals(Ernest.STIMULATION_TOUCH_WALL))
				attractiveness = - Ernest.BASE_MOTIVATION  - 10 * nbDirection;
			salience.setAttractiveness(attractiveness);
			saliencies.add(salience);
		}

		// Find the most repulsive salience in the tactile field (There is at least a wall)
		
		m_tactileAttractiveness = 0;
		for (ISalience salience : saliencies)
			if (salience.getAttractiveness() < m_tactileAttractiveness)
			{
				m_tactileAttractiveness = salience.getAttractiveness();
				m_tactileDirection = salience.getDirection();
				m_tactileSalience = salience;
			}
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
	public int getTactileDirection()
	{
		return m_tactileDirection;
	}
	public int getTactileAttractiveness()
	{
		return m_tactileAttractiveness;
	}

}
