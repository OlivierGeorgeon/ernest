package ernest;

/**
 * An activation consists of the activated act and a weight that determines the strength of the activation.
 * Primitive acts that receive the highest summarized activation weight are selected for enaction. 
 * @author ogeorgeon
 */
public interface IActivation extends Comparable<IActivation> 
{

	/**
	 * @return The activation's act.
	 */
	public IAct getAct();
	
	/**
	 * @return The activation's weight.
	 */
	public int getWeight();
	
	/**
	 * Adds weight to the activation
	 * @param w The weight to be added to the activation
	 */
	public void update(int w);

	/**
	 * Two activations are equal if they have the same noème.
	 */
	public boolean equals(Object o);
	
	/**
	 * @return A string representation of the activation for debug.
	 */
	public String toString();
}
