package imos2;

import java.util.ArrayList;

/**
 * A proposition to enact an interaction. 
 * @author ogeorgeon
 */
public interface IProposition extends Comparable<IProposition> 
{
	/**
	 * @return The interaction proposed by this proposition.
	 */
	public IInteraction getInteraction();
	
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
	
	/**
	 * The angst depends on the values at stake and on how tight are the pros and cons
	 * @return The angst value associated with this choice
	 */
	public int getAngst();
	
	//public boolean addAlternateInteraction(IInteraction alternateInteraction);
	//public ArrayList<IInteraction> getAlternateInteractions();
	
	//public void setTransferred(boolean transferred);
	//public boolean getTransferred();
}
