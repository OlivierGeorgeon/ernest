package eca.spas.egomem;

import eca.construct.PhenomenonType;

/**
 * An instance of phenomenon known to be present in the surrounding environment
 * @author Olivier
 */
public class PhenomenonInstanceImpl implements PhenomenonInstance {

	private PhenomenonType phenomenonType = null;
	private Place place = null;
	
	public PhenomenonInstanceImpl(PhenomenonType phenomenonType, Place place){
		this.phenomenonType = phenomenonType;
		this.place = place;
	}
	
	public PhenomenonType getPhenomenonType() {
		return this.phenomenonType;
	}

	public Place getPlace() {
		return this.place;
	}

}
