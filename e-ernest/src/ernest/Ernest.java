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
	public static final int ACTIVATION_THRESH = 1;
	public static final int REG_SENS_THRESH = 5;
	
	/**
	 *  used to break a tie when selecting schema...
	 */
	private static Random m_rand = new Random(); 
	
	/**
	 *  a list of all of the schemas ever created ...
	 */
	private List<ISchema> m_schemas = new ArrayList<ISchema>();
	
	/**
	 *  the base and current contexts
	 */
	private IContext m_baseContext = Main.factory().createContext();
	private IContext m_currentContext = Main.factory().createContext();
	
	private boolean m_bored = false;
	
	/**
	 *  the logger
	 */
	private ILogger m_logger = Main.factory().createLogger("trace.txt", true);
	
	
	/**
	 * Adds a primitive schema and its succeeding and failing acts for intialization
	 */
	public void addPrimitiveSchema(String tag, int valSucceed, int valFail) 
	{
		ISchema s = Main.factory().createPrimitiveSchema(m_schemas.size() + 1, tag, valSucceed, valFail); 
		m_schemas.add(s);	
	}

	/**
	 * Initialize the logger that generates the trace file
	 */
	public void setLogger(ILogger logger) 
	{
		m_logger = logger;
	}

	/**
	 * Clears Ernest's schema list
	 * TODO Make sure the memory is actually cleared.
	 */
	public void clear() 
	{
		m_schemas.clear();
	}

	/**
	 * Run Ernest one step
	 * @return the next primitive schema to enact
	 * @author ogeorgeon
	 */
	public String step(boolean status) 
	{
		
		// Compute the current actually enacted act
		
		IAct intendedPrimitiveAct = m_currentContext.getPrimitiveIntention();
		IAct enactedPrimitiveAct = null;
		IAct enactedAct = null;
		IAct intentionAct = null;
		
		if (intendedPrimitiveAct == null)
			// Context is empty. Intialization.
			m_logger.writeLine("Context empty");
		else
		{
			ISchema enactedPrimitiveSchema = intendedPrimitiveAct.getSchema();
			enactedPrimitiveAct = 	enactedPrimitiveSchema.getResultingAct(status);
			enactedAct = enactedAct(enactedPrimitiveSchema, enactedPrimitiveAct);
			
			System.out.println("Enacted " + enactedAct );
			// Proceed the ongoing schema's enaction to the next act to enact
			
			intentionAct = nextAct(intendedPrimitiveAct, status);

			if (intentionAct == null)
			{
				// No ongoing schema to enact
				
				// Determine the performed act
				IAct performedAct = performedAct(m_currentContext.getIntentionAct(), enactedAct);
				System.out.println("Performed " + performedAct );
				
				// Log the trace
				m_logger.writeLine(enactedAct.getTag());
				if (m_currentContext.getIntentionAct() != enactedAct)
					m_logger.writeLine("!");
				
				// Learn ====
			
				// learn from the current context and the performed act
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
				
				m_currentContext = Main.factory().createContext();
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
				System.out.println("Context: ");
			}
		}	

		// Select next =====
	
		// create a proposition list of possible intention schemas
		List<IProposition>  propositions = propose(m_currentContext);
		
		// Select an act to enact among the proposition list 
		intentionAct = selectAct(propositions);

		// Prescribe subacts and subschemas		
		m_currentContext.setIntentionAct(intentionAct);
		IAct nextPrimitiveAct = prescribeSubacts(intentionAct);
		m_currentContext.setPrimitiveIntention(nextPrimitiveAct);

		return nextPrimitiveAct.getSchema().getTag();
		
	}
	
	/**
	 * Recursively construct the current actually enacted act. 
	 * WARNING: may construct extra intermediary acts
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
				ISchema enactedSchema = Main.factory().addSchema(m_schemas, prescriberSchema.getContextAct(), a);
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
				a.setPrescriberSchema(null);						
			}
			else
			{
				// move to prescriber act with a failure status
				IAct prescriberAct = prescriberSchema.getPrescriberAct();
				nextAct = nextAct(prescriberAct, false);				
			}
		}

		a.setPrescriberSchema(null);
		return nextAct;
	}
	
	/**
	 * Compute the performed act
	 * The performed act is at the same hierarchical level as the intended act
	 * If the intention was correctly enacted then the performed act equals the intention act
	 * If the intention was incorrectly enacted then the performed act is complementary to the intention act 
	 *  (failure if expected success and success if expected failure)
	 * @author ogeorgeon
	 * @return the performed act 
	 */
	protected IAct performedAct(IAct intention, IAct enacted)
	{
		IAct performedAct = null;
		
		if (intention != null)
		{
			performedAct = enacted;
			// for non primitive intentions
			if (!intention.getSchema().isPrimitive())
			{
				// if the enacted act is not that intended
				if (enacted != intention)
				{
					// if intended to succeed
					if (intention.isSuccess())
					{
						// Initialize or update the intention's failing act using the actually enacted act's satisfaction
						// the performed act is the filing act
						performedAct = intention.getSchema().initFailingAct(enacted.getSat());
					}
					// if intended to fail
					else
					{
						// if failed indeed then the performed act is the intention
						if (enacted.getSchema() != intention.getSchema())
							performedAct = intention;
						// if accidentally succeeded then the performed act is the enacted act
					}
				}
				// if the enacted act is that intended then the performed act equals the enacted act
			}
			// for primitive intended acts, the performed act is the enacted act
		}		
		return performedAct;
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
		IContext newContext = Main.factory().createContext();
		
		// For each act in the context ...
		for (IAct contextAct : context.getContextList())
		{
			// Build a new schema with the context act 
			// and the intention act 
			ISchema newSchema = Main.factory().addSchema(m_schemas, contextAct, intentionAct);
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
					if (s.getIntentionAct().getSchema().getWeight() > REG_SENS_THRESH)
					{
						IProposition p = Main.factory().createProposition(s.getIntentionAct().getSchema(), w, e);
	
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
								IProposition p = Main.factory().createProposition(s.getIntentionAct().getSchema().getContextAct().getSchema(), w, e);
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

			// Schemas that pass the threshold also receive a default proposition for themselves
			//if (s.getWeight() > Schema.REG_SENS_THRESH)
			// Primitive schemas also receive a default proposition for themselves
			if (s.isPrimitive())
			{
				IProposition p = Main.factory().createProposition(s, 0, 0);
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
	 * When bored, clear the context
	 * Bored when the enacted act is a repetition and secondary
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
