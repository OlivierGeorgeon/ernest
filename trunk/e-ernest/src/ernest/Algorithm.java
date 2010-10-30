package ernest;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

/**
 * Defines a default algorithm that determines ernest behavior. 
 * @author mcohen
 * @author ogeorgeon
 */
public class Algorithm implements IAlgorithm 
{
	public static final int ACTIVATION_THRESH = 1;
	public static final int REG_SENS_THRESH = 5;
	
	/**
	 *  used to break a tie when selecting schema...
	 */
	private static Random m_rand = new Random(); 
	
	/**
	 * the environment that Ernest is operating in...
	 */
	private IEnvironment m_env = Ernest.factory().getEnvironment();
	
	/**
	 *  a list of all of the schemas ever created ...
	 */
	private List<ISchema> m_schemas = new ArrayList<ISchema>();
	
	/**
	 *  the base, current, and new contexts
	 */
	private IContext m_baseContext = Ernest.factory().createContext();
	private IContext m_currentContext = Ernest.factory().createContext();
	
	private boolean m_bored = false;
	
	/**
	 * Creates a new instance of this algorithm...
	 */ 
	public static IAlgorithm createAlgorithm()
	{ return new Algorithm(); }
	
	/**
	 * Starts the algorithm and sets Ernest in motion...
	 * @author mcohen
	 * @author ogeorgeon
	 */
	public void run()
	{
		// a loop of decision cycles...
		int iCycle = 0;
		while (!m_bored)
		{
			System.out.println("Decision cycle #" + iCycle++);
			
			// print all of the existing schemas..
			System.out.println("Schemas: ");
			//for (ISchema s : m_schemas)
				//System.out.println(s);
			
			// create a proposition list of possible intention schemas
			List<IProposition>  propositions = propose(m_currentContext);
			
			// Select an act to enact among the proposition list 
			IAct intentionAct = selectAct(propositions);

			// enact the selected act...
			IAct enactedAct = enactAct(intentionAct);
			System.out.println("Enacted " + enactedAct );
			
			// Determine the performed act
			IAct performedAct = performedAct(intentionAct, enactedAct);
			System.out.println("Performed " + performedAct );
			
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

			// Assess the new context
			m_baseContext = m_currentContext;
			
			m_currentContext = Ernest.factory().createContext();
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
			for (IAct a : m_currentContext.getContextList())
				System.out.println(a);
			
			// boredeome
			m_bored = boredome(enactedAct);
		}
		
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
						System.out.println("Activate " + s);
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
						IProposition p = Ernest.factory().createProposition(s.getIntentionAct().getSchema(), w, e);
	
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
								IProposition p = Ernest.factory().createProposition(s.getIntentionAct().getSchema().getContextAct().getSchema(), w, e);
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
				IProposition p = Ernest.factory().createProposition(s, 0, 0);
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
	 * This method is called recursively as it performs a depth first search, following
	 * the context branch first and then the intention branch.  As it encounters
	 * primitive schema it enacts them.
	 * @author mcohen
	 * @author ogeorgeon
	 * @param a the act to be enacted
	 * @return the act that was actually enacted
	 */
	protected IAct enactAct(IAct a)
	{
		// System.out.println("Enacting " + a);

		// get the schema associated with the act that we need
		// to enact...
		ISchema s = a.getSchema();
		
		// if the schema is not primitive, then we need to search for
		// a primitive schema to enact, so we search the context branch
		// first, then the intention branch... 
		if (!s.isPrimitive())
		{
			// first search the left branch (context)...
			// TODO check the construction of the actually enacted act
			IAct intendedContext = s.getContextAct();
			IAct enactedContext = enactAct(intendedContext) ;

			boolean contextCorrect = (enactedContext == intendedContext);
			
			// if unexpectedly succeeded then the context incorrect enacted
			if (!intendedContext.isSuccess() && enactedContext == intendedContext.getSchema().getSucceedingAct())  
				contextCorrect = false;
			
			if (contextCorrect)
			{
				// then the right branch (intention)...
				IAct intendedIntention = s.getIntentionAct();
				IAct enactedIntention = enactAct(intendedIntention); 
				
				boolean intentionCorrect = (enactedIntention == intendedIntention);
				
				// if unexpectedly succeeded then the intention is incorrecty enacted
				if (!intendedIntention.isSuccess() && enactedIntention == intendedIntention.getSchema().getSucceedingAct())
						intentionCorrect = false;
				
				if (intentionCorrect)
				{
					// If intended to succeed then the enaction is correct
					if (a.isSuccess())
						// returns the intended act
						return a;
					// If intended to fail then the enaction is incorrect					
					else
						// the enacted schema is the previously enacted context with the actually enacted intention
						{
							ISchema newS = Ernest.factory().addSchema(m_schemas, enactedContext, enactedIntention);
							System.out.println("incorrectly enacted act  " + a);					
							System.out.println("enacted " + newS.getSucceedingAct());					
							return 	newS.getSucceedingAct();
						}
				}
				// the intention is incorrectly enacted
				else
				{
					// if intended to succeed then the enaction is incorrect
					if (a.isSuccess())
					{
						// the enacted schema is the previously enacted context with the actually enacted intention
						ISchema newS = Ernest.factory().addSchema(m_schemas, enactedContext, enactedIntention);
						// m_context.add(newS.getSucceedingAct());
						System.out.println("incorrectly enacted act  " + a);					
						System.out.println("enacted " + newS.getSucceedingAct());					
						return 	newS.getSucceedingAct();
					}
					// if intended to fail then the enaction is correct
					else
						return a;
				}
			}
			// the context is incorrectly enacted
			else
			{
				// then the current enaction is interrupted
				System.out.println("Context incorrectly enacted: " + enactedContext);					
				System.out.println("Act interrupted: " + a);					
				return 	enactedContext;
			}
		}
		else
		{
			// We found a primitive schema so enact it in the environment
			// Return the actually enacted primitive act (may be that intended or not)
			if (m_env.enactSchema(s))
			{
				return a.getSchema().getSucceedingAct();
			}
			else
			{
				return 	a.getSchema().getFailingAct();
			}
		}
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
		IAct performedAct = enacted;
		
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
		IContext newContext = Ernest.factory().createContext();
		
		// For each act in the context ...
		for (IAct contextAct : context.getContextList())
		{
			// Build a new schema with the context act 
			// and the intention act 
			ISchema newSchema = Ernest.factory().addSchema(m_schemas, contextAct, intentionAct);
			newSchema.incWeight();
			System.out.println("Reinfocing schema " + newSchema);
			
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

	/**
	 * Prevents this class from being created explicitly.  Instead, the createAlgorithm method
	 * must be called.  This makes it possible to derive new algorithms from this class
	 * and Ernest will use the new algorithm without any code breaking.
	 */
	private Algorithm()
	{
		// start off with all primitive schema supported
		// by the environment...
		m_schemas.addAll(m_env.getPrimitiveSchema());
	}
}
