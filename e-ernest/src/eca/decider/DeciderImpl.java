package eca.decider;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tracing.ITracer;
import eca.construct.Action;
import eca.construct.ActionImpl;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.construct.egomem.Displacement;
import eca.construct.experiment.Experiment;
import eca.construct.experiment.ExperimentImpl;
import eca.spas.Spas;
import eca.spas.egomem.PhenomenonInstance;
import eca.ss.ActProposition;
import eca.ss.Appearance;
import eca.ss.AppearanceImpl;
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
		PhenomenonInstance phenomenonInstance = enaction.getPhenomenonInstance();
		Appearance	preAppearance = AppearanceImpl.createOrGet(phenomenonInstance.getPhenomenonType(), phenomenonInstance.getPlace().getArea());
			

		// Choose the next action
		
		ArrayList<ActProposition> actPropositions = this.imos.propose(enaction);	
		List<ActionProposition> actionPropositions = proposeActions(actPropositions, preAppearance);
		
		Collections.sort(actionPropositions, new ActionPropositionComparator(ActionPropositionComparator.SS) ); // or SPAS

		ActionProposition selecteProposition = actionPropositions.get(0);
		Action	selectedAction = selecteProposition.getAction();
		Act intendedAct = selecteProposition.getSSAnticipatedAct();	
		if (intendedAct == null)
			intendedAct = selecteProposition.getAnticipatedAct();		

		// Anticipate the consequences
		
		Displacement intendedDisplacement = selectedAction.predictDisplacement(preAppearance);
		Appearance intendedPostAppearance = selectedAction.predictPostAppearance(preAppearance); 
		
		// Trace the decision
		
		if (this.tracer != null){
			Object decisionElmt = this.tracer.addEventElement("decision", true);
			
			Object apElmnt = this.tracer.addSubelement(decisionElmt, "selected_proposition");
			this.tracer.addSubelement(apElmnt, "action", selecteProposition.getAction().getLabel());
			this.tracer.addSubelement(apElmnt, "weight", selecteProposition.getSSWeight() + "");
			this.tracer.addSubelement(apElmnt, "spas_act", selecteProposition.getAnticipatedAct().getLabel());
			this.tracer.addSubelement(apElmnt, "spas_value", selecteProposition.getAnticipatedAct().getValue() +"");
			if (selecteProposition.getSSAnticipatedAct() != null){
				this.tracer.addSubelement(apElmnt, "ss_act", selecteProposition.getSSAnticipatedAct().getLabel());
				this.tracer.addSubelement(apElmnt, "ss_value", selecteProposition.getSSAnticipatedAct().getValue() +"");					
			}				

			Object actionElmt = this.tracer.addSubelement(decisionElmt, "actions");
			for (Action action : ActionImpl.getACTIONS())
				this.tracer.addSubelement(actionElmt, "action", action.toString());
			
			Object phenomenonElmt = this.tracer.addSubelement(decisionElmt, "phenomena");
			for (PhenomenonType phenomenonType : PhenomenonTypeImpl.getPhenomenonTypes())
				this.tracer.addSubelement(phenomenonElmt, "phenomenon", phenomenonType.toString());
			
			//Object experimentElmt = this.tracer.addSubelement(decisionElmt, "experiments");
			//for (Experiment a : ExperimentImpl.getExperiments())
			//	if (a.isTested())
			//		this.tracer.addSubelement(experimentElmt, "experiment", a.toString());
			
			Object predictElmt = this.tracer.addSubelement(decisionElmt, "predict");
			this.tracer.addSubelement(predictElmt, "act", intendedAct.getLabel());
			this.tracer.addSubelement(predictElmt, "displacement", intendedDisplacement.getLabel());
			//this.tracer.addSubelement(predictElmt, "postAppearance", intendedPostAppearance.getLabel());
		}		
		System.out.println("Select:" + selectedAction.getLabel());
		System.out.println("Act " + intendedAct.getLabel());
		
		// Prepare the new enaction.
		
		newEnaction.setPhenomenonInstance(phenomenonInstance);
		newEnaction.setTopIntendedAct(intendedAct);
		newEnaction.setTopRemainingAct(intendedAct);
		newEnaction.setPreviousLearningContext(enaction.getInitialLearningContext());
		newEnaction.setInitialLearningContext(enaction.getFinalLearningContext());
		newEnaction.setIntendedAction(selectedAction);
		
		return newEnaction;
	}
	
	/**
	 * Weight the actions according to the proposed interactions
	 */
	private List<ActionProposition> proposeActions(List<ActProposition> actPropositions, Appearance preAppearance){
		
		List<ActionProposition> actionPropositions = new ArrayList<ActionProposition>();
		
		for (Action action : ActionImpl.getACTIONS()){
			// All Actions are proposed with their anticipated Act predicted on the basis of the preAppearance
			Act anticipatedAct = action.predictAct(preAppearance);
			ActionProposition actionProposition = new ActionPropositionImpl(action, 0);
			actionProposition.setAnticipatedAct(anticipatedAct);

			// Add weight to this action according that the actPropositions that propose an act whose primitive belongs to this action
			for (ActProposition actProposition : actPropositions){
				if (action.contains(actProposition.getAct().getPrimitive())){
					if (actionProposition.getSSActWeight() <= actProposition.getWeight()){
						actionProposition.setSSAnticipatedAct(actProposition.getAct());
						actionProposition.setSSActWeight(actProposition.getWeight());
					}
					actionProposition.addSSWeight(actProposition.getWeightedValue());
				}
			}
			actionPropositions.add(actionProposition);
		}
		
		// trace action propositions 
		Object decisionElmt = null;
		if (this.tracer != null){
			decisionElmt = this.tracer.addEventElement("actionPropositions", true);
			for (ActionProposition ap : actionPropositions){
				System.out.println("propose action " + ap.getAction().getLabel() + " with weight " + ap.getSSWeight());
				this.tracer.addSubelement(decisionElmt, "proposition", ap.toString());
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
