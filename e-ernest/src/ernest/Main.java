package ernest;

/**
 * The main program.  
 * Gives an example code to run Ernest in a simulated environment.
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
		BinarySensorymotorSystem sms = new BinarySensorymotorSystem();
		ITracer tracer = new Tracer("trace.txt", true);
		
		// Initialize the Ernest 
		
		ernest.setParameters(6, 1, 6);
		ernest.setSensorymotorSystem(sms);
		ernest.setTracer(tracer);
		
		// Set Ernest's primitive interactions and motivations
		
		sms.addPrimitiveAct(">", true,   50); // Move
		sms.addPrimitiveAct(">", false, -80); // Bump
		
		sms.addPrimitiveAct("^", true,  -20); // Left toward empty
		sms.addPrimitiveAct("^", false, -50); // Left toward wall

		sms.addPrimitiveAct("v", true,  -20); // Right toward empty
		sms.addPrimitiveAct("v", false, -50); // Right toward wall

		sms.addPrimitiveAct("-", true,  -10); // Touch wall
		sms.addPrimitiveAct("-", false, -10); // Touch empty

		sms.addPrimitiveAct("\\", true, -10); // Touch right wall
		sms.addPrimitiveAct("\\", false,-10); // Touch right empty

		sms.addPrimitiveAct("/", true,  -10); // Touch left wall
		sms.addPrimitiveAct("/", false, -10); // Touch left empty

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
