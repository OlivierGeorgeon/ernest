package ernest;

/**
 * Represents a proposal that Ernest enacts a schema.  A proposal consists of 
 * the proposed schema and a weight that determines the strength of the proposal.
 * Proposals with higher weight are given priority. 
 * @author mcohen
 * @author ogeorgeon
 *
 */
public interface IProposition extends Comparable<IProposition> 
{
	public int getWeight();
	
	public void addWeight(int w);
	
	public ISchema getSchema();
		
	public boolean equals(Object o);
	
	public String toString();
}
