package ernest;

import java.awt.Color;


/**
 * The binary sensorymotor system can only sense a binary feedback from the environment.
 * This sensorymotor system is provided as an example for the SimpleMaze environment,
 * and as a parent class for more complex sensorymotor systems.
 * @author ogeorgeon
 */
public class BinarySensorymotorSystem implements ISensorymotorSystem 
{
	protected EpisodicMemory m_episodicMemory;
	protected StaticSystem m_staticSystem;
	protected IAttentionalSystem m_attentionalSystem;
	protected ITracer m_tracer;

	public void init(EpisodicMemory episodicMemory, StaticSystem staticSystem, IAttentionalSystem attentionalSystem, ITracer tracer)
	{
		m_episodicMemory = episodicMemory;
		m_staticSystem = staticSystem;
		m_attentionalSystem = attentionalSystem;
		m_tracer = tracer;
		// TODO clean this up.
		if (tracer == null) System.out.println("The method Ernest.setTracer() must be called before the method Ernest.setSensorymotorSystem.");
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
	public IAct enactedAct(IAct act, boolean status) 
	{
		// The schema is null during the first cycle
		if (act == null) return null;
		
		String label = act.getSchema().getLabel();
		
		if (status)
			label = "(" + label + ")";
		else 
			label = "[" + label + "]";
			
		// Create the act in episodic memory if it does not exist.	
		IAct enactedAct = m_episodicMemory.addAct(label, act.getSchema(), status, 0, Ernest.RELIABLE);
		
		return enactedAct;
	}

	public IAct enactedAct(IAct act, int[][] matrix) {
		// TODO Auto-generated method stub
		return null;
	}

}
