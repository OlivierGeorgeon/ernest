package ernest;

import java.awt.Color;

import org.w3c.dom.Element;

/**
 * Implement Ernest 9.2's sensorymotor system.
 * Demoed here http://e-ernest.blogspot.com/2011/03/ernest-92.html 
 * Ernest 9.2 has a visual resolution of 2x12 pixels.
 * @author ogeorgeon
 */
public class Visual92SensorymotorSystem  extends BinarySensorymotorSystem
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

		m_wallAhead = colliculus[6][1].getLandmark();
		
		// Trace the retina
		
		Element retinaElmt = m_tracer.addEventElement("retina", "");
		for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0 ; i--)
			m_tracer.addSubelement(retinaElmt, "pixel_0_" + i, colliculus[i][0].getLandmark().getHexColor());				
		for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0 ; i--)
			m_tracer.addSubelement(retinaElmt, "pixel_1_" + i, colliculus[i][1].getLandmark().getHexColor());			
		
		// Shift observations
		
		m_previousObservation  = m_currentObservation;

		// The new observation is the most motivating observation in the colliculus
		
		m_currentObservation = m_staticSystem.salientObservation(colliculus);
		m_currentObservation.setDynamicFeature(m_previousObservation);
		
		// If shift to a more motivating landmark then check the previous landmark
		ILandmark previousLandmark = m_previousObservation.getLandmark();
		//if (previousLandmark != null && !previousLandmark.equals(m_currentObservation.getLandmark())
		//		&& m_currentObservation.getMotivation() > m_previousObservation.getMotivation())
		//	m_staticSystem.check(previousLandmark);
		
		m_currentObservation.trace(m_tracer, "current_observation");
		
		// Taste =====
		
		int taste = matrix[Ernest.RESOLUTION_RETINA][0];
		
		if (taste == TASTE_WATER)
			m_staticSystem.drink(colliculus[0][0].getLandmark()); // Look for the landmark under Ernest's feet
		if (taste == TASTE_FOOD)
			m_staticSystem.eat(colliculus[0][0].getLandmark());
		if (taste > TASTE_FOOD)
			m_staticSystem.visit(colliculus[0][0].getLandmark());
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