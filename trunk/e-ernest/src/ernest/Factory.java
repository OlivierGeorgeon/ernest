package ernest;

/**
 * This factory class populates earnest with a set of concrete 
 * classes that represent a default implementation of ernest.  If you 
 * wish to tweak various aspects of ernest's behavior you can derive a 
 * new factory from this class and return your own custom implementations 
 * of the objects used by ernest.
 * 
 * @author mcohen
 *
 */
public class Factory implements IFactory  
{
	private IEnvironment m_env = null; 
	private IAlgorithm m_alg = null;
	
	public IEnvironment getEnvironment()
	{
		if (m_env == null)
			m_env = SimpleMaze.createEnvironment();
		return m_env;
	}
	
	public IAlgorithm getAlgorithm()
	{
		if (m_alg == null)
			m_alg = Algorithm.createAlgorithm();
		
		return m_alg;
	}
	
	public IAct createAct(ISchema s, boolean success, int satisfaction)
	{
		return Act.createAct(s, success, satisfaction);
	}
	
	public ISchema createSchema()
	{
		return Schema.createSchema();
	}

	public ISchema createPrimitiveSchema(String id, int successSat, int failureSat)
	{
		return Schema.createSchema(id, successSat, failureSat);
	}
	
	public IProposition createProposition(IAct act)
	{
		return Proposition.createProposition(act);
	}

}
