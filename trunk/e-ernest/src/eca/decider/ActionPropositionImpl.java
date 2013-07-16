package eca.decider;

import eca.construct.Action;
import eca.ss.enaction.Act;

/**
 * A proposition to perform an action. 
 * @author ogeorgeon
 */
public class ActionPropositionImpl implements ActionProposition {

	private Action action;
	private int ssWeight = 0; 
	private Act anticipatedAct = null;
	
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

	public Act getAnticipatedAct() {
		return anticipatedAct;
	}

	public void setAnticipatedAct(Act anticipatedAct) {
		this.anticipatedAct = anticipatedAct;
	}

	public int getSSActWeight() {
		return ssActWeight;
	}

	public void setSSActWeight(int ssActWeight) {
		this.ssActWeight = ssActWeight;
	}

	public Act getSSAnticipatedAct() {
		return ssAnticipatedAct;
	}

	public void setSSAnticipatedAct(Act ssAnticipatedAct) {
		this.ssAnticipatedAct = ssAnticipatedAct;
	}

	public String toString(){
		String proposition = "action " + this.getAction().getLabel();
		proposition += " weight " + this.getSSWeight();
		proposition += " spas_act " + this.getAnticipatedAct().getLabel();
		proposition += " spas_value " + this.getAnticipatedAct().getValue();
		if (this.getSSAnticipatedAct() != null){
			proposition += " ss_act " + this.getSSAnticipatedAct().getLabel();
			proposition += " ss_value " + this.getSSAnticipatedAct().getValue();					
		}				
		return proposition;
	}
}
