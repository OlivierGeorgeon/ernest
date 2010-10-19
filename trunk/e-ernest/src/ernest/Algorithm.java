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
	 */
	private IAct m_enactedAct = null;

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
			propose();
			
			// Determines the next act to enact...
			m_intentionAct = selectAct();
			
			// the previous current context becomes the base context
			swapContext();
			
			// enact the selected act...
			m_enactedAct = enactAct(m_intentionAct);
			System.out.println("Enacted " + m_enactedAct );

			
			setEnactedContext();
			
			// learning mechanism...
			learnFromIncorrectEnaction(m_intentionAct, m_enactedAct);
			learn1();
			
			// TODO learn from an incorrectly enacted secondary schema

			
			// determine the scope to be considered in the next 
			// cycle...
			assessScope();
		}
	}
	
	/**
	 * Generates the list of proposed schemas
	 * Activate the schemas whose context act belongs to the current context
	 * @author ogeorgeon
	 */
	protected void propose()
	{

		// Clear the previous proposal list
		m_proposals.clear();
		
		// Browse all the schemas 
		for (ISchema s : m_schemas)
		{
			if (!s.isPrimitive())
			{
				// Activate the schemas that match the context 
				s.setActivated(false);
				for (IAct c : m_context)
				{
					if (s.getContextAct().equals(c))
					{
						s.setActivated(true);
						System.out.println("Activating " + s);
					}
				}
				
				// Activated schemas propose their intention
				if (s.isActivated());
				{
					// The weight is the proposing schema's weight multiplied by the proposed act's satisfaction
					int w = s.getWeight() * s.getIntentionAct().getSat();
					// The expectation is the proposing schema's weight signed with the proposed act's status  
					int e = s.getWeight() * (s.getIntentionAct().isSuccess() ? 1 : -1);
					
					IProposition p = Ernest.factory().createProposition(s.getIntentionAct().getSchema(), w, e);
					int i = m_proposals.indexOf(p);
					if (i == -1)
						m_proposals.add(p);
					else
					{
						m_proposals.get(i).update(w, e);
					}
				}
			}

			// Schemas that pass the threshold also receive a default proposition for themselves
			if (s.getWeight() > Schema.REG_SENS_THRESH)
			{
				IProposition p = Ernest.factory().createProposition(s, 0, 0);
				if (!m_proposals.contains(p))
					m_proposals.add(p);
			}
		}

		System.out.println("Proposals:");
		for (IProposition p : m_proposals)
			System.out.println(p);
	}

	/**
	 * Select the intention schema with the highest proposition
	 * @return the next act that should be enacted
	 * @author ogeorgeon
	 */
	protected IAct selectAct()
	{
		// sort by weighted proposition...
		Collections.sort(m_proposals);
		
		// count how many are tied with the  highest weighted proposition
		int count = 0;
		int wp = m_proposals.get(0).getWeight();
		for (IProposition p : m_proposals)
		{
			if (p.getWeight() != wp)
				break;
			count++;
		}

		// pick one at random from the top the proposal list
		// count is equal to the number of proposals that are tied...

		IProposition p = m_proposals.get(m_rand.nextInt(count));
		
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
			IAct c = enactAct(s.getContextAct()) ;
			if (c == s.getContextAct())
			{
				// then the right branch (intention)...
				IAct i = enactAct(s.getIntentionAct()); 
				if ( i == s.getIntentionAct())
				{
					// If intended to succeed then the enaction is correct
					if (a.isSuccess())
						// returns the intended act
						return a;
					// If intended to fail then the enaction is incorrect					
					else
						// the enacted schema is the previously enacted context with the actually enacted intention
						{
							ISchema newS = Ernest.factory().addSchema(m_schemas, c, i);
							// m_context.add(newS.getSucceedingAct());
							System.out.println("incorrectly enacted act  " + a);					
							System.out.println("enacted context " + newS.getSucceedingAct());					
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
						ISchema newS = Ernest.factory().addSchema(m_schemas, c, i);
						// m_context.add(newS.getSucceedingAct());
						System.out.println("incorrectly enacted act  " + a);					
						System.out.println("enacted context " + newS.getSucceedingAct());					
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
				// If intended to succeed then the enaction is incorrect
				if (a.isSuccess())
				{
					// returns the enacted context 
					// m_context.add(c);
					System.out.println("incorrectly enacted act  " + a);					
					System.out.println("enacted context " + c);					
					return 	c;
				}
				// If intended to fail then the enaction is correct
				else
					// returns the intended failing act
					return a;
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

		m_context.clear();
	}

	/**
	 * The actually enacted act is added to the context
	 * as well as its possible intention
	 * @author ogeorgeon
	 */
	protected void setEnactedContext()
	{
		// Add the actually enacted act to the context
		m_context.add(m_enactedAct);
		
		// if the actually enacted act is not primitive, its intention also belongs to the context
		if (!m_enactedAct.getSchema().isPrimitive())
		{
			m_context.add(m_enactedAct.getSchema().getIntentionAct());			
		}		
	}
	
	/**
	 * Learning from incorrect enaction
	 * The intention's failing act is updated and becomes part of the next context
	 * The intention's failing act is added to the context when needed
	 * @author ogeorgeon
	 */
	protected void learnFromIncorrectEnaction(IAct intention, IAct enacted)
	{
		// for non primitive intentions
		if (!intention.getSchema().isPrimitive())
		{
			// if the enacted act is not that intended
			if (enacted != intention)
			{
				// if intended to succeed
				if (intention.isSuccess())
				{
					// Initialize or update the intention's failing act from the actually enacted act's satisfaction
					IAct a = intention.getSchema().initFailingAct(enacted.getSat());
					// add the filing act to the context
					m_context.add(a);
					System.out.println("failing act  " + intention.getSchema().getFailingAct());					
				}
				// if intended to fail
				else
				{
					// if failed indeed then add the intention to the context
					if (enacted.getSchema() != intention.getSchema())
						m_context.add(intention);
					// if accidentally succeeded then nothing more to do
				}
			}
		}
		// nothing to do for primitive intentions
	}

	/**
	 * First learning mechanism:
	 * Aggregate the base context with the enacted act
	 * TODO learn from failing act
	 * @author mcohen
	 * @author ogeorgeon
	 */
	protected void learn1()
	{
		// For each act of the base context...
		for (IAct a : m_baseContext)
		{
			// Build a new schema with the base context 
			// and the enacted act to compare to the schema list
			ISchema newS = Ernest.factory().addSchema(m_schemas, a, m_enactedAct);
			newS.incWeight();
			System.out.println("Reinfocing schema " + newS);
			
			// add the created act to the context
			if (newS.getWeight() > Schema.REG_SENS_THRESH)
			{
				m_context.add(newS.getSucceedingAct());
			}
		}
	}

	/**
	 * Second learning mechanism:
	 * Aggregate the penultimate context with the activated schemas that are successfully enacted
	 * @author ogeorgeon
	 */
	protected void learn2()
	{
		// For each act of the penultimate context...
		for (IAct a : m_penultimateContext)
		{
			// Build a new schema with the penultimate context 
			// and the succeeding activated schemas
			ISchema newS = Ernest.factory().addSchema(m_schemas, a, m_enactedAct);
			newS.incWeight();
			System.out.println("Reinfocing schema " + newS);
			
			// add the created act to the context
			if (newS.getWeight() > Schema.REG_SENS_THRESH)
			{
				m_context.add(newS.getSucceedingAct());
			}
		}
	}

	/**
	 * Determines what schema will be considered in the next decision
	 * cycle.
	 */
	protected void assessScope()
	{
		// for now, assume infinite scope
		
		// TODO: eventually we want to create a bounded scope, however,
		// until higher-level schema learning is implemented, there
		// number of schema that can be learned is bounded by the number
		// of primitive schema, so this won't get out of hand until
		// higher-level learning is implemented...
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
