package ernest;

/**
 * The main earnest program.  Run this class to execute earnest.  Importantly,
 * this class defines the concrete factory used by the entire codebase.  This
 * factory detemines the types of objects used by ernest.   
 * @author mcohen 
 *
 */
public class Ernest 
{
	private static IFactory m_factory = new Factory();
	
	public static IFactory factory()
	{ return m_factory; }
	
	public static void main(String[] args) 
	{
		factory().getAlgorithm().run();
	}
}
