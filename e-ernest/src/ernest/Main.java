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
	private static IFactory m_factory = new Factory();
	
	public static IFactory factory()
	{ return m_factory; }
	
	public static void main(String[] args) 
	{
		factory().getAlgorithm().run();
		//run();
	}

	/**
	 * Run a simulation of ernest in a simple environment
	 * @author ogeorgeon
	 */
	protected static void run()
	{
		// Create the environment
		
		IEnvironment environment = SimpleMaze.createEnvironment(); 
		
		// Create a logger 
		
		ILogger logger = new Logger("trace.txt", true);
		
		// Create an Ernest 
		
		IErnest ernest = new Ernest();
		
		// Initialize the Ernest 
		
		ernest.addPrimitiveSchema(">", 10, -10); // Move
		ernest.addPrimitiveSchema("^", 0, -5);   // Left
		ernest.addPrimitiveSchema("v", 0, -5);   // Right
		ernest.addPrimitiveSchema("-", -1, 0);   // Touch
		ernest.addPrimitiveSchema("\\", -1, 0);  // Touch right
		ernest.addPrimitiveSchema("/", -1, 0);   // Touch left
		
		ernest.setLogger(logger);
		
		// Run in an infinite loop
		
		int iCycle = 1;
		String schema = "";
		boolean status = false;
		
		while (true)
		{
			System.out.println("Decision cycle #" + iCycle++);
			logger.writeLine(" " + iCycle );
			
			schema = ernest.step(status);
			status = environment.enact(schema);
		}

	}
}
