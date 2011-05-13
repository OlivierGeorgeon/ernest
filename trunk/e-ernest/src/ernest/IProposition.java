package ernest;

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
	public int getWeight();
	
	public void update(int w, int e);

	public int getExpectation();
	
	public ISchema getSchema();
	
	public IAct getAct();
		
	public boolean equals(Object o);
	
	public String toString();
}
