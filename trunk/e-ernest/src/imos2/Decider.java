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

	/**
	 * @param imos The sequential system
	 * @param spas The spatial system
	 */
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
		IInteraction nextTopIntention = selectInteraction(actPropositions);
		
		newEnaction.setTopInteraction(nextTopIntention);
		newEnaction.setTopRemainingInteraction(nextTopIntention);
		newEnaction.setPreviousLearningContext(enaction.getInitialLearningContext());
		newEnaction.setInitialLearningContext(enaction.getFinalLearningContext());
		
		return newEnaction;
	}
	
	/**
	 * Construct a list of propositions based on the current context
	 * @param activationList The list of interactions that form the current context.
	 * @return The list of propositions.
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

		// Primitive interactions receive a default proposition for themselves
		for(IInteraction a : m_imos.getInteractions())
		{
			if (a.getPrimitive())
			{
				IProposition p = new Proposition(a, 0, a.getMoveLabel());
				if (!propositions.contains(p))
					propositions.add(p);
			}       
		}

		// Browse all the existing interactions 
		for (IInteraction i : m_imos.getInteractions())
		{
			if (!i.getPrimitive())
			{
				// If this interaction's pre-interaction belongs to the context then this interaction is activated 
				boolean activated = false;
				for (IInteraction contextAct : activationList)
				{
					if (i.getPreInteraction().equals(contextAct))
					{
						activated = true;
						if (m_tracer != null)
							m_tracer.addSubelement(activationElmt, "interaction", i + " intention " + i.getPostInteraction());
					}
				}
				
				// Activated interactions propose their post-interaction
				if (activated)
				{
					IInteraction proposedInteraction = i.getPostInteraction();
					// The weight is the proposing interaction's weight multiplied by the proposed interaction's satisfaction
					int w = i.getEnactionWeight() * proposedInteraction.getEnactionValue();
					
					// If the intention is reliable then a proposition is constructed
//					if ((proposedInteraction.getEnactionWeight() > m_imos.getRegularityThreshold() ) &&						 
//						(proposedInteraction.getLength() <= m_maxSchemaLength ))
					{
						IProposition p = new Proposition(proposedInteraction, w, proposedInteraction.getMoveLabel());
	
						int j = propositions.indexOf(p);
						if (j == -1)
							propositions.add(p);
						else
							propositions.get(j).addWeight(w);
					}
					// If the intention is not reliable
					// then the activation is propagated to the intention's pre-interaction
//					else
//					{
//						if (!proposedInteraction.getPrimitive())
//						{
//							// only if the intention's intention is positive (this is some form of positive anticipation)
//							if (proposedInteraction.getPostInteraction().getEnactionValue() > 0)
//							{
//								IProposition p = new Proposition(proposedInteraction.getPreInteraction(), w, proposedInteraction.getPreInteraction().getMoveLabel());
//								int j = propositions.indexOf(p);
//								if (j == -1)
//									propositions.add(p);
//								else
//									propositions.get(j).addWeight(w);
//							}
//						}
//					}
				}
			}
		}
		
		// Trace the propositions
		
		//System.out.println("Propose: ");
		Object proposalElmt = null;
		if (m_tracer != null)
		{
			proposalElmt = m_tracer.addSubelement(decisionElmt, "propositions");
		
			for (IProposition p : propositions)
			{
				System.out.println("proposition " + p);
				m_tracer.addSubelement(proposalElmt, "proposition", p.toString());
			}
		}
		
		return propositions;
	}
	
	/**
	 * Select an interaction from the list of proposed interactions
	 * @param propositions The list of propositions.
	 * @return The selected interaction.
	 */
	protected IInteraction selectInteraction(ArrayList<IProposition> propositions)
	{
		Object selectionElmt = null;
		Object consolidationElmt = null;
		if (m_tracer != null)
		{
			selectionElmt = m_tracer.addEventElement("selection", true);
			consolidationElmt = m_tracer.addSubelement(selectionElmt, "consolidation");
		}
		
		// Transfer the weight of alternate interactions to their prominent interactions
		for (IProposition interactionProposition : propositions)
		{
			for (IInteraction i : interactionProposition.getInteraction().getAlternateInteractions())
			{
				for (IProposition ip : propositions)
				{
					if (ip.getInteraction().equals(i))// && !ip.getTransferred())
					{
						interactionProposition.addWeight(ip.getWeight());
						ip.setTransferred(true);
						if (m_tracer != null)
						{
							m_tracer.addSubelement(consolidationElmt, "proposition", ip.getInteraction().getLabel() +  " transfers " + ip.getWeight()/10 + " to " + interactionProposition.getInteraction().getLabel());						
						}
					}
				}
			}
		}
		
		// Sort the propositions by weight.
		Collections.sort(propositions);
		
		// Pick the most weighted proposition
		IInteraction selectedInteraction = propositions.get(0).getInteraction();
		
		System.out.println("Select:" + selectedInteraction);

		// Trace the propositions
		if (m_tracer != null)
		{
			//Object propositionElmt = m_tracer.addEventElement("consolidated_propositions", true);
			Object propositionElmt = m_tracer.addSubelement(selectionElmt, "consolidated_propositions");
			for (IProposition p : propositions)
				if (!p.getTransferred())
					m_tracer.addSubelement(propositionElmt, "proposition", p.toString());
			
			m_tracer.addSubelement(selectionElmt, "selected_interaction", selectedInteraction.toString());
			m_tracer.addSubelement(selectionElmt, "angst", "" + propositions.get(0).getAngst());
		}
		
		return selectedInteraction;
	}

	public void carry(IEnaction enaction)
	{
		IInteraction intendedPrimitiveInteraction = enaction.getTopRemainingInteraction().prescribe();
		enaction.setIntendedPrimitiveInteraction(intendedPrimitiveInteraction);
		enaction.setStep(enaction.getStep() + 1);
		enaction.traceCarry(m_tracer);
	}
}
