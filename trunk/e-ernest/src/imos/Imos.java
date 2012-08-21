package imos;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import spas.IPlace;
import spas.LocalSpaceMemory;
import ernest.Ernest;
import ernest.ISensorymotorSystem;
import ernest.ITracer;

/**
 * The Intrinsically Motivated Schema mechanism.
 * @author ogeorgeon
 */

public class Imos implements IImos 
{	
	/** Default regularity sensibility threshold (The weight threshold for an act to become reliable). */
	public final int REG_SENS_THRESH = 5;

	/** Default maximum length of a schema (For the schema to be chosen as an intention) */
	public final int SCHEMA_MAX_LENGTH = 100;

	/** Default Activation threshold (The weight threshold for higher-level learning with the second learning mechanism). */
	public final int ACTIVATION_THRESH = 1;

	/** Hypothetical act (Cannot be chosen as an intention, cannot support higher-level learning). */
	public static final int HYPOTHETICAL = 1;

	/** Reliable act (Can be chosen as an intention and can support higher-level learning). */
	public static final int RELIABLE = 2;
	
	/**
	 * The episodic memory
	 */
	private EpisodicMemory m_episodicMemory;

	/** The Tracer. */
	private ITracer<Object> m_tracer = null; //new Tracer("trace.txt");

	/** A representation of the internal state for display in the environment. */
	private String m_internalState = "";
	
	/** Random generator used to break a tie when selecting a schema... */
	//private static Random m_rand = new Random(); 
	
	private boolean m_newIntention = true;

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

	/** Counter of cognitive cycles. */
	private int m_imosCycle = 0;
	
	private ISensorymotorSystem m_sensorimotorSystem = null;

	/**
	 * Constructor for the Intrinsic Motivation system.
	 * Use default parameters REG_SENS_THRESH and SCHEMA_MAX_LENGTH.
	 */
	public Imos()
	{
		m_episodicMemory = new EpisodicMemory(REG_SENS_THRESH, SCHEMA_MAX_LENGTH);
	}
	
	/**
	 * Constructor for the Intrinsic Motivation system.
	 * @param regularitySensibilityThreshold  The regularity sensibility threshold.
	 * A lower value favors the faster adoption of possibly less satisfying sequences, 
	 * a higher value favors the slower adoption of possibly more satisfying sequences.
	 * @param maxShemaLength The maximum length of schemas.
	 * A lower value makes learning longer sequences impossible, 
	 * a higher value makes possible the adoption of unnecessarily long sequences.
	 */
	public Imos(int regularitySensibilityThreshold, int maxShemaLength)
	{
		m_episodicMemory = new EpisodicMemory(regularitySensibilityThreshold, maxShemaLength);
	}

	/**
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer<Object> tracer)
	{
		m_tracer = tracer;
		m_episodicMemory.setTracer(tracer);
	}
	
	public void setSensorimotorSystem(ISensorymotorSystem sensorimotorSystem)
	{
		m_sensorimotorSystem = sensorimotorSystem;
		m_episodicMemory.setSensorimotorSystem(sensorimotorSystem);
	}
	
	/**
	 * Get a string description of the imos's internal state for display in the environment.
	 * @return A representation of the imos's internal state
	 */
	public String getInternalState()
	{
		return m_internalState;
	}

	/**
	 * Construct a new interaction or retrieve the interaction if it already exists.
	 * The interaction's action is recorded as a primitive schema.
	 * @param actionLabel The label of the interaction's action in the environment.
	 * @param stimuliLabel The label of the interaction's stimuli generated by the action.
	 * @param satisfaction The interaction's satisfaction (only needed in case this interaction was not 
	 * yet declared in imos).
	 * @return The act that was created or that already existed.
	 */
	public IAct addInteraction(String actionLabel, String stimuliLabel, int satisfaction)
	{
		IAct a = null;
		String actLabel = actionLabel + stimuliLabel;
		ISchema s =  m_episodicMemory.addPrimitiveSchema(actionLabel);
		
		// Primitive satisfactions are multiplied by 10 internally for rounding issues.   
		// (this value does not impact the agent's behavior)
		a = m_episodicMemory.addAct(actLabel, s, true,  satisfaction * 10,  RELIABLE);
		
		System.out.println("Primitive interaction " + a.toString() + " " + a.getSatisfaction());
		return a;
		
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
		
		// Acts that match a phenomenon are added to the context
		//m_episodicMemory.evokeAct(m_activationList, phenomenaList); 
		//m_episodicMemory.evokeAct(m_contextList, phenomenaList); 
		addActivationAct(m_sensorimotorSystem.situationAct());
		
		// add the streamcontext list to the context list
		addContextList(contextList);
	}
	
	/**
	 * The main method of the Intrinsic Motivation System.
	 * Follow-up the sequence at hand, and chooses the next primitive interaction to try to enact. 
	 * @param primitiveEnaction The last actually enacted primitive interaction.
	 * @return The primitive interaction to try to enact next. 
	 */
	public IAct step(IAct primitiveEnaction) 
	{
		m_imosCycle++;		
		m_internalState= "";
		
		//m_episodicMemory.reach(new Point3f(0,1,0), new Vector3f(0,1,0));
		//m_episodicMemory.reach(new Point3f(0,-1,0), new Vector3f(0,-1,0));
		//m_episodicMemory.reach(new Point3f(0,0,0), new Vector3f(-1,0,0));

//		// Trace the new event
//		
//		if (m_tracer != null)
//		{
//            m_tracer.startNewEvent(getCounter());
//			m_tracer.addEventElement("clock", getCounter() + "");
//		}                
//		
		IAct intentionAct = null;
		IAct enactedAct = null;
		
		// We follow up the current enaction (Except on startup when the primitive intention is null).

		if (m_primitiveIntention != null)
		{
			if (m_tracer != null) 
			{
				m_tracer.addEventElement("top_intention", m_intentionAct.getLabel());
				m_tracer.addEventElement("top_level", m_intentionAct.getLength() + "");
				m_tracer.addEventElement("new_intention", m_newIntention ? "true" : "false");
				m_tracer.addEventElement("primitive_intended_act", m_primitiveIntention.getLabel());
				m_tracer.addEventElement("primitive_enacted_act", primitiveEnaction.getLabel());
				m_tracer.addEventElement("primitive_enacted_schema", primitiveEnaction.getSchema().getLabel());
				m_tracer.addEventElement("satisfaction", primitiveEnaction.getSatisfaction()/10 + "");
			}

			// Compute the actually enacted act
			
			enactedAct = enactedAct(m_primitiveIntention.getSchema(), primitiveEnaction);
			System.out.println("Enacted " + enactedAct );
			
			// Compute the next sub-intention, null if we have reached the end of the intended act.
			
			intentionAct = nextAct(m_primitiveIntention, primitiveEnaction);
			
			// Check the consistency with Spas
//			if (intentionAct != null && !m_sensorimotorSystem.checkConsistency(intentionAct))
//			{
//				if (m_tracer != null)
//					m_tracer.addEventElement("interrupted", intentionAct.getLabel());
//				intentionAct = null;
//			}
		}	
		
		// Update spatial memory
		
		//if (enactedAct != null)
		//	m_sensorimotorSystem.stepSpas(enactedAct);

		//if (m_primitiveIntention != null)
			m_sensorimotorSystem.updateSpas(primitiveEnaction, enactedAct);

		//if (enactedAct != null)
		//	m_sensorimotorSystem.stepSpas(enactedAct);

		// If we have a context and the current enaction is over then we record and we shift the context. ========

		if (intentionAct == null && enactedAct != null)
		{
			//m_sensorimotorSystem.stepSpas(enactedAct);
			
			// log the previous decision cycle

			// TODO also compute surprise in the case of primitive intention acts.  
			if (m_intentionAct != enactedAct)// && !m_intentionAct.getStatus()) 
				m_internalState= "!";
			if (m_intentionAct.getSchema().isPrimitive() && m_intentionAct == enactedAct)
			{
				if (m_tracer != null) 
					m_tracer.addEventElement("intention_correct", m_intentionAct.getLabel());
			}
			if (m_intentionAct.getSchema().isPrimitive() && m_intentionAct != enactedAct)
			{
				if (m_tracer != null) 
					m_tracer.addEventElement("intention_incorrect", m_intentionAct.getLabel());
			}
			//m_tracer.addEventElement("top_enacted_act", enactedAct.getLabel());
			//m_tracer.addEventElement("interrupted", m_internalState);
			//m_tracer.addEventElement("new_intention", "true");

			System.out.println("New decision ================ ");
			
			// Process the performed act
			
			IAct performedAct = null;

			ISchema intendedSchema = m_intentionAct.getSchema();
			
			if (intendedSchema == enactedAct.getSchema()) performedAct = enactedAct;
			else	performedAct = m_episodicMemory.addFailingInteraction(intendedSchema,enactedAct.getSatisfaction());
			
			//m_tracer.addEventElement("top_performed", performedAct.getLabel() );
			System.out.println("Performed " + performedAct );
			
			// learn from the  context and the performed act
			m_episodicMemory.resetLearnCount();
			List<IAct> streamContextList = m_episodicMemory.record(m_contextList, performedAct);
						
			// learn from the base context and the stream act			
			 if (streamContextList.size() > 0) // TODO find a better way than relying on the enacted act being on the top of hte list
			 {
				 IAct streamAct = streamContextList.get(0); // The stream act is the first learned 
				 System.out.println("Streaming " + streamAct);
				 if (streamAct.getSchema().getWeight() > ACTIVATION_THRESH)
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
					if (streamAct2.getSchema().getWeight() > ACTIVATION_THRESH)
						m_episodicMemory.record(m_baseContextList, streamAct2);
				}
			}	
			
			// Learn from the phenomena
			
			m_episodicMemory.record(m_contextList,m_sensorimotorSystem.situationAct());

			// Update the context. =========
			
			// Acts that match a phenomenon are added to the activation list			
			//m_episodicMemory.evokeAct(m_activationList, m_phenomenaList);

			//ArrayList<IPlace> phenomenaList = m_sensorimotorSystem.getPhenomena();
			shiftDecisionCycle(enactedAct, performedAct, streamContextList);
			
		}
		
		// Log the activation list and the learned count for debug
		System.out.println("Activation context list: ");
		Object activation = null;
		if (m_tracer != null)
			activation = m_tracer.addEventElement("activation_context_acts");
		for (IAct a : m_activationList)	
		{	
			if (m_tracer != null)
				m_tracer.addSubelement(activation, "act", a.getLabel());
			System.out.println(a);
		}
		if (m_tracer != null)
			m_tracer.addEventElement("learn_count", m_episodicMemory.getLearnCount() + "");
		System.out.println("Learned : " + m_episodicMemory.getLearnCount() + " schemas.");
		
		
		// If we don't have an ongoing intention then we choose a new intention. ======
		
		if (intentionAct == null)
		{
			//ArrayList<IProposition> propositionList = m_sensorimotorSystem.getPropositionList();
			ArrayList<IProposition> propositionList = m_sensorimotorSystem.getPropositionList(m_episodicMemory.getActs());
			intentionAct = m_episodicMemory.selectAct(m_activationList, propositionList);
			m_intentionAct = intentionAct;
			m_newIntention = true;
		}
		else 
			m_newIntention = false;
		
//		if (m_tracer != null) {
//			m_tracer.addEventElement("top_intention", m_intentionAct.getLabel());
//			m_tracer.addEventElement("top_level", m_intentionAct.getLength() + "");
//			m_tracer.addEventElement("new_intention", m_newIntention ? "true" : "false");
//		}
		
		// Spread the selected intention's activation to primitive acts.
		// (so far, only selected intentions activate primitive acts, but one day there could be an additional bottom-up activation mechanism)		
		//IAct activePrimitiveAct = spreadActivation(intentionAct);
		//List<IAct> activePrimitiveActs = new ArrayList<IAct>(10);
		//activePrimitiveActs.add(activePrimitiveAct);
		// Sensorymotor acts compete and Ernest selects that with the highest activation
		//IAct nextPrimitiveAct = selectAct(activePrimitiveActs);		
		//m_primitiveIntention = nextPrimitiveAct;
		
		//m_primitiveIntention = selectAct(activePrimitiveActs);		

		m_primitiveIntention = spreadActivation(intentionAct);
		
		if (m_tracer != null)
			m_tracer.addEventElement("next_primitive_intention", m_primitiveIntention.getLabel());
		
		//return nextPrimitiveAct;
		return m_primitiveIntention;
				
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
	 * @param enactedAct The enacted act.
	 * @return the next intention act to enact or null if failed or completed
	 */
	private IAct nextAct(IAct prescribedAct, IAct enactedAct)
	{
		IAct nextAct = null;
		ISchema prescriberSchema = prescribedAct.getPrescriberSchema();
		//int activation = prescribedAct.getActivation();
		prescribedAct.setPrescriberSchema(null); 
		//prescribedAct.setActivation(0); // (It might be the case that the same act will be prescribed again)
		
		if (prescriberSchema != null)
		{
			if (prescribedAct == enactedAct)
			{
				if (m_tracer != null)
					m_tracer.addEventElement("intention_correct", prescribedAct.getLabel());
				// Correctly enacted
				if (prescriberSchema.getPointer() == 0)
				{
					// context act correctly enacted, move to intention act
					prescriberSchema.setPointer(1);
					nextAct = prescriberSchema.getIntentionAct();
					nextAct.setPrescriberSchema(prescriberSchema);
					//nextAct.setActivation(activation);
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
				if (m_tracer != null)
					m_tracer.addEventElement("intention_incorrect", prescribedAct.getLabel());
				// move to prescriber act with a failure status
				IAct prescriberAct = prescriberSchema.getPrescriberAct();
				nextAct = nextAct(prescriberAct, prescriberSchema.getFailingAct());				
			}
		}
		
		if (nextAct !=null && m_tracer != null)
			m_tracer.addEventElement("composite_intention", nextAct.getLabel());

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
	
		//IAct a = acts.get(m_rand.nextInt(count));
		IAct a = acts.get(0); // always select the first
		
		return a ;
	}
	
	/**
	 * Recursively prescribe an act's subacts and subschemas.
	 * (Set the subacts' activation equal to the prescribing act's activation, not used)
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
			//subact.setActivation(a.getActivation());
			primitiveAct = spreadActivation(subact);
		}
		
		if (m_tracer != null)
			m_tracer.addEventElement("prescribed_intention", primitiveAct.getLabel());
		
		return primitiveAct;
	}

	public int getCounter() 
	{
		return m_imosCycle;
	}

	public boolean newIntention() 
	{
		return m_newIntention;
	}

	public boolean compositeIntention() 
	{
		return (m_intentionAct.getLength() > 1);
	}
	
    public IAct addCompositeInteraction(IAct contextAct, IAct intentionAct)
    {
    	
    	IAct act =  m_episodicMemory.addCompositeInteraction(contextAct, intentionAct).getSucceedingAct();
    	//act.setConfidence(Imos.RELIABLE);
    	act.setConfidence(Imos.HYPOTHETICAL);
    	return act;
    }
}
