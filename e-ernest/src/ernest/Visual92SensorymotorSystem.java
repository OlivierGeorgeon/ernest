package ernest;

import java.awt.Color;

import org.w3c.dom.Element;

/**
 * Implement Ernest 9.0's sensorymotor system. 
 * Recieves a matrix of EyeFixation objects from the environment.
 * @author ogeorgeon
 */
public class Visual92SensorymotorSystem  extends BinarySensorymotorSystem
{

	/** The observations */
	private IObservation m_currentObservation;
	private IObservation m_previousObservation;
	
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
		
		String label = schema.getLabel() + m_currentObservation.getDynamicFeature();
		
		if (status)
			label = "(" + label + ")";
		else 
			label = "[" + label + "]";
		
		// Bump into a landmark TODO make sure we declare the good landmark bumped
		if (label.equals("[>]"))
			m_staticSystem.bump(m_currentObservation.getLandmark());
		
		// Compute the act's satisfaction 
		
		int satisfaction = m_currentObservation.getSatisfaction() + schema.resultingAct(status).getSatisfaction();  
		
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
		
		IObservation[][] colliculus = new Observation[Ernest.RESOLUTION_RETINA][2];
		for (int i = 0; i < Ernest.RESOLUTION_RETINA; i++)
		{
			colliculus[i][0] = new Observation();
			colliculus[i][0].setLandmark(m_staticSystem.addLandmark(matrix[i][1], matrix[i][2], matrix[i][3]));
			colliculus[i][0].setDistance(matrix[i][0]);
			colliculus[i][1] = new Observation();
			colliculus[i][1].setLandmark(m_staticSystem.addLandmark(matrix[i][5], matrix[i][6], matrix[i][7]));
			colliculus[i][1].setDistance(matrix[i][4]);
		}

		// Trace the retina
		Element retinaElmt = m_tracer.addEventElement("retina", "");
		for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0 ; i--)
		{
			m_tracer.addSubelement(retinaElmt, "pixel_0_" + i, colliculus[i][0].getLandmark().getHexColor());			
		}
		
		for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0 ; i--)
		{
			m_tracer.addSubelement(retinaElmt, "pixel_1_" + i, colliculus[i][1].getLandmark().getHexColor());			
		}
		
		// Shift observations
		m_previousObservation  = m_currentObservation;
		m_currentObservation = m_staticSystem.focusObservation(m_previousObservation, colliculus);
				
		// Trace
		
		m_currentObservation.trace(m_tracer, "current_observation");
		
		// When Ernest enters a landmark's vicinity, he checks in at the landmark.

		if (m_currentObservation.getDistance() <= PROXIMITY_DISTANCE)
			m_staticSystem.check(m_currentObservation.getLandmark());
		if (m_currentObservation.getDistance() <= PROXIMITY_DISTANCE) 
			m_staticSystem.check(m_currentObservation.getLandmark());
				
		// Taste =====
		
		int taste = matrix[Ernest.RESOLUTION_RETINA][0];
		
		if (taste == TASTE_WATER && m_currentObservation.getDistance() == 0)
			m_staticSystem.drink(m_currentObservation.getLandmark());
		if (taste == TASTE_FOOD && m_currentObservation.getDistance() == 0)
			m_staticSystem.eat(m_currentObservation.getLandmark());
		if (taste > TASTE_FOOD && m_currentObservation.getDistance() == 0)
			m_staticSystem.visit(m_currentObservation.getLandmark());
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
		
		//m_satisfaction += satisfaction;

		return feature;
	}
	
}