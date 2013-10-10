package ernest;

import tracing.Tracer;
import tracing.XMLStreamTracer;

/**
 * The main program.  
 * Gives an example code to run Ernest in the Small Loop Environment.
 * See an example demo that corresponds to these settings at 
 * http://e-ernest.blogspot.fr/2012/03/small-loop-challenge.html
 * and
 * http://e-ernest.blogspot.fr/2012/05/challenge-emergent-cognition.html
 * 
 * @author ogeorgeon
 */
public class Main 
{
	public static void main(String[] args) 
	{
		// Create the environment.
		
		IEnvironment environment = new Roesch2(); 
		
		// Create an Ernest agent.
		
		IErnest ernest = new Ernest();
		ITracer tracer = null; 
		
		///////////// Uncomment this line to generate traces ///////////////////////////
		tracer = new XMLStreamTracer("http://macbook-pro-de-olivier-2.local/alite/php/stream/","BGKGGBbdjxbYzYAlvXrjbVMjOwyXEA");
		// tracer = new Tracer(null);
        ////////////////////////////////////////////////////////////////////////////////
		
		// Initialize the Ernest agent.
		
		ernest.setParameters(6, 10);
		ernest.setTracer(tracer);

		// Set Ernest's primitive interactions and motivations.

		environment.initErnest(ernest);
		
//		ernest.addInteraction("-", "t", -2); // Touch wall
//		ernest.addInteraction("-", "f", -1); // Touch empty
//		ernest.addInteraction("\\", "t", -2);// Touch right wall
//		ernest.addInteraction("\\", "f", -1);// Touch right empty
//		ernest.addInteraction("/", "t", -2); // Touch left wall
//		ernest.addInteraction("/", "f", -1); // Touch left empty
//		ernest.addInteraction(">", "t",  5); // Move
//		ernest.addInteraction(">", "f", -10);// Bump		
//		ernest.addInteraction("v", "t", -3); // Right toward empty
//		ernest.addInteraction("v", "f", -3); // Right toward wall		
//		ernest.addInteraction("^", "t", -3); // Left toward empty
//		ernest.addInteraction("^", "f", -3); // Left toward wall		
		
		// Run in an infinite loop
		
		int iCycle = 1;
		String schema = "";
		IEffect effect = new Effect();
		
		while (true)
		{
			////////// Insert a breakpoint below to easily follow Ernest in Eclipse debugger. ////// 
			System.out.println("Step #" + iCycle++);
			sleep(100);
			//////////////////////////////////////////////////////////////////////////////////////////
			

			schema = ernest.step(effect);
			environment.trace(tracer);
			effect = environment.enact(schema);
			
			
			//tracer.close(); // Needed with the XMLTracer to save the xml file on each interaction cycle.
		}

	}
	
	private static void sleep(int t)
	{
		try
		{ 
			Thread.currentThread().sleep(t);
		}
		catch(InterruptedException ie)
		{}
	}


}
