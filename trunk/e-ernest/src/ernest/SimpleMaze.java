package ernest;

import java.util.List;
import java.util.ArrayList;

/**
 * A very simple maze environment used to test Ernest.  
 * Corresponds to the demo at http://e-ernest.blogspot.com/2010/12/java-ernest-72-in-vacuum.html
 * @author mcohen
 * @author ogeorgeon
 */
public class SimpleMaze implements IEnvironment 
{

	private static final int WIDTH = 9;	
	private static final int HEIGHT = 8;	

	private static final int ORIENTATION_UP    = 0;
	private static final int ORIENTATION_RIGHT = 1;
	private static final int ORIENTATION_DOWN  = 2;
	private static final int ORIENTATION_LEFT  = 3;

	private int m_x = 3;
	private int m_y = 5;
	private int m_o = 0;
	
	private char[][] m_board = 
		{
		 {'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x'},
		 {'x', ' ', ' ', ' ', 'x', 'x', 'x', 'x', 'x'},
		 {'x', ' ', 'x', ' ', ' ', ' ', 'x', 'x', 'x'},
		 {'x', ' ', 'x', 'x', 'x', ' ', ' ', ' ', 'x'},
		 {'x', ' ', ' ', ' ', 'x', 'x', 'x', ' ', 'x'},
		 {'x', 'x', 'x', ' ', ' ', ' ', 'x', ' ', 'x'},
		 {'x', 'x', 'x', 'x', 'x', ' ', ' ', ' ', 'x'},
		 {'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x'},
		};
	
	private char[] m_agent = 
	{ '^', '>', 'v', '<' };

	/**
	 * Process a primitive schema and return its enaction status.
	 * @param s The string code that represents the primitive schema to enact.
	 * @return The boolean feedback resulting from the schema enaction.
	 */

	public boolean enact(String s) 
	{
		boolean bRet = false;
		
		if (s.equals(">"))
			bRet = move();
		else if (s.equals("^"))
			bRet = left();
		else if (s.equals("v"))
			bRet = right();
		else if (s.equals("-"))
			bRet = Touch();
		else if (s.equals("\\"))
			bRet = TouchRight();
		else if (s.equals("/"))
			bRet = TouchLeft();
		
		// print the maze
		for (int i = 0; i < HEIGHT; i++)
		{
			for (int j = 0; j < WIDTH; j++)
			{
				if (i == m_y && j== m_x)
					System.out.print(m_agent[m_o]);
				else
					System.out.print(m_board[i][j]);	
			}
			System.out.println();
		}
		
		return bRet;
	}

	/**
	 * Turn to the right. 
	 */
	private boolean right()
	{
		m_o++;
		
		if (m_o > ORIENTATION_LEFT)
			m_o = ORIENTATION_UP;

		boolean status =  false  ;

		if ((m_o == ORIENTATION_UP) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' '))
			status = true;

		if ((m_o == ORIENTATION_DOWN) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' '))
			status = true;

		if ((m_o == ORIENTATION_RIGHT) && (m_x < WIDTH) && (m_board[m_y][m_x + 1] == ' '))
			status = true;

		if ((m_o == ORIENTATION_LEFT) && (m_x > 0) && (m_board[m_y][m_x - 1] == ' '))
			status = true;

		return status;
	}
	
	/**
	 * Turn to the left. 
	 */
	private boolean left()
	{
		m_o--;
		
		if (m_o < 0)
			m_o = ORIENTATION_LEFT;

		boolean status =  false  ;

		if ((m_o == ORIENTATION_UP) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' '))
			status = true;

		if ((m_o == ORIENTATION_DOWN) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' '))
			status = true;

		if ((m_o == ORIENTATION_RIGHT) && (m_x < WIDTH) && (m_board[m_y][m_x + 1] == ' '))
			status = true;

		if ((m_o == ORIENTATION_LEFT) && (m_x > 0) && (m_board[m_y][m_x - 1] == ' '))
			status = true;

		return status;
	}
	
	/**
	 * Move forward to the direction of the current orientation.
	 */
	private boolean move()
	{

		boolean status = false;

		if ((m_o == ORIENTATION_UP) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' ' ))
					{m_y--; status = true; }

		if ((m_o == ORIENTATION_DOWN) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' ' ))
					{m_y++; status = true; }

		if ((m_o == ORIENTATION_RIGHT) && ( m_x < WIDTH ) && (m_board[m_y][m_x + 1] == ' ' ))
					{m_x++; status = true; }

		if ((m_o == ORIENTATION_LEFT) && ( m_x > 0 ) && (m_board[m_y][m_x - 1] == ' ' ))
					{m_x--; status = true; }

		if (!status)
			System.out.println("Ouch");

		return status;
	}
	
	/**
	 * Touch the square forward.
	 * Succeeds if there is a wall, fails otherwise 
	 */
	private boolean Touch()
	{

		boolean status = true;

		if ((m_o == ORIENTATION_UP) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' '))
		    	status = false;

		if ((m_o == ORIENTATION_DOWN) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' '))
				status = false;

		if ((m_o == ORIENTATION_RIGHT) && (m_x < WIDTH) && (m_board[m_y][m_x + 1] == ' '))
		    	status = false;

		if ((m_o == ORIENTATION_LEFT) && (m_x > 0) && (m_board[m_y][m_x - 1] == ' '))
		    	status = false;

		return status;
	}
	
	/**
	 * Touch the square to the right.
	 * Succeeds if there is a wall, fails otherwise. 
	 */
	private boolean TouchRight()
	{

		boolean status = true;
	
		if ((m_o == ORIENTATION_UP) && (m_x > 0) && (m_board[m_y][m_x + 1] == ' '))
		    	status = false;

		if ((m_o == ORIENTATION_DOWN) && (m_x < WIDTH) && (m_board[m_y][m_x - 1] == ' '))
				status = false;

		if ((m_o == ORIENTATION_RIGHT) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' '))
		    	status = false;

		if ((m_o == ORIENTATION_LEFT) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' '))
		    	status = false;
	
		return status;
	}

	/**
	 * Touch the square forward.
	 * Succeeds if there is a wall, fails otherwise 
	 */
	private boolean TouchLeft()
	{

		boolean status = true;
	
		if ((m_o == ORIENTATION_UP) && (m_x > 0) && (m_board[m_y][m_x - 1] == ' '))
		    	status = false;

		if ((m_o == ORIENTATION_DOWN) && (m_x < WIDTH) && (m_board[m_y][m_x + 1] == ' '))
				status = false;

		if ((m_o == ORIENTATION_RIGHT) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' '))
		    	status = false;

		if ((m_o == ORIENTATION_LEFT) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' '))
		    	status = false;
	
		return status;
	}
}
