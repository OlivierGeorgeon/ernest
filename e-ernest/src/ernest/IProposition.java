package ernest;

/**
 * Represents a proposal that ernest enacts and Act.  A proposal consists of 
 * the proposed act and a weight that determines the strength of the proposal.
 * Proposals with higher weight are given priority. 
 * @author mcohen
 *
 */
public interface IProposition extends Comparable<IProposition> 
{
	public int getWP();
	
	public IAct getAct();
		
	public boolean equals(Object o);
	
	public String toString();
}
