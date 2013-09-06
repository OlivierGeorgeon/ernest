package eca.construct;

import eca.spas.egomem.ActInstance;

/**
 * An instance of phenomenon known to be present in the surrounding environment
 * @author Olivier
 */
public class PhenomenonInstanceImpl implements PhenomenonInstance {

	private PhenomenonType phenomenonType = null;
	private ActInstance actInstance = null;
	
	public PhenomenonInstanceImpl(PhenomenonType phenomenonType, ActInstance actInstance){
		this.phenomenonType = phenomenonType;
		this.actInstance = actInstance;
	}
	
	public PhenomenonType getPhenomenonType() {
		return this.phenomenonType;
	}

	public ActInstance getPlace() {
		return this.actInstance;
	}

	public void setPhenomenonType(PhenomenonType phenomenonType) {
		this.phenomenonType = phenomenonType;
	}
	
	public String toString(){
		return ("Type " + this.phenomenonType.getLabel() + " in area " + actInstance.getArea().getLabel()); 
	}

}
