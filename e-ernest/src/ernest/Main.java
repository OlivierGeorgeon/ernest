package ernest;

/**
 * The main program.  Run this class to execute Ernest in a simulated environment.
 * Importantly, this class defines the concrete factory used by the entire codebase.  
 * This factory detemines the types of objects used by Ernest.   
 * @author mcohen
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
		
		ernest.addPrimitiveInteraction(">", 10,-10); // Move
		ernest.addPrimitiveInteraction("^",  0, -5); // Left
		ernest.addPrimitiveInteraction("v",  0, -5); // Right
		ernest.addPrimitiveInteraction("-", -1,  0); // Touch
		ernest.addPrimitiveInteraction("\\",-1,  0); // Touch right
		ernest.addPrimitiveInteraction("/", -1,  0); // Touch left
		
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
