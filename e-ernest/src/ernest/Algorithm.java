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
			
			// Determine the performed act
			m_performedAct = setPerformedAct(m_intentionAct, m_enactedAct);
			System.out.println("Performed " + m_performedAct );
			
			updateContext();
			
			// learn from the performed act
			m_streamAct = learn(m_baseContext, m_performedAct);
			System.out.println("Streaming " + m_streamAct );
			
			// learn from the stream act
			learn(m_penultimateContext, m_streamAct);
			
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
						System.out.println("Activate " + s);
					}
				}
				
				// Activated schemas propose their intention
				if (s.isActivated())
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

		System.out.println("Propose: ");
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
	 * Learning from incorrect enaction
	 * The intention's failing act is updated and becomes part of the next context
	 * The intention's failing act is added to the context when needed
	 * @author ogeorgeon
	 * @return the current act is the enacted at at the level of the initial intention
	 */
	protected IAct setPerformedAct(IAct intention, IAct enacted)
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
					// Initialize or update the intention's failing act using the actually enacted act's satisfaction
					IAct a = intention.getSchema().initFailingAct(enacted.getSat());
					// returns the filing act
					System.out.println("failing act  " + intention.getSchema().getFailingAct());					
					return a;
				}
				// if intended to fail
				else
				{
					// if failed indeed then returns the intention
					if (enacted.getSchema() != intention.getSchema())
						return intention;
					// if accidentally succeeded then nothing more to do
				}
			}
		}
		// other cases, the current act equals the enacted act 
		return enacted;
	}

	/**
	 * Learn from an enacted act after the base context
	 * TODO learn from failing act
	 * @author mcohen
	 * @author ogeorgeon
	 */
	protected IAct learn(List<IAct> context, IAct enacted)
	{
		IAct r = null;
		
		// For each act of the base context...
		for (IAct a : context)
		{
			// Build a new schema with the base context 
			// and the enacted act to compare to the schema list
			ISchema newS = Ernest.factory().addSchema(m_schemas, a, enacted);
			newS.incWeight();
			System.out.println("Reinfocing schema " + newS);
			
			// returns the act made from the previously performed act
			if (a == m_basePerformedAct)
				r = newS.getSucceedingAct();
			
			// add the created act to the context
			if (newS.getWeight() > Schema.REG_SENS_THRESH)
			{
				context.add(newS.getSucceedingAct());
			}
		}
		return r; 
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
