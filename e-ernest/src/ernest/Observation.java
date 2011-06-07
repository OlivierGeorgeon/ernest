package ernest;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;


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
	
	// The map of tactile stimulations
	IStimulation[][] m_tactileMap = new IStimulation[3][3];
	
	// The map of surrounding bundles 
	IBundle[][] m_bundle = new IBundle[3][3];
	
	private String getHexColor() 
	{
		if (m_salience != null)
//			return String.format("%06X", m_salience.getColor().getRGB()  & 0x00ffffff);
			return m_salience.getColor().getHexCode();
		else
			return "008000";
	}

	private String getHexColor(int x, int y) 
	{
//		return String.format("%06X", getColor(x, y).getRGB()  & 0x00ffffff);
		return getColor(x,y).getHexCode();
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

	public void trace(ITracer tracer, String element) 
	{
		if (tracer == null)
			return;
		Object e = tracer.addEventElement(element);

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
		tracer.addSubelement(localMap, "position_6", getHexColor(0,2));
		tracer.addSubelement(localMap, "position_5", getHexColor(0,1));
		tracer.addSubelement(localMap, "position_4", getHexColor(0,0));
		tracer.addSubelement(localMap, "position_3", getHexColor(1,0));
		tracer.addSubelement(localMap, "position_2", getHexColor(2,0));
		tracer.addSubelement(localMap, "position_1", getHexColor(2,1));
		tracer.addSubelement(localMap, "position_0", getHexColor(2,2));
	}
	
	public void setDynamicFeature(IAct act)
	{
		
		String dynamicFeature = "";
		
		int minFovea = Ernest.CENTER_RETINA - 30; // 25;
		int maxFovea = Ernest.CENTER_RETINA + 30; // 85;
		
		int satisfaction = 0;

		if (m_attractiveness >= 0)
		//if (m_attractiveness != Ernest.ATTRACTIVENESS_OF_EMPTY)
		{
//			if (m_previousAttractiveness == Ernest.ATTRACTIVENESS_OF_EMPTY)
//			{
//				// Reached the edge of a wall (can now move forward)
//				// (We need a transitional step otherwise we would be comparing direction of repulsiveness with direction of attractiveness)
//				dynamicFeature = "_";
//				satisfaction = 20;
//			}
//			else
			{
				// Attractiveness feature
				if (m_previousAttractiveness > m_attractiveness)
					// Farther
					dynamicFeature = "-";		
				else if (m_previousAttractiveness < m_attractiveness)
					// Closer
					dynamicFeature = "+";
				else if (Math.abs(m_previousDirection - Ernest.CENTER_RETINA ) < Math.abs(m_direction - Ernest.CENTER_RETINA))
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
	
				// Direction feature
				
				if (!dynamicFeature.equals(""))
				{
					if (minFovea >= m_direction)
						dynamicFeature = "|" + dynamicFeature;
					else if (m_direction >= maxFovea )
						dynamicFeature = dynamicFeature + "|";
				}		
			}
		}
		else
		{
			// Attractiveness feature
			if (m_previousAttractiveness >= 0)
			//if (m_previousAttractiveness != Ernest.ATTRACTIVENESS_OF_EMPTY)
				// A wall appeared with a part of it in front of Ernest
				dynamicFeature = "*";		
			else if (Math.abs(m_previousDirection - 30 ) < Math.abs(m_direction - 30))
				// The wall went more outward (Ernest closer to the edge)
				dynamicFeature = "_";
			else if (Math.abs(m_previousDirection - 30 ) > Math.abs(m_direction - 30))
				// The wall went more inward (Ernest farther to the edge)
				dynamicFeature = "*";
	
			if (dynamicFeature.equals("*"))
				satisfaction = -100;
			if (dynamicFeature.equals("_"))
				satisfaction = 20;
			
			// Direction feature
			
			if (!dynamicFeature.equals(""))
			{
				if (30 > m_direction) // (30 is the center in the tactile referential)
					dynamicFeature = "|" + dynamicFeature;
				else if (m_direction > 30 )
					dynamicFeature = dynamicFeature + "|";
			}		
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
			satisfaction = 100;
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
	
	public EColor getColor(int x, int y)
	{
		EColor c = null;
		if (Ernest.STIMULATION_KINEMATIC_BUMP.equals(m_kinematicStimulation) && (x == 1) && (y == 0))
			c = EColor.RED;
		else
		{
			if (m_bundle[x][y] == null)
			{
				if (m_tactileMap[x][y] == null)
				{
					// at startup, the tactile map is not yet initialized
					c = Ernest.COLOR_TOUCH_EMPTY;
				}
				else
				{
					if (m_tactileMap[x][y].equals(Ernest.STIMULATION_TOUCH_EMPTY))
						c = Ernest.COLOR_TOUCH_EMPTY;
					if (m_tactileMap[x][y].equals(Ernest.STIMULATION_TOUCH_SOFT))
						c = Ernest.COLOR_TOUCH_ALGA;
					if (m_tactileMap[x][y].equals(Ernest.STIMULATION_TOUCH_WALL))
						c = Ernest.COLOR_TOUCH_WALL;
					if (m_tactileMap[x][y].equals(Ernest.STIMULATION_TOUCH_FISH))
						c = Ernest.COLOR_TOUCH_FISH;
				}
			}
			else
				c = m_bundle[x][y].getColor();
		}		
		return c;
	}
	
	public IBundle getBundle(int x, int y)
	{
		return m_bundle[x][y];
	}
	
	public IStimulation getTactileStimulation(int x, int y)
	{
		if (m_tactileMap[x][y] == null)
			return Ernest.STIMULATION_TOUCH_EMPTY;
		else
			return m_tactileMap[x][y];
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
			boolean status = false;
			ISchema schema = act.getSchema();
			// Local map
			if (schema.getLabel().equals(">"))
			{
				//if (previousObservation.getBundle(1,0) == null || previousObservation.getBundle(1,0).getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_FORWARD))
				//if (previousObservation.getBundle(1,0) == null || !previousObservation.getBundle(1,0).getTactileStimulation().equals(Ernest.STIMULATION_TOUCH_WALL))
				if (!previousObservation.getTactileStimulation(1, 0).equals(Ernest.STIMULATION_TOUCH_WALL))
				{
					// Move forward
					if (!getTactileStimulation(0,2).equals(Ernest.STIMULATION_TOUCH_EMPTY))
						m_bundle[0][2] = previousObservation.getBundle(0,1);
					else 
						m_bundle[0][2] = null;
					m_bundle[1][2] = previousObservation.getBundle(1,1);
					if (!getTactileStimulation(2,2).equals(Ernest.STIMULATION_TOUCH_EMPTY))
						m_bundle[2][2] = previousObservation.getBundle(2,1);
					else 
						m_bundle[2][2] = null;
					
					//if (!getTactileStimulation(0,1).equals(Ernest.STIMULATION_TOUCH_EMPTY))
						m_bundle[0][1] = previousObservation.getBundle(0,0);
					//else 
					//	m_bundle[0][1] = null;
					m_bundle[1][1] = previousObservation.getBundle(1,0);
					//if (!getTactileStimulation(2,1).equals(Ernest.STIMULATION_TOUCH_EMPTY))
						m_bundle[2][1] = previousObservation.getBundle(2,0);
					//else 
					//	m_bundle[2][1] = null;
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_FORWARD;
					status = true;
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
					status = false;
				}
			}
			if (schema.getLabel().equals("^"))
			{
				// Turn left
				m_bundle[0][0] = previousObservation.getBundle(0,1);
				m_bundle[0][1] = previousObservation.getBundle(0,2);
				m_bundle[0][2] = previousObservation.getBundle(1,2);
				
				if (!getTactileStimulation(1,2).equals(Ernest.STIMULATION_TOUCH_EMPTY))
					m_bundle[1][2] = previousObservation.getBundle(2,2);
				else
					m_bundle[1][2] = null;
				m_bundle[2][2] = previousObservation.getBundle(2,1);
				m_bundle[2][1] = previousObservation.getBundle(2,0);
				m_bundle[2][0] = previousObservation.getBundle(1,0);
				m_bundle[1][0] = previousObservation.getBundle(0,0); // The front cell is updated when adjusting the observation

				m_bundle[1][1] = previousObservation.getBundle(1,1);
				
				if (m_bundle[1][0] == null || m_bundle[1][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_FORWARD))
				{
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_LEFT_EMPTY;
					status = true;
				}
				else
				{
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_LEFT_WALL;
					status = false;
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
				if (!getTactileStimulation(1,2).equals(Ernest.STIMULATION_TOUCH_EMPTY))
					m_bundle[1][2] = previousObservation.getBundle(0,2);
				else
					m_bundle[1][2] = null;
				m_bundle[0][2] = previousObservation.getBundle(0,1);
				m_bundle[0][1] = previousObservation.getBundle(0,0);

				m_bundle[1][1] = previousObservation.getBundle(1,1);
				
				if (m_bundle[1][0] == null || m_bundle[1][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_FORWARD)) 
				{
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_RIGHT_EMPTY;
					status = true;
				}
				else
				{
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_RIGHT_WALL;
					status = false;
				}
			}
			
		m_previousDirection = previousObservation.getDirection();
		m_previousAttractiveness = previousObservation.getAttractiveness();
		m_confirmation = (status == act.getStatus()); 
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

    public ISalience getTactileSalience()
    {
    	ISalience salience = null;
	
        IStimulation[] tactileStimulations = new Stimulation[7];
        tactileStimulations[0] = m_tactileMap[2][2];
        tactileStimulations[1] = m_tactileMap[2][1];
        tactileStimulations[2] = m_tactileMap[2][0];
        tactileStimulations[3] = m_tactileMap[1][0];
        tactileStimulations[4] = m_tactileMap[0][0];
        tactileStimulations[5] = m_tactileMap[0][1];
        tactileStimulations[6] = m_tactileMap[0][2];

        int span = 0;
        int sumDirection = 0;
        boolean front = false;
        for (int i = 0 ; i < 7; i++)
        {
        	if (tactileStimulations[i].equals(Ernest.STIMULATION_TOUCH_WALL))
        	{
				// measure the salience span and average direction
        		span++;
                sumDirection += i * 10;
                if (i == 3) // Ernest's front
                	front = true;
        	}
        	else
        	{
        		// record the previous salience if it is frontal
        		if (front)
		        {
		        	salience = new Salience();
		            salience.setDirection((int) (sumDirection / span + .5));
		            salience.setSpan(span);
		            salience.setColor(Ernest.COLOR_TOUCH_WALL); 
		        	salience.setAttractiveness(Ernest.ATTRACTIVENESS_OF_EMPTY);
		        }
        		
        		// look for the next salience
        		front = false;
        		span = 0;
        		sumDirection = 0;
        	}
        }
		// record the last salience if it is frontal
		if (front)
        {
        	salience = new Salience();
            salience.setDirection((int) (sumDirection / span + .5));
            salience.setSpan(span);
            salience.setColor(Ernest.COLOR_TOUCH_WALL); 
        	salience.setAttractiveness(Ernest.ATTRACTIVENESS_OF_EMPTY);
        }

        return salience;
    }

//	public void setTactileAttractiveness()
//	{
//		
//		// If Ernest is facing a wall
//		if (m_bundle[1][0] != null && m_bundle[1][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_BUMP))
//		{
//			// If there is no wall on the left then attracted to the left
//			if (m_bundle[0][0] == null || !m_bundle[0][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_BUMP))
//			{
//				m_direction = 90;
//				m_attractiveness = Ernest.ATTRACTIVENESS_OF_UNKNOWN;
//			}
//			else if (m_bundle[2][0] == null || !m_bundle[2][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_BUMP))
//			{
//				// If there is no wall on the right then attracted to the right
//				m_direction = 20;
//				m_attractiveness = Ernest.ATTRACTIVENESS_OF_UNKNOWN;
//			}
//			else if (m_bundle[0][1] == null || !m_bundle[0][1].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_BUMP))
//			{
//				// if there is no wall on the left side then attracted to the left side
//				m_direction = 110;
//				m_attractiveness = Ernest.ATTRACTIVENESS_OF_UNKNOWN;
//			}
//			else
//			{
//				// else attracted to the right side
//				m_direction = 0;
//				m_attractiveness = Ernest.ATTRACTIVENESS_OF_UNKNOWN;
//			}				
//		}
//	}

	public void setTactileMap()
	{
		
		// Check for existing gray bundles.
		boolean grayBundle = false;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (m_bundle[i][j] != null && m_bundle[i][j].equals(Ernest.BUNDLE_GRAY)) 
					grayBundle = true;
		
		// If there is no gray bundle yet, then create a gray bundle if a fish is touched.
		if (!grayBundle)
		{
			if (m_tactileMap[1][0].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundle[1][0] == null)
				m_bundle[1][0] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[0][0].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundle[0][0] == null)
				m_bundle[0][0] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[2][0].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundle[2][0] == null)
				m_bundle[2][0] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[0][1].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundle[0][1] == null)
				m_bundle[0][1] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[2][1].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundle[2][1] == null)
				m_bundle[2][1] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[0][2].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundle[0][2] == null)
				m_bundle[0][2] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[2][2].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundle[2][2] == null)
				m_bundle[2][2] = Ernest.BUNDLE_GRAY;
			else if (m_tactileMap[1][2].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundle[1][2] == null)
				m_bundle[1][2] = Ernest.BUNDLE_GRAY;
		}
		
	}

}
