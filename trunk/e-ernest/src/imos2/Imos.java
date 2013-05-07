package imos2;

import java.util.ArrayList;
import java.util.List;

import spas.Area;
import spas.SimuImpl;
import ernest.ActionImpl;
import ernest.Primitive;
import ernest.ITracer;

/**
 * The Intrinsically Motivated Schema mechanism.
 * @author ogeorgeon
 */

public class Imos implements IImos 
{	
	/** Default maximum length of a schema (For the schema to be chosen as an intention) */
	public final int SCHEMA_MAX_LENGTH = 100;

	/** Default Activation threshold (The weight threshold for higher-level learning with the second learning mechanism). */
	public final int ACTIVATION_THRESH = 1;

	/** Regularity sensibility threshold (The weight threshold for an act to become reliable). */
	private int regularityThreshold = 6;
	
	/** The maximul length of acts. */
	private int maxSchemaLength = 10;

	/** A list of all the acts ever created. */
	//private ArrayList<Act> acts = new ArrayList<Act>(2000);
	
	/** Counter of learned schemas for tracing */
	private int m_nbSchemaLearned = 0;
	
	/** The Tracer. */
	private ITracer<Object> m_tracer = null; //new Tracer("trace.txt");

	/** A representation of the internal state for display in the environment. */
	private String m_internalState = "";
	
	/** Counter of cognitive cycles. */
	private int m_imosCycle = 0;
	
	public void setRegularityThreshold(int regularityThreshold)
	{
		this.regularityThreshold = regularityThreshold;
	}
	
	public void setMaxSchemaLength(int maxSchemaLength)
	{
		this.maxSchemaLength = maxSchemaLength;
	}
	
	/**
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer<Object> tracer)
	{
		m_tracer = tracer;
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
	 * Construct a new act or retrieve the act if it already exists.
	 * @param interaction The interaction of this act.
	 * @param area The area of this act.
	 * @return The act that was created or that already existed.
	 */
	public Act addAct(Primitive interaction, Area area)
	{
		Act act = ActImpl.createOrGetPrimitiveAct(interaction, area);
		act.setAction(SimuImpl.getAction(interaction));
		act.setAspect(SimuImpl.getAspect(interaction));
		return act;	
	}

	/**
	 * Add a composite schema and its succeeding act that represent a composite possibility 
	 * of interaction between Ernest and its environment. 
	 * @param preInteraction The context Act.
	 * @param postInteraction The intention Act.
	 * @return The schema made of the two specified acts, whether it has been created or it already existed. 
	 */
    private Act addCompositeInteraction(Act preInteraction, Act postInteraction)
    {
    	Act i = ActImpl.createOrGetCompositeAct(preInteraction, postInteraction);
    	
//		int j = this.acts.indexOf(i);
//		if (j == -1)
//		{
//			// The schema does not exist: create its succeeding act and add it to Ernest's memory
//			this.acts.add(i);
//			m_nbSchemaLearned++;
//		}
//		else
//			// The schema already exists: return a pointer to it.
//			i =  this.acts.get(j);
    	
    	// Any alternate interactions of the preInteraction is an alternate interaction of the composite interaction
		Object alternateElmnt = null;
		if (m_tracer != null)
			alternateElmnt = m_tracer.addEventElement("alternate", true);
    	for (Act a: preInteraction.getAlternateActs())
    	{
    		boolean newAlternate = i.addAlternateInteraction(a);
			if (m_tracer != null && newAlternate)
				m_tracer.addSubelement(alternateElmnt, "prominent", i + " alternate " + a);
    	}

    	return i;
    }

	/**
	 * Track the current enaction. 
	 * Use the intended primitive act and the effect.
	 * Generates the enacted primitive act, the top enacted act, and the top remaining act.
	 * @param enaction The current enaction.
	 */
	public void track(IEnaction enaction) 
	{
		m_imosCycle++;		
		
		Act intendedPrimitiveInteraction = enaction.getIntendedPrimitiveInteraction();
		Act enactedPrimitiveInteraction  = null;
		Act topEnactedInteraction        = null;
		Act topRemainingInteraction      = null;

		// If we are not on startup
		if (intendedPrimitiveInteraction != null)
		{
			// Compute the enacted primitive interaction from the move and the effect.
			// Compute the enaction value of interactions that were not yet recorded
			//enactedPrimitiveInteraction = addAct(enaction.getEnactedPrimitive().getLabel() + enaction.getArea(), enaction.getEnactedPrimitive().getValue());
			enactedPrimitiveInteraction = addAct(enaction.getEnactedPrimitive(), enaction.getArea());

			// Compute the top actually enacted interaction
			//topEnactedInteraction = enactedInteraction(enactedPrimitiveInteraction, enaction);
			// TODO compute the actually enacted top interaction.
			topEnactedInteraction = topEnactedInteraction(enactedPrimitiveInteraction, intendedPrimitiveInteraction);
			
			// Update the prescriber hierarchy.
			if (intendedPrimitiveInteraction.equals(enactedPrimitiveInteraction)) 
				topRemainingInteraction = intendedPrimitiveInteraction.updatePrescriber();
			else
			{
				intendedPrimitiveInteraction.terminate();
			}
			
			System.out.println("Enacted primitive interaction " + enactedPrimitiveInteraction );
			System.out.println("Top remaining interaction " + topRemainingInteraction );
			System.out.println("Enacted top interaction " + topEnactedInteraction );
			
		}					
		
		// Update the current enaction
		enaction.setEnactedPrimitiveInteraction(enactedPrimitiveInteraction);
		enaction.setTopEnactedInteraction(topEnactedInteraction);
		enaction.setTopRemainingInteraction(topRemainingInteraction);

		// Trace
		enaction.traceTrack(m_tracer);
	}
	
	/**
	 * Terminate the current enaction.
	 * Use the top intended interaction, the top enacted interaction, the previous learning context, and the initial learning context.
	 * Generates the final activation context and the final learning context.
	 * Record or reinforce the learned interactions. 
	 * @param enaction The current enaction.
	 */
	public void terminate(IEnaction enaction)
	{

		Act intendedTopInteraction = enaction.getTopInteraction();
		Act enactedTopInteraction  = enaction.getTopEnactedInteraction();
		ArrayList<Act> previousLearningContext = enaction.getPreviousLearningContext();
		ArrayList<Act> initialLearningContext = enaction.getInitialLearningContext();

		Object alternateElmnt = null;
		if (m_tracer != null)
			alternateElmnt = m_tracer.addEventElement("alternate", true);

		
		// if we are not on startup
		if (enactedTopInteraction != null)
		{
			// Surprise if the enacted interaction is not that intended
			if (intendedTopInteraction != enactedTopInteraction) 
			{
				m_internalState= "!";
				enaction.setCorrect(false);	
				
				// The enacted and intended interactions correspond to the same action.
				// Under construction /// not tested
				ActionImpl.remove(enactedTopInteraction.getAction().getLabel());
				enactedTopInteraction.setAction(intendedTopInteraction.getAction());
				
				
				boolean newAlternate = intendedTopInteraction.addAlternateInteraction(enactedTopInteraction);
				if (m_tracer != null && newAlternate)
					m_tracer.addSubelement(alternateElmnt, "prominent", intendedTopInteraction + " alternate " + enactedTopInteraction);

				if (enactedTopInteraction.isPrimitive() && enactedTopInteraction.isPrimitive())
				{
					newAlternate = enactedTopInteraction.addAlternateInteraction(intendedTopInteraction);
					if (m_tracer != null && newAlternate)
						m_tracer.addSubelement(alternateElmnt, "prominent", enactedTopInteraction + " alternate " + intendedTopInteraction);
				}
			}
			
			// learn from the  context and the enacted interaction
			m_nbSchemaLearned = 0;
			System.out.println("Learn from enacted top interaction");
			ArrayList<Act> streamContextList = record(initialLearningContext, enactedTopInteraction);
						
			// learn from the base context and the stream interaction	
			 if (streamContextList.size() > 0) // TODO find a better way than relying on the enacted act being on the top of the list
			 {
				 Act streamInteraction = streamContextList.get(0); // The stream act is the first learned 
				 System.out.println("Streaming " + streamInteraction);
				 if (streamInteraction.getWeight() > ACTIVATION_THRESH)
				 {
					System.out.println("Learn from stream interaction");
					record(previousLearningContext, streamInteraction);
				 }
			 }

			//enaction.setFinalContext(topEnactedAct, performedAct, streamContextList);			
			enaction.setFinalContext(enactedTopInteraction, enactedTopInteraction, streamContextList);			
		}
		//enaction.setNbActLearned(m_episodicMemory.getLearnCount());
		enaction.setNbActLearned(m_nbSchemaLearned);
		enaction.traceTerminate(m_tracer);

	}

	/**
	 * Learn from an enacted interaction after a given context.
	 * Returns the list of learned acts that are based on reliable subacts. The first act of the list is the stream act.
	 * @param contextList The list of acts that constitute the context in which the learning occurs.
	 * @param enactedInteraction The intention.
	 * @return A list of the acts created from the learning. The first act of the list is the stream act if the first act of the contextList was the performed act.
	 */
	private ArrayList<Act> record(List<Act> contextList, Act enactedInteraction)
	{
		
		Object learnElmnt = null;
		if (m_tracer != null)
		{
			//Object propositionElmt = m_tracer.addSubelement(decision, "proposed_moves");
			learnElmnt = m_tracer.addEventElement("learned", true);
		}
		
		ArrayList<Act> newContextList= new ArrayList<Act>(20);
		
		if (enactedInteraction != null)
		{
			for (Act preInteraction : contextList)
			{
				// Build a new interaction with the context pre-interaction and the enacted post-interaction 
				Act newInteraction = addCompositeInteraction(preInteraction, enactedInteraction);
				newInteraction.setWeight(newInteraction.getWeight() + 1);
				System.out.println("learned " + newInteraction);
				if (m_tracer != null)	
					m_tracer.addSubelement(learnElmnt, "interaction", newInteraction.toString());
			
				// The new interaction belongs to the context 
				// if its pre-interaction and post-interaction have passed the regularity threshold
				if ((preInteraction.getWeight()     > regularityThreshold) &&
  				    (enactedInteraction.getWeight() > regularityThreshold))
				{
					newContextList.add(newInteraction);
					// System.out.println("Reliable schema " + newSchema);
				}
			}
		}
		return newContextList; 
	}

//	public ArrayList<Act> getActs()
//	{
//		return this.acts;
//	}

	public int getCounter() 
	{
		return m_imosCycle;
	}

	/**
	 * Recursively construct the current actually enacted act. 
	 *  (may construct extra intermediary schemas but that's ok because their weight is not incremented)
	 * @param enactedInteraction The enacted interaction.
	 * @param intendedInteraction The intended interaction.
	 * @return the actually enacted interaction
	 */
	private Act topEnactedInteraction(Act enactedInteraction, Act intendedInteraction)
	{
		Act topEnactedInteraction = null;
		Act prescriberInteraction = intendedInteraction.getPrescriber();
		
		if (prescriberInteraction == null)
			// top interaction
			topEnactedInteraction = enactedInteraction;
		else
		{
			// The i was prescribed
			if (prescriberInteraction.getStep() == 0)
			{
				// enacted the prescriber's pre-interaction 
				//topEnactedInteraction = enactedAct(prescriberSchema, a);
				topEnactedInteraction = topEnactedInteraction(enactedInteraction, prescriberInteraction);
			}
			else
			{
				// enacted the prescriber's post-interaction
				Act act = addCompositeInteraction(prescriberInteraction.getPreAct(), enactedInteraction);
				topEnactedInteraction = topEnactedInteraction(act, prescriberInteraction);
				//topEnactedInteraction = enactedAct(prescriberSchema, enactedSchema.getSucceedingAct());
			}
		}
			
		return topEnactedInteraction;
	}
	
	public ArrayList<IProposition> propose(IEnaction enaction)
	{
		ArrayList<IProposition> propositions = new ArrayList<IProposition>();
		
		Object activationElmt = null;
		if (m_tracer != null)
			activationElmt = m_tracer.addEventElement("activation", true);
		
		//for (Act activatedAct : this.acts)
		for (Act activatedAct : ActImpl.getACTS())
		{
			if (!activatedAct.isPrimitive())
			{
				// If this act's pre-act belongs to the context then this act is activated 
				for (Act contextAct : enaction.getFinalActivationContext())
				{
					if (activatedAct.getPreAct().equals(contextAct))
					{
						addProposition(propositions, activatedAct);
						if (m_tracer != null)
							m_tracer.addSubelement(activationElmt, "ActivatedAct", activatedAct + " intention " + activatedAct.getPostAct());
					}
				}
			}
		}
		return propositions;
	}
	
	private void addProposition(ArrayList<IProposition> propositions, Act activatedAct)
	{
		IProposition proposition = propose(activatedAct);
		
		if (proposition!=null)
		{
			int j = propositions.indexOf(proposition);
			if (j == -1)
				propositions.add(proposition);
			else
			{
				IProposition previsousProposition = propositions.get(j);
				previsousProposition.addWeight(proposition.getWeight());
			}
		}
	}
	
	private IProposition propose(Act activatedAct)
	{
		IProposition proposition = null;
		Act proposedAct = activatedAct.getPostAct();
		int w = activatedAct.getWeight() * proposedAct.getEnactionValue();
		
		if ((proposedAct.getWeight() > this.regularityThreshold ) &&						 
				(proposedAct.getLength() <= this.maxSchemaLength ))
		{
			proposition = new Proposition(proposedAct, w);
		}
		// if the intended act has not passed the threshold then  
		// the activation is propagated to the intended interaction's pre interaction
		else
		{
			if (!proposedAct.isPrimitive())
			{
				// only if the intention's intention is positive (this is some form of positive anticipation)
				if (proposedAct.getPostAct().getEnactionValue() > 0)
					proposition = new Proposition(proposedAct.getPreAct(), w);
			}
		}
		return proposition;
	}
}
