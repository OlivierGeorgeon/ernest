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
		// The list of activated interactions
		ArrayList<IInteraction> activatedInteractions = new ArrayList<IInteraction>();	
		// The list of propositions
		ArrayList<IProposition> propositions = new ArrayList<IProposition>();	
		
		// Prepare the tracer.
		Object decisionElmt = null;
		Object activationElmt = null;
		Object consolidationElmt = null;
		if (m_tracer != null)
		{
			decisionElmt = m_tracer.addEventElement("activation", true);
			activationElmt = m_tracer.addSubelement(decisionElmt, "activated_interactions");
			consolidationElmt = m_tracer.addSubelement(decisionElmt, "consolidation");
		}

		// Construct a default proposition for primitive interactions
		for(IInteraction a : m_imos.getInteractions())
		{
			if (a.getPrimitive())
			{
				IProposition p = new Proposition(a, 0);
				if (!propositions.contains(p))
					propositions.add(p);
			}       
		}
		
		// Construct the list of activated interactions 
		for (IInteraction i : m_imos.getInteractions())
		{
			if (!i.getPrimitive())
			{
				// If this interaction's pre-interaction belongs to the context then this interaction is activated 
				for (IInteraction contextAct : activationList)
				{
					if (i.getPreInteraction().equals(contextAct))
					{
						activatedInteractions.add(i);
						if (m_tracer != null)
							m_tracer.addSubelement(activationElmt, "interaction", i + " intention " + i.getPostInteraction());
					}
				}
			}
		}
			
		// Activated interactions propose their post interactions 
		for (IInteraction i : activatedInteractions)
		{
			if (!i.getPrimitive())
			{
				IInteraction proposedInteraction = i.getPostInteraction();
				int w = i.getEnactionWeight() * proposedInteraction.getEnactionValue();
				IProposition p = new Proposition(proposedInteraction, w);
				
				int j = propositions.indexOf(p);
				if (j == -1)
					propositions.add(p);
				else
				{
					p = propositions.get(j);
					p.addWeight(w);
				}
				
				for (IInteraction alt : i.getAlternateInteractions())
				{
					p.addAlternateInteraction(alt);
				}
			}
		}
		
		// Transfer the weight of alternate interactions to their prominent interactions
		for (IProposition p : propositions)
		{
			for (IInteraction alt : p.getAlternateInteractions())
			{
				for (IInteraction act : activatedInteractions)
				{
					if (act.getPostInteraction().equals(alt))
					{
						int w = act.getEnactionWeight() * alt.getEnactionValue();
						p.addWeight(w);
						if (m_tracer != null)
						{
							m_tracer.addSubelement(consolidationElmt, "balance", p.getInteraction().getLabel() +  " weight  " + w/10 + " from " + alt.getLabel());						
						}
					}
				}
			}
		}
		
		// Trace the final propositions
		
		//System.out.println("Propose: ");
		Object proposalElmt = null;
		if (m_tracer != null)
		{
			proposalElmt = m_tracer.addSubelement(decisionElmt, "propositions");
		
			for (IProposition p : propositions)
			{
				System.out.println("proposition " + p);
				m_tracer.addSubelement(proposalElmt, "proposition", p.toString());
				for (IInteraction i : p.getAlternateInteractions())
				{
					m_tracer.addSubelement(consolidationElmt, "alternate", i.getLabel());						
				}
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
		//Object consolidationElmt = null;
		if (m_tracer != null)
		{
			selectionElmt = m_tracer.addEventElement("selection", true);
			//consolidationElmt = m_tracer.addSubelement(selectionElmt, "consolidation");
		}
		
//		// Transfer the weight of alternate interactions to their prominent interactions
//		for (IProposition interactionProposition : propositions)
//		{
//			for (IInteraction i : interactionProposition.getInteraction().getAlternateInteractions())
//			{
//				for (IProposition ip : propositions)
//				{
//					if (ip.getInteraction().equals(i))// && !ip.getTransferred())
//					{
//						interactionProposition.addWeight(ip.getWeight());
//						ip.setTransferred(true);
//						if (m_tracer != null)
//						{
//							m_tracer.addSubelement(consolidationElmt, "proposition", ip.getInteraction().getLabel() +  " transfers " + ip.getWeight()/10 + " to " + interactionProposition.getInteraction().getLabel());						
//						}
//					}
//				}
//			}
//		}
		
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
