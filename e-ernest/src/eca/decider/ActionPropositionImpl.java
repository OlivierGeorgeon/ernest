package eca.decider;

import eca.construct.Action;
import eca.construct.experiment.Experiment;
import eca.ss.Appearance;
import eca.ss.enaction.Act;

/**
 * A proposition to perform an action. 
 * @author ogeorgeon
 */
public class ActionPropositionImpl implements ActionProposition {

	private Action action;
	private int ssWeight = 0;
	private Experiment experiment = null;
	//private Appearance anticipatedAppearance = null;
	//private float confidence = 0;
	
	private Act ssAnticipatedAct = null;
	private int ssActWeight = 0;
	
	/**
	 * Constructor. 
	 * @param a The proposed action.
	 * @param ssWeight The weight proposed by the ss.
	 */
	public ActionPropositionImpl(Action a, int ssWeight){
		this.action = a;
		this.ssWeight = ssWeight;
	}

	public int compareTo(ActionProposition a){
		return new Integer(a.getSSWeight()).compareTo(ssWeight);
	}

	public Action getAction(){
		return action;
	}

	public int getSSWeight(){
		return ssWeight;
	}
	
	public void addSSWeight(int w){
		ssWeight += w;
	}

	/**
	 * Two propositions are equal if they propose the same action. 
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{		
			ActionPropositionImpl other = (ActionPropositionImpl)o;
			ret = other.getAction() == this.action;
		}
		
		return ret;
	}

	/**
	 * Generate a textual representation of the proposition for debug.
	 * @return A string that represents the proposition. 
	 */

//	public Appearance getAnticipatedAppearance() {
//		return anticipatedAppearance;
//	}
//
//	public void setAnticipatedAppearance(Appearance anticipatedAct) {
//		this.anticipatedAppearance = anticipatedAct;
//	}

	public int getSSActWeight() {
		return ssActWeight;
	}

	public void setSSActWeight(int ssActWeight) {
		this.ssActWeight = ssActWeight;
	}

	public Act getSpatialAnticipatedAct() {
		return ssAnticipatedAct;
	}

	public void setSpatialAnticipatedAct(Act ssAnticipatedAct) {
		this.ssAnticipatedAct = ssAnticipatedAct;
	}

	public String toString(){
		String proposition = "action " + this.getAction().getLabel();
		proposition += " weight " + this.getSSWeight() / 10;
		if (this.experiment != null)
			proposition += " exeperiment " + this.experiment.getLabel();
		if (this.getSpatialAnticipatedAct() != null){
			proposition += " ss_act " + this.getSpatialAnticipatedAct().getLabel();
			proposition += " ss_value " + this.getSpatialAnticipatedAct().getValue();					
		}				
		return proposition;
	}

//	public float getConfidence() {
//		return confidence;
//	}
//
//	public void setConfidence(float confidence) {
//		this.confidence = confidence;
//	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

}
