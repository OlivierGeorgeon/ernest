package eca.decider;

import eca.construct.Action;

/**
 * A proposition to perform an action. 
 * @author ogeorgeon
 */
public class ActionPropositionImpl implements ActionProposition {

	private Action action;
	private int ssWeight = 0; 
	private int spasWeight = 0;
	
	/**
	 * Constructor. 
	 * @param a The proposed action.
	 * @param ssWeight The weight proposed by the ss.
	 * @param spasWeight The weight proposed by Spas.
	 */
	public ActionPropositionImpl(Action a, int ssWeight, int spasWeight){
		this.action = a;
		this.ssWeight = ssWeight;
		this.spasWeight = spasWeight;
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
	public String toString(){
		return action + " with weight = " + ssWeight/10;
	}

	public int getSpasWeight() {
		return this.spasWeight;
	}

	public void addSpasWeight(int spasWeight) {
		this.spasWeight = spasWeight;
	}
}
