package eca.ss;

import eca.ss.enaction.Act;

/**
 * A proposition to enact an act. 
 * @author ogeorgeon
 */
public interface ActProposition extends Comparable<ActProposition> 
{
	/**
	 * @return The interaction proposed by this proposition.
	 */
	public Act getAct();
	
	/**
	 * @return The weight of the prosing act.
	 */
	public int getWeight();
	
	/**
	 * @param w The weight of the proposing act.
	 */
	public void addWeight(int w);
	
	/**
	 * The weighted value is needed to handles the propagation of the proposition weight to the sub act 
	 * @param weightedValue The weight of the proposing Act times the value of the proposed act
	 */
	public void setWeightedValue(int weightedValue);
	
	/**
	 * The weighted value is needed to handles the propagation of the proposition weight to the sub act 
	 * @return The weight of the proposing Act times the value of the proposed act
	 */
	public int getWeightedValue();
		
	/**
	 * Two propositions are equal if they propose the same interaction. 
	 */
	public boolean equals(Object o);	
}
