package ernest;

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
	private IEffect step(){
		IEffect effect = new Effect();
		effect.setLabel("f");
		effect.setColor(0xFF0000);
		if (m_x < WIDTH -1){
			if (board[m_x] <= board[m_x + 1])
				effect.setLabel("t");
			m_x++;
		}else {
			if (board[m_x] <= board[0])
				effect.setLabel("t");
			m_x = 0;
		}
		
		if (effect.getLabel().equals("t"))
			effect.setColor(0xFFFFFF);
		
		return effect;
	}
	
	/**
	 * @return true if the next item is greater than the current item
	 */
	private IEffect test_up(){
		IEffect effect = new Effect();
		effect.setLabel("f");
		effect.setColor(0xFF0000);
		if (m_x < WIDTH -1){
			if (board[m_x] <= board[m_x +1])
				effect.setLabel("t");
		}else {
			if (board[m_x] <= board[0])
				effect.setLabel("t");
		}

		if (effect.getLabel().equals("t"))
			effect.setColor(0xFFFFFF);
		
		return effect;		
	}
	
	/**
	 * Invert the next item and the current item
	 * @return true if the next item is greater than the current item
	 */
	private IEffect invert(){
		IEffect effect = new Effect();
		effect.setLabel("f");
		effect.setColor(0xFF0000);
		int temp = board[m_x];
		if (m_x < WIDTH -1){
			if (board[m_x] > board[m_x +1]){
				board[m_x] = board[m_x + 1];
				board[m_x + 1] = temp;
				effect.setLabel("t");
			}
		}else {
			if (board[m_x] < board[0]){
				board[m_x] = board[0];
				board[0] = temp;
				effect.setLabel("t");
			}
//			if (board[m_x] > board[0]){
//				effect.setLabel("t");
//			}
		}
		if (effect.getLabel().equals("t"))
			effect.setColor(0xFFFFFF);
		
		return effect;		
	}
	
	/**
	 * Inverts the agent's direction
	 * @return true 
	 */
//	private IEffect uturn(){
//		IEffect effect = new Effect();
//		effect.setLabel("t");
//		if (m_o == ORIENTATION_RIGHT){
//			m_o = ORIENTATION_LEFT;
//		}else{
//			m_o = ORIENTATION_RIGHT;
//		}
//		return effect;
//	}
	
	public IEffect enact(String s) 
	{
		IEffect effect = null;
		
		if (s.equals(">"))
			effect = step();
//		else if (s.equals("u"))
//			effect = uturn();
		else if (s.equals("-"))
			effect = test_up();
		else if (s.equals("i"))
			effect = invert();
		
		System.out.println("enacted " + s + effect.getLabel());
		printEnv();
		return effect;
	}
	
	private void printEnv(){
		// print the board
		for (int i = 0; i < WIDTH; i++)
			System.out.print(ErnestUtils.formatHex(board[i]) + " ");
		System.out.println();

		// print the agent
		for (int i = 0; i < m_x; i++)
			System.out.print("   ");
		System.out.print(">");

		System.out.println();
	}

	public void initErnest(IErnest ernest) {
		ernest.addInteraction(">", "t", 4); // step
		ernest.addInteraction(">", "f", -10); // step
		ernest.addInteraction("-", "t", -4); // Test up
		ernest.addInteraction("-", "f", -4); // Test up
		//ernest.addInteraction("u", "t", -3); // UTurn
		ernest.addInteraction("i", "t", 4); // invert
		ernest.addInteraction("i", "f", -10); // invert
	}

	public void trace(ITracer tracer) {
		Object e = tracer.addEventElement("environment");
		String stringBoard = "";
		for (int i = 0; i < WIDTH; i++)
			stringBoard += ErnestUtils.formatHex(this.board[i]) + " ";
		tracer.addSubelement(e,"board", stringBoard);

		String stringAgent = "";
		for (int i = 0; i < m_x; i++)
			stringAgent +="--- ";
		stringAgent += ">";
		tracer.addSubelement(e,"agent", stringAgent);
		tracer.addSubelement(e,"position", m_x + "");
		if (m_x < WIDTH - 1)
			tracer.addSubelement(e,"next", this.board[m_x + 1] + "");
	}

}
