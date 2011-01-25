package ernest;

/**
 * The main program.  
 * Constitutes an example code to run Ernest in a simulated environment.
 * @author ogeorgeon
 */
public class Main 
{
	public static void main(String[] args) 
	{
		// Create the environment
		
		IEnvironment environment = new SimpleMaze(); 
		
		// Create an Ernest 
		
		IErnest ernest = new Ernest();
		BinarySensorymotorSystem sss = new BinarySensorymotorSystem();
		ITracer tracer = new Tracer("trace.txt", true);
		
		// Initialize the Ernest 
		
		ernest.setParameters(6, 1, 6);
		ernest.setSensorymotorSystem(sss);
		ernest.setTracer(tracer);
		
		sss.addPrimitiveAct(">", true,   50); // Move
		sss.addPrimitiveAct(">", false, -80); // Bump
		
		sss.addPrimitiveAct("^", true,  -20); // Left toward empty
		sss.addPrimitiveAct("^", false, -50); // Left toward wall

		sss.addPrimitiveAct("v", true,  -20); // Right toward empty
		sss.addPrimitiveAct("v", false, -50); // Right toward wall

		sss.addPrimitiveAct("-", true,  -10); // Touch wall
		sss.addPrimitiveAct("-", false, -10); // Touch empty

		sss.addPrimitiveAct("\\", true, -10); // Touch right wall
		sss.addPrimitiveAct("\\", false,-10); // Touch right empty

		sss.addPrimitiveAct("/", true,  -10); // Touch left wall
		sss.addPrimitiveAct("/", false, -10); // Touch left empty

//		ernest.addPrimitive(">",  50, -80, Ernest.CENTRAL); // Move
//		ernest.addPrimitive("^", -20, -50, Ernest.CENTRAL); // Left
//		ernest.addPrimitive("v", -20, -50, Ernest.CENTRAL); // Right
//		ernest.addPrimitive("-", -10, -10, Ernest.CENTRAL); // Touch
//		ernest.addPrimitive("\\",-10, -10, Ernest.CENTRAL); // Touch right
//		ernest.addPrimitive("/", -10, -10, Ernest.CENTRAL); // Touch left
		
		
		// Run in an infinite loop
		
		int iCycle = 1;
		String schema = "";
		boolean status = false;
		
		while (true)
		{
			System.out.println("Step #" + iCycle);
			tracer.writeLine(" " + iCycle++ );
			
			schema = ernest.step(status);
			status = environment.enact(schema);
		}

	}

}
