package imos;



/**
 * A proposal that Ernest enacts a schema.  A proposal consists of 
 * the proposed schema and a weight that determines the strength of the proposal.
 * Schemas that receive the highest summarized proposal weight are selected for enaction. 
 * @author mcohen
 * @author ogeorgeon
 *
 */
public interface IProposition extends Comparable<IProposition> 
{
	/**
	 * @return The proposition's schema.
	 */
	public ISchema getSchema();
	
	/**
	 * @return The proposition's weight.
	 */
	public int getWeight();
	
	/**
	 * @return The act that this proposition expects to enact.
	 */
	public IAct getAct();
	
	/**
	 * @return The proposition's expectation.
	 */
	public int getExpectation();

	/**
	 * @param w The weight to add to the proposition.
	 * @param e The expectatin to add to the proposition.
	 * @param act The resulting expected act.
	 */
	public void update(int w, int e, IAct act);
		
	/**
	 * Two propositions are equal if they propose the same schema. 
	 */
	public boolean equals(Object o);
	
	public String toString();
}
