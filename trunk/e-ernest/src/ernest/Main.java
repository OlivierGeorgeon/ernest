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
		
		ernest.addMotorInteraction(">",  50, -80); // Move
		ernest.addMotorInteraction("^", -20, -50); // Left
		ernest.addMotorInteraction("v", -20, -50); // Right
		ernest.addMotorInteraction("-", -10, -10); // Touch
		ernest.addMotorInteraction("\\",-10, -10); // Touch right
		ernest.addMotorInteraction("/", -10, -10); // Touch left
		
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
