package eca.construct;

import javax.vecmath.Point3f;

import eca.spas.Place;
import eca.spas.PlaceImpl;
import eca.spas.Place;

/**
 * An instance of phenomenon known to be present in the surrounding environment
 * @author Olivier
 */
public class PhenomenonInstanceImpl implements PhenomenonInstance {

	private PhenomenonType phenomenonType = null;
	//private ActInstance actInstance = null;
	private Place place;
	
	public PhenomenonInstanceImpl(PhenomenonType phenomenonType, Point3f position){
	//public PhenomenonInstanceImpl(PhenomenonType phenomenonType, ActInstance actInstance){
		this.phenomenonType = phenomenonType;
		//this.actInstance = actInstance;
		place = new PlaceImpl(position);
	}
	
	public PhenomenonType getPhenomenonType() {
		return this.phenomenonType;
	}

	//public ActInstance getPlace() {
	public Place getPlace() {
		//return this.actInstance;
		return this.place;
	}

	public void setPhenomenonType(PhenomenonType phenomenonType) {
		this.phenomenonType = phenomenonType;
	}
	
	public String toString(){
		return ("Type " + this.phenomenonType.getLabel() + " in area " + place.getArea().getLabel()); 
	}

}
