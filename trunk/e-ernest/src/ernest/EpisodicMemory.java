package ernest;

import java.util.ArrayList;
import java.util.List;

/**
 * Ernest's episodic memory contains all the schemas and acts ever created
 * @author ogeorgeon
 */
public class EpisodicMemory 
{
	/** A list of all the schemas ever created ... */
	private List<ISchema> m_schemas = new ArrayList<ISchema>(1000);

	/** A list of all the acts ever created. */
	public List<IAct> m_acts = new ArrayList<IAct>(2000);
	
	/** Counter of learned schemas for tracing */
	private int m_learnCount = 0;
	public int getLearnCount() { return m_learnCount; };
	public void resetLearnCount() { m_learnCount = 0; };

	/**
	 * Add an act to episodic memory if it does not already exist
	 * @param label The act's label
	 * @param schema The act's schema
	 * @param status The act's status
	 * @param satisfaction The act's satisfaction
	 * @param confidence The act's confidence
	 * @return the new art if created or the already existing act
	 */
	public IAct addAct(String label, ISchema schema, boolean status, int satisfaction, int confidence)
	{
		IAct a = Act.createAct(label, schema, status, satisfaction, Ernest.CENTRAL, confidence);
		
		int i = m_acts.indexOf(a);
		if (i == -1)
			// The act does not exist
			m_acts.add(a);
		else 
			// The act already exists: return a pointer to it.
			a =  m_acts.get(i);
		return a;
	}
	
	/**
	 * Add a primitive schema 
	 * @param label The schema's string identifier.
	 * @return The created primitive schema.
	 */
	public ISchema addPrimitiveSchema(String label) 
	{
		ISchema s =  Schema.createMotorSchema(m_schemas.size() + 1, label, Ernest.CENTRAL);
		int i = m_schemas.indexOf(s);
		if (i == -1)
			m_schemas.add(s);
		else
			// The schema already exists: return a pointer to it.
			s =  m_schemas.get(i);
    	return s;
	}

	/**
	 * Add a composite schema and its succeeding act that represent a composite possibility 
	 * of interaction between Ernest and its environment. 
	 * @param contextAct The context Act.
	 * @param intentionAct The intention Act.
	 * @return The schema made of the two specified acts, whether it has been created or it already existed. 
	 */
    public ISchema addCompositeInteraction(IAct contextAct, IAct intentionAct)
    {
    	ISchema s = Schema.createCompositeSchema(m_schemas.size() + 1, contextAct, intentionAct);
    	
		int i = m_schemas.indexOf(s);
		if (i == -1)
		{
			// The schema does not exist: create its succeeding act and add it to Ernest's memory
	    	s.setSucceedingAct( Act.createCompositeSucceedingAct(s) );
			m_schemas.add(s);
			m_learnCount++;
		}
		else
			// The schema already exists: return a pointer to it.
			s =  m_schemas.get(i);
    	return s;
    }
}
