package ernest;

import java.util.List;
import java.util.ArrayList;

/**
 * A very simple, not graphical, maze environment used to test the 
 * ernest algorithm. 
 * @author mcohen
 *
 */
public class SimpleMaze implements IEnvironment 
{

	private int m_x = 1;
	private int m_y = 1;
	private char[][] m_board = 
		{
		 {'x', 'x', 'x', 'x'},
		 {'x', ' ', ' ', 'x'},
		 {'x', ' ', ' ', 'x'},
		 {'x', 'x', 'x', 'x'},
		 {'x', 'x', 'x', 'x'}
		};
	
	public static IEnvironment createEnvironment()
	{ return new SimpleMaze(); }
	
	public boolean enactSchema(ISchema s) 
	{
		boolean bRet = false;
		int x = m_x;
		int y = m_y;
		if (s.getTag().equals("NORTH"))
			y--;
		else if (s.getTag().equals("SOUTH"))
			y++;
		else if (s.getTag().equals("EAST"))
			x++;	
		else if (s.getTag().equals("WEST"))
			x--;
		
		if (x < 0 || x > 3)
			bRet = false;
		else if (y < 0 || y > 3)
			bRet = false;
		else if (m_board[y][x] == 'x')
			bRet = false;
		else
		{
			m_x = x;
			m_y = y;
			bRet = true;
		}
		
		System.out.println("Moving " + s.getTag());
		if (bRet == false)
			System.out.println("Ouch");
		
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if (i == m_y && j== m_x)
					System.out.print("*");
				else
					System.out.print(m_board[i][j]);	
			}
			System.out.println();
		}
		
		return bRet;
	}

	public List<ISchema> getPrimitiveSchema() 
	{
		List<ISchema> l = new ArrayList<ISchema>();
		
		ISchema s = Ernest.factory().createPrimitiveSchema(1, "NORTH", 1, -1);
		l.add(s);

		s = Ernest.factory().createPrimitiveSchema(2, "SOUTH", 1, -1);
		l.add(s);

		s = Ernest.factory().createPrimitiveSchema(3, "EAST", 1, -1);
		l.add(s);
		
		s = Ernest.factory().createPrimitiveSchema(4, "WEST", 1, -1);
		l.add(s);
		
		return l;
	}

	private SimpleMaze()
	{}
	
}
