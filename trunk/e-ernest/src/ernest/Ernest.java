package ernest;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

/**
 * The main Ernest class used to create an Ernest agent.
 * @author ogeorgeon
 */
public class Ernest implements IErnest 
{
	/** A big value that can represent infinite for diverse purpose. */
	public static final int INFINITE = 1000;

	/** Central module. */
	public static final int CENTRAL = 1;
	/** Iconic module. */
	public static final int ICONIC = 2;
	/** Homeostatic module. */
	public static final int HOMEOSTATIC = 3;
	
	/** Hypothetical noème (Can't be chosen as an intention nor support higher-level learning). */
	public static final int HYPOTHETICAL_NOEME = 1;
	/** Reliable noème (Can be chosen as an intention and support higher-level learning). */
	public static final int RELIABLE_NOEME = 2;

	/** Regularity sensibility threshold (The weight threshold for a noème to become reliable). */
	public static int REG_SENS_THRESH = 5;
	/** Activation threshold (The weight threshold for higher-level learning with the second learning mechanism). */
	private static int ACTIVATION_THRESH = 1;
	/** Maximum length of a schema (For the schema to be chosen as an intention)*/
	private static int SCHEMA_MAX_LENGTH = INFINITE;
	
	/** Random generator used to break a tie when selecting a schema... */
	private static Random m_rand = new Random(); 
	/** Counter of learned schemas for tracing */
	private int m_learnCount = 0;
	
	/** Indication of Ernest's boredom. */
	private boolean m_bored = false;

	/** A representation of Ernest's internal state. */
	private String m_internalState = "";
	
	/** A list of all the schemas ever created ... */
	private List<ISchema> m_schemas = new ArrayList<ISchema>(1000);

	/** A list of all the noèmes ever created ... */
	//private List<IAct> m_noemes = new ArrayList<IAct>(100);

	/** The base context. */
	private IContext m_baseContext = new Context();
	/** The current context. */
	private IContext m_currentContext = new Context();
	
	/** The Tracer. */
	private ITracer m_logger = null;
	public void setTracer(ITracer tracer) { m_logger = tracer; }

	/** Architectural modules. */
	private IconicModule m_iconicModule = new IconicModule(); 
	private HomeostaticModule m_homeostaticModule = new HomeostaticModule(); 
	
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
	 * Add a primitive schema and its two resulting acts that represent a primitive possibility 
	 * of interaction between Ernest and its environment.
	 * @param label The schema's string identifier.
	 * @param successSatisfaction The satisfaction in case of success.
	 * @param failureSatisfaction The satisfaction in case of failure.
	 * @param module The module that can handle this interaction.
	 * @return The created primitive schema.
	 */
	public ISchema addPrimitive(String label, int successSatisfaction, int failureSatisfaction, int module) 
	{
		ISchema s =  Schema.createMotorSchema(m_schemas.size() + 1, label, module);
		IAct succeedingAct = Act.createAct("(" + s.getLabel() + ")", s, true,  successSatisfaction, module, Ernest.RELIABLE_NOEME);
		IAct failingAct = Act.createAct("[" + s.getLabel() + "]", s, false, failureSatisfaction, module, Ernest.RELIABLE_NOEME);
		
		s.setSucceedingAct(succeedingAct);
		s.setFailingAct(failingAct);

		m_schemas.add(s);
		// System.out.println("Primitive schema " + s);
		return s;
	}

	/**
	 * Add a primitive icon to Ernest's iconic module.
	 * @param label The icon's string identifier.
	 * @param matrix The distal sensory state.
	 * @return The created primitive iconic noème.
	 */
	public IAct addIconicPrimitive(String label, int[][] matrix) 
	{
		return m_iconicModule.addInteraction(label, matrix);
	}

	/**
	 * Add a composite schema and its succeeding act that represent a composite possibility 
	 * of interaction between Ernest and its environment. 
	 * @param contextAct The context Act.
	 * @param intentionAct The intention Act.
	 * @return The schema made of the two specified acts, whether it has been created or it already existed. 
	 */
    public ISchema addCompositeInteraction(IAct contextAct, IAct intentionAct)
    {
    	ISchema s = Schema.createCompositeSchema(m_schemas.size() + 1, contextAct, intentionAct);
    	
		int i = m_schemas.indexOf(s);
		if (i == -1)
		{
			// The schema does not exist: create its succeeding act and add it to Ernest's memory
	    	s.setSucceedingAct( Act.createCompositeSucceedingAct(s) );
			m_schemas.add(s);
			m_learnCount++;
		}
		else
			// The schema already exists: return a pointer to it.
			s =  m_schemas.get(i);
    	return s;
    }

	/**
	 * Reset Ernest by clearing all its long-term memory.
	 */
	public void clear() 
	{
		m_schemas.clear();
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
	 * Run Ernest one step. Exploit the matrix sensed in the environment.
	 * @param status The status received as feedback from the intended sensorymotor enaction.
	 * @param matrix A matrix sensed in the environment to inform the iconic module.
	 * @return The next primitive schemas to enact.
	 */
	public String step(boolean status, int[][] matrix) 
	{
		// The iconic module senses the matrix provided by the environment
		m_iconicModule.senseMatrix(matrix);
		
		// The central module processes the current enaction
		return step(status);
		
	}

	/**
	 * Ernest's central process.
	 * @param status The status received as a feedback from the previous primitive enaction.
	 * @return The next primitive schema to enact.
	 */
	public String step(boolean status) 
	{
		m_internalState= "";

		IAct intendedPrimitiveAct = m_currentContext.getPrimitiveIntention();
		IAct enactedPrimitiveAct = null;
		IAct intentionAct = null;
		IAct enactedAct = null;
		
		// Review the enaction if any.

		if (intendedPrimitiveAct != null)
		{
			// Compute the actually enacted act
			
			ISchema enactedPrimitiveSchema = intendedPrimitiveAct.getSchema();
			enactedPrimitiveAct = 	enactedPrimitiveSchema.resultingAct(status);
			enactedAct = enactedAct(enactedPrimitiveSchema, enactedPrimitiveAct);
			
			System.out.println("Enacted " + enactedAct );
			
			// The selected intention is it over?
			
			intentionAct = nextAct(intendedPrimitiveAct, status);
						
		}	

		// Process the iconic animation noème every primitive step
		
		IAct animationNoeme = null;
		if (m_currentContext.getSensedIcon() != null)
		{
			ISchema animationSchema = addCompositeInteraction(m_currentContext.getSensedIcon(), enactedPrimitiveAct );
			animationSchema.incWeight();
			animationNoeme = animationSchema.getSucceedingAct();
			System.out.println ("Animation schema: " + animationSchema);
		}

		// Shift decision cycle if no ongoing enaction

		if (intentionAct == null && enactedAct != null)
		{
			System.out.println("Schift ================ ");
			// No ongoing schema to enact. The decision cycle is over.  
			// Shift to the next decision cycle
			shiftDecisionCycle(enactedAct);
		}

		// Update the iconic context every step
		// (Only if the environment sends matrix)
		m_currentContext.setAnimationNoeme(animationNoeme);
		if (!m_iconicModule.checkIcon(m_currentContext, m_schemas))
			m_internalState = "!";
		m_currentContext = m_iconicModule.updateContext(m_currentContext);
		
		// learn the iconic evocation noème.
		if (m_currentContext.getSensedIcon() != null && animationNoeme != null)
		{
			ISchema evocationSchema = addCompositeInteraction(animationNoeme, m_iconicModule.getSensedIcon());
			evocationSchema.incWeight();
			System.out.println ("Evocation schema: " + evocationSchema);
		}

		// print the new current context
		System.out.println("Context: ");
		 for (IAct a : m_currentContext.getActivationList())
		 {	System.out.println(a);}
		System.out.println("Learned : " + m_learnCount + " schemas.");
			
		// Activate a new central intention if the decision cycle has shifted
		
		if (intentionAct == null)
		{
			// Activate a new sensorymotor intention =====
			intentionAct = activateNoeme(m_currentContext, m_schemas);
			m_currentContext.setIntentionAct(intentionAct);
		}
		
		// Spread the central intention's activation to sensorymotor noèmes.
		// (so far, only central intentions activate sensorymotor noèmes)
		
		IAct activeSensorymotorNoeme = spreadActivation(intentionAct);
		List<IAct> activeSensorymotorNoemes = new ArrayList<IAct>(10);
		activeSensorymotorNoemes.add(activeSensorymotorNoeme);
		
		// Sensorymotor noèmes compete and Ernest selects that with the highest activation
		IAct nextPrimitiveAct = selectAct(activeSensorymotorNoemes);		
		m_currentContext.setPrimitiveIntention(nextPrimitiveAct);

		// The iconic context may activate a homeostatic noème.
		// (so far, only iconic noèmes activate homeostatic noèmes through a composite schema)
		
		IAct activeHomeostaticNoeme = m_homeostaticModule.activateNoeme(m_currentContext, m_schemas); 
				
		
		// Return the schemas to enact in the environment
		
		String enact = nextPrimitiveAct.getSchema().getLabel();
		if (activeHomeostaticNoeme != null)
			enact = enact + "," + activeHomeostaticNoeme.getSchema().getLabel();
		
		return enact;
		
	}
	
	/**
	 * Swing to the next decision cycle 
	 * Learn from the ending decision cycle
	 * swap 
	 */
	private void shiftDecisionCycle(IAct enactedAct)
	{

		IAct performedAct = null;

		// Log the previous decision cycle's trace

		// if (m_currentContext.getIntentionAct() != enactedAct)
		//	 m_internalState= "!";
		m_logger.writeLine(enactedAct.getLabel() + m_internalState);

		// Determine the performed act
		
		ISchema intendedSchema = m_currentContext.getIntentionAct().getSchema();
		if (intendedSchema == enactedAct.getSchema())
			performedAct = enactedAct;
		else
			performedAct = addFailingInteraction(intendedSchema,enactedAct.getSatisfaction());
		System.out.println("Performed " + performedAct );
		
		// learn from the  context and the performed act
		
		m_learnCount = 0;
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
		
		m_currentContext = new Context();
		m_currentContext.setCoreAct(enactedAct); // rather than performedAct to avoid too many schemas including failing subschemas
		if (enactedAct != performedAct)
			m_currentContext.addActivationAct(performedAct);

		// if the actually enacted act is not primitive, its intention also belongs to the context
		if (!enactedAct.getSchema().isPrimitive())
			m_currentContext.addActivationAct(enactedAct.getSchema().getIntentionAct());	
		
		// add the streamcontext to the context list
		m_currentContext.addContext(streamContext);
		
		// m_currentContext.addContext(streamContext2);
		
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
				ISchema enactedSchema = addCompositeInteraction(prescriberSchema.getContextAct(), a);
				enactedAct = enactedAct(prescriberSchema, enactedSchema.getSucceedingAct());
			}
		}
			
		return enactedAct;
	}
	
	/**
	 * Recursively finds the next act to enact in the hierarchy of prescribers.
	 * @param a The enacted act.
	 * @param status The enaction status.
	 * @return the next intention act to enact or null if failed or completed
	 */
	private IAct nextAct(IAct a, boolean status)
	{
		IAct nextAct = null;
		ISchema prescriberSchema = a.getPrescriberSchema();
		int activation = a.getActivation();
		a.setPrescriberSchema(null); 
		a.setActivation(0); // (It might be the case that the same act will be prescribed again)
		
		if (prescriberSchema != null)
		{
			if (a.getStatus() == status)
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
					nextAct = nextAct(prescriberAct, true);
				}
				// a.setPrescriberSchema(null);						
			}
			else
			{
				// move to prescriber act with a failure status
				IAct prescriberAct = prescriberSchema.getPrescriberAct();
				nextAct = nextAct(prescriberAct, false);				
			}
		}

		return nextAct;
	}
	
	/**
	 * Learn from an enacted intention after a given context.
	 * Learned schemas that are based on reliable acts are returned in the returned context.
	 * @param The context in which the learning occurs.
	 * @param The intention.
	 * @return A partial new context created from the learning.
	 */
	private IContext learn(IContext context, IAct intentionAct)
	{
		IContext newContext = new Context();
		
		// For each act in the context ...
		for (IAct contextAct : context.getContextList())
		{
			// Build a new schema with the context act 
			// and the intention act 
			ISchema newSchema = addCompositeInteraction(contextAct, intentionAct);
			newSchema.incWeight();
			// System.out.println("Reinfocing schema " + newSchema);
			
			boolean reg = (contextAct.getConfidence() == RELIABLE_NOEME) &&
			  (intentionAct.getConfidence() == RELIABLE_NOEME);

			// the returned core act is the act made from the previous core act
			if (contextAct == context.getCoreAct())
				newContext.setCoreAct(newSchema.getSucceedingAct());
			
			// other created acts are part of the context 
			// if their context and intention have passed the regularity
			// if they are based on reliable noèmes
			else if (reg)
			{
				newContext.addContextAct(newSchema.getSucceedingAct());
			}
		}
		return newContext; 
	}

	/**
	 * Activates a sensorymotor noème from the focus list of a context.
	 * @param context The context that generates the proposals.
	 * @param schemas The list of all existing schemas.
	 * @return The selected noème.
	 */
	private IAct activateNoeme(IContext context, List<ISchema> schemas)
	{

		List<IProposition> proposals = new ArrayList<IProposition>();	
		
		// Browse all the schemas 
		for (ISchema s : schemas)
		{
			if (!s.isPrimitive())
			{
				// Activate the schemas that match the context 
				boolean activated = false;
				for (IAct contextAct : context.getActivationList())
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
					int w = s.getWeight() * s.getIntentionAct().getSatisfaction();
					// The expectation is the proposing schema's weight signed with the proposed act's status  
					int e = s.getWeight() * (s.getIntentionAct().getStatus() ? 1 : -1);
					
					// If the intention is reliable
					if ((s.getIntentionAct().getModule() == CENTRAL) &&
						(s.getIntentionAct().getConfidence() == RELIABLE_NOEME ) &&						 
						(s.getIntentionAct().getSchema().getLength() <= SCHEMA_MAX_LENGTH ))
					{
						IProposition p = new Proposition(s.getIntentionAct().getSchema(), w, e);
	
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
							if (s.getIntentionAct().getSchema().getIntentionAct().getSatisfaction() > 0)
							{
								IProposition p = new Proposition(s.getIntentionAct().getSchema().getContextAct().getSchema(), w, e);
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

			// Primitive sensorymotor schemas also receive a default proposition for themselves
			if (s.isPrimitive() && (s.getSucceedingAct().getModule() == Ernest.CENTRAL))
			{
				IProposition p = new Proposition(s, 0, 0);
				if (!proposals.contains(p))
					proposals.add(p);
			}
		}

		// System.out.println("Propose central: ");
		// for (IProposition p : proposals)
		//	System.out.println(p);

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

		// pick one at random from the top of the proposal list
		// count is equal to the number of proposals that are tied...

		IProposition p = proposals.get(m_rand.nextInt(count));
		
		ISchema s = p.getSchema();
		
		IAct a = (p.getExpectation() >= 0 ? s.getSucceedingAct() : s.getFailingAct());
		
		// The noème's activation is set equal to its proposition's weight
		a.setActivation(p.getWeight());
		
		System.out.println("Select:" + a);

		return a ;
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
	 * Set the subacts' activation equal to the prescribing act's actvation.
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
	 * Add or update a failing possibility of interaction between Ernest and its environment.
	 * Add or update the schema's failing act to Ernest's memory. 
	 * If the failing act does not exist then create it. 
	 * If the failing act exists then update its satisfaction.
	 * @param The schema that failed.
	 * @param The satisfaction obtained during the failure.
	 * @return The failing act.
	 */
    private IAct addFailingInteraction(ISchema schema, int satisfaction)
    {
    	IAct failingAct = schema.getFailingAct();
    	
		if (!schema.isPrimitive())
		{
			if (failingAct == null)
			{
				failingAct = Act.createCompositeFailingAct(schema, satisfaction);
				schema.setFailingAct(failingAct);
			}
			else
				// If the failing act already exists then 
				//  its satisfaction is averaged with the previous value
				failingAct.setSatisfaction((failingAct.getSatisfaction() + satisfaction)/2);
		}
		
		return failingAct;
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
