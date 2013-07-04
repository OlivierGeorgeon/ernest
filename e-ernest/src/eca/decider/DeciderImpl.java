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
import eca.construct.egomem.Displacement;
import eca.construct.experiment.Experiment;
import eca.construct.experiment.ExperimentImpl;
import eca.spas.Spas;
import eca.ss.ActProposition;
import eca.ss.IImos;
import eca.ss.enaction.Act;
import eca.ss.enaction.Enaction;
import eca.ss.enaction.EnactionImpl;

/**
 * This is the regular decider for Ernest 7 that does not use spatial memory.
 * @author Olivier
 */
public class DeciderImpl implements Decider 
{
	private IImos imos;
	private Spas spas;
	private ITracer tracer;

	/**
	 * @param imos The sequential system
	 * @param spas The spatial system
	 */
	public DeciderImpl(IImos imos, Spas spas){
		this.imos = imos;
		this.spas = spas;
	}

	public void setTracer(ITracer tracer){
		this.tracer = tracer;
	}
	
	public Enaction decide(Enaction enaction) 
	{
		System.out.println("New decision ================ ");

		Enaction newEnaction = new EnactionImpl();		
		Appearance preAppearance = enaction.getAppearance();

		// Choose the next action
		
		ArrayList<ActProposition> actPropositions = this.imos.propose(enaction);	
		List<ActionProposition> actionPropositions = proposeActions(actPropositions, preAppearance);
		
		Collections.sort(actionPropositions, new ActionSSWeightComparator(ActionSSWeightComparator.SS) ); // or SPAS
		//Collections.sort(actionPropositions, new ActionSpasWeightComparator() );

		Action	selectedAction = actionPropositions.get(0).getAction();

		// Anticipate the consequences
		
		Act intendedAct = selectedAction.predictAct(preAppearance);
		Displacement intendedDisplacement = selectedAction.predictDisplacement(preAppearance);
		Appearance intendedPostAppearance = selectedAction.predictPostAppearance(preAppearance); 
		
		// Trace the decision
		
		if (this.tracer != null){
			Object decisionElmt = this.tracer.addEventElement("decision", true);
			
			this.tracer.addSubelement(decisionElmt, "selected_action", selectedAction.getLabel());

			Object aspectElmt = this.tracer.addSubelement(decisionElmt, "phenomena");
			for (PhenomenonType phenomenonType : PhenomenonTypeImpl.getPhenomenonTypes())
				this.tracer.addSubelement(aspectElmt, "phenomenon", phenomenonType.toString());
			
			Object experimentElmt = this.tracer.addSubelement(decisionElmt, "experiments");
			for (Experiment a : ExperimentImpl.getExperiments())
				this.tracer.addSubelement(experimentElmt, "experiment", a.toString());
			
			Object predictElmt = this.tracer.addSubelement(decisionElmt, "predict");
			this.tracer.addSubelement(predictElmt, "act", intendedAct.getLabel());
			this.tracer.addSubelement(predictElmt, "displacement", intendedDisplacement.getLabel());
			this.tracer.addSubelement(predictElmt, "postAppearance", intendedPostAppearance.getLabel());
		}		
		System.out.println("Select:" + selectedAction.getLabel());
		System.out.println("Act " + intendedAct.getLabel());
		
		// Prepare the new enaction.
		
		newEnaction.setAppearance(preAppearance);
		newEnaction.setTopIntendedAct(intendedAct);
		newEnaction.setTopRemainingAct(intendedAct);
		newEnaction.setPreviousLearningContext(enaction.getInitialLearningContext());
		newEnaction.setInitialLearningContext(enaction.getFinalLearningContext());
		
		return newEnaction;
	}
	
	/**
	 * Weight the actions according to the proposed interactions
	 */
	private List<ActionProposition> proposeActions(List<ActProposition> actPropositions, Appearance preAppearance){
		
		List<ActionProposition> actionPropositions = new ArrayList<ActionProposition>();
		
		// All actions are proposed with their spasWeight
		for (Action a : ActionImpl.getACTIONS()){
			Act intendedAct = a.predictAct(preAppearance);
			ActionProposition actionProposition = new ActionPropositionImpl(a, 0, intendedAct.getPrimitive().getValue());
			actionPropositions.add(actionProposition);
		}		
		
		for (ActProposition ap : actPropositions){
			if (ap.getAct().getPrimitive() != null){
				ActionProposition actionProposition = new ActionPropositionImpl(ap.getAct().getAction(), ap.getWeight(), 0);
				int j = actionPropositions.indexOf(actionProposition);
				if (j == -1)
					actionPropositions.add(actionProposition);
				else
				{
					ActionProposition previsousProposition = actionPropositions.get(j);
					previsousProposition.addSSWeight(actionProposition.getSSWeight());
				}
			}
		}
		
		//Collections.sort(actionPropositions);

		// trace weighted actions 
		Object decisionElmt = null;
		if (this.tracer != null){
			decisionElmt = this.tracer.addEventElement("propositions", true);
			for (ActionProposition a : actionPropositions){
				String details = " ";
				for (Primitive primitive : a.getAction().getPrimitives())
					details += (" " + primitive.getLabel());
				System.out.println("propose action " + a.getAction().getLabel() + " with weight " + a.getSSWeight());
				this.tracer.addSubelement(decisionElmt, "Action", a.getAction().getLabel() + " SS weight " + a.getSSWeight() + " Spas weight " + a.getSpasWeight() + " " + details);
			}
		}
		
		return actionPropositions;
	}
	
	public void carry(Enaction enaction)
	{
		Act intendedPrimitiveInteraction = enaction.getTopRemainingAct().prescribe();
		enaction.setIntendedPrimitiveAct(intendedPrimitiveInteraction);
		enaction.setStep(enaction.getStep() + 1);
		enaction.traceCarry(this.tracer);
	}
}
