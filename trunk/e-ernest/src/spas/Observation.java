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

	/** Ernest's internal clock  */
	//private int m_clock;

	/** Ernest's persistence memory  */
	//private PersistenceSystem m_persistenceMemory = new PersistenceSystem();
	
	private int m_direction = Ernest.CENTER_RETINA;
	private int m_previousDirection = Ernest.CENTER_RETINA;
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
		// Return the salient bundle's color
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
		tracer.addSubelement(e, "stimuli", m_stimuli);
		tracer.addSubelement(e, "dynamic_feature", m_visualStimuli);
		
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
		
		int minFovea = Ernest.CENTER_RETINA - 30; // 25;
		int maxFovea = Ernest.CENTER_RETINA + 30; // 85;
		
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
					if (minFovea >= m_direction)
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
			if (m_bundleMap[1][1] != null)
			{
				//m_bundleMap[1][1].setGustatoryStimulation(m_gustatoryStimulation);
				m_bundleMap[1][1] = null; // The fish disappears from the local map (into Ernest's stomach) !
				
			}
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
	
	public int getColor(int x, int y)
	{
		int c = 0;
		if (m_kinematicStimulation != null && Ernest.STIMULATION_KINEMATIC_BUMP.equals(m_kinematicStimulation) && (x == 1) && (y == 0))
			c = 255 * 65536; // red
		else
		{
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
		}		
		return c;
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
		            salience.setValue(Ernest.STIMULATION_TOUCH_WALL.getValue()); 
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
            salience.setValue(Ernest.STIMULATION_TOUCH_WALL.getValue()); 
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

	public void setTactileMap(IBundle bundleFish)
	{
		
		// Check if there is already a fish bundle in the local map.
		boolean grayBundle = false;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (m_bundleMap[i][j] != null && m_bundleMap[i][j].getGustatoryStimulation().equals(Ernest.STIMULATION_GUSTATORY_FISH)) 
					grayBundle = true;
		
		// If there is no gray bundle yet, then create a gray bundle if a fish is touched.
		if (!grayBundle)
		{
			if (m_tactileMap[1][0].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundleMap[1][0] == null)
				m_bundleMap[1][0] = bundleFish;
			else if (m_tactileMap[0][0].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundleMap[0][0] == null)
				m_bundleMap[0][0] = bundleFish;
			else if (m_tactileMap[2][0].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundleMap[2][0] == null)
				m_bundleMap[2][0] = bundleFish;
			else if (m_tactileMap[0][1].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundleMap[0][1] == null)
				m_bundleMap[0][1] = bundleFish;
			else if (m_tactileMap[2][1].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundleMap[2][1] == null)
				m_bundleMap[2][1] = bundleFish;
			else if (m_tactileMap[0][2].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundleMap[0][2] == null)
				m_bundleMap[0][2] = bundleFish;
			else if (m_tactileMap[2][2].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundleMap[2][2] == null)
				m_bundleMap[2][2] = bundleFish;
			else if (m_tactileMap[1][2].equals(Ernest.STIMULATION_TOUCH_FISH) && m_bundleMap[1][2] == null)
				m_bundleMap[1][2] = bundleFish;
		}
		
	}

	/**
	 * Update the current spatial system based on the anticipated spatial system and on to the sensory stimulations.
	 * @param visualCortex The set of visual stimulations in the visual cortex.
	 * @param tactileCortex The set of tactile stimulations in the tactile cortex.
	 * @param kinematicStimulation The kinematic stimulation.
	 * @param gustatoryStimulation The gustatory stimulation.
	 * @return A pointer to the current observation that has been updated.
	 */
//	public void adjust(IStimulation[] visualCortex, IStimulation[][] tactileCortex, IStimulation kinematicStimulation, IStimulation gustatoryStimulation)
//	{
//		List<ISalience> saliences = new ArrayList<ISalience>(Ernest.RESOLUTION_COLLICULUS);
//		//EColor frontColor = null;
//		IStimulation frontVisualStimulation = null;
//		
//		// Create a List of the various saliences in the visual field
//
//		IStimulation stimulation = visualCortex[0];
//		int span = 1;
//		int sumDirection = 0;
//		for (int i = 1 ; i < Ernest.RESOLUTION_RETINA; i++)
//		{
//			if (visualCortex[i].equals(stimulation))
//			{
//				// measure the salience span and average direction
//				span++;
//				sumDirection += i * 10;
//			}
//			else 
//			{	
//				// record the previous salience
//				ISalience salience = new Salience();
//				salience.setDirection((int) (sumDirection / span + .5));
//				salience.setDistance(stimulation.getDistance());
//				salience.setSpan(span);
//				salience.setValue(stimulation.getValue());
//				salience.setBundle(m_persistenceMemory.seeBundle(stimulation));
//				salience.setAttractiveness(m_persistenceMemory.attractiveness(stimulation) + 5 * span );
//				saliences.add(salience);
//				if (salience.getDirection() >= 50 &&  salience.getDirection() <= 60 && span >= 3 )
//					//frontColor = stimulation.getColor();
//					frontVisualStimulation = stimulation;
//				// look for the next salience
//				stimulation = visualCortex[i];
//				span = 1;
//				sumDirection = i * 10;
//			}
//		}
//		// record the last salience
//		ISalience last = new Salience();
//		last.setDirection((int) (sumDirection / span + .5));
//		last.setDistance(stimulation.getDistance());
//		last.setSpan(span);
//		last.setValue(stimulation.getValue());
//		last.setBundle(m_persistenceMemory.seeBundle(stimulation));
//		last.setAttractiveness(m_persistenceMemory.attractiveness(stimulation) + 5 * span );
//		saliences.add(last);
//		if (last.getDirection() >= 50 &&  last.getDirection() <= 60 && span >= 3 )
//			frontVisualStimulation = stimulation;
//			//frontColor = stimulation.getColor();
//
//		// Tactile salience of fish 
//		// Generates fictitious bundles when touching a fish (this helps).
//		// TODO use touch fish-eat bundles
//		
//		setMap(tactileCortex);
//		setTactileMap();
//		
//		// Tactile salience of walls.
//		
//		ISalience tactileSalience = getTactileSalience();
//		if (tactileSalience != null)
//			saliences.add(tactileSalience);
//		
//
//		// Add the various saliences in the local map to the list
//		// Each bundle in the local map creates a salience.
//		
//		if (getBundle(1, 0) != null)
//		{
//			ISalience salience = new Salience();
//			salience.setDirection(55);
//			salience.setSpan(4);
//			salience.setValue(getBundle(1, 0).getValue());
//			salience.setBundle(getBundle(1, 0));
//			salience.setAttractiveness(getBundle(1, 0).getAttractiveness(m_clock) + 20);
//			saliences.add(salience);
//		}
//		else if (getBundle(0, 0) != null)
//		{
//			ISalience salience = new Salience();
//			salience.setDirection(85);
//			salience.setSpan(4);
//			salience.setValue(getBundle(0, 0).getValue());
//			salience.setBundle(getBundle(0, 0));
//			salience.setAttractiveness(getBundle(0, 0).getAttractiveness(m_clock) + 20);
//			saliences.add(salience);
//		}
//		else if (getBundle(2, 0) != null)
//		{
//			ISalience salience = new Salience();
//			salience.setDirection(25);
//			salience.setSpan(4);
//			salience.setValue(getBundle(2, 0).getValue());
//			salience.setBundle(getBundle(2, 0));
//			salience.setAttractiveness(getBundle(2, 0).getAttractiveness(m_clock) + 20);
//			saliences.add(salience);
//		}
//		else if (getBundle(0, 1) != null)
//		{
//			ISalience salience = new Salience();
//			salience.setDirection(110);
//			salience.setSpan(4);
//			salience.setValue(getBundle(0, 1).getValue());
//			salience.setBundle(getBundle(0, 1));
//			salience.setAttractiveness(getBundle(0, 1).getAttractiveness(m_clock) + 20);
//			saliences.add(salience);
//		}
//		else if (getBundle(2, 1) != null)
//		{
//			ISalience salience = new Salience();
//			salience.setDirection(0);
//			salience.setSpan(4);
//			salience.setValue(getBundle(2, 1).getValue());
//			salience.setBundle(getBundle(2, 1));
//			salience.setAttractiveness(getBundle(2, 1).getAttractiveness(m_clock) + 20);
//			saliences.add(salience);
//		}
//		else if (getBundle(0, 2) != null)
//		{
//			ISalience salience = new Salience();
//			salience.setDirection(140);
//			salience.setSpan(4);
//			salience.setValue(getBundle(0, 2).getValue());
//			salience.setBundle(getBundle(0, 2));
//			salience.setAttractiveness(getBundle(0, 2).getAttractiveness(m_clock) + 20);
//			saliences.add(salience);
//		}
//		else if (getBundle(2, 2) != null)
//		{
//			ISalience salience = new Salience();
//			salience.setDirection(-25);
//			salience.setSpan(4);
//			salience.setValue(getBundle(2, 2).getValue());
//			salience.setBundle(getBundle(2, 2));
//			salience.setAttractiveness(getBundle(2, 2).getAttractiveness(m_clock) + 20);
//			saliences.add(salience);
//		}
//		
//		// Find the most attractive salience in the list (There is at least a wall)
//		
//		int maxAttractiveness = 0;
//		int direction = 0;
//		for (ISalience salience : saliences)
//			if (Math.abs(salience.getAttractiveness()) > Math.abs(maxAttractiveness))
//			{
//				maxAttractiveness = salience.getAttractiveness();
//				direction = salience.getDirection();
//				setSalience(salience);
//				setFocusBundle(salience.getBundle());
//			}
//
//		setAttractiveness(maxAttractiveness);
//		setDirection(direction);
//		
//		// Taste
//		
//		setGustatory(gustatoryStimulation);
//		if (gustatoryStimulation.equals(Ernest.STIMULATION_GUSTATORY_FISH))
//		{
//			if (getBundle(1, 1) != null && !getBundle(1, 1).getGustatoryStimulation().equals(Ernest.STIMULATION_GUSTATORY_FISH))
//			{
//				getBundle(1, 1).setGustatoryStimulation(gustatoryStimulation);
//				getBundle(1, 1).trace(m_tracer, "bundle");				
//			}
//		}
//		
//		// Kinematic
//		
//		setConfirmation(kinematicStimulation.equals(getKinematic()));
//		setKinematic(kinematicStimulation);
//
//		// If the current stimulation does not match the anticipated local map then the local map is cleared.
//		// TODO The criteria for deciding whether the matching is correct or incorrect need to be learned ! 
//
//		if (getBundle(1, 1) != null && getBundle(1, 1).getTactileStimulation().equals(Ernest.STIMULATION_TOUCH_WALL))
//			clearMap();
//		
//		// Check peripersonal space.
//		
//		setTactileMap();
//
//		// Bundle the visual icon with the tactile stimulation in front
//		
//		if (frontVisualStimulation != null )
//		{
//			if (!tactileCortex[1][0].equals(Ernest.STIMULATION_TOUCH_EMPTY))		
//			{
//				IBundle bundle = m_persistenceMemory.addBundle(frontVisualStimulation, tactileCortex[1][0]);
//				setFrontBundle(bundle);
//			}
//		}
//	}

}
