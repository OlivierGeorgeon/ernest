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
	 *  a list of the current proposed acts...
	 */
	private List<IProposition> m_proposals = new ArrayList<IProposition>();	
	
	/**
	 *  the current context, which is a list of some of the most recently enacted acts...
	 */
	private List<IAct> m_context = new ArrayList<IAct>();
	
	/**
	 *  the base context, which is the context of the previous decision cycle
	 */
	private List<IAct> m_baseContext = new ArrayList<IAct>();
	
	/**
	 *  the penultimate context, which is the context of the second previous decision cycle
	 */
	private List<IAct> m_penultimateContext = new ArrayList<IAct>();
	
	/**
	 *  the act that is intended to be enacted
	 */
	private IAct m_intentionAct = null;

	/**
	 *  the act that was actually enacted by the enactAct function
	 *  may be a succeeding lower-level act if the enaction was incorrect 
	 */
	private IAct m_enactedAct = null;

	/**
	 *  the current-level act that was enacted 
	 *  either the succeeding or failing intended act 
	 */
	private IAct m_performedAct = null;

	/**
	 *  the previous performed act 
	 */
	private IAct m_basePerformedAct = null;

	/**
	 *  the act made from the two previously performed acts 
	 */
	private IAct m_streamAct = null;
	private IAct m_streamAct2 = null;

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
		while (true)
		{
			System.out.println("Decision cycle #" + iCycle++);
			
			// print all of the existing schemas..
			System.out.println("Schemas: ");
			for (ISchema s : m_schemas)
				System.out.println(s);
			
			// create a proposition list of possible intention schemas
			m_proposals = propose();
			
			// Determines the next act to enact among the proposition list 
			m_intentionAct = selectAct(m_proposals);
			
			// the previous current context becomes the base context
			swapContext();
			
			// enact the selected act...
			m_enactedAct = enactAct(m_intentionAct);
			System.out.println("Enacted " + m_enactedAct );
			
			// Determine the performed act
			m_performedAct = setPerformedAct(m_intentionAct, m_enactedAct);
			System.out.println("Performed " + m_performedAct );
			
			updateContext();
			
			// learn from the performed act
			m_streamAct = learn(m_baseContext, m_basePerformedAct, m_performedAct);
			System.out.println("Streaming " + m_streamAct );
			
			// learn from the stream act
			learn(m_penultimateContext, null, m_streamAct);

			// learn from the actually enacted act
			if (m_enactedAct != m_performedAct)
			{
				m_streamAct2 = learn(m_baseContext, m_basePerformedAct, m_enactedAct);
				System.out.println("Streaming2 " + m_streamAct2 );
				learn(m_penultimateContext, null, m_streamAct2);
			}			
		}
	}
	
	/**
	 * Generates the list of proposed schemas
	 * Activate the schemas whose context act belongs to the current context
	 * @author ogeorgeon
	 */
	protected List<IProposition> propose()
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
				for (IAct c : m_context)
				{
					if (s.getContextAct().equals(c))
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
					if (s.getIntentionAct().getSchema().getWeight() > Schema.REG_SENS_THRESH)
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
			if (s.getWeight() > Schema.REG_SENS_THRESH)
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
	 * The current base context is passed to the penultimate context 
	 * The current context is passed to the base context
	 * The previous penultimate context is lost
	 * @author ogeorgeon
	 */
	protected void swapContext()
	{
		m_penultimateContext = new ArrayList<IAct>(m_baseContext);
		
		m_baseContext = new ArrayList<IAct>(m_context);
		if (m_baseContext.isEmpty())
			System.out.println("Base context is empty");
		
		m_basePerformedAct = m_performedAct;

		m_context.clear();
	}

	/**
	 * The actually enacted act is added to the context
	 * as well as its possible intention
	 * @author ogeorgeon
	 */
	protected void updateContext()
	{
		// Add the actually enacted act to the context
		m_context.add(m_enactedAct);
		if (m_enactedAct != m_performedAct)
			m_context.add(m_performedAct);
		
		// if the actually enacted act is not primitive, its intention also belongs to the context
		if (!m_enactedAct.getSchema().isPrimitive())
		{
			m_context.add(m_enactedAct.getSchema().getIntentionAct());			
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
	protected IAct setPerformedAct(IAct intention, IAct enacted)
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
	 * @return the stream act based on the basePerformedAct
	 */
	protected IAct learn(List<IAct> contextList, IAct basePerformedAct, IAct intentionAct)
	{
		IAct streamAct = null;
		
		// For each act in the context...
		for (IAct a : contextList)
		{
			// Build a new schema with the context act 
			// and the intention act 
			ISchema newS = Ernest.factory().addSchema(m_schemas, a, intentionAct);
			newS.incWeight();
			System.out.println("Reinfocing schema " + newS);
			
			// returns the act made from the previously performed act
			if (a == basePerformedAct)
				streamAct = newS.getSucceedingAct();
			
			// add the created act to the context
			if (newS.getWeight() > Schema.REG_SENS_THRESH)
			{
				m_context.add(newS.getSucceedingAct());
			}
		}
		return streamAct; 
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
