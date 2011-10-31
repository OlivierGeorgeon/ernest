package spas;

import java.util.ArrayList;
import java.util.List;

import ernest.Ernest;
import ernest.ITracer;

import imos.IAct;
import imos.ISchema;


/**
 * An element of Ernest's visual system.
 * @author Olivier
 */
public class Observation implements IObservation 
{

	/** The Tracer. */
	private ITracer m_tracer = null; 

	private float m_direction = Ernest.CENTER_RETINA;
	private float m_previousDirection = Ernest.CENTER_RETINA;
	private int m_attractiveness = 0;
	private int m_previousAttractiveness = 0;
	private IBundle m_previousFocusBundle = null;

	private String m_stimuli  = "";
	private String m_visualStimuli  = "";
	private int m_satisfaction = 0;
	private IStimulation m_kinematicStimulation;
	private IStimulation m_gustatoryStimulation;

	private boolean m_confirmation;
	private ISalience m_salience;
	private IBundle m_focusBundle;
	
	// The map of tactile stimulations
	IStimulation[][] m_tactileMap = new IStimulation[3][3];
	
	// The map of surrounding bundles 
	IBundle[][] m_bundleMap = new IBundle[3][3];

	private String getHexColor() 
	{
		// Return the salience's color
		if (m_salience != null)
			return getHexColor(m_salience.getValue());
		else
			// Return white if there is no salience.
			return "FFFFFF";
	}

	private String getHexColor(int x, int y) 
	{
		return getHexColor(getColor(x,y));
	}
	
	public int getColor(int x, int y)
	{
		int c = 0;
		if (m_bundleMap[x][y] == null)
		{
			if (m_tactileMap[x][y] == null)
				// at startup, the tactile map is not yet initialized
				c = Ernest.STIMULATION_TOUCH_EMPTY.getValue();
			else
				c = m_tactileMap[x][y].getValue();
		}
		else
			c = m_bundleMap[x][y].getVisualStimulation().getValue();
		return c;
	}
	
	private String getHexColor(int rgb) 
	{
		int r = rgb/65536;
		int g = (rgb - r * 65536)/256;
		int b = rgb - r * 65536 - g * 256;
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

	public String getStimuli() 
	{
		return m_stimuli;
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

	public void trace(ITracer tracer, String element) 
	{
		if (tracer == null)
			return;
		Object e = tracer.addEventElement(element);

		tracer.addSubelement(e, "color", getHexColor());
		
		if (getFocusBundle() != null)
			getFocusBundle().trace(tracer, "focus_bundle");
		
		tracer.addSubelement(e, "stimuli", m_stimuli);
		tracer.addSubelement(e, "dynamic_feature", m_visualStimuli);
		
		tracer.addSubelement(e, "satisfaction", m_satisfaction + "");
		tracer.addSubelement(e, "direction", m_direction + "");
		if (m_kinematicStimulation != null)
			tracer.addSubelement(e, "kinematic", m_kinematicStimulation.getHexColor());
		if (m_gustatoryStimulation != null)
			tracer.addSubelement(e, "gustatory", m_gustatoryStimulation.getHexColor());
		if (m_salience != null)
		{
			tracer.addSubelement(e, "distance", m_salience.getDistance() + "");
			tracer.addSubelement(e, "attractiveness", m_salience.getAttractiveness() + "");
			tracer.addSubelement(e, "span", m_salience.getSpan() + "");
		}
		
		if (m_focusBundle !=null && !m_focusBundle.equals(m_previousFocusBundle))
		{
			m_focusBundle.trace(tracer, "focus");
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
	
	/**
	 * Generate the stimuli for imos.
	 * The stimuli come from: 
	 * - The kinematic feature.
	 * - The variation in attractiveness and in direction of the object of interest. 
	 * @param act The enacted act.
	 */
	public void setDynamicFeature(IAct act)
	{
		
		String dynamicFeature = "";
		
		//int minFovea = Ernest.CENTER_RETINA - 30; // 25;
		//int maxFovea = Ernest.CENTER_RETINA + 30; // 85;
		
		float minFovea =  - (float)Math.PI / 4 + 0.01f;
		float maxFovea =    (float)Math.PI / 4 - 0.01f;
		
		int satisfaction = 0;

		if (m_attractiveness >= 0)
		{
			// Positive attractiveness
			{
				// Attractiveness
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
		
				if (dynamicFeature.equals("-"))
					satisfaction = -100;
				if (dynamicFeature.equals("+"))
					satisfaction = 20;
	
				// Direction
				
				if (!dynamicFeature.equals(""))
				{
					if (m_direction <= minFovea)
						dynamicFeature = "|" + dynamicFeature;
					else if (m_direction >= maxFovea )
						dynamicFeature = dynamicFeature + "|";
				}		
			}
		}
		else
		{
			// Negative attractiveness (repulsion)
			
			// Variation in attractiveness
			if (m_previousAttractiveness >= 0)
				// A wall appeared with a part of it in front of Ernest
				dynamicFeature = "*";		
			else if (Math.abs(m_previousDirection) < Math.abs(m_direction))
				// The wall went more outward (Ernest closer to the edge)
				dynamicFeature = "_";
			else if (Math.abs(m_previousDirection) > Math.abs(m_direction))
				// The wall went more inward (Ernest farther to the edge)
				dynamicFeature = "*";
	
			if (dynamicFeature.equals("*"))
				satisfaction = -100;
			if (dynamicFeature.equals("_"))
				satisfaction = 20;
			
			// Direction feature
			
			if (!dynamicFeature.equals(""))
			{
				if (m_direction < -0.1f ) 
					dynamicFeature = "|" + dynamicFeature;
				else if (m_direction > 0.1f )
					dynamicFeature = dynamicFeature + "|";
			}		
		}
		
		// Gustatory
		
		if (m_gustatoryStimulation.equals(Ernest.STIMULATION_GUSTATORY_FISH))
		{
			if (m_bundleMap[1][1] != null)
				m_bundleMap[1][1] = null; // The fish disappears from the local map (into Ernest's stomach) !
			dynamicFeature = "e";
			satisfaction = 100;
		}
		
		m_visualStimuli = dynamicFeature;
		
		// Kinematic
		
		boolean status = true;
		if (m_kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_BUMP)) status = false;
		
		dynamicFeature = (status ? " " : "w") + dynamicFeature;
		if (act != null)
		{
			if (act.getSchema().getLabel().equals(">"))
				satisfaction = satisfaction + (status ? 20 : -100);
			else
				satisfaction = satisfaction + (status ? -10 : -20);
		}
				
		m_stimuli = dynamicFeature;
		m_satisfaction = satisfaction;
		
		if (act != null) m_confirmation = (status == act.getStatus());
		
	}
	
	public void setMap(IStimulation[][] tactileMatrix)
	{
		for (int i = 0 ; i < 3; i++)
			for (int j = 0 ; j < 3; j++)	
				m_tactileMap[i][j] = tactileMatrix[i][j];
	}
	
	public IBundle getBundle(int x, int y)
	{
		return m_bundleMap[x][y];
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
		m_bundleMap[0][2] = null;
		m_bundleMap[1][2] = null;
		m_bundleMap[2][2] = null;
		
		m_bundleMap[0][1] = null;
		m_bundleMap[1][1] = null;
		m_bundleMap[2][1] = null;
	
		m_bundleMap[0][0] = null;
		m_bundleMap[1][0] = null; // The front cell is updated when creating or recognizing a bundle
		m_bundleMap[2][0] = null;
	}

	public void setFrontBundle(IBundle bundle)
	{
		m_bundleMap[1][0] = bundle;
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
						m_bundleMap[0][2] = previousObservation.getBundle(0,1);
					else 
						m_bundleMap[0][2] = null;
					m_bundleMap[1][2] = previousObservation.getBundle(1,1);
					if (!getTactileStimulation(2,2).equals(Ernest.STIMULATION_TOUCH_EMPTY))
						m_bundleMap[2][2] = previousObservation.getBundle(2,1);
					else 
						m_bundleMap[2][2] = null;
					
					//if (!getTactileStimulation(0,1).equals(Ernest.STIMULATION_TOUCH_EMPTY))
					m_bundleMap[0][1] = previousObservation.getBundle(0,0);
					//else 
					//	m_bundle[0][1] = null;
					m_bundleMap[1][1] = previousObservation.getBundle(1,0);
					//if (!getTactileStimulation(2,1).equals(Ernest.STIMULATION_TOUCH_EMPTY))
					m_bundleMap[2][1] = previousObservation.getBundle(2,0);
					//else 
					//	m_bundle[2][1] = null;
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_FORWARD;
					status = true;
				}
				else
				{
					// No change but bump
					m_bundleMap[0][2] = previousObservation.getBundle(0,2);
					m_bundleMap[1][2] = previousObservation.getBundle(1,2);
					m_bundleMap[2][2] = previousObservation.getBundle(2,2);
					
					m_bundleMap[0][1] = previousObservation.getBundle(0,1);
					m_bundleMap[1][1] = previousObservation.getBundle(1,1);
					m_bundleMap[2][1] = previousObservation.getBundle(2,1);
				
					m_bundleMap[0][0] = previousObservation.getBundle(0,0);
					m_bundleMap[1][0] = previousObservation.getBundle(1,0); // The front cell is updated when creating or recognizing a bundle
					m_bundleMap[2][0] = previousObservation.getBundle(2,0);	
					m_kinematicStimulation = Ernest.STIMULATION_KINEMATIC_BUMP;
					status = false;
				}
			}
			if (schema.getLabel().equals("^"))
			{
				// Turn left
				m_bundleMap[0][0] = previousObservation.getBundle(0,1);
				m_bundleMap[0][1] = previousObservation.getBundle(0,2);
				m_bundleMap[0][2] = previousObservation.getBundle(1,2);
				
				if (!getTactileStimulation(1,2).equals(Ernest.STIMULATION_TOUCH_EMPTY))
					m_bundleMap[1][2] = previousObservation.getBundle(2,2);
				else
					m_bundleMap[1][2] = null;
				m_bundleMap[2][2] = previousObservation.getBundle(2,1);
				m_bundleMap[2][1] = previousObservation.getBundle(2,0);
				m_bundleMap[2][0] = previousObservation.getBundle(1,0);
				m_bundleMap[1][0] = previousObservation.getBundle(0,0); // The front cell is updated when adjusting the observation

				m_bundleMap[1][1] = previousObservation.getBundle(1,1);
				
				if (m_bundleMap[1][0] == null || m_bundleMap[1][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_FORWARD))
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
				m_bundleMap[0][0] = previousObservation.getBundle(1,0);
				m_bundleMap[1][0] = previousObservation.getBundle(2,0);  // The front cell is updated when adjusting the observation
				m_bundleMap[2][0] = previousObservation.getBundle(2,1);
				m_bundleMap[2][1] = previousObservation.getBundle(2,2);
				m_bundleMap[2][2] = previousObservation.getBundle(1,2);
				if (!getTactileStimulation(1,2).equals(Ernest.STIMULATION_TOUCH_EMPTY))
					m_bundleMap[1][2] = previousObservation.getBundle(0,2);
				else
					m_bundleMap[1][2] = null;
				m_bundleMap[0][2] = previousObservation.getBundle(0,1);
				m_bundleMap[0][1] = previousObservation.getBundle(0,0);

				m_bundleMap[1][1] = previousObservation.getBundle(1,1);
				
				if (m_bundleMap[1][0] == null || m_bundleMap[1][0].getKinematicStimulation().equals(Ernest.STIMULATION_KINEMATIC_FORWARD)) 
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
		m_previousFocusBundle = previousObservation.getFocusBundle();
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

	public void setFocusBundle(IBundle bundle)
	{
		m_focusBundle = bundle;
	}
	
	public IBundle getFocusBundle()
	{
		return m_focusBundle;
	}

	public void setDirection(float direction) 
	{
		m_direction = direction;
	}

	public float getDirection() 
	{
		return m_direction;
	}

	public void setPreviousDirection(float direction) 
	{
		m_previousDirection = direction;		
	}

	public float getPreviousDirection() 
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

	public int getPreviousAttractiveness() 
	{
		return m_previousAttractiveness;
	}

}
