package eca;

import eca.spas.egomem.Place;

/**
 * An instance of Primitive interaction that was enacted
 * @author Olivier
 */
public interface PrimitiveInstance {
	
	/**
	 * @return The primitive interaction
	 */
	public Primitive getPrimitive();
	
	/**
	 * @return The place
	 */
	public Place getPlace();
	
	/**
	 * @return the age in number of clock ticks.
	 */
	public int getAge();

}
