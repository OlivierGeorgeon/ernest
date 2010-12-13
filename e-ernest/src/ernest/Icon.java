package ernest;

/**
 * An Icon is a state of the distal sensory system
 * @author ogeorgeon
 */
public class Icon implements IIcon 
{
	public static final int WIDTH = 2;
	public static final int HIGHT = 1;
	
	private int[][] m_blue = new int[WIDTH][HIGHT]; 

	public Icon(int[][] matrix)
	{
		m_blue = matrix;
	}
	
	public int[][] getIcon() 
	{
		return m_blue;
	}
	
	public String toString()
	{
		return("["+ m_blue[0][0] + "," + m_blue[1][0] + "]");	
	}
	/**
	 * Icons are equal if they have the same matrix 
	 * @author ogeorgeon
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
