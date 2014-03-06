package eca.ss;


import java.util.ArrayList;
import java.util.List;
import tracing.ITracer;
import eca.construct.ActionImpl;
import eca.construct.Appearance;
import eca.construct.AppearanceImpl;
import eca.construct.DisplacementImpl;
import eca.ss.enaction.Act;
import eca.ss.enaction.ActImpl;
import eca.ss.enaction.Enaction;

/**
 * The sequential system of the Enactive Cognitive Architecture.
 * @author ogeorgeon
 */

public class Imos implements IImos 
{	
	/** Default Activation threshold (The weight threshold for higher-level learning with the second learning mechanism). */
	public final int ACTIVATION_THRESH = 1;

	/** Regularity sensibility threshold (The weight threshold for an act to become reliable). */
	private int regularityThreshold = 6;
	
	/** Counter of learned schemas for tracing */
	private int m_nbSchemaLearned = 0;
	
	/** The Tracer. */
	private ITracer<Object> m_tracer = null;

	/** A representation of the internal state for display in the environment. */
	private String m_internalState = "";
	
	public void setRegularityThreshold(int regularityThreshold)
	{
		this.regularityThreshold = regularityThreshold;
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

		Act intendedTopAct = enaction.getTopAct();
		Act enactedTopAct  = enaction.getTopEnactedAct();
		ArrayList<Act> previousLearningContext = enaction.getPreviousLearningContext();
		ArrayList<Act> initialLearningContext = enaction.getInitialLearningContext();
		List<Appearance> preAppearances = enaction.getAppearances();
		
		// if we are not on startup
		if (enactedTopAct != null)
		{
			// Surprise if the enacted interaction is not that intended
			if (intendedTopAct != enactedTopAct) 
			{
				m_internalState= "!";
				enaction.setSuccessful(false);					
				enaction.getIntendedAction().addAct(enactedTopAct);
				ActionImpl.absorbIdenticalAction(enaction.getIntendedAction(), m_tracer);
			}
			
			// learn from the  context and the enacted interaction
			m_nbSchemaLearned = 0;
			System.out.println("Learn from enacted top interaction");
			ArrayList<Act> streamContextList = record(initialLearningContext, enactedTopAct);
						
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

			enaction.setFinalContext(enactedTopAct, enactedTopAct, streamContextList);	
			
			// Add the enacted act to the pre-appearance
			if (preAppearances.size()==1){
				Appearance preAppearance = preAppearances.get(0);
				if (preAppearance != null){
					// check the consistency with the existing appearance
					boolean consistent = true;
					for (Act act : preAppearance.getActs())
						if (ActionImpl.getAction(enactedTopAct).equals(ActionImpl.getAction(act)))
							if(!enactedTopAct.equals(act))
								consistent = false;
							
					if (consistent)
						preAppearance.addAct(enactedTopAct);
					else{
						Appearance splittedAppearance  = AppearanceImpl.createOrGet(enactedTopAct);
						splittedAppearance.addAct(enactedTopAct);
						splittedAppearance.addAct(preAppearance.getStillAct());
						splittedAppearance.setStillAct(preAppearance.getStillAct());
						if (m_tracer != null) this.m_tracer.addEventElement("new_appearance", splittedAppearance.getLabel());
					}
				}
			}
			
			// If the enacted act is STILL then keep the appearance
			if (enactedTopAct.isPrimitive() && enactedTopAct.getPrimitive().getDisplacement().equals(DisplacementImpl.DISPLACEMENT_STILL))
				enaction.setAppearances(AppearanceImpl.getAppeareances(enactedTopAct));
			else
				enaction.setAppearances(new ArrayList<Appearance>(0));
			
			enaction.setNbActLearned(m_nbSchemaLearned);
			enaction.traceTerminate(m_tracer);
		}		
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
	
	/**
	 * Construct the list of ActProposition from the final activation context in the current enaction.
	 * @param enaction The current enaction.
	 * @return The list of ActProposition.
	 */
	public ArrayList<ActProposition> propose(Enaction enaction)
	{
		ArrayList<ActProposition> propositions = new ArrayList<ActProposition>();
		
		Object activationElmt = null;
		Object propositionElmt = null;
		if (m_tracer != null){
			activationElmt = m_tracer.addEventElement("activation", true);
			propositionElmt = m_tracer.addEventElement("actPropositions", true);
		}
		for (Act activatedAct : ActImpl.getACTS())
		{
			if (activatedAct.isPrimitive()){
				addProposition(propositions, activatedAct, 0);
			}
			else
			{
				if (enaction.getFinalActivationContext().contains(activatedAct.getPreAct())){
					addProposition(propositions, activatedAct);
					if (m_tracer != null)
						m_tracer.addSubelement(activationElmt, "ActivatedAct", activatedAct + " intention " + activatedAct.getPostAct());
				}
			}
		}
		if (this.m_tracer != null){
			for (ActProposition ap : propositions)
				this.m_tracer.addSubelement(propositionElmt, "act_proposition", ap.toString());
		}
		
		return propositions;
	}
	
	/**
	 * Propose the activated act's post-act.
	 * @param propositions The list of propositions.
	 * @param activatedAct The activated act.
	 */
	private void addProposition(ArrayList<ActProposition> propositions, Act activatedAct)
	{
		ActProposition proposition = new ActPropositionImpl(activatedAct.getPostAct(), activatedAct.getWeight());
		proposition.setWeightedValue(activatedAct.getPostAct().getValue() * activatedAct.getWeight());
	
		int j = propositions.indexOf(proposition);
		if (j == -1)
			propositions.add(proposition);
		else
		{
			ActProposition previousProposition = propositions.get(j);
			previousProposition.addWeight(proposition.getWeight());
			previousProposition.setWeightedValue(proposition.getWeightedValue() + previousProposition.getWeightedValue());
		}
	}

	/**
	 * Propose the activated act's post-act.
	 * @param propositions The list of propositions.
	 * @param activatedAct The activated act.
	 */
	private void addProposition(ArrayList<ActProposition> propositions, Act proposededAct, int weight)
	{
		ActProposition proposition = new ActPropositionImpl(proposededAct, weight);
		proposition.setWeightedValue(proposededAct.getValue() * weight);
	
		int j = propositions.indexOf(proposition);
		if (j == -1)
			propositions.add(proposition);
		else
		{
			ActProposition previousProposition = propositions.get(j);
			previousProposition.addWeight(proposition.getWeight());
			previousProposition.setWeightedValue(proposition.getWeightedValue() + previousProposition.getWeightedValue());
		}
	}
}
