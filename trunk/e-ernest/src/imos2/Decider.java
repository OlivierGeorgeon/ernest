package imos2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.vecmath.Point3f;
import ernest.IInteractionCategorizer;
import ernest.IModality;
import ernest.IPrimitive;
import ernest.ITracer;
import ernest.InteractionCategorizer;
import spas.ISpas;

/**
 * This is the regular decider for Ernest 7 that does not use spatial memory.
 * @author Olivier
 */
public class Decider implements IDecider 
{
	private IImos imos;
	private ISpas spas;
	private Map<String , IPrimitive> interactions;
	private ITracer tracer;
	private IInteractionCategorizer interactionCategorizer = new InteractionCategorizer();

	/**
	 * @param imos The sequential system
	 * @param spas The spatial system
	 * @param interactions2 The list of primitive interactions
	 */
	public Decider(IImos imos, ISpas spas, Map<String, IPrimitive> interactions)
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

		List<IModality> modalities = proposeModalities(enaction);
		String nextModality = selectInteraction(modalities);
		
		IAct nextTopIntention = predictAct(nextModality);
		System.out.println("Act " + nextTopIntention.getLabel());
		
		//IAct nextTopIntention = this.imos.addAct(nextPrimitive, this.spas.categorizePosition(new Point3f()));
		
		newEnaction.setTopInteraction(nextTopIntention);
		newEnaction.setTopRemainingInteraction(nextTopIntention);
		newEnaction.setPreviousLearningContext(enaction.getInitialLearningContext());
		newEnaction.setInitialLearningContext(enaction.getFinalLearningContext());
		
		return newEnaction;
	}
	
	private IAct predictAct(String nextModality){
		String actLabel ="  ";
		if (nextModality.equals(">")){
			actLabel = this.spas.simulateShiftForward();
		}
		if (nextModality.equals("^")){
			actLabel = this.spas.simulateShiftRight();
		}
		if (nextModality.equals("v")){
			actLabel = this.spas.simulateShiftLeft();
		}
		
		IPrimitive nextPrimitive = this.interactions.get(nextModality + actLabel.substring(0, 1));
				
		return this.imos.addAct(nextPrimitive, this.spas.getArea(actLabel.substring(1, 2)));
		
	}
	
	/**
	 * Construct a list of propositions based on the current context
	 * @param activationContext The list of interactions that form the current context.
	 * @return The list of propositions.
	 */
	protected List<IModality> proposeModalities(IEnaction enaction)
	{
		// The list of propositions proposed by the sequential system
		ArrayList<IProposition> propositions = this.imos.propose(enaction);	

		// Add the propositions to enact primitive interactions in the area of point (0,0,0).
		for (IPrimitive i : this.interactions.values())
		{
			IAct a = this.imos.addAct(i, this.spas.categorizePosition(new Point3f()));
			IProposition p = new Proposition(a, 0);
			if (!propositions.contains(p))
				propositions.add(p);
		}
				
		// Compute the weight of each modality.
		for (IModality m : this.interactionCategorizer.getModalities().values()){
			m.setPropositionWeight(0);
		}
		for (IProposition p: propositions){
			// TODO also propose modalities made of composite interactions
			if (p.getAct().getPrimitive()){
				this.interactionCategorizer.categorize(p.getAct().getInteraction()).addPropositionWeight(p.getWeight());	
			}
		}
		
		// Return the list of weighted modalities 
		Object decisionElmt = null;
		if (this.tracer != null){
			decisionElmt = this.tracer.addEventElement("modalities", true);
		}
		List<IModality> modalities = new ArrayList<IModality>();
		for (IModality m : this.interactionCategorizer.getModalities().values()){
			System.out.println("Propose modality " + m.getLabel() + " with weight " + m.getPropositionWeight());
			modalities.add(m);
			if (this.tracer != null)
				this.tracer.addSubelement(decisionElmt, "Modality", m.getLabel() + " proposition weight " + m.getPropositionWeight());
		}
		
		return modalities;		
	}
	
	/**
	 * Select an interaction from the list of proposed interactions
	 * @param propositions The list of propositions.
	 * @return The selected interaction.
	 */
	protected String selectInteraction(List<IModality> modalities)
	{
		// Sort the propositions by weight.
		Collections.sort(modalities);

		// Pick the most weighted modality
		String selectedInteraction = modalities.get(0).getPrototypeInteraction().getLabel().substring(0, 1);
		
		System.out.println("Select:" + selectedInteraction);

		// Trace the selected interaction
		if (this.tracer != null){			
			Object selectionElmt = this.tracer.addEventElement("selection", true);
			this.tracer.addSubelement(selectionElmt, "selected_interaction", selectedInteraction);
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
