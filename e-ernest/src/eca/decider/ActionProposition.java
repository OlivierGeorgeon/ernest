package eca.decider;

import eca.construct.Action;

/**
 * A proposition to perform an action. 
 * @author ogeorgeon
 */
public interface ActionProposition extends Comparable {
	/**
	 * @return The interaction proposed by this proposition.
	 */
	public Action getAction();
	
	/**
	 * @return The proposition's weight.
	 */
	public int getWeight();
	
	/**
	 * @param w The weight to add to the proposition.
	 */
	public void addWeight(int w);
		
	/**
	 * Two propositions are equal if they propose the same interaction. 
	 */
	public boolean equals(Object o);	
}
