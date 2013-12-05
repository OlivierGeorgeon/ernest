package ernest;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;

import eca.ActInstance;
import eca.ActInstanceImpl;
import eca.Primitive;
import eca.PrimitiveImpl;
import eca.construct.Aspect;
import eca.construct.AspectImpl;
import tracing.ITracer;
import utils.ErnestUtils;

/**
 * This class implements the environment proposed by Roesch et al. in 
 * "Exploration of the Functional Properties of Interaction: 
 * Computer Models and Pointers for Theory"
 *  
 * @author ogeorgeon
 */
public class Roesch2 implements IEnvironment 
{
	private static final int WIDTH = 10;	
	private int m_x = 0;
	private Transform3D transform = new Transform3D();
	
	private int[] board = {6, 3, 5, 4, 7, 3, 5, 3, 9, 5};	

	/**
	 * Process a primitive schema and return its enaction status.
	 * @param s The string code that represents the primitive schema to enact.
	 * @return The boolean feedback resulting from the schema enaction.
	 */

	
	/**
	 * Step forward
	 * @return true if the agent went up
	 */
	private Effect step(){
		Effect effect = new EffectImpl();
		effect.setLabel("f");
		effect.setColor(0xFF0000);
		effect.setLocation(new Point3f(1, 0, 0));
		effect.setTransformation(0f, -1f);

		if (m_x < WIDTH -1){
			if (board[m_x] <= board[m_x + 1])
				effect.setLabel("t");
			m_x++;
		}else {
			if (board[m_x] <= board[0])
				effect.setLabel("t");
			m_x = 0;
			effect.setLocation(new Point3f(0, 0, 0)); // End of Line appears at the agent' position. 
		}
		
		if (effect.getLabel().equals("t"))
			effect.setColor(0xFFFFFF);
		
		return effect;
	}
	
	/**
	 * @return true if the next item is greater than the current item
	 */
	private Effect feel(){
		Effect effect = new EffectImpl();
		effect.setLabel("f");
		effect.setColor(0xFF0000);
		effect.setLocation(new Point3f(1, 0, 0));
		effect.setTransformation(0f, 0f);
		
		if (m_x < WIDTH -1){
			if (board[m_x] <= board[m_x +1])
				effect.setLabel("t");
		}else {
			if (board[m_x] <= board[0])
				effect.setLabel("t");
			effect.setLocation(new Point3f(0, 0, 0)); // End of Line appears at the agent' position. 
		}

		if (effect.getLabel().equals("t"))
			effect.setColor(0xFFFFFF);

		return effect;		
	}
	
	/**
	 * Invert the next item and the current item
	 * @return true if the next item is greater than the current item
	 */
	private Effect swap(){
		Effect effect = new EffectImpl();
		effect.setLabel("f");
		effect.setLocation(new Point3f(1, 0, 0));
		effect.setTransformation(0f, 0f); // simulates a displacement because the environment changes
		effect.setColor(0xFF0000);
		int temp = board[m_x];
		if (m_x < WIDTH -1){
			if (board[m_x] > board[m_x +1])
			{
				board[m_x] = board[m_x + 1];
				board[m_x + 1] = temp;
				effect.setLabel("t");
				effect.setTransformation(0f, -1f); // simulates a displacement because the environment changes
			}
		}
		else {
			effect.setLocation(new Point3f(0, 0, 0)); // End of Line appears at the agent' position. 
//			if (board[m_x] < board[0])
//			{
//				board[m_x] = board[0];
//				board[0] = temp;
//				effect.setLabel("t");
//			}
//			if (board[m_x] > board[0]){
//				effect.setLabel("t");
//			}
		}
		if (effect.getLabel().equals("t"))
			effect.setColor(0xFFFFFF);
		

		return effect;		
	}
	
	public Effect enact(String s) 
	{
		Effect effect = null;
		
		if (s.equals(">"))
			effect = step();
		else if (s.equals("-"))
			effect = feel();
		else if (s.equals("i"))
			effect = swap();
		
		System.out.println("enacted " + s + effect.getLabel());
		printEnv();
		return effect;
	}
	
	private void printEnv(){
		// print the board
		for (int i = 0; i < WIDTH; i++)
			System.out.print(board[i] + " ");
		System.out.println();

		// print the agent
		for (int i = 0; i < m_x; i++)
			System.out.print("  ");
		System.out.print(">");

		System.out.println();
	}

	public void initErnest(IErnest ernest) {
		ernest.addInteraction(">t", 4);   // step up
		ernest.addInteraction(">f", -10); // step down
		ernest.addInteraction("-t", -4);  // feel up
		ernest.addInteraction("-f", -4);  // feel down
		ernest.addInteraction("it", 4);   // swap
		ernest.addInteraction("if", -10); // not swap
	}

	public void trace(ITracer tracer) {
		Object e = tracer.addEventElement("environment");
		String stringBoard = "";
		for (int i = 0; i < WIDTH; i++)
			stringBoard += this.board[i] + " ";
		tracer.addSubelement(e,"board", stringBoard);

		String stringAgent = "";
		for (int i = 0; i < m_x; i++)
			stringAgent +=".. ";
		stringAgent += ">";
		tracer.addSubelement(e,"agent", stringAgent);
		tracer.addSubelement(e,"position", m_x + "");
		if (m_x < WIDTH - 1)
			tracer.addSubelement(e,"next", this.board[m_x + 1] + "");
	}

	public ActInstance enact(Primitive primitive) {
		Effect effect = enact(primitive.getLabel().substring(0,1));
		Primitive enactedPrimitive = PrimitiveImpl.createOrGet(primitive.getLabel().substring(0,1) + effect.getLabel(), 0);
		ActInstance enactedActInstance = new ActInstanceImpl(enactedPrimitive, effect.getLocation());
		Aspect aspect = AspectImpl.createOrGet(effect.getColor());
		enactedActInstance.setAspect(aspect);
		this.transform = effect.getTransformation();
		return enactedActInstance;
	}

	public Transform3D getTransformation(){
		return this.transform;
	}

}
