package ernest;

/**
 * The binary sensorymotor system can only sense a binary feedback from the environment.
 * This sensorymotor system is provided as an example for the SimpleMaze environment,
 * and as a parent class for more complex sensorymotor systems.
 * @author ogeorgeon
 */
public class BinarySensorymotorSystem implements ISensorymotorSystem 
{
	protected EpisodicMemory m_episodicMemory;
	protected IAttentionalSystem m_attentionalSystem;

	public void init(EpisodicMemory episodicMemory, IAttentionalSystem attentionalSystem)
	{
		m_episodicMemory = episodicMemory;
		m_attentionalSystem = attentionalSystem;
	}
	
	public IAct addPrimitiveAct(String schemaLabel, boolean status, int satisfaction) 
	{
		IAct a = null;
		ISchema s =  m_episodicMemory.addPrimitiveSchema(schemaLabel);
		
		if (status)
		{
			a = m_episodicMemory.addAct("(" + schemaLabel + ")", s, status,  satisfaction,  Ernest.RELIABLE);
			s.setSucceedingAct(a);
		}
		else 
		{
			a = m_episodicMemory.addAct("[" + schemaLabel + "]", s, status,  satisfaction,  Ernest.RELIABLE);
			s.setFailingAct(a);
		}
		
		System.out.println("Primitive schema " + s);
		return a;
	}

	/**
	 * Determine the enacted act 
	 * This implementation does not assume that the resulting act already exists
	 * If the resulting act did not exist then it is created with a satisfaction value of 0.
	 * @param schema The enacted primitive schema
	 * @param status The status returned as a feedback from the enacted schema
	 * @return The enacted act
	 */
	public IAct enactedAct(ISchema schema, boolean status) 
	{
		// The schema is null during the first cycle
		if (schema == null) return null;
		
		String label = schema.getLabel();
		
		if (status)
			label = "(" + label + ")";
		else 
			label = "[" + label + "]";
			
		// Create the act in episodic memory if it does not exist.	
		IAct enactedAct = m_episodicMemory.addAct(label, schema, status, 0, Ernest.RELIABLE);
		
		return enactedAct;
	}

	/**
	 * Not used by the binary sensorymotor system
	 */
	public void senseMatrix(int[][] matrix) 
	{
	}

}
