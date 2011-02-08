package ernest;

/**
 * Implement Ernest 8.3's sensorymotor system. 
 * Can detect a two-pixel matrix. Each pixel represents the distance to a blue square in the corresponding visual field.
 * Generate sensed features that correspond to changes in the visual fields.
 * @author ogeorgeon
 */
public class Visual83SensorymotorSystem  extends BinarySensorymotorSystem
{

	/** The values of the pixels */
	private int m_currentLeftPixel   = Ernest.INFINITE;
	private int m_currentRightPixel  = Ernest.INFINITE;
	private int m_previousLeftPixel  = Ernest.INFINITE;
	private int m_previousRightPixel = Ernest.INFINITE;
	
	private int m_currentBlueCount   = 0;
	private int m_previousBlueCount  = 0;
	
	/** The features that are sensed by the distal system. */
	private String m_feature = " ";
	
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
		
		if (!m_feature.equals(" "))
			label = label + m_feature ;
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
		m_previousLeftPixel  = m_currentLeftPixel;
		m_previousRightPixel = m_currentRightPixel;
		m_previousBlueCount  = m_currentBlueCount;
		m_currentLeftPixel   = matrix[0][0];
		m_currentRightPixel  = matrix[1][0];
		int blink            = matrix[2][0];
		m_currentBlueCount   = (matrix[0][0] < Ernest.INFINITE ? 1 : 0) + (matrix[1][0] < Ernest.INFINITE ? 1 : 0);
		
		m_satisfaction = 0;
		
		m_feature = " ";
		
		// Arrived
		if (m_currentLeftPixel == 0)
		{
			m_feature = "x";
			m_satisfaction = 200;
		}
		
		// Closer on any of the pixels
		// (it may appear or disappear on the other pixel but we don't care)
		else if ((m_previousLeftPixel < Ernest.INFINITE && m_currentLeftPixel < m_previousLeftPixel) ||
				(m_previousRightPixel < Ernest.INFINITE && m_currentRightPixel < m_previousRightPixel))
		{
			m_feature = "+";
			m_satisfaction = 200;
		}

		// More blue 
		// (The total number of blue pixels has increased either from blank or from a single blue pixel) 
		else if (m_currentBlueCount > m_previousBlueCount)
		{
			m_feature = "*";
			m_satisfaction = 50;
		}
		
		// Less blue 
		// (The total number of blue pixels has decreased) 
		else if (m_currentBlueCount < m_previousBlueCount)
		{
			m_feature = "-";
			m_satisfaction = - 150;
		}
		
		// empty (emptiness makes Ernest unhappy)
		// See a blink while turning
		// (no intrinsic satisfaction othewise Ernest keeps turning for blinking)
		else if (blink == 1)
		{
			m_feature = ".";
			m_satisfaction = 0;
		}

		// empty (emptiness makes Ernest unhappy)
		else if (m_currentBlueCount == 0)
		{
			m_feature = "o";
			m_satisfaction = -50;
		}

		System.out.println("Sensed " + m_feature);
	}
}
