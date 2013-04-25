package imos2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import ernest.IPrimitive;
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
	HashMap<String , IPrimitive> interactions;
	ITracer m_tracer;
	int maxSchemaLength = 10;

	/**
	 * @param imos The sequential system
	 * @param spas The spatial system
	 */
	public Decider(IImos imos, ISpas spas, HashMap<String , IPrimitive> interactions)
	{
		m_imos = imos;
		m_spas = spas;
		this.interactions = interactions;
	}

	public void setTracer(ITracer tracer)
	{
		m_tracer = tracer;
	}
	
	public void setMaxSchemaLength(int maxSchemaLength)
	{
		this.maxSchemaLength = maxSchemaLength;
	}

	public IEnaction decide(IEnaction enaction) 
	{
		IEnaction newEnaction = new Enaction();
		
		System.out.println("New decision ================ ");

		ArrayList<IProposition> actPropositions = proposeInteractions(enaction.getFinalActivationContext());
		IAct nextTopIntention = selectInteraction(actPropositions);
		
		newEnaction.setTopInteraction(nextTopIntention);
		newEnaction.setTopRemainingInteraction(nextTopIntention);
		newEnaction.setPreviousLearningContext(enaction.getInitialLearningContext());
		newEnaction.setInitialLearningContext(enaction.getFinalLearningContext());
		
		return newEnaction;
	}
	
	/**
	 * Construct a list of propositions based on the current context
	 * @param activationContext The list of interactions that form the current context.
	 * @return The list of propositions.
	 */
	protected ArrayList<IProposition> proposeInteractions(ArrayList<IAct> activationContext)
	{
		// The list of activated interactions
		ArrayList<IAct> activatedActs = new ArrayList<IAct>();	
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
		
		// Construct a default proposition to enact primitive interactions in area B
		for (IPrimitive i : this.interactions.values())
		{
			IAct a = this.m_imos.addAct(i.getLabel() + "B", i.getValue());
			IProposition p = new Proposition(a, 0);
			if (!propositions.contains(p))
				propositions.add(p);
		}
		
		// Construct the list of activated composite acts 
		for (IAct activatedAct : m_imos.getActs())
		{
			if (!activatedAct.getPrimitive())
			{
				// If this interaction's pre-interaction belongs to the context then this interaction is activated 
				for (IAct contextAct : activationContext)
				{
					if (activatedAct.getPreAct().equals(contextAct))
					{
						activatedActs.add(activatedAct);
						if (m_tracer != null)
							m_tracer.addSubelement(activationElmt, "ActivatedAct", activatedAct + "intention" + activatedAct.getPostAct());
					}
				}
			}
		}
			
		// Activated acts propose their post interactions 
		for (IAct i : activatedActs)
		{
			//if (!i.getPrimitive())
			//{
				IAct proposedAct = i.getPostAct();
				int w = i.getEnactionWeight() * proposedAct.getEnactionValue();
				IProposition proposition = null;
				
				if ((proposedAct.getEnactionWeight() > m_imos.getRegularityThreshold() ) &&						 
						(proposedAct.getLength() <= this.maxSchemaLength ))
				{
					proposition = new Proposition(proposedAct, w);
				}
				// if the intended act has not passed the threshold then  
				// the activation is propagated to the intended interaction's pre interaction
				else
				{
					if (!proposedAct.getPrimitive())
					{
						// only if the intention's intention is positive (this is some form of positive anticipation)
						if (proposedAct.getPostAct().getEnactionValue() > 0)
							proposition = new Proposition(proposedAct.getPreAct(), w);
					}
				}
				
				if (proposition!=null)
				{
					int j = propositions.indexOf(proposition);
					if (j == -1)
						propositions.add(proposition);
					else
					{
						proposition = propositions.get(j);
						proposition.addWeight(w);
					}
				}
			//}
		}
		
		// Transfer the weight of alternate interactions to the proposition of their prominant interaction
		for (IProposition proposition : propositions)
		{
			//for (IInteraction alt : p.getAlternateInteractions())
			for (IAct alt : proposition.getAct().getAlternateInteractions())
			{
				for (IAct act : activatedActs)
				{
					if (act.getPostAct().equals(alt))
					{
						int w = act.getEnactionWeight() * alt.getEnactionValue();
						if (m_tracer != null)
						{
							m_tracer.addSubelement(consolidationElmt, "proposition", proposition + " alternate " + alt.getLabel() +  " of weight  " + w/10);						
						}
						proposition.addWeight(w);
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
			}
		}
		
		return propositions;
	}
	
	/**
	 * Select an interaction from the list of proposed interactions
	 * @param propositions The list of propositions.
	 * @return The selected interaction.
	 */
	protected IAct selectInteraction(ArrayList<IProposition> propositions)
	{
		// Sort the propositions by weight.
		Collections.sort(propositions);
		
		// Pick the most weighted proposition
		IAct selectedInteraction = propositions.get(0).getAct();
		
		System.out.println("Select:" + selectedInteraction);

		// Trace the propositions
		if (m_tracer != null)
		{			
			Object selectionElmt = m_tracer.addEventElement("selection", true);
			m_tracer.addSubelement(selectionElmt, "selected_interaction", selectedInteraction.toString());
			//m_tracer.addSubelement(selectionElmt, "angst", "" + propositions.get(0).getAngst());
		}
		
		return selectedInteraction;
	}

	public void carry(IEnaction enaction)
	{
		IAct intendedPrimitiveInteraction = enaction.getTopRemainingInteraction().prescribe();
		enaction.setIntendedPrimitiveInteraction(intendedPrimitiveInteraction);
		enaction.setStep(enaction.getStep() + 1);
		enaction.traceCarry(m_tracer);
	}
}
