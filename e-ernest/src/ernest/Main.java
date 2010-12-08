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
		
		// Create a tracer 
		
		ITracer tracer = new Tracer("trace.txt", true);
		
		// Create an Ernest 
		
		IErnest ernest = new Ernest();
		
		// Initialize the Ernest 
		
		ernest.setParameters(6, 1, 6);
		
		ernest.addPrimitiveInteraction(">",  50, -80); // Move
		ernest.addPrimitiveInteraction("^", -20, -50); // Left
		ernest.addPrimitiveInteraction("v", -20, -50); // Right
		ernest.addPrimitiveInteraction("-", -10, -10); // Touch
		ernest.addPrimitiveInteraction("\\",-10, -10); // Touch right
		ernest.addPrimitiveInteraction("/", -10, -10); // Touch left
		
		ernest.setTracer(tracer);
		
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
