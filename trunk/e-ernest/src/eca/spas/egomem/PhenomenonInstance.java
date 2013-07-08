package eca.spas.egomem;

import eca.construct.PhenomenonType;

/**
 * An instance of phenomenon known to be present in the surrounding environment
 * @author Olivier
 */
public interface PhenomenonInstance {
	/**
	 * @return The primitive interaction
	 */
	public PhenomenonType getPhenomenonType();
	
	/**
	 * @return The place
	 */
	public Place getPlace();
	
}
