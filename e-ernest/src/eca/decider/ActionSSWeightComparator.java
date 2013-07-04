package eca.decider;

import java.util.Comparator;

/**
 * A comparator to sort Action Propositions by their descending SS weight
 * @author Olivier
 */
public class ActionSSWeightComparator implements Comparator<ActionProposition>{

    public int compare(ActionProposition p1, ActionProposition p2) {
        return - Integer.valueOf(p1.getSSWeight()).compareTo(p2.getSSWeight());
    }

}
