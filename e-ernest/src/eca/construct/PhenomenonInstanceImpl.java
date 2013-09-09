package eca.construct;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import tracing.ITracer;
import eca.spas.Place;
import eca.spas.PlaceImpl;

/**
 * An instance of phenomenon known to be present in the surrounding environment
 * @author Olivier
 */
public class PhenomenonInstanceImpl implements PhenomenonInstance {

	private PhenomenonType phenomenonType = null;
	private Place place;
	
	/**
	 * @param phenomenonType The type of this phenomenon instance.
	 * @param position The position.
	 */
	public PhenomenonInstanceImpl(PhenomenonType phenomenonType, Point3f position){
		this.phenomenonType = phenomenonType;
		place = new PlaceImpl(position);
	}
	
	/**
	 * Clone A phenomenon Instance
	 * @return The cloned phenomenon Instance
	 */
	public PhenomenonInstanceImpl clone(){
		PhenomenonInstanceImpl clonePlace = null;
		try {
			clonePlace = (PhenomenonInstanceImpl) super.clone();
		} catch(CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		// We must clone the objects because they are passed by reference by default
		clonePlace.place = this.place.clone();
		return clonePlace;
	}

	public PhenomenonType getPhenomenonType() {
		return this.phenomenonType;
	}

	public Place getPlace() {
		return this.place;
	}
	
	public void transform(Transform3D transform){
		this.place.transform(transform);
	}
	

	public void setPhenomenonType(PhenomenonType phenomenonType) {
		this.phenomenonType = phenomenonType;
	}
	
	public String toString(){
		return ("Type " + this.phenomenonType.getLabel() + " in area " + place.getArea().getLabel()); 
	}

	public Area getArea() {
		return this.place.getArea();
	}

	public void setPosition(Point3f position) {
		this.place.setPosition(position);
	}

	public Point3f getPosition() {
		return this.place.getPosition();
	}

	public int getDisplayCode() {
		return this.phenomenonType.getAspect().getCode();
	}

	public int getClock() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getDisplayLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getOrientationAngle() {
		return this.place.getOrientationAngle();
	}

	public void incClock() {
		// TODO Auto-generated method stub
		
	}

	public boolean isInCell(Point3f position) {
		// TODO Auto-generated method stub
		return false;
	}

	public float getDistance() 
	{
		return this.place.getDistance();
	}
	
	public void trace(ITracer tracer, Object e) {
		
		Object p = tracer.addSubelement(e, "phenomenon_instance");		
		this.phenomenonType.trace(tracer, p);
		tracer.addSubelement(p, "position", "(" + this.place.getPosition().x + "," + this.place.getPosition().y + ")");
	}
}
