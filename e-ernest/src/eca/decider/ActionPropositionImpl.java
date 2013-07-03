package eca.decider;

import eca.construct.Action;

/**
 * A proposition to perform an action. 
 * @author ogeorgeon
 */
public class ActionPropositionImpl implements ActionProposition {

	private Action action;
	private int weight = 0; 
	
	/**
	 * Constructor. 
	 * @param a The proposed action.
	 * @param w The proposal's weight.
	 */
	public ActionPropositionImpl(Action a, int w){
		this.action = a;
		this.weight = w;
	}

	public int compareTo(Object o){
		ActionProposition a = (ActionProposition)o;
		return new Integer(a.getWeight()).compareTo(weight);
	}

	public Action getAction(){
		return action;
	}

	public int getWeight(){
		return weight;
	}
	
	public void addWeight(int w){
		weight += w;
	}

	/**
	 * Two propositions are equal if they propose the same interaction. 
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
		return action + " with weight = " + weight/10;
	}
}
