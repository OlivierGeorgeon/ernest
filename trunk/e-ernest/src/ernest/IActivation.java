package ernest;

/**
 * Represents an activation of a schema 
 * An activation is made of:
 * - The activated schema 
 * - Its intention act and a weight that represents the tendency or the apprehension to enact this act
 *    (activated schema's weight * intention act's satisfaction) 
 * @author ogeorgeon
 *
 */
public interface IActivation extends Comparable<IActivation> 
{
	public ISchema getSchema();
	
	public IAct getIntention();
		
	public int getWeight();
	
	public boolean equals(Object o);
	
	public String toString();
}
