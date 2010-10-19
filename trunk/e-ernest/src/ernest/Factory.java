package ernest;

import java.util.List;

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
	
	/**
	 * Add a new schema to the listif it does not exist
	 * If it exists, increments its weight
	 * @author ogeorgeon
	 * @return the new or existing schema
	 */
	public ISchema addSchema(List<ISchema> m_schemas, IAct context, IAct intention) 
	{
		ISchema newS = createSchema(m_schemas.size() + 1);
		newS.setContextAct(context);
		newS.setIntentionAct(intention);
		newS.initSucceedingAct();		

		int i = m_schemas.indexOf(newS);
		if (i == -1)
		{
			m_schemas.add(newS);
			System.out.println("Adding new schema: " + newS);
			return newS;
		}
		else
		{
			return m_schemas.get(i);
		}
	}

	public ISchema createSchema(int id)
	{
		return Schema.createSchema(id);
	}

	public ISchema createPrimitiveSchema(int id, String tag, int successSat, int failureSat)
	{
		return Schema.createSchema(id, tag, successSat, failureSat);
	}
	
	public IProposition createProposition(ISchema schema, int weight, int expectation)
	{
		return Proposition.createProposition(schema, weight, expectation);
	}

}
