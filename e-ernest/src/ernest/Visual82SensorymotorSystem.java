package ernest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implement Ernest 8.2's sensorymotor system. Extend the BinarySensorymotorSystem.
 * Can detect a two-pixel matrix. Each pixel represents the distance to a blue square in the corresponding visual field.
 * The sensed features correspond to changes in each visual fields.
 * @author ogeorgeon
 */
public class Visual82SensorymotorSystem  extends BinarySensorymotorSystem
{

	/** The values of the pixels */
	private int m_currentLeftPixel   = Ernest.INFINITE;
	private int m_currentRightPixel  = Ernest.INFINITE;
	private int m_previousLeftPixel  = Ernest.INFINITE;
	private int m_previousRightPixel = Ernest.INFINITE;
	
	
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
		// Computes the act's label from the features returned by the sensory system and the status.
		
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
		
		IAct enactedAct = m_episodicMemory.addAct(label, schema, status, satisfaction, Ernest.RELIABLE_NOEME);
		
		return enactedAct;
	}
	
	/**
	 * Convert the matrix provided by the environment into an icon.
	 * Update the currently sensed icon. 
	 * @param matrix The matrix sensed in the environment. 
	 */
	public void senseMatrix(int[][] matrix) 
	{
		m_previousLeftPixel  = m_currentLeftPixel;
		m_previousRightPixel = m_currentRightPixel;
		m_currentLeftPixel   = matrix[0][0];
		m_currentRightPixel  = matrix[1][0];
		
		m_satisfaction = 0;
		
		// The sensed features correspond to changes in the pixels.
		m_leftFeature  = sensePixel(m_previousLeftPixel, m_currentLeftPixel);
		m_rightFeature = sensePixel(m_previousRightPixel, m_currentRightPixel);		
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
			satisfaction = 100;
		}

		// appear
		else if (previousPixel == Ernest.INFINITE && currentPixel < Ernest.INFINITE)
		{
			feature = "*";
			satisfaction = 150;
		}
		
		// disappear
		else if (previousPixel < Ernest.INFINITE && currentPixel == Ernest.INFINITE)
		{
			feature = "o";
			satisfaction = -150;
		}

		System.out.println("Sensed " + feature);
		
		m_satisfaction += satisfaction;

		return feature;
	}
	
}