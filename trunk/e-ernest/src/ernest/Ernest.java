package ernest;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

/**
 * The main Ernest class used to create an Ernest agent in the environment.
 * @author ogeorgeon
 */
public class Ernest implements IErnest 
{
	/** A big value that can represent infinite for diverse purpose. */
	public static final int INFINITE = 1000;

	/** Hypothetical act (Cannot be chosen as an intention. Cannot support higher-level learning). */
	public static final int HYPOTHETICAL = 1;

	/** Reliable act (Can be chosen as an intention and can support higher-level learning). */
	public static final int RELIABLE = 2;

	/** Regularity sensibility threshold (The weight threshold for an act to become reliable). */
	public static int REG_SENS_THRESH = 5;

	/** Activation threshold (The weight threshold for higher-level learning with the second learning mechanism). */
	public static int ACTIVATION_THRESH = 1;

	/** Maximum length of a schema (For the schema to be chosen as an intention)*/
	public static int SCHEMA_MAX_LENGTH = INFINITE;
	
	/** Random generator used to break a tie when selecting a schema... */
	private static Random m_rand = new Random(); 

	/** Indication of Ernest's boredom. */
	private boolean m_bored = false;

	/** A representation of Ernest's internal state. */
	private String m_internalState = "";
	
	/** The Tracer. */
	private ITracer m_tracer = null; //new Tracer("trace.txt", true);

	/** Ernest's episodic memory. */
	private EpisodicMemory m_episodicMemory = new EpisodicMemory();

	/** Ernest's working memory. */
	private IContext m_context = new Context();
	
	/** Ernest's sensorymotor system. */
	private ISensorymotorSystem m_sensorymotorSystem;

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
	 * Let the environment set the sensorymotor system.
	 * @param sensor The sensorymotor system.
	 */
	public void setSensorymotorSystem(ISensorymotorSystem sensor) 
	{
		m_sensorymotorSystem = sensor;
		m_sensorymotorSystem.setEpisodicMemory(m_episodicMemory);
	};
	
	/**
	 * Let the environment set the tracer.
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer tracer) 
	{ 
		m_tracer = tracer; 
	}

	/**
	 * Provide access to Ernest's episodic memory
	 * (The environment can populate episodic memory with inborn composite schemas) 
	 * @return Ernest's episodic memory. 
	 */
    public EpisodicMemory getEpisodicMemory()
    {
    	return m_episodicMemory;
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
	 * Ernest's central process.
	 * @param status The status received as a feedback from the previous primitive enaction.
	 * @return The next primitive schema to enact.
	 */
	public String step(boolean status) 
	{
		// Retrieve the enacted primitive act
		
		IAct enactedPrimitiveAct = null;
		IAct intendedPrimitiveAct = m_context.getPrimitiveIntention();
		
		if (intendedPrimitiveAct != null)
			enactedPrimitiveAct = m_sensorymotorSystem.enactedAct(intendedPrimitiveAct.getSchema(), status);
		
		m_context.setPrimitiveEnaction(enactedPrimitiveAct);

		// Run Ernest one step
		
		m_context = stepCentral(m_context);
		
		// Return the schemas to enact in the environment
		
		String enact = m_context.getPrimitiveIntention().getSchema().getLabel();
		
		return enact;
	}
		
	/**
	 * Ernest's central process.
	 * @param context The context that determines Ernest's next action.
	 * @return The next context.
	 */
	public IContext stepCentral(IContext context) 
	{
		m_internalState= "";

		IAct intendedPrimitiveAct = context.getPrimitiveIntention();
		IAct enactedPrimitiveAct = context.getPrimitiveEnaction();
		IAct intentionAct = null;
		IAct enactedAct = null;
		
		// Review the enaction if any.

		if (intendedPrimitiveAct != null)
		{
			// Compute the actually enacted act
			
			enactedAct = enactedAct(intendedPrimitiveAct.getSchema(), enactedPrimitiveAct);
			
			System.out.println("Enacted " + enactedAct );
			
			// The selected intention is it over?
			
			intentionAct = nextAct(intendedPrimitiveAct, enactedPrimitiveAct);
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
			if (context.getIntentionAct() != enactedAct && !context.getIntentionAct().getStatus())
				 m_internalState= "!";
			m_tracer.writeLine(enactedAct.getLabel() + m_internalState);

			// Determine the performed act
			
			ISchema intendedSchema = context.getIntentionAct().getSchema();
			
			if (intendedSchema == enactedAct.getSchema())
				performedAct = enactedAct;
			else
				performedAct = m_episodicMemory.addFailingInteraction(intendedSchema,enactedAct.getSatisfaction());
			
			System.out.println("Performed " + performedAct );
			
			// learn from the  context and the performed act
			
			m_episodicMemory.resetLearnCount();
			
			List<IAct> streamContextList = learn(context.getContextList(), performedAct);
			
			
			// learn from the base context and the stream act
			
			 if (streamContextList.size() > 0) // TODO find a better way than relying on the enacted act being on the top of hte list
			 {
				 IAct streamAct = streamContextList.get(0); // The stream act is the first learned 
				 System.out.println("Streaming " + streamAct);
				 if (streamAct.getSchema().getWeight() > ACTIVATION_THRESH)
					 learn(context.getBaseContextList(), streamAct);
			 }

			// learn from the current context and the actually enacted act
			
			if (enactedAct != performedAct)
			{
				System.out.println("Learn from enacted");
				List<IAct> streamContextList2 = learn(context.getContextList(), enactedAct);
				// learn from the base context and the streamAct2
				if (streamContextList2.size() > 0)
				{
					IAct streamAct2 = streamContextList2.get(0);
					System.out.println("Streaming2 " + streamAct2 );
					if (streamAct2.getSchema().getWeight() > ACTIVATION_THRESH)
						learn(context.getBaseContextList(), streamAct2);
				}
			}			

			// Assess the new context ====
			
			// Shift the context and renitialize it with the new enactedAct 
			// The enacted act needs to be the first of the context list to construct the stream act
			context.shiftDecisionCycle(enactedAct, performedAct, streamContextList);
			
		}
		
		// print the new current context
		System.out.println("Activation context list: ");
		for (IAct a : context.getActivationList())
			{	System.out.println(a);}
		System.out.println("Learned : " + m_episodicMemory.getLearnCount() + " schemas.");
			
		// Select a new intention if the decision cycle has shifted
		
		if (intentionAct == null)
		{
			intentionAct = m_episodicMemory.selectAct(context);
			context.setIntentionAct(intentionAct);
		}
		
		// Spread the selected intention's activation to primitive acts.
		// (so far, only selected intentions activate primitive acts)
		
		IAct activeSensorymotorNoeme = spreadActivation(intentionAct);
		List<IAct> activeSensorymotorNoemes = new ArrayList<IAct>(10);
		activeSensorymotorNoemes.add(activeSensorymotorNoeme);
		
		// Sensorymotor noèmes compete and Ernest selects that with the highest activation
		IAct nextPrimitiveAct = selectAct(activeSensorymotorNoemes);		
		context.setPrimitiveIntention(nextPrimitiveAct);
				
		// Return the new context
		
		return context;
		
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
	 * Learn from an enacted intention after a given context.
	 * Returns the list of learned acts that are based on reliable subacts. The first act of the list is the stream act.
	 * @param contextList The list of acts that constitute the context in which the learning occurs.
	 * @param The intention.
	 * @return A list of the acts created from the learning. The first act of the list is the stream act if the first act of the contextList was the performed act.
	 */
	private List<IAct> learn(List<IAct> contextList, IAct intentionAct)
	{
		List<IAct> newContextList= new ArrayList<IAct>(20);;
		
		if (intentionAct != null)
		{
			// For each act in the context ...
			for (IAct contextAct : contextList)
			{
				// Build a new schema with the context act and the intention act 
				ISchema newSchema = m_episodicMemory.addCompositeInteraction(contextAct, intentionAct);
				newSchema.incWeight();
				System.out.println("learned " + newSchema.getLabel());
				
					// Created acts are part of the context 
					// if their context and intention have passed the regularity
					// if they are based on reliable noèmes
				if ((contextAct.getConfidence() == RELIABLE) &&
  				   (intentionAct.getConfidence() == RELIABLE))
				{
					newContextList.add(newSchema.getSucceedingAct());
					// System.out.println("Reliable schema " + newSchema);
				}
			}
		}
		return newContextList; 
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
