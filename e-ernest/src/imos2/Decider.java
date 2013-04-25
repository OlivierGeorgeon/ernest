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
	IImos imos;
	ISpas spas;
	HashMap<String , IPrimitive> interactions;
	ITracer tracer;

	/**
	 * @param imos The sequential system
	 * @param spas The spatial system
	 * @param interactions The list of primitive interactions
	 */
	public Decider(IImos imos, ISpas spas, HashMap<String , IPrimitive> interactions)
	{
		this.imos = imos;
		this.spas = spas;
		this.interactions = interactions;
	}

	public void setTracer(ITracer tracer)
	{
		this.tracer = tracer;
	}
	
	public IEnaction decide(IEnaction enaction) 
	{
		IEnaction newEnaction = new Enaction();
		
		System.out.println("New decision ================ ");

		ArrayList<IProposition> propositions = proposeActs(enaction);
		IAct nextTopIntention = selectAct(propositions);
		
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
	protected ArrayList<IProposition> proposeActs(IEnaction enaction)
	{
		// The list of propositions proposed by the sequential system
		ArrayList<IProposition> propositions = this.imos.propose(enaction);	

		// Add the propositions to enact primitive interactions in area B
		for (IPrimitive i : this.interactions.values())
		{
			IAct a = this.imos.addAct(i.getLabel() + "B", i.getValue());
			IProposition p = new Proposition(a, 0);
			if (!propositions.contains(p))
				propositions.add(p);
		}
				
		
		// Prepare the tracer.
		Object decisionElmt = null;
		Object consolidationElmt = null;
		if (this.tracer != null)
		{
			decisionElmt = this.tracer.addEventElement("activation", true);
			consolidationElmt = this.tracer.addSubelement(decisionElmt, "consolidation");
		}

		// Transfer the weight of alternate acts to the proposition of their prominant acts
		for (IProposition proposition : propositions)
		{
			for (IAct alt : proposition.getAct().getAlternateActs())
			{
				for (IProposition alternateProposition : propositions)
				{
					if (alternateProposition.getAct().equals(alt))
					{
						int w = alternateProposition.getWeight();
						if (this.tracer != null)
						{
							this.tracer.addSubelement(consolidationElmt, "proposition", proposition + " alternate " + alt.getLabel() +  " of weight  " + w/10);						
						}
						proposition.addWeight(w);
					}
				}
			}
		}

		// Trace the final propositions
		
		//System.out.println("Propose: ");
		Object proposalElmt = null;
		if (this.tracer != null)
		{
			proposalElmt = this.tracer.addSubelement(decisionElmt, "propositions");
		
			for (IProposition p : propositions)
			{
				System.out.println("proposition " + p);
				this.tracer.addSubelement(proposalElmt, "proposition", p.toString());
			}
		}
		
		return propositions;
	}
	
	/**
	 * Select an interaction from the list of proposed interactions
	 * @param propositions The list of propositions.
	 * @return The selected interaction.
	 */
	protected IAct selectAct(ArrayList<IProposition> propositions)
	{
		// Sort the propositions by weight.
		Collections.sort(propositions);
		
		// Pick the most weighted proposition
		IAct selectedInteraction = propositions.get(0).getAct();
		
		System.out.println("Select:" + selectedInteraction);

		// Trace the propositions
		if (this.tracer != null)
		{			
			Object selectionElmt = this.tracer.addEventElement("selection", true);
			this.tracer.addSubelement(selectionElmt, "selected_interaction", selectedInteraction.toString());
			//m_tracer.addSubelement(selectionElmt, "angst", "" + propositions.get(0).getAngst());
		}
		
		return selectedInteraction;
	}

	public void carry(IEnaction enaction)
	{
		IAct intendedPrimitiveInteraction = enaction.getTopRemainingInteraction().prescribe();
		enaction.setIntendedPrimitiveInteraction(intendedPrimitiveInteraction);
		enaction.setStep(enaction.getStep() + 1);
		enaction.traceCarry(this.tracer);
	}
}
