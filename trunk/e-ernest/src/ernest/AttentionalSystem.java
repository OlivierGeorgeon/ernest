package ernest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Ernest's attentional system.
 * Maintain lists of acts that represent Ernest's current situation
 * Control the current enaction.
 * @author ogeorgeon
 */

public class AttentionalSystem implements IAttentionalSystem {
	
	/**
	 * Pointer to Ernest's episodic memory
	 */
	private EpisodicMemory m_episodicMemory;

	/** The Tracer. */
	private ITracer m_tracer = null; //new Tracer("trace.txt", true);

	/** A representation of Ernest's internal state. */
	private String m_internalState = "";
	
	/** Random generator used to break a tie when selecting a schema... */
	private static Random m_rand = new Random(); 

	/**
	 * The context to learn new schemas with the first learning mechanism.
	 */
	private List<IAct> m_contextList = new ArrayList<IAct>();
	
	/**
	 * The context to learn new schemas with the second learning mechanism.
	 */
	private List<IAct> m_baseContextList = new ArrayList<IAct>();
	
	/**
	 * The list of acts that can activate a new intention. 
	 */
	private List<IAct> m_activationList = new ArrayList<IAct>();

	/** 
	 * The decided intention
	 */
	private IAct m_intentionAct = null;

	/**
	 * The primitive intended act in the current automatic loop.
	 */
	private IAct m_primitiveIntention = null;

	/**
	 * Constructor for the attentional system.
	 * Initialize the pointer to episodic memory.
	 */
	protected AttentionalSystem(EpisodicMemory episodicMemory)
	{
		m_episodicMemory = episodicMemory;
	}

	public void setTracer(ITracer tracer)
	{
		m_tracer = tracer;
	}
	
	/**
	 * Get a string description of Ernest's internal state for display in the environment.
	 * @return A representation of Ernest's internal state
	 */
	public String getInternalState()
	{
		return m_internalState;
	}

	/**
	 * Add a list of acts to the context list (scope). 
	 * This list is used to learn new schemas in the next decision cycle.
	 * @param actList The list of acts to append in the context list.
	 */
	private void addContextList(List<IAct> actList) 
	{
		for (IAct act : actList)
		{
			if (!m_contextList.contains(act))
				m_contextList.add(act);
		}
	}

	/**
	 * Add an act to the list of acts in the context and in the focus list. 
	 * The focus list is used for learning new schemas in the next decision cycle.
	 * @param act The act that will be added to the context list and to the focus list.
	 */
	private void addActivationAct(IAct act) 
	{
		if (act != null)
		{
			if (!m_contextList.contains(act))
				m_contextList.add(act);
			if (!m_activationList.contains(act))
				m_activationList.add(act);
		}
	}

	/**
	 * @return The primitive intention act in the current automatic loop.
	 */
	public IAct getPrimitiveIntention() 
	{
		return m_primitiveIntention;
	}

	/**
	 * Shift the context when a decision cycle terminates and the next begins.
	 * The context list is passed to the base context list.
	 * The activation list is reinitialized from the enacted act and the performed act.
	 * The context list is reinitialized from the activation list an the additional list provided as a parameter. 
	 * @param enactedAct The act that was actually enacted during the terminating decision cycle.
	 * @param performedAct The act that was performed during the terminating decision cycle.
	 * @param contextList The additional acts to add to the new context list
	 */
	private void shiftDecisionCycle(IAct enactedAct, IAct performedAct, List<IAct> contextList)
	{
		// The current context list becomes the base context list
		m_baseContextList = new ArrayList<IAct>(m_contextList);
		
		m_contextList.clear();
		m_activationList.clear();
		
		// The enacted act is added first to the activation list
		addActivationAct(enactedAct); 

		// Add the performed act if different
		if (enactedAct != performedAct)
			addActivationAct(performedAct);

		// if the actually enacted act is not primitive, its intention also belongs to the context
		if (!enactedAct.getSchema().isPrimitive())
			addActivationAct(enactedAct.getSchema().getIntentionAct());	
		
		// add the streamcontext list to the context list
		addContextList(contextList);
	}
	
	/**
	 * Ernest's main process.
	 */
	public ISchema step(IAct primitiveEnaction) 
	{
		m_internalState= "";

		IAct intentionAct = null;
		IAct enactedAct = null;
		
		// Review the enaction if any.

		if (m_primitiveIntention != null)
		{
			// Compute the actually enacted act
			
			enactedAct = enactedAct(m_primitiveIntention.getSchema(), primitiveEnaction);
			
			System.out.println("Enacted " + enactedAct );
			
			// The selected intention is it over?
			
			intentionAct = nextAct(m_primitiveIntention, primitiveEnaction);
		}	

		// Shift decision cycle if no ongoing enaction

		if (intentionAct == null && enactedAct != null)
		{
			System.out.println("Schift ================ ");
			// No ongoing schema to enact. The decision cycle is over.  
			// Shift to the next decision cycle
			//shiftDecisionCycle(enactedAct);
			
			IAct performedAct = null;

			// Log the previous decision cycle's trace

			// TODO also compute surprise in the case of primitive intention acts.  
			if (m_intentionAct != enactedAct && !m_intentionAct.getStatus())
				 m_internalState= "!";
			m_tracer.writeLine(enactedAct.getLabel() + m_internalState);

			// Determine the performed act
			
			ISchema intendedSchema = m_intentionAct.getSchema();
			
			if (intendedSchema == enactedAct.getSchema())
				performedAct = enactedAct;
			else
				performedAct = m_episodicMemory.addFailingInteraction(intendedSchema,enactedAct.getSatisfaction());
			
			System.out.println("Performed " + performedAct );
			
			// learn from the  context and the performed act
			
			m_episodicMemory.resetLearnCount();
			
			List<IAct> streamContextList = m_episodicMemory.record(m_contextList, performedAct);
			
			
			// learn from the base context and the stream act
			
			 if (streamContextList.size() > 0) // TODO find a better way than relying on the enacted act being on the top of hte list
			 {
				 IAct streamAct = streamContextList.get(0); // The stream act is the first learned 
				 System.out.println("Streaming " + streamAct);
				 if (streamAct.getSchema().getWeight() > Ernest.ACTIVATION_THRESH)
					 m_episodicMemory.record(m_baseContextList, streamAct);
			 }

			// learn from the current context and the actually enacted act
			
			if (enactedAct != performedAct)
			{
				System.out.println("Learn from enacted");
				List<IAct> streamContextList2 = m_episodicMemory.record(m_contextList, enactedAct);
				// learn from the base context and the streamAct2
				if (streamContextList2.size() > 0)
				{
					IAct streamAct2 = streamContextList2.get(0);
					System.out.println("Streaming2 " + streamAct2 );
					if (streamAct2.getSchema().getWeight() > Ernest.ACTIVATION_THRESH)
						m_episodicMemory.record(m_baseContextList, streamAct2);
				}
			}			

			// Assess the new context ====
			
			// Shift the context and renitialize it with the new enactedAct 
			// The enacted act needs to be the first of the context list to construct the stream act
			shiftDecisionCycle(enactedAct, performedAct, streamContextList);
			
		}
		
		// print the new current context
		System.out.println("Activation context list: ");
		for (IAct a : m_activationList)
			{	System.out.println(a);}
		System.out.println("Learned : " + m_episodicMemory.getLearnCount() + " schemas.");
			
		// Select a new intention if the decision cycle has shifted
		
		if (intentionAct == null)
		{
			intentionAct = m_episodicMemory.selectAct(m_activationList);
			m_intentionAct = intentionAct;
		}
		
		// Spread the selected intention's activation to primitive acts.
		// (so far, only selected intentions activate primitive acts)
		
		IAct activeSensorymotorNoeme = spreadActivation(intentionAct);
		List<IAct> activeSensorymotorNoemes = new ArrayList<IAct>(10);
		activeSensorymotorNoemes.add(activeSensorymotorNoeme);
		
		// Sensorymotor no�mes compete and Ernest selects that with the highest activation
		IAct nextPrimitiveAct = selectAct(activeSensorymotorNoemes);		
		m_primitiveIntention = nextPrimitiveAct;
		
		return nextPrimitiveAct.getSchema();
				
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
				ISchema enactedSchema = m_episodicMemory.addCompositeInteraction(prescriberSchema.getContextAct(), a);
				enactedAct = enactedAct(prescriberSchema, enactedSchema.getSucceedingAct());
			}
		}
			
		return enactedAct;
	}
	
	/**
	 * Recursively finds the next act to enact in the hierarchy of prescribers.
	 * @param prescribedAct The prescribed act.
	 * @param prescribedAct The enacted act.
	 * @return the next intention act to enact or null if failed or completed
	 */
	private IAct nextAct(IAct prescribedAct, IAct enactedAct)
	{
		IAct nextAct = null;
		ISchema prescriberSchema = prescribedAct.getPrescriberSchema();
		int activation = prescribedAct.getActivation();
		prescribedAct.setPrescriberSchema(null); 
		prescribedAct.setActivation(0); // (It might be the case that the same act will be prescribed again)
		
		if (prescriberSchema != null)
		{
			if (prescribedAct == enactedAct)
			{
				// Correctly enacted
				if (prescriberSchema.getPointer() == 0)
				{
					// context act correctly enacted, move to intention act
					prescriberSchema.setPointer(1);
					nextAct = prescriberSchema.getIntentionAct();
					nextAct.setPrescriberSchema(prescriberSchema);
					nextAct.setActivation(activation);
				}
				else
				{
					// intention act correctly enacted, move to prescriber act with a success status
					IAct prescriberAct = prescriberSchema.getPrescriberAct();
					nextAct = nextAct(prescriberAct, prescriberSchema.getSucceedingAct());
				}
			}
			else
			{
				// move to prescriber act with a failure status
				IAct prescriberAct = prescriberSchema.getPrescriberAct();
				nextAct = nextAct(prescriberAct, prescriberSchema.getFailingAct());				
			}
		}

		return nextAct;
	}
	
	/**
	 * Select the act that has the highest activation in a list.
	 * If several are tied pick one at random.
	 * @param acts the list of acts to search in.
	 * @return The selected act.
	 */
	private IAct selectAct(List<IAct> acts)
	{
		// sort by weighted proposition...
		Collections.sort(acts);
		
		// count how many are tied with the  highest weighted proposition
		int count = 0;
		int wp = acts.get(0).getActivation();
		for (IAct a : acts)
		{
			if (a.getActivation() != wp)
				break;
			count++;
		}
	
		// pick one at random from the top of the proposal list
		// count is equal to the number of proposals that are tied...
	
		IAct a = acts.get(m_rand.nextInt(count));
		
		return a ;
	}
	
	/**
	 * Recursively prescribe an act's subacts and subschemas.
	 * Set the subacts' activation equal to the prescribing act's activation.
	 * @param The prescriber act.
	 * @return The prescribed act.
	 */
	private IAct spreadActivation(IAct a)
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
			subact.setActivation(a.getActivation());
			primitiveAct = spreadActivation(subact);
		}
		
		return primitiveAct;
	}

}