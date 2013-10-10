package eca.decider;

import java.util.Comparator;

/**
 * A comparator to sort Action Propositions by their weight 
 * (propositions with highest weights come first) 
 * @author Olivier
 */
public class ActionPropositionComparator implements Comparator<ActionProposition>{
	
	/** Sort according to the Sequential System weight */
	public static int SS = 0;

	/** Sort according to the Spatial System weight */
	public static int SPAS = 1;
	
	private int system = 0;

	ActionPropositionComparator(int system){
		this.system = system;
	}
	
    public int compare(ActionProposition p1, ActionProposition p2) {
    	if (this.system == SS)
    		return - Integer.valueOf(p1.getSSWeight()).compareTo(p2.getSSWeight());
    	else
            return - Integer.valueOf(p1.getAnticipatedAct().getPrimitive().getValue()).compareTo(p2.getAnticipatedAct().getPrimitive().getValue());
    }
}
