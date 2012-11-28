package imos2;


import java.util.ArrayList;
import java.util.Collections;
import ernest.ITracer;
import spas.ISpas;

/**
 * This is the regular decider for Ernest 7 that does not use spatial memory.
 * @author Olivier
 */
public class Decider implements IDecider 
{
	IImos m_imos;
	ISpas m_spas;
	ITracer m_tracer;
	int m_maxSchemaLength = 10;

	public Decider(IImos imos, ISpas spas)
	{
		m_imos = imos;
		m_spas = spas;
	}

	public void setTracer(ITracer tracer)
	{
		m_tracer = tracer;
	}
	
	public void setMaxSchemaLength(int maxSchemaLength)
	{
		m_maxSchemaLength = maxSchemaLength;
	}

	public IEnaction decide(IEnaction enaction) 
	{
		IEnaction newEnaction = new Enaction();
		
		System.out.println("New decision ================ ");

		ArrayList<IProposition> actPropositions = proposeInteractions(enaction.getFinalActivationContext());
		IInteraction nextTopIntention = selectInteraction2(actPropositions);
		//IAct nextTopIntention = selectAct(enaction.getFinalActivationContext());
		
		newEnaction.setTopInteraction(nextTopIntention);
		newEnaction.setTopRemainingInteraction(nextTopIntention);
		newEnaction.setPreviousLearningContext(enaction.getInitialLearningContext());
		newEnaction.setInitialLearningContext(enaction.getFinalLearningContext());
		
		return newEnaction;
	}
	
	/**
	 * Construct a list of proposed acts based on the current context
	 * @param activationList The list of acts that form the current context.
	 * @return The list of proposed acts.
	 */
	protected ArrayList<IProposition> proposeInteractions(ArrayList<IInteraction> activationList)
	{
		ArrayList<IProposition> propositions = new ArrayList<IProposition>();	
		
		// Prepare the tracer.
		Object decisionElmt = null;
		Object activationElmt = null;
		if (m_tracer != null)
		{
			decisionElmt = m_tracer.addEventElement("activation", true);
			activationElmt = m_tracer.addSubelement(decisionElmt, "activated_interactions");
		}

		// Primitive acts receive a default proposition for themselves
		for(IInteraction a : m_imos.getInteractions())
		{
			if (a.getPrimitive())
			{
				IProposition p = new Proposition(a, 0, 0, a.getMoveLabel());
				if (!propositions.contains(p))
					propositions.add(p);
			}       
		}

		// Browse all the existing schemas 
		for (IInteraction s : m_imos.getInteractions())
		{
			if (!s.getPrimitive())
			{
				// Activate the schemas that match the context 
				boolean activated = false;
				for (IInteraction contextAct : activationList)
				{
					if (s.getPreInteraction().equals(contextAct))
					{
						activated = true;
						if (m_tracer != null)
							m_tracer.addSubelement(activationElmt, "interaction", s + " intention " + s.getPostInteraction());
						//System.out.println("Activate " + s + " s=" + s.getIntentionAct().getSatisfaction());
					}
				}
				
				// Activated schemas propose their intention
				if (activated)
				{
					IInteraction proposedInteraction = s.getPostInteraction();
					// The weight is the proposing schema's weight multiplied by the proposed act's satisfaction
					int w = s.getEnactionWeight() * proposedInteraction.getEnactionValue();
					//int w = s.getWeight();
                    // The expectation is the proposing schema's weight signed with the proposed act's status  
                    int e = s.getEnactionWeight();// * (s.getIntentionAct().getStatus() ? 1 : -1);
                    //int e = 0;
					
					// If the intention is reliable then a proposition is constructed
					if ((proposedInteraction.getEnactionWeight() > m_imos.getRegularityThreshold() ) &&						 
						(proposedInteraction.getLength() <= m_maxSchemaLength ))
					{
						IProposition p = new Proposition(proposedInteraction, w, e, proposedInteraction.getMoveLabel());
	
						int i = propositions.indexOf(p);
						if (i == -1)
							propositions.add(p);
						else
							propositions.get(i).update(w, e);
					}
					// If the intention is not reliable
					// if the intention's schema has not passed the threshold then  
					// the activation is propagated to the intention's schema's context
					else
					{
						// Expect the value of the intention's schema's intention
						//e = proposedAct.getSchema().getIntentionAct().getSatisfaction();
						
						if (!proposedInteraction.getPrimitive())
						{
							// only if the intention's intention is positive (this is some form of positive anticipation)
							if (proposedInteraction.getPostInteraction().getEnactionValue() > 0)
							{
								IProposition p = new Proposition(proposedInteraction.getPreInteraction(), w, e, proposedInteraction.getPreInteraction().getMoveLabel());
								int i = propositions.indexOf(p);
								if (i == -1)
									propositions.add(p);
								else
									propositions.get(i).update(w, e);
							}
						}
					}
				}
			}
		}
		
		// Log the propositions
		
		//System.out.println("Propose: ");
		Object proposalElmt = null;
		if (m_tracer != null)
		{
			proposalElmt = m_tracer.addSubelement(decisionElmt, "proposed_interaction");
		
			for (IProposition p : propositions)
			{
				System.out.println("proposition " + p);
				m_tracer.addSubelement(proposalElmt, "proposition", p.toString());
			}
		}
		return propositions;
	}
	
	/**
	 * Select an act from the list of proposed acts
	 * @param propositions The list of act propostion.
	 * @return The selected act.
	 */
	protected IInteraction selectInteraction(ArrayList<IProposition> propositions)
	{
		
		// Construct a list of move propositions from the list of interaction propositions.
		ArrayList<IMoveProposition> movePropositions = new ArrayList<IMoveProposition>();	
		
		for (IProposition interactionProposition : propositions)
		{
			int w = interactionProposition.getWeight();
			int e = interactionProposition.getExpectation();
			IMoveProposition moveProposition = new MoveProposition(interactionProposition.getInteraction().getMoveLabel(), w, e, interactionProposition.getInteraction());
			int i = movePropositions.indexOf(moveProposition);
			if (i == -1)
				movePropositions.add(moveProposition);
			else
				movePropositions.get(i).update(w, e, interactionProposition.getInteraction());
		}
		
		// Sort the propositions by weight.
		Collections.sort(movePropositions);
		
		// Count how many are tied with the highest weighted proposition
		int count = 0;
		int wp = movePropositions.get(0).getWeight();
		for (IMoveProposition p : movePropositions)
		{
			if (p.getWeight() != wp)
				break;
			count++;
		}

		// pick one at random from the top of the proposal list
		// count is equal to the number of proposals that are tied...

		IMoveProposition selectedProposition = null;
		//if (DETERMINISTIC)
			selectedProposition = movePropositions.get(0); // Always take the first
		//else
		//	p = schemaPropositions.get(m_rand.nextInt(count)); // Break the tie at random
				
		IInteraction a = selectedProposition.getInteraction();
		
		//a.setActivation(selectedProposition.getWeight());
		
		System.out.println("Select:" + a);

		// Trace the schema propositions
		Object decision = null;
		if (m_tracer != null)
		{
			//Object propositionElmt = m_tracer.addSubelement(decision, "proposed_moves");
			Object propositionElmt = m_tracer.addEventElement("proposed_moves", true);
			for (IMoveProposition p : movePropositions)
				m_tracer.addSubelement(propositionElmt, "move", p.toString());
			
			decision = m_tracer.addEventElement("decision", true);
			m_tracer.addSubelement(decision, "select", a.toString());
		}
		
		return a;
	}

	/**
	 * Select an act from the list of proposed acts
	 * @param propositions The list of act propostion.
	 * @return The selected act.
	 */
	protected IInteraction selectInteraction2(ArrayList<IProposition> propositions)
	{
		// Propositions for alternate interactions get the move label of their parent interaction
		for (IProposition interactionProposition : propositions)
		{
			for (IInteraction i : interactionProposition.getInteraction().getAlternateInteractions())
			{
				for (IProposition ip : propositions)
				{
					if (ip.getInteraction().equals(i) && !ip.getTransferred())
					{
						interactionProposition.addWeight(ip.getWeight());
						ip.setTransferred(true);
					}
				}
			}
		}
		
//		// Construct a list of move propositions from the list of interaction propositions.
//		ArrayList<IMoveProposition> movePropositions = new ArrayList<IMoveProposition>();	
//		
//		for (IProposition interactionProposition : propositions)
//		{
//			int w = interactionProposition.getWeight();
//			int e = interactionProposition.getExpectation();
//			IMoveProposition moveProposition = new MoveProposition(interactionProposition.getMoveLabel(), w, e, interactionProposition.getInteraction());
//			int i = movePropositions.indexOf(moveProposition);
//			if (i == -1)
//				movePropositions.add(moveProposition);
//			else
//				movePropositions.get(i).update(w, e, interactionProposition.getInteraction());
//		}
		
		
		// Sort the propositions by weight.
		Collections.sort(propositions);
		
		// Count how many are tied with the highest weighted proposition
		int count = 0;
		int wp = propositions.get(0).getWeight();
		for (IProposition p : propositions)
		{
			if (p.getWeight() != wp)
				break;
			count++;
		}

		// pick one at random from the top of the proposal list
		// count is equal to the number of proposals that are tied...

		IProposition selectedProposition = null;
		//if (DETERMINISTIC)
			selectedProposition = propositions.get(0); // Always take the first
		//else
		//	p = schemaPropositions.get(m_rand.nextInt(count)); // Break the tie at random
				
		IInteraction a = selectedProposition.getInteraction();
		
		//a.setActivation(selectedProposition.getWeight());
		
		System.out.println("Select:" + a);

		// Trace the schema propositions
		Object decision = null;
		if (m_tracer != null)
		{
			//Object propositionElmt = m_tracer.addSubelement(decision, "proposed_moves");
			Object propositionElmt = m_tracer.addEventElement("consolidated_propositions", true);
			for (IProposition p : propositions)
				m_tracer.addSubelement(propositionElmt, "move", p.toString());
			
			decision = m_tracer.addEventElement("decision", true);
			m_tracer.addSubelement(decision, "select", a.toString());
		}
		
		return a;
	}

	public void carry(IEnaction enaction)
	{
		IInteraction intendedPrimitiveInteraction = enaction.getTopRemainingInteraction().prescribe();
		enaction.setIntendedPrimitiveInteraction(intendedPrimitiveInteraction);
		enaction.setStep(enaction.getStep() + 1);
		enaction.traceCarry(m_tracer);
	}
}
