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
	 * @return The proposition's weight.
	 */
	public int getWeight();
	
	/**
	 * @param w The weight to add to the proposition.
	 */
	public void addWeight(int w);
	
	public void setWeightedValue(int weightedValue);
	public int getWeightedValue();
		
		
	/**
	 * Two propositions are equal if they propose the same interaction. 
	 */
	public boolean equals(Object o);	
}
