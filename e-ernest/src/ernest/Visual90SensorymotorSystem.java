package ernest;

import java.awt.Color;

import org.w3c.dom.Element;

/**
 * Implement Ernest 9.0's sensorymotor system. 
 * Recieves a matrix of EyeFixation objects from the environment.
 * @author ogeorgeon
 */
public class Visual90SensorymotorSystem  extends BinarySensorymotorSystem
{

	/** The values of the pixels */
	private int m_currentLeftDistance   = Ernest.INFINITE;
	private int m_currentRightDistance  = Ernest.INFINITE;
	private int m_previousLeftDistance  = Ernest.INFINITE;
	private int m_previousRightDistance = Ernest.INFINITE;
	
	private ILandmark m_currentLeftLandmark;
	private ILandmark m_currentRightLandmark;
	private ILandmark m_previousLeftLandmark;
	private ILandmark m_previousRightLandmark;
	
	/** The features that are sensed by the distal system. */
	private String m_leftFeature = " ";
	private String m_rightFeature = " ";
	
	/** The intrinsic satisfaction of sensing the current features */
	private int m_satisfaction = 0;
	
	private int PROXIMITY_DISTANCE = 21; // two squares straight or one in diagonal
	
	/** The taste of water */
	private int TASTE_WATER = 1;

	/** The taste of glucose */
	private int TASTE_FOOD = 2;
	
	/**
	 * Determine the enacted act.
	 * Made from the binary feedback status plus the features provided by the distal sensory system. 
	 * @param schema The selected schema. 
	 * @param status The binary feedback
	 */
	public IAct enactedAct(ISchema schema, boolean status)
	{
		// The schema is null during the first cycle, then the enacted act is null.
		if (schema == null) return null;
		
		m_tracer.addEventElement("primitive_enacted_schema", schema.getLabel());
		m_tracer.addEventElement("primitive_feedback", new Boolean(status).toString());

		// Computes the act's label from the features returned by the sensory system and from the status.
		
		String label = schema.getLabel();
		
		if (!m_leftFeature.equals(" ") || !m_rightFeature.equals(" ") )
			label = label + "" + m_leftFeature + "|" + m_rightFeature;
		if (status)
			label = "(" + label + ")";
		else 
			label = "[" + label + "]";
		
		// Bump into a landmark
		if (label.equals("[>]") && m_currentLeftLandmark.equals(m_currentRightLandmark))
			m_staticSystem.bump(m_currentLeftLandmark);
		
		// Compute the act's satisfaction 
		
		int satisfaction = m_satisfaction + schema.resultingAct(status).getSatisfaction();  
		
		m_tracer.addEventElement("primitive_satisfaction", satisfaction + "");
		
		// Create the act in episodic memory if it does not exist.
		
		IAct enactedAct = m_episodicMemory.addAct(label, schema, status, satisfaction, Ernest.RELIABLE);
		
		return enactedAct;
	}
	
	/**
	 * Generate sensory features from the sensed matrix sent by the environment.
	 * @param matrix The matrix sensed in the environment. 
	 */
	public void senseMatrix(int[][] matrix) 
	{
		// Vision =====
		
		m_previousLeftDistance  = m_currentLeftDistance;
		m_previousRightDistance = m_currentRightDistance;
		m_currentLeftDistance   = matrix[0][0];
		m_currentRightDistance  = matrix[1][0];
		
		m_previousLeftLandmark  = m_currentLeftLandmark;
		m_previousRightLandmark = m_currentRightLandmark;
		m_currentLeftLandmark   = m_staticSystem.addLandmark(matrix[0][1], matrix[0][2], matrix[0][3]);
		m_currentRightLandmark  = m_staticSystem.addLandmark(matrix[1][1], matrix[1][2], matrix[1][3]);
		
		// Trace 
		
		Element leftElement = m_tracer.addEventElement("eye_left", "");
		m_tracer.addSubelement(leftElement, "color", m_currentLeftLandmark.getHexColor());
		m_tracer.addSubelement(leftElement, "distance", m_currentLeftDistance + "");
		m_tracer.addSubelement(leftElement, "time_to_water", m_currentLeftLandmark.getDistanceToWater() + "");
		m_tracer.addSubelement(leftElement, "time_to_food", m_currentLeftLandmark.getDistanceToFood() + "");
		m_tracer.addSubelement(leftElement, "last_checked", m_currentLeftLandmark.getLastTimeChecked() + "");
		
		Element rightElement = m_tracer.addEventElement("eye_right", "");
		m_tracer.addSubelement(rightElement, "color", m_currentRightLandmark.getHexColor());
		m_tracer.addSubelement(rightElement, "distance", m_currentRightDistance + "");
		m_tracer.addSubelement(rightElement, "time_to_water", m_currentRightLandmark.getDistanceToWater() + "");
		m_tracer.addSubelement(rightElement, "time_to_food", m_currentRightLandmark.getDistanceToFood() + "");
		m_tracer.addSubelement(rightElement, "last_checked", m_currentRightLandmark.getLastTimeChecked() + "");
		
		// Inhibited landmarks are not processed for dynamic visual features. 
		// nor checked in.
		// TODO Inhibited landmarks are not even transmitted to the visual system
		
		if (m_staticSystem.isInhibited(m_currentLeftLandmark))
			m_currentLeftDistance = Ernest.INFINITE;
		if (m_staticSystem.isInhibited(m_currentRightLandmark))
			m_currentRightDistance = Ernest.INFINITE;
			
		// When Ernest enters a landmark's vicinity, he checks in at the landmark.

		if (m_currentLeftDistance <= PROXIMITY_DISTANCE)
			m_staticSystem.check(m_currentLeftLandmark);
		if (m_currentRightDistance <= PROXIMITY_DISTANCE && !m_currentLeftLandmark.equals(m_currentRightLandmark)) 
			m_staticSystem.check(m_currentRightLandmark);
		
		m_satisfaction = 0;
		
		// Compute the dynamic visual features that reflect changes in how uninhibited landmarks are seen.
		
		m_leftFeature  = sensePixel(m_previousLeftDistance, m_currentLeftDistance, m_previousLeftLandmark, m_currentLeftLandmark);
		m_rightFeature = sensePixel(m_previousRightDistance, m_currentRightDistance, m_previousRightLandmark, m_currentRightLandmark);		
		
		m_tracer.addSubelement(leftElement, "dynamic_feature", m_leftFeature);
		m_tracer.addSubelement(rightElement, "dynamic_feature", m_rightFeature);
		
		// Taste =====
		
		int taste = matrix[2][0];
		
		if (taste == TASTE_WATER && m_currentLeftDistance == 0)
			m_staticSystem.drink(m_currentLeftLandmark);
		if (taste == TASTE_FOOD && m_currentLeftDistance == 0)
			m_staticSystem.eat(m_currentLeftLandmark);
		if (taste > TASTE_FOOD && m_currentLeftDistance == 0)
			m_staticSystem.visit(m_currentLeftLandmark);
	}

	/**
	 * Sense the feature based on a pixel change 
	 * @param previousPixel The pixel's previous value.
	 * @param currentPixel The pixel's current value.
	 * @return The sensed feature
	 */
	private String sensePixel(int previousDistance, int currentDistance, ILandmark previousLandmark, ILandmark currentLandmark) 
	{
		String feature = " ";
		int satisfaction = 0;
		
		// arrived
		if (previousDistance > currentDistance && currentDistance == 0)
		{
			feature = "x";
			satisfaction = 100;
		}
		
		// closer
		else if (previousDistance < Ernest.INFINITE && currentDistance < previousDistance 
				&& currentLandmark.equals(previousLandmark))
		{
			feature = "+";
			satisfaction = 200;
		}

		// appear
		else if (previousDistance == Ernest.INFINITE && currentDistance < Ernest.INFINITE)
		{
			feature = "*";
			satisfaction = 50;
		}
		
		// disappear
		//else if (previousPixel < Ernest.INFINITE && currentPixel == Ernest.INFINITE)
		//else if (currentDistance  == Ernest.INFINITE || !currentLandmark.equals(previousLandmark))
		else if (previousDistance < Ernest.INFINITE 
				&& (currentDistance  == Ernest.INFINITE || !currentLandmark.equals(previousLandmark)))
		{
			feature = "o";
			satisfaction = -100;
		}

		System.out.println("Sensed " + feature);
		
		m_satisfaction += satisfaction;

		return feature;
	}
	
}