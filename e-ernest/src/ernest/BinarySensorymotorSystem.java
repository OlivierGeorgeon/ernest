package ernest;

import spas.ISpas;
import imos.IAct;
import imos.IImos;




/**
 * The binary sensorymotor system can only sense a binary feedback from the environment.
 * This sensorymotor system is provided as an example for the SimpleMaze environment,
 * and as a parent class for more complex sensorymotor systems.
 * @author ogeorgeon
 */
public class BinarySensorymotorSystem implements ISensorymotorSystem 
{
	protected IImos m_imos;
	protected ITracer m_tracer;
	protected spas.ISpas m_spas;

	public void init(ISpas spas, IImos imos, ITracer tracer)
	{
		m_spas = spas;
		m_imos = imos;
		m_tracer = tracer;
		// TODO clean this up.
		if (tracer == null) System.out.println("The method Ernest.setTracer() must be called before the method Ernest.setSensorymotorSystem.");
	}
	
	public IAct addPrimitiveAct(String schemaLabel, boolean status, int satisfaction) 
	{
		return m_imos.addPrimitiveAct(schemaLabel, status, satisfaction);
	}

	public IAct addInteraction(String schemaLabel, String stimuliLabel, int satisfaction)
	{
		return m_imos.addInteraction(schemaLabel, stimuliLabel, satisfaction);
	}

	/**
	 * Determine the enacted act 
	 * This implementation does not assume that the resulting act already exists
	 * If the resulting act did not exist then it is created with a satisfaction value of 0.
	 * @param status The status returned as a feedback from the enacted schema
	 * @return The enacted act
	 */
	public IAct enactedAct(IAct act, boolean status) 
	{
		// The schema is null during the first cycle
		if (act == null) return null;
		
		String actLabel;
		
		if (status)
			actLabel = "(" + act.getSchema().getLabel() + ")";
		else 
			actLabel = "[" + act.getSchema().getLabel() + "]";
			
		// Create the act in episodic memory if it does not exist.	
		//IAct enactedAct = m_imos.addAct(label, act.getSchema(), status, 0, Ernest.RELIABLE);
		IAct enactedAct = m_imos.addInteraction(act.getSchema().getLabel(), (status ? "t" : "f"), 0);
		
		return enactedAct;
	}

	public IAct enactedAct(IAct act, int[][] matrix) {
		// TODO Auto-generated method stub
		return null;
	}

	public int impulsion(int intentionSchema) {
		// TODO Auto-generated method stub
		return 0;
	}

}
