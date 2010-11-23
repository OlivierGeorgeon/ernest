package ernest;

/**
 * The main program.  Run this class to execute Ernest in a simulated environment.
 * Importantly, this class defines the concrete factory used by the entire codebase.  
 * This factory detemines the types of objects used by Ernest.   
 * @author mcohen
 *
 */
public class Main 
{
	private static IFactory m_factory = new Factory();
	
	public static IFactory factory()
	{ return m_factory; }
	
	public static void main(String[] args) 
	{
		factory().getAlgorithm().run();
	}
}
