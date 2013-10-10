package imos;

/**
 * A proposition of in intention act.
 * The proposed act and the weight that determines the strength of the proposal.
 * the proposed schema and a weight that determines the strength of the proposal.
 * Act propositions are used to select the schema proposition
 * @author ogeorgeon
 */
public interface IActProposition extends Comparable<IActProposition> 
{
	/**
	 * @return The proposition's act.
	 */
	public IAct getAct();
			
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
	 * @param e The expectation of this proposition.
	 */
	public void update(int w, int e);

	/**
	 * Two propositions are equal if they propose the same act. 
	 */
	public boolean equals(Object o);
	
	/**
	 * The greatest proposal is that that has the greatest weight. 
	 * @return the order of comparison
	 */
	public String toString();
	
	public void setStatus(int status);
	public int getStatus();
}
