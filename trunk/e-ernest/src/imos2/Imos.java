package imos2;

import java.util.ArrayList;
import java.util.List;
import ernest.ActionImpl;
import ernest.ITracer;

/**
 * The sequential system of the Enactive Cognitive Architecture.
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
	
	/** The maximal length of acts. */
	private int maxSchemaLength = 10;

	/** Counter of learned schemas for tracing */
	private int m_nbSchemaLearned = 0;
	
	/** The Tracer. */
	private ITracer<Object> m_tracer = null;

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
	 * Track the current enaction. 
	 * Use the intended primitive act and the effect.
	 * Generates the enacted primitive act, the top enacted act, and the top remaining act.
	 * @param enaction The current enaction.
	 */
	public void track(Enaction enaction) 
	{
		m_imosCycle++;		
		
		Act intendedPrimitiveAct = enaction.getIntendedPrimitiveAct();
		Act enactedPrimitiveAct  = enaction.getEnactedPrimitiveAct();
		Act topEnactedAct        = null;
		Act topRemainingAct      = null;
		
		if (intendedPrimitiveAct != null){
			topEnactedAct = topEnactedInteraction(enactedPrimitiveAct, intendedPrimitiveAct);
			
			// Update the prescriber hierarchy.
			if (intendedPrimitiveAct.equals(enactedPrimitiveAct)) 
				topRemainingAct = intendedPrimitiveAct.updatePrescriber();
			else
				intendedPrimitiveAct.terminate();
			
			System.out.println("Enacted primitive act " + enactedPrimitiveAct );
			System.out.println("Top remaining act " + topRemainingAct );
			System.out.println("Enacted top act " + topEnactedAct );			
		}					
		
		enaction.setTopEnactedAct(topEnactedAct);
		enaction.setTopRemainingAct(topRemainingAct);
	}
	
	/**
	 * Terminate the current enaction.
	 * Use the top intended interaction, the top enacted interaction, the previous learning context, and the initial learning context.
	 * Generates the final activation context and the final learning context.
	 * Record or reinforce the learned interactions. 
	 * @param enaction The current enaction.
	 */
	public void terminate(Enaction enaction)
	{

		Act intendedTopInteraction = enaction.getTopAct();
		Act enactedTopInteraction  = enaction.getTopEnactedAct();
		ArrayList<Act> previousLearningContext = enaction.getPreviousLearningContext();
		ArrayList<Act> initialLearningContext = enaction.getInitialLearningContext();

		// if we are not on startup
		if (enactedTopInteraction != null)
		{
			// Surprise if the enacted interaction is not that intended
			if (intendedTopInteraction != enactedTopInteraction) 
			{
				m_internalState= "!";
				enaction.setSuccessful(false);	
				
				if (!enactedTopInteraction.getPrimitive().getAction().equals(intendedTopInteraction.getPrimitive().getAction())){
					System.out.println("Action " + enactedTopInteraction.getPrimitive().getAction().getLabel() + " merged to " + intendedTopInteraction.getPrimitive().getAction().getLabel());
					if (m_tracer != null){
						m_tracer.addEventElement("action", enactedTopInteraction.getPrimitive().getAction().getLabel() + " merged to intended action " + intendedTopInteraction.getPrimitive().getAction().getLabel());
					}
				}
				
				ActionImpl.merge(enactedTopInteraction.getPrimitive().getAction(), intendedTopInteraction.getPrimitive().getAction());
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
	 * Add a composite schema and its succeeding act that represent a composite possibility 
	 * of interaction between Ernest and its environment. 
	 * @param preInteraction The context Act.
	 * @param postInteraction The intention Act.
	 * @return The schema made of the two specified acts, whether it has been created or it already existed. 
	 */
    private Act addCompositeAct(Act preInteraction, Act postInteraction)
    {
    	return  ActImpl.createOrGetCompositeAct(preInteraction, postInteraction);
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
				Act newInteraction = addCompositeAct(preInteraction, enactedInteraction);
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
				Act act = addCompositeAct(prescriberInteraction.getPreAct(), enactedInteraction);
				topEnactedInteraction = topEnactedInteraction(act, prescriberInteraction);
				//topEnactedInteraction = enactedAct(prescriberSchema, enactedSchema.getSucceedingAct());
			}
		}
			
		return topEnactedInteraction;
	}
	
	public ArrayList<IProposition> propose(Enaction enaction)
	{
		ArrayList<IProposition> propositions = new ArrayList<IProposition>();
		
		Object activationElmt = null;
		if (m_tracer != null)
			activationElmt = m_tracer.addEventElement("activation", true);
		
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
