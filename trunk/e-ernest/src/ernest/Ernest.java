package ernest;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

/**
 * The main Ernest class used to create an Ernest agent.
 * @author ogeorgeon
 */
public class Ernest implements IErnest 
{
	/** A big value that can represent infinite for diverse purpose. */
	public static final int INFINITE = 1000;
	/** Ernest's types of interaction. */
	public static final int MOTOR = 1;
	public static final int SENSOR = 2;

	/** Ernest's fundamental learning parameters. */
	private static int REG_SENS_THRESH = 5;
	private static int ACTIVATION_THRESH = 1;
	private static int SCHEMA_MAX_LENGTH = INFINITE;
	
	/** Used to break a tie when selecting a schema... */
	private static Random m_rand = new Random(); 
	/** Counter of learned schemas for tracing */
	private int m_learnCount = 0;
	
	/** Indication of Ernest's boredom. */
	private boolean m_bored = false;

	/** A representation of Ernest's internal state. */
	private String m_internalState = "";
	
	private IAct m_sensorAct = null;
	
	/** A list of all the schemas ever created ... */
	private List<ISchema> m_schemas = new ArrayList<ISchema>(1000);

	/** The base context. */
	private IContext m_baseContext = new Context();
	/** The current context. */
	private IContext m_currentContext = new Context();
	
	/** The Tracer. */
	private ITracer m_logger = null;
	public void setTracer(ITracer tracer) { m_logger = tracer; }


	/**
	 * Set Ernest's fundamental learning parameters.
	 * Use null to leave a value unchanged.
	 * @param regularityThreshold The Regularity Sensibility Threshold.
	 * @param activationThreshold The Activation Threshold.
	 * @param schemaMaxLength The Maximum Schema Length
	 */
	public void setParameters(Integer regularityThreshold, Integer activationThreshold, Integer schemaMaxLength) 
	{
		if (regularityThreshold != null)
			REG_SENS_THRESH = regularityThreshold.intValue();
		
		if (activationThreshold != null)
			ACTIVATION_THRESH = activationThreshold.intValue();
		
		if (schemaMaxLength != null)
			SCHEMA_MAX_LENGTH = schemaMaxLength.intValue();
	}

	/**
	 * Add a primitive schema and its two resulting acts that represent a primitive possibility 
	 * of interaction between Ernest and its environment.
	 * @param label The schema's string identifier.
	 * @param successSatisfaction The satisfaction in case of success.
	 * @param failureSatisfaction The satisfaction in case of failure.
	 * @return The created primitive schema.
	 */
	public ISchema addMotorInteraction(String label, int successSatisfaction, int failureSatisfaction) 
	{
		ISchema s =  Schema.createMotorSchema(m_schemas.size() + 1, label);
		IAct succeedingAct = new Act(s, true,  successSatisfaction);
		IAct failingAct = new Act(s, false, failureSatisfaction);
		
		s.setSucceedingAct(succeedingAct);
		s.setFailingAct(failingAct);

		m_schemas.add(s);
		System.out.println("Primitive schema " + s);
		return s;
	}

	/**
	 * Add a primitive schema and its two resulting acts that represent a primitive possibility 
	 * of interaction between Ernest and its environment.
	 * @param label The schema's string identifier.
	 * @param successSatisfaction The satisfaction in case of success.
	 * @param failureSatisfaction The satisfaction in case of failure.
	 * @return The created primitive schema.
	 */
	public ISchema addSensorInteraction(String label, int[][] matrix) 
	{
		ISchema s =  Schema.createSensorSchema(m_schemas.size() + 1, label, matrix);

		int i = m_schemas.indexOf(s);
		if (i == -1)
		{
			// The schema does not exist: create its succeeding act and add it to Ernest's memory
			IAct succeedingAct = new Act(s, true,  0);
			IAct failingAct = new Act(s, false, 0);			
			s.setSucceedingAct(succeedingAct);
			s.setFailingAct(failingAct);
	
			m_schemas.add(s);
		}
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
	    	s.setSucceedingAct(new Act(s, true, contextAct.getSatisfaction() + intentionAct.getSatisfaction()));
			m_schemas.add(s);
			m_learnCount++;
		}
		else
			// The schema already exists: return a pointer to it.
			s =  m_schemas.get(i);
    	return s;
    }

	/**
	 * Reset Ernest by clearing all its long-term memory.
	 */
	public void clear() 
	{
		m_schemas.clear();
	}

	/**
	 * Get a description of Ernest's internal state.
	 * @return A representation of Ernest's internal state
	 */
	public String internalState() 
	{
		return m_internalState;
	}

	/**
	 * Set the current state of Ernest's sensory system. 
	 * Convert the sensory state into an icon and add it to the context.
	 * @param matrix The matrix that inform Ernest's distal sensory system.
	 */
	public void setSensor(int[][] matrix) 
	{
    	ISchema s = addSensorInteraction("Icon", matrix);
    	
    	m_sensorAct = s.getSucceedingAct();
   	}
		
	/**
	 * Run Ernest one step.
	 * @param status The status received in return from the previous schema enaction.
	 * @return The next primitive schema to enact.
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
			enactedPrimitiveAct = 	enactedPrimitiveSchema.resultingAct(status);
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
					performedAct = addFailingInteraction(intendedSchema,enactedAct.getSatisfaction());
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
				
				// add the sensory act to the focus list
				m_currentContext.addFocusAct(m_sensorAct);
								
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
	 * @param s The enacted schema.
	 * @param a The intention act.
	 * @return the actually enacted act
	 */
	private IAct enactedAct(ISchema s, IAct a)
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
	 * Recursively finds the next act to enact in the hierarchy of prescribers.
	 * @param a The enacted act.
	 * @param status The enaction status.
	 * @return the next intention act to enact or null if failed or completed
	 */
	private IAct nextAct(IAct a, boolean status)
	{
		IAct nextAct = null;
		ISchema prescriberSchema = a.getPrescriberSchema();
		a.setPrescriberSchema(null); // (It might be the case that the same act will be prescribed again)
		
		if (prescriberSchema != null)
		{
			if (a.getStatus() == status)
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
	 * Learn from an enacted intention after a given context.
	 * @param The context in which the learning occurs.
	 * @param The intention.
	 * @return A partial new context created from the learning.
	 */
	private IContext learn(IContext context, IAct intentionAct)
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
	 * Generates the list of proposed schemas.
	 * @param The context that generates the proposals.
	 * @return A list of proposals.
	 */
	private List<IProposition> propose(IContext context)
	{

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
					int w = s.getWeight() * s.getIntentionAct().getSatisfaction();
					// The expectation is the proposing schema's weight signed with the proposed act's status  
					int e = s.getWeight() * (s.getIntentionAct().getStatus() ? 1 : -1);
					
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

			// Primitive schemas also receive a default proposition for themselves
			if (s.isPrimitive() && (s.getType() == MOTOR))
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
	 * Select the intention schema with the highest proposition.
	 * @param The list of proposals that will generate the selection.
	 * @return The act that wins the selection contest.
	 */
	private IAct selectAct(List<IProposition> proposals)
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

		// pick one at random from the top of the proposal list
		// count is equal to the number of proposals that are tied...

		IProposition p = proposals.get(m_rand.nextInt(count));
		
		ISchema s = p.getSchema();
		
		IAct a = (p.getExpectation() >= 0 ? s.getSucceedingAct() : s.getFailingAct());
		
		System.out.println("Select:" + a);

		return a ;
	}

	/**
	 * Recursively prescribe an act's subacts and subschemas.
	 * @param The prescriber act.
	 * @return The prescribed act.
	 */
	private IAct prescribeSubacts(IAct a)
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
	 * Add or update a failing possibility of interaction between Ernest and its environment.
	 * Add or update the schema's failing act to Ernest's memory. 
	 * If the failing act does not exist then create it. 
	 * If the failing act exists then update its satisfaction.
	 * @param The schema that failed.
	 * @param The satisfaction obtained during the failure.
	 * @return The failing act.
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
				failingAct.setSatisfaction((failingAct.getSatisfaction() + satisfaction)/2);
		}
		
		return failingAct;
    }

	/**
	 * Detect boredom when the enacted act is a repetition and secondary.
	 * @param enacted The enacted act to test for boredom.
	 * @return True if bored, false if not bored.
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
