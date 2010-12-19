package ernest;

/**
 * An iconic noème in Ernest's iconic module
 * @author ogeorgeon
 */
public class Icon extends Act implements IIcon
{	
	/** The icon's matrix of pixels */
	private int[][] m_matrix = new int[IconicModule.ICON_WIDTH][IconicModule.ICON_HEIGHT]; 

	/**
	 * Constructor for an autonomously learned iconic noème.
	 * @param matrix The matrix of pixels that constitutes the icon.
	 */
	public Icon(int[][] matrix)
	{
		super("["+ matrix[0][0] + "," + matrix[1][0] + "]", 0, Ernest.ICONIC_NOEME, Ernest.RELIABLE_NOEME);
		m_matrix = matrix;
	}
	
	/**
	 * Constructor for an modeler-defined iconic noème.
	 * @param matrix The matrix of pixels that constitutes the icon.
	 */
	public Icon(String label, int[][] matrix)
	{
		super(label, 0, Ernest.ICONIC_NOEME, Ernest.RELIABLE_NOEME);
		m_matrix = matrix;
	}
	/**
	 * Get the icon's matrix of pixels
	 * @return The icon's matrix of pixels.
	 */
	public int[][] getMatrix() 
	{
		return m_matrix;
	}
	
	/**
	 * Generate a string that represents the icon for debug.
	 * @return The string that represents the icon. 
	 */
	public String toString()
	{
		return("["+ m_matrix[0][0] + "," + m_matrix[1][0] + "]");	
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
