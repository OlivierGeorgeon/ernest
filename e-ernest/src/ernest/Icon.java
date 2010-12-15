package ernest;

/**
 * A state of Ernest's distal sensory system
 * @author ogeorgeon
 */
public class Icon implements IIcon 
{	
	/** The icon's width (first coordinate: x) */
	public static final int WIDTH = 2;
	/** The icon's height (second coordinate: y) */
	public static final int HEIGHT = 1;
	
	/** The icon matrix of pixels */
	private int[][] m_blue = new int[WIDTH][HEIGHT]; 

	/**
	 * Constructor.
	 * @param matrix The matrix of pixels that constitutes the icon.
	 */
	public Icon(int[][] matrix)
	{
		m_blue = matrix;
	}
	
	/**
	 * Get the icon's matrix of pixels
	 * @return The icon's matrix of pixels.
	 */
	public int[][] getMatrix() 
	{
		return m_blue;
	}
	
	/**
	 * Generate a string that represents the icon for debug.
	 * @return The string that represents the icon. 
	 */
	public String toString()
	{
		return("["+ m_blue[0][0] + "," + m_blue[1][0] + "]");	
	}

	/**
	 * Icons are equal if they have the same matrix. 
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{		
			IIcon other = (IIcon)o;
			ret = toString().equals(other.toString());
		}
		
		return ret;
	}
}
