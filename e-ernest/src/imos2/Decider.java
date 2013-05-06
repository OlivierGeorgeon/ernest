package imos2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.vecmath.Point3f;
import ernest.ActionImpl;
import ernest.Action;
import ernest.Primitive;
import ernest.ITracer;
import ernest.Observation;
import ernest.PrimitiveImpl;
import spas.ISpas;
import spas.SimuImpl;

/**
 * This is the regular decider for Ernest 7 that does not use spatial memory.
 * @author Olivier
 */
public class Decider implements IDecider 
{
	private IImos imos;
	private ISpas spas;
	private ITracer tracer;

	/**
	 * @param imos The sequential system
	 * @param spas The spatial system
	 */
	public Decider(IImos imos, ISpas spas)
	{
		this.imos = imos;
		this.spas = spas;
	}

	public void setTracer(ITracer tracer)
	{
		this.tracer = tracer;
	}
	
	public IEnaction decide(IEnaction enaction) 
	{
		IEnaction newEnaction = new Enaction();
		
		System.out.println("New decision ================ ");

		weightActions(enaction);
		Action action = selectAction();
		Observation  observation = this.spas.predict(action);
		Primitive nextPrimitive = PrimitiveImpl.getInteraction(action, observation.getAspect());
		Act nextTopIntention = this.imos.addAct(nextPrimitive, observation.getArea());
				
		System.out.println("Act " + nextTopIntention.getLabel());
		
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
	private void weightActions(IEnaction enaction)
	{
		// The list of propositions proposed by the sequential system
		ArrayList<IProposition> propositions = this.imos.propose(enaction);	

		// Add the propositions to enact primitive interactions in the area of point (0,0,0).
		for (Primitive i : PrimitiveImpl.getINTERACTIONS())
		{
			Act a = this.imos.addAct(i, this.spas.categorizePosition(new Point3f()));
			IProposition p = new Proposition(a, 0);
			if (!propositions.contains(p))
				propositions.add(p);
		}
				
		// Compute the weight of each modality.
		for (Action m : ActionImpl.getACTIONS()){
			m.setPropositionWeight(0);
		}
		for (IProposition p: propositions){
			// TODO also propose actions made of composite interactions
			if (p.getAct().getPrimitive()){
				//this.spas.getAction(p.getAct().getInteraction()).addPropositionWeight(p.getWeight());	
				SimuImpl.getAction(p.getAct().getInteraction()).addPropositionWeight(p.getWeight());	
			}
		}
		
		// Return the list of weighted modalities 
		Object decisionElmt = null;
		if (this.tracer != null){
			decisionElmt = this.tracer.addEventElement("modalities", true);
		}
		for (Action a : ActionImpl.getACTIONS()){
			System.out.println("Propose modality " + a.getLabel() + " with weight " + a.getPropositionWeight());
			if (this.tracer != null)
				this.tracer.addSubelement(decisionElmt, "Modality", a.getLabel() + " proposition weight " + a.getPropositionWeight());
		}
	}
	
	/**
	 * Select an interaction from the list of proposed interactions
	 * @param propositions The list of propositions.
	 * @return The selected interaction.
	 */
	protected Action selectAction()
	{
		// Sort the propositions by weight.
		List<Action> actions = new ArrayList<Action>();
		// Oddly, i could not directly cast ACTIONS.values() to List<Action>
		for (Action a : ActionImpl.getACTIONS())
			actions.add(a);
		Collections.sort(actions);

		// Pick the most weighted modality
		Action selectedAction = actions.get(0);
		
		System.out.println("Select:" + selectedAction.getLabel());

		// Trace the selected interaction
		if (this.tracer != null){			
			Object selectionElmt = this.tracer.addEventElement("selection", true);
			this.tracer.addSubelement(selectionElmt, "selected_action", selectedAction.getLabel());
		}
		
		return selectedAction;
	}

	public void carry(IEnaction enaction)
	{
		Act intendedPrimitiveInteraction = enaction.getTopRemainingInteraction().prescribe();
		enaction.setIntendedPrimitiveInteraction(intendedPrimitiveInteraction);
		enaction.setStep(enaction.getStep() + 1);
		enaction.traceCarry(this.tracer);
	}
}
