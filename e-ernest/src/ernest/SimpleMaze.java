package ernest;

import java.util.List;
import java.util.ArrayList;

/**
 * A very simple, not graphical, maze environment used to test the 
 * ernest algorithm. 
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
		
		// the feedback
		if (x < 0 || x >= WIDTH)
			bRet = false;
		else if (y < 0 || y >= HEIGHT)
			bRet = false;
		else if (m_board[y][x] == 'x')
			bRet = false;
		else
		{
			m_x = x;
			m_y = y;
			bRet = true;
		}
		//System.out.println("Moving " + s.getTag());
		//if (bRet == false)
		//	System.out.println("Ouch");
		
		
		if (s.getTag().equals(">"))
			bRet = move();
		else if (s.getTag().equals("^"))
			bRet = left();
		else if (s.getTag().equals("v"))
			bRet = right();
		else if (s.getTag().equals("-"))
			bRet = Touch();
		else if (s.getTag().equals("\\"))
			bRet = TouchRight();
		else if (s.getTag().equals("/"))
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

	public List<ISchema> getPrimitiveSchema() 
	{
		List<ISchema> l = new ArrayList<ISchema>();
		
		ISchema s = Ernest.factory().createPrimitiveSchema(1, ">", 10, -10); // Move
		l.add(s);

		s = Ernest.factory().createPrimitiveSchema(2, "^", 0, -5); // Left
		l.add(s);

		s = Ernest.factory().createPrimitiveSchema(3, "v", 0, -5); // Right
		l.add(s);
		
		s = Ernest.factory().createPrimitiveSchema(4, "-", -1, 0); // Touch
		l.add(s);
		
		s = Ernest.factory().createPrimitiveSchema(5, "\\", -1, 0); // Touch right
		l.add(s);
		
		s = Ernest.factory().createPrimitiveSchema(6, "/", -1, 0); // Touch left
		l.add(s);

		return l;
	}

	private SimpleMaze()
	{}
	
	/**
	 * turn right 
	 * @author ogeorgeon
	 */
	public boolean right()
	{
		m_o++;
		
		if (m_o > ORIENTATION_LEFT)
			m_o = ORIENTATION_UP;

		boolean status =  true  ;

		if (m_o == ORIENTATION_UP)
		{
			if (m_y > 0){
				if (m_board[m_y - 1][m_x] == 'x'){
					status = false;}}
			else
				status = false;
		}
		if (m_o == ORIENTATION_DOWN)
		{
			if (m_y < HEIGHT){
				if (m_board[m_y + 1][m_x] == 'x'){
					status = false;}}
			else
				status = false;
		}
		if (m_o == ORIENTATION_RIGHT)
		{
			if (m_x < WIDTH){
				if (m_board[m_y][m_x + 1] == 'x'){
					status = false;}}
			else
				status = false;
		}
		if (m_o == ORIENTATION_LEFT)
		{
			if (m_x > 0){
				if (m_board[m_y][m_x - 1] == 'x'){
					status = false;}}
			else
				status = false;
		}

		return status;
	}
	/**
	 * turn left 
	 * @author ogeorgeon
	 */
	public boolean left()
	{
		m_o--;
		
		if (m_o < 0)
			m_o = ORIENTATION_LEFT;

		boolean status =  true  ;

		if (m_o == ORIENTATION_UP)
		{
			if (m_y > 0){
				if (m_board[m_y - 1][m_x] == 'x'){
					status = false;}}
			else
				status = false;
		}
		if (m_o == ORIENTATION_DOWN)
		{
			if (m_y < HEIGHT){
				if (m_board[m_y + 1][m_x] == 'x'){
					status = false;}}
			else
				status = false;
		}
		if (m_o == ORIENTATION_RIGHT)
		{
			if (m_x < WIDTH){
				if (m_board[m_y][m_x + 1] == 'x'){
					status = false;}}
			else
				status = false;
		}
		if (m_o == ORIENTATION_LEFT)
		{
			if (m_x > 0){
				if (m_board[m_y][m_x - 1] == 'x'){
					status = false;}}
			else
				status = false;
		}

		return status;
	}
	/**
	 * Move forward to the direction of the current orientation
	 * @author ogeorgeon
	 */
	public boolean move()
	{

		boolean status = false;

		if (m_o == ORIENTATION_UP)
		{
			if (m_y > 0) {
				if  (m_board[m_y - 1][m_x] == ' ' )
					{m_y--; status = true; }
			} 
		}
		if (m_o == ORIENTATION_DOWN)
		{
			if (m_y < HEIGHT) {
				if  (m_board[m_y + 1][m_x] == ' ' )
					{m_y++; status = true; }
			}
		}
		if (m_o == ORIENTATION_RIGHT)
		{
			if ( m_x < WIDTH ) {
				if  (m_board[m_y][m_x + 1] == ' ' )
					{m_x++; status = true; }
			} 
		}
		if (m_o == ORIENTATION_LEFT)
		{
			if ( m_x > 0 ) {
				if  (m_board[m_y][m_x - 1] == ' ' )
					{m_x--; status = true; }
			} 
		}

		if (!status)
			System.out.println("Ouch");

		return status;
	}
	/**
	 * Touch the square forward
	 * Succeeds if there is a wall, fails otherwise 
	 * @author ogeorgeon
	 */
	public boolean Touch()
	{

		boolean status = true;

		if (m_o == ORIENTATION_UP)
		{
		    if ((m_y > 0) && m_board[m_y - 1][m_x] == ' ')
		    	status = false;
		}
		if (m_o == ORIENTATION_DOWN)
		{
			if ((m_y < HEIGHT) && m_board[m_y + 1][m_x] == ' ')
				status = false;
		}
		if (m_o == ORIENTATION_RIGHT)
		{
		    if ((m_x < WIDTH) && m_board[m_y][m_x + 1] == ' ')
		    	status = false;
		}
		if (m_o == ORIENTATION_LEFT)
		{
		    if ((m_x > 0) && m_board[m_y][m_x - 1] == ' ')
		    	status = false;
		}

		return status;
	}
	
/**
 * Touch the square to the right
 * Succeeds if there is a wall, fails otherwise 
 * @author ogeorgeon
 */
public boolean TouchRight()
	{

		boolean status = true;
	
		if (m_o == ORIENTATION_UP)
		{
		    if ((m_x > 0) && m_board[m_y][m_x + 1] == ' ')
		    	status = false;
		}
		if (m_o == ORIENTATION_DOWN)
		{
			if ((m_x < WIDTH) && m_board[m_y][m_x - 1] == ' ')
				status = false;
		}
		if (m_o == ORIENTATION_RIGHT)
		{
		    if ((m_y < HEIGHT) && m_board[m_y + 1][m_x] == ' ')
		    	status = false;
		}
		if (m_o == ORIENTATION_LEFT)
		{
		    if ((m_y > 0) && m_board[m_y - 1][m_x] == ' ')
		    	status = false;
		}
	
		return status;
	}

/**
 * Touch the square forward
 * Succeeds if there is a wall, fails otherwise 
 * @author ogeorgeon
 */
public boolean TouchLeft()
	{

		boolean status = true;
	
		if (m_o == ORIENTATION_UP)
		{
		    if ((m_x > 0) && m_board[m_y][m_x - 1] == ' ')
		    	status = false;
		}
		if (m_o == ORIENTATION_DOWN)
		{
			if ((m_x < WIDTH) && m_board[m_y][m_x + 1] == ' ')
				status = false;
		}
		if (m_o == ORIENTATION_RIGHT)
		{
		    if ((m_y > 0) && m_board[m_y - 1][m_x] == ' ')
		    	status = false;
		}
		if (m_o == ORIENTATION_LEFT)
		{
		    if ((m_y < HEIGHT) && m_board[m_y + 1][m_x] == ' ')
		    	status = false;
		}
	
		return status;
	}

}
