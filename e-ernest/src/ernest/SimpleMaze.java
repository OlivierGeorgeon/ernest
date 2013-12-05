package ernest;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;

import tracing.ITracer;

import eca.ActInstance;
import eca.ActInstanceImpl;
import eca.Primitive;
import eca.PrimitiveImpl;
import eca.construct.AspectImpl;

/**
 * This class implements the Small Loop Environment
 *  
 * The Small Loop Problem: A challenge for artificial emergent cognition. 
 * Olivier L. Georgeon, James B. Marshall. 
 * BICA2012, Annual Conference on Biologically Inspired Cognitive Architectures. 
 * Palermo, Italy. (October 31, 2012).
 * http://e-ernest.blogspot.fr/2012/05/challenge-emergent-cognition.html
 *   
 * @author mcohen
 * @author ogeorgeon
 */
public class SimpleMaze implements IEnvironment 
{
	private static final int ORIENTATION_UP    = 0;
	private static final int ORIENTATION_RIGHT = 1;
	private static final int ORIENTATION_DOWN  = 2;
	private static final int ORIENTATION_LEFT  = 3;
	
	private Transform3D transform = new Transform3D();

	// The Small Loop Environment
	
	private static final int WIDTH = 6;	
	private static final int HEIGHT = 6;	
	private int m_x = 4;
	private int m_y = 1;
	private int m_o = 2;
	
	private char[][] m_board = 
		{
		 {'x', 'x', 'x', 'x', 'x', 'x'},
		 {'x', ' ', ' ', ' ', ' ', 'x'},
		 {'x', ' ', 'x', 'x', ' ', 'x'},
		 {'x', ' ', ' ', 'x', ' ', 'x'},
		 {'x', 'x', ' ', ' ', ' ', 'x'},
		 {'x', 'x', 'x', 'x', 'x', 'x'},
		};
	
//  This is the Simple Maze environment presented here: 	
//	http://e-ernest.blogspot.com/2010/12/java-ernest-72-in-vacuum.html
//	
//	private static final int WIDTH = 9;	
//	private static final int HEIGHT = 8;	
//	private int m_x = 3;
//	private int m_y = 5;
//	private int m_o = 0;
//	
//	private char[][] m_board = 
//		{
//		 {'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x'},
//		 {'x', ' ', ' ', ' ', 'x', 'x', 'x', 'x', 'x'},
//		 {'x', ' ', 'x', ' ', ' ', ' ', 'x', 'x', 'x'},
//		 {'x', ' ', 'x', 'x', 'x', ' ', ' ', ' ', 'x'},
//		 {'x', ' ', ' ', ' ', 'x', 'x', 'x', ' ', 'x'},
//		 {'x', 'x', 'x', ' ', ' ', ' ', 'x', ' ', 'x'},
//		 {'x', 'x', 'x', 'x', 'x', ' ', ' ', ' ', 'x'},
//		 {'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x'},
//		};
	
	private char[] m_agent = 
	{ '^', '>', 'v', '<' };

	/**
	 * Process a primitive schema and return its enaction status.
	 * @param s The string code that represents the primitive schema to enact.
	 * @return The boolean feedback resulting from the schema enaction.
	 */
	public ActInstance enact(Primitive intendedPrimitive){
		Effect effect = enact(intendedPrimitive.getLabel());
		Primitive enactedPrimitive = PrimitiveImpl.createOrGet(intendedPrimitive.getLabel().substring(0,1) + effect.getLabel(), 0);
		ActInstance enactedActInstance = new ActInstanceImpl(enactedPrimitive, effect.getLocation());		
		enactedActInstance.setAspect(AspectImpl.createOrGet(effect.getColor()));
		this.transform = effect.getTransformation();
		return enactedActInstance;
	}

	private Effect enact(String intendedInteraction) 
	{
		Effect effect = null;
		String s = intendedInteraction.substring(0,1);
		
		if (s.equals(">"))
			effect = move();
		else if (s.equals("^"))
			effect = left();
		else if (s.equals("v"))
			effect = right();
		else if (s.equals("-"))
			effect = Touch();
		else if (s.equals("\\"))
			effect = TouchRight();
		else if (s.equals("/"))
			effect = TouchLeft();
		
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
		
		return effect;
	}

	/**
	 * Turn to the right. 
	 */
	private Effect right()
	{
		Effect effect = new EffectImpl();
		effect.setLabel("f");
		effect.setColor(0xFFFFFF);
		
		m_o++;		
		if (m_o > ORIENTATION_LEFT)
			m_o = ORIENTATION_UP;

		effect.setLocation(new Point3f());
		effect.setTransformation((float)Math.PI/2, 0f);
		effect.setColor(0xFFFFFF);

		// In the Simple Maze, the effect may vary according to the wall in front after turning
//		if (((m_o == ORIENTATION_UP) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' ')) ||
//			((m_o == ORIENTATION_DOWN) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' ')) ||
//			((m_o == ORIENTATION_RIGHT) && (m_x < WIDTH) && (m_board[m_y][m_x + 1] == ' ')) ||
//			((m_o == ORIENTATION_LEFT) && (m_x > 0) && (m_board[m_y][m_x - 1] == ' ')))
//			{effect.setLabel("t");effect.setColor(0x00FF00);}

		return effect;
	}
	
	/**
	 * Turn to the left. 
	 */
	private Effect left()
	{
		Effect effect = new EffectImpl();
		effect.setLabel("f");
		effect.setColor(0xFFFFFF);
		
		m_o--;
		if (m_o < 0)
			m_o = ORIENTATION_LEFT;
		
		effect.setLocation(new Point3f());
		effect.setTransformation((float)-Math.PI/2, 0f);
		effect.setColor(0xFFFFFF);

		// In the Simple Maze, the effect may vary according to the wall in front after turning
//		if (((m_o == ORIENTATION_UP) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' ')) ||
//			((m_o == ORIENTATION_DOWN) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' ')) ||
//			((m_o == ORIENTATION_RIGHT) && (m_x < WIDTH) && (m_board[m_y][m_x + 1] == ' ')) ||
//			((m_o == ORIENTATION_LEFT) && (m_x > 0) && (m_board[m_y][m_x - 1] == ' ')))
//			{effect.setLabel("t");effect.setColor(0x00FF00);}

		return effect;
	}
	
	/**
	 * Move forward to the direction of the current orientation.
	 */
	private Effect move()
	{
		Effect effect = new EffectImpl();
		effect.setLabel("f");
		effect.setColor(0xFF0000);

		//boolean status = false;

		if ((m_o == ORIENTATION_UP) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' ' ))
				{m_y--; effect.setLabel("t"); effect.setColor(0xFFFFFF);}

		if ((m_o == ORIENTATION_DOWN) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' ' ))
				{m_y++; effect.setLabel("t"); effect.setColor(0xFFFFFF);}

		if ((m_o == ORIENTATION_RIGHT) && ( m_x < WIDTH ) && (m_board[m_y][m_x + 1] == ' ' ))
				{m_x++; effect.setLabel("t"); effect.setColor(0xFFFFFF);}

		if ((m_o == ORIENTATION_LEFT) && ( m_x > 0 ) && (m_board[m_y][m_x - 1] == ' ' ))
				{m_x--; effect.setLabel("t"); effect.setColor(0xFFFFFF);}

		if (effect.getLabel().equals("t")){
			effect.setLocation(new Point3f(1, 0, 0));
			effect.setTransformation(0, -1f);
			effect.setColor(0xFFFFFF);
		}
		else{
			effect.setLocation(new Point3f(1, 0, 0));
			effect.setTransformation(0f, 0f);
			effect.setColor(0xFF0000);
		}
		//if (!status)
		//	System.out.println("Ouch");

		return effect;
	}
	
	/**
	 * Touch the square forward.
	 * Succeeds if there is a wall, fails otherwise 
	 */
	private Effect Touch()
	{
		Effect effect = new EffectImpl();
		effect.setLabel("t");
		effect.setColor(0x008000);

		if (((m_o == ORIENTATION_UP) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' ')) ||
			((m_o == ORIENTATION_DOWN) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' ')) ||
			((m_o == ORIENTATION_RIGHT) && (m_x < WIDTH) && (m_board[m_y][m_x + 1] == ' ')) ||
			((m_o == ORIENTATION_LEFT) && (m_x > 0) && (m_board[m_y][m_x - 1] == ' ')))
		   	{effect.setLabel("f");effect.setColor(0xFFFFFF);}

		effect.setLocation(new Point3f(1, 0, 0));
		effect.setTransformation(0f, 0f);

		return effect;
	}
	
	/**
	 * Touch the square to the right.
	 * Succeeds if there is a wall, fails otherwise. 
	 */
	private Effect TouchRight()
	{
		Effect effect = new EffectImpl();
		effect.setLabel("t");
		effect.setColor(0x008000);

		if (((m_o == ORIENTATION_UP) && (m_x > 0) && (m_board[m_y][m_x + 1] == ' ')) ||
			((m_o == ORIENTATION_DOWN) && (m_x < WIDTH) && (m_board[m_y][m_x - 1] == ' ')) ||
			((m_o == ORIENTATION_RIGHT) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' ')) ||
			((m_o == ORIENTATION_LEFT) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' ')))
			{effect.setLabel("f");effect.setColor(0xFFFFFF);}

		effect.setLocation(new Point3f(0, -1, 0));
		effect.setTransformation(0f, 0f);

		return effect;
	}

	/**
	 * Touch the square forward.
	 * Succeeds if there is a wall, fails otherwise 
	 */
	private Effect TouchLeft()
	{
		Effect effect = new EffectImpl();
		effect.setLabel("t");
		effect.setColor(0x008000);
	
		if (((m_o == ORIENTATION_UP) && (m_x > 0) && (m_board[m_y][m_x - 1] == ' ')) ||
			((m_o == ORIENTATION_DOWN) && (m_x < WIDTH) && (m_board[m_y][m_x + 1] == ' ')) ||
			((m_o == ORIENTATION_RIGHT) && (m_y > 0) && (m_board[m_y - 1][m_x] == ' ')) ||
			((m_o == ORIENTATION_LEFT) && (m_y < HEIGHT) && (m_board[m_y + 1][m_x] == ' ')))
			{effect.setLabel("f");effect.setColor(0xFFFFFF);}

		effect.setLocation(new Point3f(0, 1, 0));
		effect.setTransformation(0f, 0f);

		return effect;
	}

	public void initErnest(IErnest ernest) {
		ernest.addInteraction("-tB", -2); // Touch wall
		ernest.addInteraction("-fB", -1); // Touch empty
		ernest.addInteraction("\\tC", -2);// Touch right wall
		ernest.addInteraction("\\fC", -1);// Touch right empty
		ernest.addInteraction("/tA", -2); // Touch left wall
		ernest.addInteraction("/fA", -1); // Touch left empty
		ernest.addInteraction(">tB",  5); // Move
		ernest.addInteraction(">fB", -10);// Bump		
		ernest.addInteraction("vt", -3); // Right toward empty
		ernest.addInteraction("vf", -3); // Right toward wall		
		ernest.addInteraction("^t", -3); // Left toward empty
		ernest.addInteraction("^f", -3); // Left toward wall		

//		Settings for a nice demo in the Simple Maze Environment
//		sms.addInteraction(">", "t",  5); // Move
//		sms.addInteraction(">", "f", -8); // Bump		
//		sms.addInteraction("^", "t", -2); // Left toward empty
//		sms.addInteraction("^", "f", -5); // Left toward wall		
//		sms.addInteraction("v", "t", -2); // Right toward empty
//		sms.addInteraction("v", "f", -5); // Right toward wall		
//		sms.addInteraction("-", "t", -1); // Touch wall
//		sms.addInteraction("-", "f", -1); // Touch empty
//		sms.addInteraction("\\", "t", -1); // Touch right wall
//		sms.addInteraction("\\", "f", -1); // Touch right empty
//		sms.addInteraction("/", "t", -1); // Touch left wall
//		sms.addInteraction("/", "f", -1); // Touch left empty

	}

	public void trace(ITracer tracer) {
		// TODO Auto-generated method stub
		
	}
	
	public Transform3D getTransformation(){
		return this.transform;
	}

}
