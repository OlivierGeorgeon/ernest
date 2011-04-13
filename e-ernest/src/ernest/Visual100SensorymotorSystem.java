package ernest;

import java.awt.Color;

import org.w3c.dom.Element;

/**
 * Implement Ernest 10.0's sensorymotor system.
 * Ernest 10.0 has a visual resolution of 2x12 pixels and a kinesthetic resolution of 3x3 pixels.
 * @author ogeorgeon
 */
public class Visual100SensorymotorSystem  extends BinarySensorymotorSystem
{

	/** The observations */
	private IObservation m_currentObservation = new Observation();
	private IObservation m_previousObservation;
	
	private ILandmark m_wallAhead;
	
	/** The taste of water */
	private int TASTE_WATER = 1;

	/** The taste of food */
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
			m_staticSystem.bump(m_wallAhead);
		
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
			m_tracer.addSubelement(retinaElmt, "pixel_" + i, colliculus[i][0].getLandmark().getHexColor());				
		
		// Shift observations
		
		m_previousObservation  = m_currentObservation;

		// The new observation is the most motivating observation in the colliculus
		
		m_currentObservation = m_staticSystem.salientObservation(colliculus);
		m_currentObservation.setDynamicFeature(m_previousObservation);
		
		if (m_currentObservation.getDirection() >= 50 && m_currentObservation.getDirection() <= 60) // looking forward, then if bumped, that would be a wall
			m_wallAhead = m_currentObservation.getLandmark();

		m_currentObservation.trace(m_tracer, "current_observation");
		
		// Taste =====
		
		int taste = matrix[0][8];
		
		if (taste == TASTE_WATER)
			m_staticSystem.drink(colliculus[0][1].getLandmark()); // Look for the landmark under Ernest's feet
		if (taste == TASTE_FOOD)
			m_staticSystem.eat(colliculus[0][1].getLandmark());
		if (taste > TASTE_FOOD)
			m_staticSystem.visit(colliculus[0][1].getLandmark());
		
		// Kinesthetic =====
		Element s = m_tracer.addEventElement("somatotopic_map", "");
		int [][] somatoMap = new int[3][3];
		for (int j = 0; j < 3; j++)
			for (int i = 0; i < 3; i++)
			{
				somatoMap[i][j] = matrix[i][9 + j];
				m_tracer.addSubelement(s, "cell_" + i + "_" + j, somatoMap[i][j] + "");
			}
	}	
	
	public Color getColor()
	{
		ILandmark l = m_currentObservation.getLandmark();
		if (l == null)
			return Ernest.WALL_COLOR;
		else
			return l.getColor();
	}
}