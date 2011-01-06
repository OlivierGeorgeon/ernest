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
	
	public IAct enactedAct(ISchema schema, boolean status)
	{
		String label = schema.getLabel();
		if (!m_labels[0].equals(" ") || !m_labels[1].equals(" ") )
			label = label + "" + m_labels[0] + "|" + m_labels[1];
		if (status)
			label = "(" + label + ")";
		else 
			label = "[" + label + "]";
			
		int satisfaction = m_satisfactions[0] + m_satisfactions[1] + schema.resultingAct(status).getSatisfaction();  
		IAct enacted = Act.createAct(label, schema, status, satisfaction, Ernest.CENTRAL, Ernest.RELIABLE_NOEME);
		
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
		String label = " ";
		int satisfaction = 0;
		
		// arrived
		if (previousPixel > nextPixel && nextPixel == 0)
		{
			label = "x";
			satisfaction = 100;
		}
		
		// closer
		else if (previousPixel < Ernest.INFINITE && nextPixel < previousPixel)
		{
			label = "+";
			satisfaction = 100;
		}

		// appear
		else if (previousPixel == Ernest.INFINITE && nextPixel < Ernest.INFINITE)
		{
			label = "*";
			satisfaction = 150;
		}
		
		// disappear
		else if (previousPixel < Ernest.INFINITE && nextPixel == Ernest.INFINITE)
		{
			label = "o";
			satisfaction = -150;
		}

		System.out.println("Sensed " + label);
		
		m_labels[index] = label;
		m_satisfactions[index] = satisfaction;
	}
	
}