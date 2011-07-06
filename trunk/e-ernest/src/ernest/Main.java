package ernest;

import tracing.KTBSTracer;
import tracing.Tracer;

/**
 * The main program.  
 * Gives an example code to run Ernest in a simulated environment.
 * See an example demo that corresponds to these settings at 
 * http://e-ernest.blogspot.com/2010/12/java-ernest-72-in-vacuum.html
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
		ITracer tracer = new KTBSTracer("http://localhost:8001/IDEAL/", "http://salade.dyndns.org/nomodel#");
		//ITracer tracer = new Tracer(null);
		
		// Initialize the Ernest 
		
		ernest.setParameters(6, 1, 6);
		ernest.setTracer(tracer);
		ernest.setSensorymotorSystem(sms);

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
			// --> Insert breakpoint below to easily follow Ernest in Eclipse debug mode. 
			System.out.println("Step #" + iCycle);
			tracer.startNewEvent(iCycle++);
			
			schema = ernest.step(status);
			status = environment.enact(schema);
			
			tracer.close(); // Needed with the XMLTracer to save the xml file each time.
		}

	}

}
