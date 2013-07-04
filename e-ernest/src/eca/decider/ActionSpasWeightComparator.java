package eca.decider;

import java.util.Comparator;

/**
 * A comparator to sort Action Propositions by their descending spatial weight
 * @author Olivier
 */
public class ActionSpasWeightComparator implements Comparator<ActionProposition>{

    public int compare(ActionProposition p1, ActionProposition p2) {
        return - Integer.valueOf(p1.getSpasWeight()).compareTo(p2.getSpasWeight());
    }

}
