package ernest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implement Ernest's skills to process icons.
 * @author ogeorgeon
 */
public class IconicModule
{

	/** Used to break a tie when evoking an icon... */
	private static Random m_rand = new Random(); 
 
	/** A list of all the icons in the iconic module. */
	private List<IIcon> m_icons = new ArrayList<IIcon>(100);
	private List<IAct> m_pixelAnims = new ArrayList<IAct>(100);
	
	/** The icon currently sensed. */
	private IAct m_sensedIcon;
	
	private IAct[] m_pixelMatrix = new Act[2];

	private int[][] m_previousIcon = {{Ernest.INFINITE},{Ernest.INFINITE}};
	
	private String[] m_labels = new String[2];
	private int[] m_satisfactions = new int[2];
	
	public IAct enactedAct(ISchema schema)
	{
		String label = "(" + schema.getLabel() + "|" + m_labels[0] + "|" + m_labels[1] + ")";
		int satisfaction = m_satisfactions[0] + m_satisfactions[1];  
		IAct enacted = Act.createAct(label, schema, true, satisfaction, Ernest.CENTRAL, Ernest.RELIABLE_NOEME);
		
		int i = m_pixelAnims.indexOf(enacted);
		if (i == -1)
			// The icon does not exist
			m_pixelAnims.add(enacted);
		else 
			// The icon already exists: return a pointer to it.
			enacted =  m_pixelAnims.get(i);
		
		return enacted;
	}
	
	/**
	 * Create an icon if it does not yet exist. 
	 * @param label A string label defined by the modeler for trace and debug.
	 * @param matrix The sensor matrix. 
	 * @return The iconic nome that was created or retrieved
	 */
	public IAct addInteraction(String label, int[][] matrix) 
	{
		IIcon icon =  new Icon(label, matrix);

		int i = m_icons.indexOf(icon);
		if (i == -1)
			// The icon does not exist
			m_icons.add(icon);
		else 
			// The icon already exists: return a pointer to it.
			icon =  m_icons.get(i);
		return icon;
	}

	/**
	 * Convert the matrix provided by the environment into an icon.
	 * Update the currently sensed icon. 
	 * @param matrix The matrix sensed in the environment. 
	 */
	public void senseMatrix(int[][] matrix) 
	{
		// Sense the matrix as an icon
		IIcon icon =  new Icon(matrix);

		int i = m_icons.indexOf(icon);
		if (i == -1)
			// The icon does not exist
			m_icons.add(icon);
		else 
			// The icon already exists: return a pointer to it.
			icon =  m_icons.get(i);
		
		m_sensedIcon = icon;
		
		// sense the matrix as two animated pixels
		sensePixel(matrix[0][0], 0);
		sensePixel(matrix[1][0], 1);		
	}

	/**
	 * Generate or recognize a nome of type anime from a pixel.
	 * @param nextPixel The pixel's new value
	 * @param index The pixel index in the sensory matrix: 0 = left, 1 = right
	 */
	public void sensePixel(int nextPixel, int index) 
	{
		int previousPixel = m_previousIcon[index][0];
		m_previousIcon[index][0] = nextPixel;
		String label = "";
		int satisfaction = 0;
		
		// arrived
		if (previousPixel > nextPixel && nextPixel == 0)
		{
			label = "arrived";
			satisfaction = 1000;
		}
		
		// closer
		else if (previousPixel < Ernest.INFINITE && nextPixel < previousPixel)
		{
			label = "closer";
			satisfaction = 100;
		}

		// appear
		else if (previousPixel == Ernest.INFINITE && nextPixel < Ernest.INFINITE)
		{
			label = "appear";
			satisfaction = 100;
		}
		
		// disappear
		else if (previousPixel < Ernest.INFINITE && nextPixel == Ernest.INFINITE)
		{
			label = "disappear";
			satisfaction = -100;
		}


		
//		IAct pixelAnim = null;
//		if (!label.equals(""))
//		{
//			if (index == 0)
//				label = "(" + label + "-left)";
//			else
//				label = "(" + label + "-right)";
//			
//			pixelAnim = Act.createAct(label, null, true, satisfaction, Ernest.ICONIC, Ernest.RELIABLE_NOEME);
//			int i = m_pixelAnims.indexOf(pixelAnim);
//			if (i == -1)
//				// The icon does not exist
//				m_pixelAnims.add(pixelAnim);
//			else 
//				// The icon already exists: return a pointer to it.
//				pixelAnim =  m_pixelAnims.get(i);
//		}
		System.out.println("Sensed " + label);
		
		m_labels[index] = label;
		m_satisfactions[index] = satisfaction;
		//m_pixelMatrix[index] = pixelAnim;
	}
	
	/**
	 * Generate or recognize a nome of type anim from two matrix.
	 * @param matrix The matrix sensed in the environment. 
	 */
	public IAct senseAnim(int[][] previousMatrix, int[][] nextMatrix) 
	{
		String label = "";
		int satisfaction = 0;
		IAct anim = null;

		// appear right
		if (previousMatrix[0][0] == Ernest.INFINITE &&  previousMatrix[1][0] == Ernest.INFINITE
			&& nextMatrix[0][0] == Ernest.INFINITE &&  nextMatrix[1][0] < Ernest.INFINITE)
		{
			label = "appear-right";
			satisfaction = 100;
		}
		
		// appear left
		else if (previousMatrix[0][0] == Ernest.INFINITE &&  previousMatrix[1][0] == Ernest.INFINITE
				&& nextMatrix[0][0] < Ernest.INFINITE &&  nextMatrix[1][0] == Ernest.INFINITE)
		{
			label = "appear-left";
			satisfaction = 100;
		}
		
		// shift right left
		else if (previousMatrix[0][0] == Ernest.INFINITE &&  previousMatrix[1][0] < Ernest.INFINITE
				&& nextMatrix[0][0] < Ernest.INFINITE &&  nextMatrix[1][0] == Ernest.INFINITE)
		{
			label = "shift-right-left";
			satisfaction = 0;
		}
			
		// shift left right
		else if (previousMatrix[0][0] < Ernest.INFINITE &&  previousMatrix[1][0] == Ernest.INFINITE
				&& nextMatrix[0][0] == Ernest.INFINITE &&  nextMatrix[1][0] < Ernest.INFINITE)
		{
			label = "shift-left-right";
			satisfaction = 0;
		}
		
		// shift right straight
		
		// shift left straight 
		
		// shift straight right
		
		// shift straight left
		
		// lost right
		
		// lost left
		
		// Closer
			
		// unchanged
		if (previousMatrix[0][0] == nextMatrix[0][0] && previousMatrix[1][0] == nextMatrix[1][0])
		{
			label = "unchanged";
			satisfaction = 0;
		}
		
		return anim;
	}

	/**
	 * @return The icon currently sensed
	 */
	public IAct getSensedIcon() 
	{
		return m_sensedIcon;
	}
	
	public IAct[] getPixelMatrix()
	{
		return m_pixelMatrix;
	}
	
	/**
	 * Check whether the sensed icon was that expected or not
	 * @param context The current context to update
	 * @param schemas The list of schemas that can invoke an icon from the input context.
	 * @return True if expected, false if not expected.
	 */
	public boolean checkIcon(IContext context, List<ISchema> schemas)
	{
		// Evoke an icon from the current context
		IAct EvokedIcon = evokeIcon(context.getAnimationNoeme(), schemas);
		if (m_sensedIcon.equals(EvokedIcon))
		{
			System.out.println("Expected Icon " + EvokedIcon );
			return true;
		}
		else
		{
			System.out.println("Unexpected Icon! Evoked " + EvokedIcon +" but sensed " + m_sensedIcon);
			return false;
		}
	}
	
	/**
	 * Evoke an icon in a specific context.
	 * @param contextNoeme The context that generates the proposals.
	 * @param schemas The list of schemas capable of evoking the icon.
	 * @return The evoked icon.
	 */
	public IAct evokeIcon(IAct contextNoeme, List<ISchema> schemas)
	{

		List<IActivation> activations = new ArrayList<IActivation>();	
		
		// Browse all the schemas in the provided list
		for (ISchema s : schemas)
		{
			if (!s.isPrimitive())
			{
				// Activate the schemas that match the context 
				boolean activated = false;
				//for (IAct contextAct : contextList)
				//{
					if ((s.getModule() == Ernest.ICONIC) && (s.getContextAct().equals(contextNoeme)))
					{
						activated = true;
						// System.out.println("Activate " + s);
					}
				//}
				
				// Activated schemas propose their intention
				if (activated)
				{
					// The weight is the proposing schema's weight 
					int w = s.getWeight();
					
					// If the intention is a iconic nome. 
					if (s.getIntentionAct().getModule() == Ernest.ICONIC)
					{
						IActivation a = new Activation(s.getIntentionAct(), w);
	
						int i = activations.indexOf(a);
						if (i == -1)
							activations.add(a);
						else
							activations.get(i).update(w);
					}
				}
			}
		}

		// System.out.println("Activate icons: ");
		// for (IProposition p : proposals)
		//	System.out.println(p);
		
		// If the proposal list is not empty
		
		if (activations.size() > 0)
		{
			// sort by weighted proposition...
			Collections.sort(activations);
			
			// count how many are tied with the  highest weighted proposition
			int count = 0;
			int wp = activations.get(0).getWeight();
			for (IActivation a : activations)
			{
				if (a.getWeight() != wp)
					break;
				count++;
			}
	
			// pick one at random from the top of the proposal list
			// count is equal to the number of proposals that are tied...
	
			IActivation a = activations.get(m_rand.nextInt(count));
			System.out.println("Activate: " + a);
			
			return a.getNoeme() ;
		}
		else
			return null;
		
	}
	
}
