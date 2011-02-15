package ernest;

import java.awt.Color;

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
	
	/** The features that are sensed by the distal system. */
	private String m_leftFeature = " ";
	private String m_rightFeature = " ";
	
	/** The intrinsic satisfaction of sensing the current features */
	private int m_satisfaction = 0;
	
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
		
		// Computes the act's label from the features returned by the sensory system and from the status.
		
		String label = schema.getLabel();
		
		if (!m_leftFeature.equals(" ") || !m_rightFeature.equals(" ") )
			label = label + "" + m_leftFeature + "|" + m_rightFeature;
		if (status)
			label = "(" + label + ")";
		else 
			label = "[" + label + "]";
			
		// Compute the act's satisfaction 
		
		int satisfaction = m_satisfaction + schema.resultingAct(status).getSatisfaction();  
		
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
		m_previousLeftDistance  = m_currentLeftDistance;
		m_previousRightDistance = m_currentRightDistance;
		m_currentLeftDistance   = matrix[0][0];
		m_currentRightDistance  = matrix[1][0];
		
		m_currentLeftLandmark   = m_episodicMemory.addLandmark(matrix[0][1], matrix[0][2], matrix[0][3]);
		m_currentRightLandmark  = m_episodicMemory.addLandmark(matrix[1][1], matrix[1][2], matrix[1][3]);
		
		
		// TODO: See other goals
		if (!m_currentLeftLandmark.equals(m_attentionalSystem.getGoalLandmark()))
		{
			m_currentLeftDistance = Ernest.INFINITE;
			if (m_currentLeftLandmark.isSingularity())
				m_attentionalSystem.setNearbyLandmark(m_currentLeftLandmark);
		}
		if (!m_currentRightLandmark.equals(m_attentionalSystem.getGoalLandmark()))
		{
			m_currentRightDistance = Ernest.INFINITE;
			if (m_currentRightLandmark.isSingularity())
				m_attentionalSystem.setNearbyLandmark(m_currentRightLandmark);
		}
		
		m_satisfaction = 0;
		
		// The sensed features correspond to changes in the pixels.
		m_leftFeature  = sensePixel(m_previousLeftDistance, m_currentLeftDistance);
		m_rightFeature = sensePixel(m_previousRightDistance, m_currentRightDistance);		
		
		if (m_leftFeature.equals("o") && m_rightFeature.equals("o"))
			m_satisfaction = -100;
	}

	/**
	 * Sense the feature based on a pixel change 
	 * @param previousPixel The pixel's previous value.
	 * @param currentPixel The pixel's current value.
	 * @return The sensed feature
	 */
	private String sensePixel(int previousPixel, int currentPixel) 
	{
		String feature = " ";
		int satisfaction = 0;
		
		// arrived
		if (previousPixel > currentPixel && currentPixel == 0)
		{
			feature = "x";
			satisfaction = 100;
		}
		
		// closer
		else if (previousPixel < Ernest.INFINITE && currentPixel < previousPixel)
		{
			feature = "+";
			satisfaction = 200;
		}

		// appear
		else if (previousPixel == Ernest.INFINITE && currentPixel < Ernest.INFINITE)
		{
			feature = "*";
			satisfaction = 50;
		}
		
		// disappear
		else if (previousPixel < Ernest.INFINITE && currentPixel == Ernest.INFINITE)
		{
			feature = "o";
			satisfaction = -100;
		}

		System.out.println("Sensed " + feature);
		
		m_satisfaction += satisfaction;

		return feature;
	}
	
}