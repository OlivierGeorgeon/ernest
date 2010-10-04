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
	 * the environment that ernest is operating in...
	 */
	private IEnvironment m_env = Ernest.factory().getEnvironment();
	
	/**
	 *  a list of all of the schemas ever created ...
	 */
	private List<ISchema> m_schemas = new ArrayList<ISchema>();
	
	/**
	 *  a list of the current proposed acts...
	 *  TODO: needs a list of proposed schemas
	 */
	private List<IProposition> m_proposals = new ArrayList<IProposition>();	
	
	/**
	 *  a list of the activated schemas
	 * @author ogeorgeon
	 */
	private List<IActivation> m_activations = new ArrayList<IActivation>();	
	
	/**
	 *  the current context, which is the most recently enacted act...
	 *  TODO: Should be a set of acts
	 */
	private IAct m_context = null;
	
	/**
	 *  the act that was most recently enacted...
	 */
	private IAct m_actualIntention = null;

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
			
			// select the nest schema to enact...
			ISchema s = selectSchema();
			
			// enact the selected act...
			// TODO the selected act should be anticipated
			m_actualIntention = null;
			boolean bSuccess = enactAct(s.getSuccessAct());
			
			// learn from experience...
			learn(bSuccess);
			
			// determine the scope to be considered in the next 
			// cycle...
			assessScope();
		}
	}
	
	/**
	 * This method is called recursively as it performs a depth first search, following
	 * the context branch first and then the intention branch.  As it encounters
	 * primitive schema it enacts them.
	 * @param a the act to be enacted
	 * @return the success status of the enactment
	 */
	protected boolean enactAct(IAct a)
	{
		boolean bRet = true;
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
			if (s.getContextAct() != null);
				bRet = bRet && enactAct(s.getContextAct());
				
			// then search the right branch (intention)...
			if (s.getIntentionAct() != null);
				bRet = bRet && enactAct(s.getIntentionAct());
		}
		else
		{
			// we found a primitve shcema so enact it in the environment
			// note: if a schema ever fails when enacted in the environment, 
			// the return value will cause the recursively loop to halt because
			// all recursive calls are predicated by bRet being equal to true,
			// so a failure here will cause the entire method to unwind without
			// enacting any further schema and then return false...
			bRet = bRet && (a.isSuccess() == m_env.enactSchema(s));
		}

		// TODO: we only do this for primitive acts right now, 
		// this prevents us from leaning higher level schema that consist of 
		// more than one primitive context and one primitive intention
		// this code need to be upgraded to support more complex 
		// schema learning...
		if (a.getSchema().isPrimitive())
		{
			// set actualIntention equal to most recently enacted act
			// if the act failed, we swap from success to failure or 
			// failure to success...
			if (bRet)
			{
				m_actualIntention = a;	
			}
			else
			{
				m_actualIntention = 
					a.isSuccess() ? 
							a.getSchema().getFailureAct() : 
							a.getSchema().getSuccessAct();
			}
		}
		
		return bRet;
	}

	/**
	 * Ernest learns from experience.
	 * @param b a flag specifying if the previous act succeeded or failed.
	 */
	protected void learn(boolean b)
	{
		// reinforce the successfully enacted intention... 
		if (b)
			m_actualIntention.getSchema().incWeight();
		
		// if there is a context, then we need to build a new, higher level
		// schema...
		if (m_context != null)
		{
			// build a new schema with the current context 
			// and the most recently enacted intention...
			ISchema newS = Ernest.factory().createSchema();
			newS.setContextAct(m_context);
			newS.setIntentionAct(m_actualIntention);
			newS.updateSuccessSatisfaction();			
			
			// add the new schema to the list of all schemas,
			// if the schema already exists, it will not be added
			// TODO: the contains method relies on the schema class 
			// to have implemented an accurate valid equals method,
			// the current equals method works for single level
			// schema, but it should be re-evaluated when higher level
			// learning is enabled...
			if (!m_schemas.contains(newS))
			{
				m_schemas.add(newS);
				System.out.println("Adding new schema: " + newS);
			}
		}
		
		// the context for the next decision cycle is set equal to 
		// the act that was recently enacted... 
		m_context = m_actualIntention;
	}

	/**
	 * Selects the intention schema with the highest proposition
	 * @return the next schema that should be enacted
	 * @author ogeorgeon
	 */
//	protected IAct pickBestIntention()
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
		return m_proposals.get(m_rand.nextInt(count)).getSchema();
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
		
		// next, propose all schema that are proposed by the activated schemas
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
	 * Generates the list of activated schemas
	 * Activated schemas are schemas whose context act belongs to the current context
	 * @author ogeorgeon
	 */
	protected void activate()
	{
		// clear the list of activations before we start adding more...
		m_activations.clear();
		
		// Add all schema that match the context and have not yet been 
		// proposed...
		for (ISchema s : m_schemas)
		{
			// TODO: this works only if acts have an accurately defined equals method, 
			// the equals method should be reviewed especially when higher level
			// learning is enabled... 
			if (!s.isPrimitive() && m_context != null && s.getContextAct().equals(m_context))
			{
				IActivation a = Ernest.factory().createActivation(s);
				if (!m_activations.contains(a))
					m_activations.add(a);
			}
		}
		
		System.out.println("Activations:");
		for (IActivation a : m_activations)
			System.out.println(a);
	}

	/**
	 * Determines that schema will be considered in the next decision
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
