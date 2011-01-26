package ernest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
	
	/** Random generator used to break a tie when selecting a schema... */
	private static Random m_rand = new Random(); 

	/** Counter of learned schemas for tracing */
	private int m_learnCount = 0;
	/**
	 * @return the number of schema learned since the last reset
	 */
	public int getLearnCount() { return m_learnCount; };
	/**
	 * Reset the count of schema learned
	 */
	public void resetLearnCount() { m_learnCount = 0; };

	/**
	 * Reset the episodic memory.
	 * TODO Maybe should not clear primitive schemas and acts.
	 */
	public void clear() 
	{
		m_schemas.clear();
		m_acts.clear();
	}

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
		IAct a = Act.createAct(label, schema, status, satisfaction, confidence);
		
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
		ISchema s =  Schema.createMotorSchema(m_schemas.size() + 1, label);
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
			IAct a = Act.createCompositeSucceedingAct(s); 
	    	s.setSucceedingAct(a);
			m_schemas.add(s);
			m_acts.add(a);
			m_learnCount++;
		}
		else
			// The schema already exists: return a pointer to it.
			s =  m_schemas.get(i);
    	return s;
    }

	/**
	 * Add or update a failing possibility of interaction between Ernest and its environment.
	 * Add or update the schema's failing act to Ernest's memory. 
	 * If the failing act does not exist then create it. 
	 * If the failing act exists then update its satisfaction.
	 * @param schema The schema that failed.
	 * @param satisfaction The satisfaction obtained during the failure.
	 * @return The failing act.
	 */
    public IAct addFailingInteraction(ISchema schema, int satisfaction)
    {
    	IAct failingAct = schema.getFailingAct();
    	
		if (!schema.isPrimitive())
		{
			if (failingAct == null)
			{
				failingAct = Act.createCompositeFailingAct(schema, satisfaction);
				schema.setFailingAct(failingAct);
				m_acts.add(failingAct);
			}
			else
				// If the failing act already exists then 
				//  its satisfaction is averaged with the previous value
				failingAct.setSatisfaction((failingAct.getSatisfaction() + satisfaction)/2);
		}
		
		return failingAct;
    }

	/**
	 * Select an intention act from a given activation list.
	 * @param context The context that generates the proposals.
	 * @return The selected act.
	 */
	public IAct selectAct(IContext context)
	{

		List<IProposition> proposals = new ArrayList<IProposition>();	
		
		// Browse all the existing schemas 
		for (ISchema s : m_schemas)
		{
			if (!s.isPrimitive())
			{
				// Activate the schemas that match the context 
				boolean activated = false;
				for (IAct contextAct : context.getActivationList())
				{
					if (s.getContextAct().equals(contextAct))
					{
						activated = true;
						System.out.println("Activate " + s);
					}
				}
				
				// Activated schemas propose their intention
				if (activated)
				{
					// The weight is the proposing schema's weight multiplied by the proposed act's satisfaction
					int w = s.getWeight() * s.getIntentionAct().getSatisfaction();
					// The expectation is the proposing schema's weight signed with the proposed act's status  
					int e = s.getWeight() * (s.getIntentionAct().getStatus() ? 1 : -1);
					
					// If the intention is reliable
					if ((s.getIntentionAct().getConfidence() == Ernest.RELIABLE ) &&						 
						(s.getIntentionAct().getSchema().getLength() <= Ernest.SCHEMA_MAX_LENGTH ))
					{
						IProposition p = new Proposition(s.getIntentionAct().getSchema(), w, e);
	
						int i = proposals.indexOf(p);
						if (i == -1)
							proposals.add(p);
						else
							proposals.get(i).update(w, e);
					}
					// if the intention's schema has not passed the threshold then  
					// the activation is propagated to the intention's schema's context
					else
					{
						if (!s.getIntentionAct().getSchema().isPrimitive())
						{
							// only if the intention's intention is positive (this is some form of positive anticipation)
							if (s.getIntentionAct().getSchema().getIntentionAct().getSatisfaction() > 0)
							{
								IProposition p = new Proposition(s.getIntentionAct().getSchema().getContextAct().getSchema(), w, e);
								int i = proposals.indexOf(p);
								if (i == -1)
									proposals.add(p);
								else
									proposals.get(i).update(w, e);
							}
						}
					}
				}
			}

			// Primitive sensorymotor schemas also receive a default proposition for themselves
			if (s.isPrimitive())
			{
				IProposition p = new Proposition(s, 0, 0);
				if (!proposals.contains(p))
					proposals.add(p);
			}
		}

		// System.out.println("Propose: ");
		for (IProposition p : proposals)
			System.out.println(p);

		// sort by weighted proposition...
		Collections.sort(proposals);
		
		// count how many are tied with the  highest weighted proposition
		int count = 0;
		int wp = proposals.get(0).getWeight();
		for (IProposition p : proposals)
		{
			if (p.getWeight() != wp)
				break;
			count++;
		}

		// pick one at random from the top of the proposal list
		// count is equal to the number of proposals that are tied...

		IProposition p = proposals.get(m_rand.nextInt(count));
		
		ISchema s = p.getSchema();
		
		IAct a = (p.getExpectation() >= 0 ? s.getSucceedingAct() : s.getFailingAct());
		
		// The noème's activation is set equal to its proposition's weight
		a.setActivation(p.getWeight());
		
		System.out.println("Select:" + a);

		return a ;
	}

}
