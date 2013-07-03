package eca.decider;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tracing.ITracer;
import eca.Primitive;
import eca.construct.Action;
import eca.construct.ActionImpl;
import eca.construct.Appearance;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.construct.experiment.Experiment;
import eca.construct.experiment.ExperimentImpl;
import eca.spas.Spas;
import eca.ss.IImos;
import eca.ss.enaction.Act;
import eca.ss.enaction.ActImpl;
import eca.ss.enaction.Enaction;
import eca.ss.enaction.EnactionImpl;

/**
 * This is the regular decider for Ernest 7 that does not use spatial memory.
 * @author Olivier
 */
public class Decider implements IDecider 
{
	private IImos imos;
	private Spas spas;
	private ITracer tracer;

	/**
	 * @param imos The sequential system
	 * @param spas The spatial system
	 */
	public Decider(IImos imos, Spas spas){
		this.imos = imos;
		this.spas = spas;
	}

	public void setTracer(ITracer tracer){
		this.tracer = tracer;
	}
	
	public Enaction decide(Enaction enaction) 
	{
		Enaction newEnaction = new EnactionImpl();
		
		System.out.println("New decision ================ ");

		// Choose the next action
		ArrayList<IProposition> propositions = this.imos.propose(enaction);	
		weightActions(propositions);
		Action action = selectAction();

		// Predict the next appearance
		Appearance  appearance = this.spas.predictAppearance(action);
		
		// Construct the intended act
		Act nextTopIntention = ActImpl.getAct(action, appearance);

		if (this.tracer != null){
			Object aspectElmt = this.tracer.addEventElement("aspects", true);
			for (PhenomenonType phenomenonType : PhenomenonTypeImpl.getPhenomenonTypes())
				this.tracer.addSubelement(aspectElmt, "phenomenon", phenomenonType.toString());
			Object experimentElmt = this.tracer.addEventElement("experiments", true);
			for (Experiment a : ExperimentImpl.getExperiments())
				this.tracer.addSubelement(experimentElmt, "experiment", a.toString());
			
			Object predictElmt = this.tracer.addEventElement("predict", true);
			this.tracer.addSubelement(predictElmt, "phenomenon_type", appearance.getPhenomenon().getLabel());
			this.tracer.addSubelement(predictElmt, "area", appearance.getArea().getLabel());
		}
		
		System.out.println("Act " + nextTopIntention.getLabel());
		newEnaction.setTopInteraction(nextTopIntention);
		newEnaction.setTopRemainingAct(nextTopIntention);
		newEnaction.setPreviousLearningContext(enaction.getInitialLearningContext());
		newEnaction.setInitialLearningContext(enaction.getFinalLearningContext());
		
		return newEnaction;
	}
	
	/**
	 * Weight the actions according to the proposed interactions
	 */
	private void weightActions(ArrayList<IProposition> propositions){
		
		// Reset the weight of actions.
		for (Action m : ActionImpl.getACTIONS())
			m.setPropositionWeight(0);
		
		// Proposed interactions that correspond to an identified action support this action.
		for (IProposition p: propositions)
			if (p.getAct().getPrimitive() != null)
				p.getAct().getAction().addPropositionWeight(p.getWeight());	
		
		// trace weighted actions 
		Object decisionElmt = null;
		if (this.tracer != null){
			decisionElmt = this.tracer.addEventElement("Actions", true);
			for (Action a : ActionImpl.getACTIONS()){
				String details = " ";
				for (Primitive primitive : a.getPrimitives())
					details += (" " + primitive.getLabel());
				System.out.println("Propose action " + a.getLabel() + " with weight " + a.getPropositionWeight());
				this.tracer.addSubelement(decisionElmt, "Action", a.getLabel() + " proposition weight " + a.getPropositionWeight() + " " + details);
			}
		}
	}
	
	/**
	 * Select an interaction from the list of proposed interactions
	 * @return The selected action.
	 */
	protected Action selectAction()
	{
		// Sort the propositions by weight.
		// Oddly, i could not directly cast ACTIONS.values() to List<Action>
		List<Action> actions = new ArrayList<Action>();
		for (Action a : ActionImpl.getACTIONS())
			actions.add(a);
		Collections.sort(actions);

		// Pick the most weighted action
		Action selectedAction = actions.get(0);
		
		System.out.println("Select:" + selectedAction.getLabel());

		// Trace the selected interaction
		if (this.tracer != null){			
			Object selectionElmt = this.tracer.addEventElement("selection", true);
			this.tracer.addSubelement(selectionElmt, "selected_action", selectedAction.getLabel());
		}
		
		return selectedAction;
	}

	public void carry(Enaction enaction)
	{
		Act intendedPrimitiveInteraction = enaction.getTopRemainingAct().prescribe();
		enaction.setIntendedPrimitiveAct(intendedPrimitiveInteraction);
		enaction.setStep(enaction.getStep() + 1);
		enaction.traceCarry(this.tracer);
	}
}
