package eca.decider;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.vecmath.Point3f;
import tracing.ITracer;
import utils.ErnestUtils;
import eca.construct.Action;
import eca.construct.ActionImpl;
import eca.construct.Appearance;
import eca.construct.AppearanceImpl;
import eca.construct.Displacement;
import eca.construct.DisplacementImpl;
import eca.construct.PhenomenonInstance;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.construct.egomem.Area;
import eca.construct.egomem.AreaImpl;
import eca.construct.experiment.Experiment;
import eca.construct.experiment.ExperimentImpl;
import eca.spas.Spas;
import eca.ss.ActProposition;
import eca.ss.ActPropositionImpl;
import eca.ss.IImos;
import eca.ss.enaction.Act;
import eca.ss.enaction.ActImpl;
import eca.ss.enaction.Enaction;
import eca.ss.enaction.EnactionImpl;

/**
 * This is the regular decider for Ernest 7 that does not use spatial memory.
 * @author Olivier
 */
public class DeciderImpl implements Decider 
{
	/** Regularity sensibility threshold (The weight threshold for an act to become reliable). */
	private int regularityThreshold = 6;
	
	/** The maximal length of acts. */
	private int maxSchemaLength = 10;

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
	
	public void setRegularityThreshold(int regularityThreshold)
	{
		this.regularityThreshold = regularityThreshold;
	}
	
	public void setMaxSchemaLength(int maxSchemaLength)
	{
		this.maxSchemaLength = maxSchemaLength;
	}
	
	public Enaction decide(Enaction enaction) 
	{
		System.out.println("New decision ================ ");
		
		Appearance appearance = enaction.getAppearance();

		Enaction newEnaction = new EnactionImpl();	
		
		//PhenomenonInstance focusPhenomenonInstance = this.spas.getFocusPhenomenonInstance();
		
		//Area preArea = AreaImpl.createOrGet(new Point3f());
		//preAppearance = AppearanceImpl.createOrGet(PhenomenonType.EMPTY, preArea);
		
		//if (focusPhenomenonInstance != null){
		//	preArea = focusPhenomenonInstance.getArea();
		//	preAppearance = AppearanceImpl.createOrGet(focusPhenomenonInstance.getPhenomenonType(), preArea);
		//}

		// Choose the next action
		
		ArrayList<ActProposition> actPropositions = this.imos.propose(enaction);	
		List<ActionProposition> actionPropositions = proposeActions(actPropositions, appearance);
		
		Collections.sort(actionPropositions, new ActionPropositionComparator(ActionPropositionComparator.SS) ); // or SPAS

		ActionProposition selectedProposition = actionPropositions.get(0);
		Action	selectedAction = selectedProposition.getAction();
		Act intendedAct = selectedAction.getActs().get(0);
		//Experiment experiment = selectedProposition.getExperiment();
		
		// Trace the decision
		
		if (this.tracer != null){
			Object decisionElmt = this.tracer.addEventElement("decision", true);
			
			Object apElmnt = this.tracer.addSubelement(decisionElmt, "selected_proposition");
			this.tracer.addSubelement(apElmnt, "action", selectedProposition.getAction().getLabel());
			//if (focusPhenomenonInstance != null)
			//	focusPhenomenonInstance.trace(this.tracer, apElmnt);
			this.tracer.addSubelement(apElmnt, "weight", selectedProposition.getSSWeight() + "");
			//this.tracer.addSubelement(apElmnt, "spas_value", selecteProposition.getAnticipatedAct().getValue() +"");
			if (selectedProposition.getSpatialAnticipatedAct() != null){
				this.tracer.addSubelement(apElmnt, "ss_act", selectedProposition.getSpatialAnticipatedAct().getLabel());
				this.tracer.addSubelement(apElmnt, "ss_value", selectedProposition.getSpatialAnticipatedAct().getValue() +"");					
			}				

			Object actionElmt = this.tracer.addSubelement(decisionElmt, "actions");
			for (Action action : ActionImpl.getACTIONS())
				action.trace(tracer, actionElmt);
			
			Object appearanceElmt = this.tracer.addSubelement(decisionElmt, "observations");
			for (Appearance app : AppearanceImpl.getAppearances())
				app.trace(tracer, appearanceElmt);
			
//			Object phenomenonElmt = this.tracer.addSubelement(decisionElmt, "phenomenon_types");
//			for (PhenomenonType phenomenonType : PhenomenonTypeImpl.getPhenomenonTypes())
//				phenomenonType.trace(tracer, phenomenonElmt);
			
			//Object experimentElmt = this.tracer.addSubelement(decisionElmt, "experiments");
			//for (Experiment a : ExperimentImpl.getExperiments())
			//	if (a.isTested())
			//		this.tracer.addSubelement(experimentElmt, "experiment", a.toString());
			

			Object predictElmt = this.tracer.addSubelement(decisionElmt, "predict");
			this.tracer.addSubelement(predictElmt, "act", intendedAct.getLabel());
			//if (experiment != null)
			//	experiment.trace(tracer, predictElmt);
			//if (intendedDisplacement != null)
			//	this.tracer.addSubelement(predictElmt, "displacement", intendedDisplacement.getLabel());
			//this.tracer.addSubelement(predictElmt, "postAppearance", intendedPostAppearance.getLabel());
		}		
		System.out.println("Select:" + selectedAction.getLabel());
		System.out.println("Act " + intendedAct.getLabel());
		
		// Prepare the new enaction.
		
		//newEnaction.setPhenomenonInstance(phenomenonInstance);
		newEnaction.setTopIntendedAct(intendedAct);
		newEnaction.setTopRemainingAct(intendedAct);
		newEnaction.setPreviousLearningContext(enaction.getInitialLearningContext());
		newEnaction.setInitialLearningContext(enaction.getFinalLearningContext());
		newEnaction.setIntendedAction(selectedAction);
		newEnaction.setAppearance(appearance);
		//newEnaction.setExperiment(experiment);
		//newEnaction.setInitialArea(preArea);
		
		return newEnaction;
	}
	
	/**
	 * Weight the actions according to the proposed interactions
	 */
	private List<ActionProposition> proposeActions(List<ActProposition> actPropositions, Appearance preAppearance){
		
		List<ActionProposition> actionPropositions = new ArrayList<ActionProposition>();
		List<ActProposition> forwardedActPropositions = new ArrayList<ActProposition>();
		
		// Create actions and appearances for the proposed acts that do not have them yet.
		for (ActProposition actProposition : actPropositions){
			Act proposedAct = actProposition.getAct();
			if (proposedAct.getWeight() > this.regularityThreshold){
				if (proposedAct.getLength() <= this.maxSchemaLength){
					if (ActionImpl.getAction(proposedAct) == null){
						if (proposedAct.isPrimitive()){
							Action a = ActionImpl.createOrGet(proposedAct);
							a.addAct(proposedAct);
							if (this.tracer != null) this.tracer.addEventElement("new_action", a.getLabel());							
						}
						else{
							// Check the reliability of this sequence
							boolean reliable = true;
							for (Act act : ActImpl.getACTS())
								if (proposedAct.getPreAct().equals(act.getPreAct()))
									if (ActionImpl.getAction(proposedAct.getPostAct()).equals(ActionImpl.getAction(act.getPostAct())))
									//if (proposedAct.getPostAct().isPrimitive() && act.getPostAct().isPrimitive()) 
										if(!proposedAct.getPostAct().equals(act.getPostAct())){
											reliable = false;
											if (this.tracer != null) this.tracer.addEventElement("inconsistent_sequence", proposedAct.getLabel() + " due to " + act.getLabel());
										}
								
							if (reliable){
								// Create the action
								Action a = ActionImpl.createOrGet(proposedAct);
								a.addAct(proposedAct);
								if (this.tracer != null) this.tracer.addEventElement("new_action", a.getLabel());
							
								// Create the appearance
								if (proposedAct.getPreAct().isPrimitive())
									proposedAct.getPreAct().getPrimitive().setDisplacement(DisplacementImpl.DISPLACEMENT_STILL);
								Appearance appearance = AppearanceImpl.createOrGet(proposedAct.getPreAct());
								//appearance.addAct(proposedAct);
								appearance.addAct(proposedAct.getPreAct());
								if (this.tracer != null) this.tracer.addEventElement("new_appearance", appearance.getLabel());							
							}
						}
					}			
				}
			}
			else{
				// add a proposition for the context sub act
				if(proposedAct.getPostAct().getEnactionValue() > 0)
				{
					ActProposition proposition = new ActPropositionImpl(proposedAct.getPreAct(), actProposition.getWeight());
					proposition.setWeightedValue(proposedAct.getValue() * actProposition.getWeight());
					forwardedActPropositions.add(proposition);
				}
			}
		}
		
		// Add propositions for the context sub-acts of acts that did not pass the threshold
		for (ActProposition proposition : forwardedActPropositions)
			actPropositions.add(proposition);
		
		// For each existing action, propose it according to act propositions coming from IMOS
		for (Action action : ActionImpl.getACTIONS()){
			// All Actions are proposed with their anticipated Act predicted on the basis of the preAppearance
			//Appearance anticipatedAppearance = action.predictPostAppearance(preAppearance); // proposition based on spatial representation
			
			//Appearance anticipatedAppearance = AppearanceImpl.evoke(action.getActs().get(0)); 
			
			ActionProposition actionProposition = new ActionPropositionImpl(action, 0);
			//actionProposition.setAnticipatedAppearance(anticipatedAppearance);
			//actionProposition.setConfidence(confidence);
			if (preAppearance != null){
				for (Act act : preAppearance.getActs()){
					
				}
				//actionProposition.setExperiment(ExperimentImpl.createOrGet(preAppearance, action));
			}
			boolean isProposed = false;
			// Add weight to this action according to the actPropositions that propose an act whose primitive belongs to this action
			for (ActProposition actProposition : actPropositions){
				if (action.contains(actProposition.getAct())){
					if (actionProposition.getSSActWeight() <= actProposition.getWeight()){
						actionProposition.setSpatialAnticipatedAct(actProposition.getAct());
						actionProposition.setSSActWeight(actProposition.getWeight());
					}
					actionProposition.addSSWeight(actProposition.getWeightedValue());
					isProposed = true;
				}
			}
			if (action.getActs().get(0).isPrimitive() || isProposed)	
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
