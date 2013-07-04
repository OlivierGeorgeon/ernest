package eca.decider;

import java.util.Comparator;

/**
 * A comparator to sort Action Propositions by their descending SS weight
 * @author Olivier
 */
public class ActionSSWeightComparator implements Comparator<ActionProposition>{
	
	public static int SS = 0;
	public static int SPAS = 1;
	
	private int system = 0;

	ActionSSWeightComparator(int system){
		this.system = system;
	}
	
    public int compare(ActionProposition p1, ActionProposition p2) {
    	if (this.system == SS)
    		return - Integer.valueOf(p1.getSSWeight()).compareTo(p2.getSSWeight());
    	else
            return - Integer.valueOf(p1.getSpasWeight()).compareTo(p2.getSpasWeight());
    }
}
