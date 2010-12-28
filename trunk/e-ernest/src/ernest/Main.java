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
		
		ernest.addPrimitive(">",  50, -80, Ernest.CENTRAL); // Move
		ernest.addPrimitive("^", -20, -50, Ernest.CENTRAL); // Left
		ernest.addPrimitive("v", -20, -50, Ernest.CENTRAL); // Right
		ernest.addPrimitive("-", -10, -10, Ernest.CENTRAL); // Touch
		ernest.addPrimitive("\\",-10, -10, Ernest.CENTRAL); // Touch right
		ernest.addPrimitive("/", -10, -10, Ernest.CENTRAL); // Touch left
		
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
