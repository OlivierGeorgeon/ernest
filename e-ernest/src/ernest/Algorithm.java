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
	 *  a list of the activated schemas
	 * @author ogeorgeon
	 */
	private List<IActivation> m_activations = new ArrayList<IActivation>();	
	
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
			
			// activate the schemas that match the current context
			activate();			
			// create a proposition list of possible intention schemas
			propose();
			
			// select the next schema to enact...
			ISchema s = selectSchema();
			
			// predict the intention act
			// TODO the selected act should be anticipated
			m_intentionAct = s.getSucceedingAct();
			
			// enact the selected act...
			m_enactedAct = enactAct(m_intentionAct);
			
			// The previous current context becomes the base context
			swapContext();
			
			// Learning mechanism...
			learn1();
			learnFromIncorrectEnaction();
			
			// TODO learn from an incorrectly enacted secondary schema

			
			// determine the scope to be considered in the next 
			// cycle...
			assessScope();
		}
	}
	
	/**
	 * Generates the list of activated schemas
	 * Activated schemas are schemas whose context act belongs to the current context
	 * @author ogeorgeon
	 */
	protected void activate()
	{

		System.out.println("Activations:");

		// clear the list of activations before we start adding more...
		m_activations.clear();
		
		// Add all the schemas that match the context 
		for (ISchema s : m_schemas)
		{
			if (!s.isPrimitive())
			{
				for (IAct c : m_context)
				{
					if (s.getContextAct().equals(c))
					{
						IActivation a = Ernest.factory().createActivation(s);
						// if not already in the list then add it (this verification is superfluous)
						if (!m_activations.contains(a))
							m_activations.add(a);
						System.out.println(a);
					}
				}
			}
		}
	}

	/**
	 * Determines what acts should be proposed and the weight of these
	 * proposals.  
	 * @author ogeorgeon
	 * @author mcohen
	 */
	protected void propose()
	{
		// clear the list of proposals before we start adding more...
		m_proposals.clear();
		
		// first, propose all schema that meet the threshold...
		for (ISchema s : m_schemas)
		{
			if (s.getWeight() > Schema.REG_SENS_THRESH)
			{
				m_proposals.add(Ernest.factory().createProposition(s, 1));
			}
		}
		
		// next, propose all the schemas that are proposed by the activated schemas
		for (IActivation a : m_activations)
		{
			ISchema s = a.getIntention().getSchema();
			int w = a.getWeight();
			IProposition p = Ernest.factory().createProposition(s, w);
			int i = m_proposals.indexOf(p);
			if (i == -1)
			{
				m_proposals.add(p);
			}
			else
			{
				m_proposals.get(i).addWeight(w);
			}
		}
		
		System.out.println("Proposals:");
		for (IProposition p : m_proposals)
			System.out.println(p);
	}

	/**
	 * Selects the intention schema with the highest proposition
	 * @return the next schema that should be enacted
	 * @author ogeorgeon
	 */
	protected ISchema selectSchema()
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

		ISchema s = m_proposals.get(m_rand.nextInt(count)).getSchema();
		
		System.out.println("Select:");
		System.out.println(s);

		return s ;
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
		System.out.println("Enacting " + a);

		// get the schema associated with the act that we need
		// to enact...
		ISchema s = a.getSchema();
		
		// if the schema is not primitive, then we need to search for
		// a primitive schema to enact, so we search the context branch
		// first, then the intention branch... 
		if (!s.isPrimitive())
		{
			// first search the left branch (context)...
			// TODO debug the construction of the actually enacted act
			IAct c = enactAct(s.getContextAct()) ;
			if (c == s.getContextAct())
			{
				// then the right branch (intention)...
				IAct i = enactAct(s.getIntentionAct()); 
				if ( i == s.getIntentionAct())
				{
					// the enacted act is the intended act
					return a;
				}
				else
				{
					// the enacted schema is the previously enacted context with the actually enacted intention
					ISchema newS = Ernest.factory().addSchema(m_schemas, c, i);
					return 	newS.getSucceedingAct(); 					
				}
			}
			else
			{
				// The enacted act is the actually enacted context
				return 	c; 
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
	 * The current context is replaced by the actually enacted act
	 * @author ogeorgeon
	 */
	protected void swapContext()
	{
		m_penultimateContext = new ArrayList<IAct>(m_baseContext);
		
		m_baseContext = new ArrayList<IAct>(m_context);
		if (m_baseContext.isEmpty())
			System.out.println("Base context is empty");

		m_context.clear();

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
	protected void learnFromIncorrectEnaction()
	{
		// for non primitive intentions
		if (!m_intentionAct.getSchema().isPrimitive())
		{
			// if the enacted act is not that intended
			if (m_enactedAct != m_intentionAct)
			{
				// if intended to succeed
				if (m_intentionAct.isSuccess())
				{
					// Initialize or update the intention's failing act from the actually enacted act's satisfaction
					IAct a = m_intentionAct.getSchema().initFailingAct(m_enactedAct.getSat());
					// add the filing act to the context
					m_context.add(a);
					System.out.println("failing act  " + m_intentionAct.getSchema().getFailingAct());					
				}
				// if intended to fail
				else
				{
					// if failed indeed then add the intention to the context
					if (m_enactedAct.getSchema() != m_intentionAct.getSchema())
						m_context.add(m_intentionAct);
					// if accidentally succeeded then nothing more to do
				}
			}
		}
		// nothing to do for primitive intentions
	}

	/**
	 * First learning mechanism:
	 * Aggregate the base context with the enacted act
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
