package eca.decider;

import eca.construct.Action;

/**
 * A proposition to perform an action. 
 * @author ogeorgeon
 */
public interface ActionProposition extends Comparable<ActionProposition> {
	/**
	 * @return The interaction proposed by this proposition.
	 */
	public Action getAction();
	
	/**
	 * @return The proposition's weight according to the Spatial System.
	 */
	public int getSSWeight();
	
	/**
	 * @param ssWeight The weight to add to the proposition.
	 */
	public void addSSWeight(int ssWeight);
		
	/**
	 * @return The proposition's weight according to the Spatial System.
	 */
	public int getSpasWeight();
	
	/**
	 * @param spasWeight The weight to add to the proposition.
	 */
	public void addSpasWeight(int spasWeight);
		
	/**
	 * Two propositions are equal if they propose the same action. 
	 */
	public boolean equals(Object o);	
}