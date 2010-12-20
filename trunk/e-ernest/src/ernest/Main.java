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
		
		ernest.addPrimitive(">",  50, -80, Ernest.SENSORYMOTOR); // Move
		ernest.addPrimitive("^", -20, -50, Ernest.SENSORYMOTOR); // Left
		ernest.addPrimitive("v", -20, -50, Ernest.SENSORYMOTOR); // Right
		ernest.addPrimitive("-", -10, -10, Ernest.SENSORYMOTOR); // Touch
		ernest.addPrimitive("\\",-10, -10, Ernest.SENSORYMOTOR); // Touch right
		ernest.addPrimitive("/", -10, -10, Ernest.SENSORYMOTOR); // Touch left
		
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
