package imos2;

/**
 * A proposal that Ernest performs a move. 
 * @author ogeorgeon
 */
public interface IMoveProposition extends Comparable<IMoveProposition> 
{
	public String getMove();
	
	/**
	 * @return The proposition's schema.
	 */
	public IInteraction getInteraction();
	
	/**
	 * @return The proposition's weight.
	 */
	public int getWeight();
	
	/**
	 * @return The proposition's expectation.
	 */
	public int getExpectation();

	/**
	 * @param w The weight to add to the proposition.
	 * @param e The expectatin to add to the proposition.
	 */
	public void update(int w, int e, IInteraction i);
		
	/**
	 * Two propositions are equal if they propose the same schema. 
	 */
	public boolean equals(Object o);
	
	public String toString();
}
