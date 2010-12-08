package ernest;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

/**
 * The main Ernest class. 
 * @author ogeorgeon
 */
public class Ernest implements IErnest 
{
	public static int REG_SENS_THRESH = 5;
	public static int ACTIVATION_THRESH = 1;
	public static int SCHEMA_MAX_LENGTH = 1000;
	
	/**
	 *  used to break a tie when selecting schema...
	 */
	private static Random m_rand = new Random(); 
	
	/**
	 *  Ernest's perception system
	 */
	private int m_perception = 1000;
	
	/**
	 *  a list of all of the schemas ever created ...
	 */
	private List<ISchema> m_schemas = new ArrayList<ISchema>();
	
	/**
	 *  the base and current contexts
	 */
	private IContext m_baseContext = new Context();
	private IContext m_currentContext = new Context();
	
	private boolean m_bored = false;
	private String m_internalState = "";
	
	/**
	 *  the logger
	 */
	private ITracer m_logger = null;
	private int m_learnCount = 0;
	
	
	/**
	 * Set the fundamental parameters
	 * Use null to let a value unchanged
	 * @author ogeorgeon
	 */
	public void setParameters(Integer RegularityThreshold, Integer ActivationThreshold, Integer schemaMaxLength) 
	{
		if (RegularityThreshold != null)
			REG_SENS_THRESH = RegularityThreshold.intValue();
		
		if (ActivationThreshold != null)
			ACTIVATION_THRESH = ActivationThreshold.intValue();
		
		if (schemaMaxLength != null)
			SCHEMA_MAX_LENGTH = schemaMaxLength.intValue();
	}

	/**
	 * Add a primitive possibility of interaction between Ernest and its environment
	 * Add the primitive schema, its succeeding act, and its failing act to Ernest's schema memory 
	 * @author ogeorgeon
	 */
	public void addPrimitiveInteraction(String label, int successSatisfaction, int failureSatisfaction) 
	{
		ISchema s =  Schema.createPrimitiveSchema(m_schemas.size() + 1, label);
		s.setSucceedingAct( new Act(s, true,  successSatisfaction));
		s.setFailingAct(new Act(s, false, failureSatisfaction));

		m_schemas.add(s);	
		
		System.out.println("Primitive schema " + s);
	}

    /**
	 * Initialize the logger that generates the trace file
	 */
	public void setTracer(ITracer tracer) 
	{
		m_logger = tracer;
	}

	/**
	 * Clears Ernest's schema list
	 */
	public void clear() 
	{
		m_schemas.clear();
	}

	/**
	 * Returns information on Ernest's internal state
	 * @author ogeorgeon
	 */
	public String getState() 
	{
		return m_internalState;
	}

	/**
	 * Set Ernest's perceptual matrix
	 * @author ogeorgeon
	 */
	public void setSensor(String p) 
	{
		m_perception = Integer.parseInt(p);
	}
		
	/**
	 * Run Ernest one step
	 * @return the next primitive schema to enact
	 * @author ogeorgeon
	 */
	public String step(boolean status) 
	{
		m_internalState= "";

		IAct intendedPrimitiveAct = m_currentContext.getPrimitiveIntention();
		IAct enactedPrimitiveAct = null;
		IAct enactedAct = null;
		IAct intentionAct = null;
		IAct performedAct = null;
		
		if (intendedPrimitiveAct == null)
			// Context is empty. Initialization.
			System.out.println("Context empty");
		else
		{
			// Compute the actually enacted act
			
			ISchema enactedPrimitiveSchema = intendedPrimitiveAct.getSchema();
			enactedPrimitiveAct = 	enactedPrimitiveSchema.getResultingAct(status);
			enactedAct = enactedAct(enactedPrimitiveSchema, enactedPrimitiveAct);
			
			System.out.println("Enacted " + enactedAct );
			
			// Proceed the ongoing schema's enaction to the next act to enact
			
			intentionAct = nextAct(intendedPrimitiveAct, status);
			
			// Decision cycle

			if (intentionAct == null)
			{
				// No ongoing schema to enact. The decision cycle is over.  
				
				// Log the previous decision cycle's trace

				if (m_currentContext.getIntentionAct() != enactedAct)
					m_internalState= "!";
				m_logger.writeLine(enactedAct.getTag() + m_internalState);

				// Determine the performed act
				
				ISchema intendedSchema = m_currentContext.getIntentionAct().getSchema();
				if (intendedSchema == enactedAct.getSchema())
					performedAct = enactedAct;
				else
					performedAct = addFailingInteraction(intendedSchema,enactedAct.getSat());
				System.out.println("Performed " + performedAct );
				
				// learn from the  context and the performed act
				
				m_learnCount = 0;
				IContext streamContext = learn(m_currentContext, performedAct);
		
				// learn from the base context and the stream act
				
				IAct streamAct = streamContext.getCoreAct();
				System.out.println("Streaming " + streamAct);
				if (streamAct != null && streamAct.getSchema().getWeight() > ACTIVATION_THRESH)
					learn(m_baseContext, streamAct);
		
				// learn from the current context and the actually enacted act
				
				IAct streamAct2 = null;
				if (enactedAct != performedAct)
				{
					System.out.println("Learn from enacted: " );
					IContext streamContext2 = learn(m_baseContext, enactedAct);
					// learn from the base context and the streamAct2
					streamAct2 = streamContext2.getCoreAct();
					System.out.println("Streaming2 " + streamAct2 );
					if (streamAct2 != null && streamAct2.getSchema().getWeight() > ACTIVATION_THRESH)
						learn(m_baseContext, streamAct2);
				}			
		
				// Assess the new context ====
				
				m_baseContext = m_currentContext;
				
				m_currentContext = new Context();
				m_currentContext.setCoreAct(enactedAct); // rather than performedAct to avoid too many schemas including failing subschemas
				if (enactedAct != performedAct)
					m_currentContext.addFocusAct(performedAct);
		
				// if the actually enacted act is not primitive, its intention also belongs to the context
				if (!enactedAct.getSchema().isPrimitive())
					m_currentContext.addFocusAct(enactedAct.getSchema().getIntentionAct());	
				
				// add the streamcontext to the context list
				m_currentContext.addContext(streamContext);
				// m_currentContext.addContext(streamContext2);
				
				// print the new current context
				// System.out.println("Schemas: ");
				// for (ISchema s : m_schemas)
				// {	System.out.println(s);}
				System.out.println("Learned : " + m_learnCount + " schemas.");
					
			}
		}	

		if (intentionAct == null)
		{
			// Select next =====
		
			// create a proposition list of possible intention schemas
			List<IProposition>  propositions = propose(m_currentContext);
			
			// Select an act to enact among the proposition list 
			intentionAct = selectAct(propositions);
			m_currentContext.setIntentionAct(intentionAct);
		}
		
		// Prescribe subacts and subschemas		
		IAct nextPrimitiveAct = prescribeSubacts(intentionAct);
		m_currentContext.setPrimitiveIntention(nextPrimitiveAct);

		return nextPrimitiveAct.getSchema().getTag();
		
	}
	
	/**
	 * Recursively construct the current actually enacted act. 
	 *  (may construct extra intermediary schemas but that's ok because their weight is not incremented)
	 * @return the actually enacted act
	 * @author ogeorgeon
	 */
	protected IAct enactedAct(ISchema s, IAct a)
	{
		IAct enactedAct = null;
		ISchema prescriberSchema = s.getPrescriberAct().getPrescriberSchema();
		
		if (prescriberSchema == null)
			// top parent schema
			enactedAct = a;
		else
		{
			// The schema was prescribed
			if (prescriberSchema.getPointer() == 0)
			{
				// enacted the prescriber's context 
				enactedAct = enactedAct(prescriberSchema, a);
			}
			else
			{
				// enacted the prescriber's intention
				ISchema enactedSchema = addCompositeInteraction(prescriberSchema.getContextAct(), a);
				enactedAct = enactedAct(prescriberSchema, enactedSchema.getSucceedingAct());
			}
		}
			
		return enactedAct;
	}
	
	/**
	 * Recursively finds the next act to enact in the hierarchy of prescribers
	 * @return the next intention act to enact or null if failed or completed
	 * @author ogeorgeon
	 */
	protected IAct nextAct(IAct a, boolean status)
	{
		IAct nextAct = null;
		ISchema prescriberSchema = a.getPrescriberSchema();
		a.setPrescriberSchema(null); // (It might be the case that the same act will be prescribed again)
		
		if (prescriberSchema != null)
		{
			if (a.isSuccess() == status)
			{
				// Correctly enacted
				if (prescriberSchema.getPointer() == 0)
				{
					// context act correctly enacted, move to intention act
					prescriberSchema.setPointer(1);
					nextAct = prescriberSchema.getIntentionAct();
					nextAct.setPrescriberSchema(prescriberSchema);
				}
				else
				{
					// intention act correctly enacted, move to prescriber act with a success status
					IAct prescriberAct = prescriberSchema.getPrescriberAct();
					nextAct = nextAct(prescriberAct, true);
				}
				// a.setPrescriberSchema(null);						
			}
			else
			{
				// move to prescriber act with a failure status
				IAct prescriberAct = prescriberSchema.getPrescriberAct();
				nextAct = nextAct(prescriberAct, false);				
			}
		}

		return nextAct;
	}
	
	/**
	 * Learn from an enacted intention after a given context
	 * TODO: do not use the global variable m_context, that is not clean!
	 * @author mcohen
	 * @author ogeorgeon
	 * @return a partial new context created from the learning
	 */
	protected IContext learn(IContext context, IAct intentionAct)
	{
		IContext newContext = new Context();
		
		// For each act in the context ...
		for (IAct contextAct : context.getContextList())
		{
			// Build a new schema with the context act 
			// and the intention act 
			ISchema newSchema = addCompositeInteraction(contextAct, intentionAct);
			newSchema.incWeight();
			// System.out.println("Reinfocing schema " + newSchema);
			
			boolean reg = (newSchema.getContextAct().getSchema().getWeight() > REG_SENS_THRESH) &&
			  (newSchema.getIntentionAct().getSchema().getWeight() > REG_SENS_THRESH);

			// the returned core act is the act made from the previous core act
			if (contextAct == context.getCoreAct())
				newContext.setCoreAct(newSchema.getSucceedingAct());
			
			// other created acts are part of the context if 
			// their context and intention have passed the regularity
			//else if (newSchema.getWeight() > Schema.REG_SENS_THRESH)
			else if (reg)
			{
				newContext.addContextAct(newSchema.getSucceedingAct());
			}
		}
		return newContext; 
	}

	/**
	 * Generates the list of proposed schemas
	 * Activate the schemas whose context act belongs to the current context
	 * @author ogeorgeon
	 */
	protected List<IProposition> propose(IContext context)
	{

		// Clear the previous proposal list
		List<IProposition> proposals = new ArrayList<IProposition>();	
		
		// Browse all the schemas 
		for (ISchema s : m_schemas)
		{
			if (!s.isPrimitive())
			{
				// Activate the schemas that match the context 
				boolean activated = false;
				for (IAct contextAct : context.getFocusList())
				{
					if (s.getContextAct().equals(contextAct))
					{
						activated = true;
						// System.out.println("Activate " + s);
					}
				}
				
				// Activated schemas propose their intention
				if (activated)
				{
					// The weight is the proposing schema's weight multiplied by the proposed act's satisfaction
					int w = s.getWeight() * s.getIntentionAct().getSat();
					// The expectation is the proposing schema's weight signed with the proposed act's status  
					int e = s.getWeight() * (s.getIntentionAct().isSuccess() ? 1 : -1);
					
					// If the intention's schemas has passed the threshold
					if ((s.getIntentionAct().getSchema().getWeight() > REG_SENS_THRESH ) &&						 
						(s.getIntentionAct().getSchema().getLength() <= SCHEMA_MAX_LENGTH ))
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
							if (s.getIntentionAct().getSchema().getIntentionAct().getSat() > 0)
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

			// Primitive schemas also receive a default proposition for themselves
			if (s.isPrimitive())
			{
				IProposition p = new Proposition(s, 0, 0);
				if (!proposals.contains(p))
					proposals.add(p);
			}
		}

		System.out.println("Propose: ");
		for (IProposition p : proposals)
			System.out.println(p);
		return proposals;
	}

	/**
	 * Select the intention schema with the highest proposition
	 * @return the act that wins the selection contest
	 * @author ogeorgeon
	 */
	protected IAct selectAct(List<IProposition> proposals)
	{
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

		// pick one at random from the top the proposal list
		// count is equal to the number of proposals that are tied...

		IProposition p = proposals.get(m_rand.nextInt(count));
		
		ISchema s = p.getSchema();
		
		IAct a = (p.getExpectation() >= 0 ? s.getSucceedingAct() : s.getFailingAct());
		
		System.out.println("Select:" + a);

		return a ;
	}

	/**
	 * Recursively prescribe an act's subacts and subschemas
	 * @return the primitive prescribed act
	 * @author ogeorgeon
	 */
	protected IAct prescribeSubacts(IAct a)
	{
		IAct primitiveAct = null;
		ISchema subschema = a.getSchema();
		subschema.setPrescriberAct(a);
		
		if (subschema.isPrimitive())
			primitiveAct = a;
		else
		{
			subschema.setPointer(0);
			IAct subact = subschema.getContextAct();
			subact.setPrescriberSchema(subschema);
			primitiveAct = prescribeSubacts(subact);
		}
		
		return primitiveAct;
	}

	/**
	 * Add a composite possibility of interaction between Ernest and its environment 
	 * If the composite schema does not exist then add it and its succeeding act to Ernest's memory
	 * @author ogeorgeon
	 */
    private ISchema addCompositeInteraction(IAct contextAct, IAct intentionAct)
    {
    	ISchema s = Schema.createCompositeSchema(m_schemas.size() + 1, contextAct, intentionAct);
    	
		int i = m_schemas.indexOf(s);
		if (i == -1)
		{
			// The schema does not exist: create its succeeding act and add them to Ernest's memory
	    	s.setSucceedingAct(new Act(s, true, contextAct.getSat() + intentionAct.getSat()));
			m_schemas.add(s);
			m_learnCount++;
		}
		else
			// The schema already exists: return a pointer to it.
			s =  m_schemas.get(i);

    	return s;
    }

	/**
	 * Add or update a failing possibility of interaction between Ernest and its environment
	 * Add a update schema's failing act to Ernest's memory 
	 * If the failing act does not exist then create it 
	 * If the failing act exists then update its satisfaction
	 * @author ogeorgeon
	 */
    private IAct addFailingInteraction(ISchema schema, int satisfaction)
    {
    	IAct failingAct = schema.getFailingAct();
    	
		if (!schema.isPrimitive())
		{
			if (failingAct == null)
			{
				failingAct = new Act(schema, false, satisfaction);
				schema.setFailingAct(failingAct);
			}
			else
				// If the failing act already exists then 
				//  its satisfaction is averaged with the previous value
				failingAct.setSat((failingAct.getSat() + satisfaction)/2);
		}
		
		return failingAct;
    }

	/**
	 * Detect boredom when the enacted act is a repetition and secondary
	 * @author ogeorgeon
	 * @return true if bored, false if not bored
	 */
	private boolean boredome(IAct enacted)
	{
		boolean bored = false;
		
		ISchema enactedSchema = enacted.getSchema();
		if (!enactedSchema.isPrimitive())
			if (!enactedSchema.getContextAct().getSchema().isPrimitive())
				if (enactedSchema.getContextAct() == enactedSchema.getIntentionAct())
				{
					// m_context.clear();
					bored = true;
					System.out.println("Bored");				
				}
		return bored;
	}

}
