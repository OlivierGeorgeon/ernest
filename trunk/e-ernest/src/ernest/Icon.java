package ernest;

/**
 * An iconic noème in Ernest's iconic module
 * Icons are noèmes that have no schema but have a matrix.
 * @author ogeorgeon
 */
public class Icon extends Act implements IIcon
{	
	/** The icon's matrix of pixels */
	private int[][] m_matrix; 

	/**
	 * Constructor for an autonomously learned iconic noème.
	 * @param matrix The matrix of pixels that constitutes the icon.
	 */
	public Icon(int[][] matrix)
	{
		super("["+ matrix[0][0] + "," + matrix[1][0] + "]", null, true, 0, Ernest.ICONIC, Ernest.RELIABLE_NOEME);
		m_matrix = matrix;
	}
	
	/**
	 * Constructor for a modeler-defined iconic noème.
	 * @param matrix The matrix of pixels that constitutes the icon.
	 */
	public Icon(String label, int[][] matrix)
	{
		super(label, null, true, 0, Ernest.ICONIC, Ernest.RELIABLE_NOEME);
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
		//return m_label;
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
	
	/**
	 * @return the icon's satisfaction value 
	 */
	public int getSatisfaction()
	{
		int satLeft = 10 - Math.min(m_matrix[0][0], 10);
		int satRight = 10 - Math.min(m_matrix[1][0], 10);
		
		return (satLeft + satRight);
	}
}
