package eca;


import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import eca.construct.Area;
import eca.construct.AreaImpl;
import eca.construct.Aspect;
import eca.spas.Place;
import eca.spas.PlaceImpl;
import eca.ss.enaction.Act;
import eca.ss.enaction.ActImpl;
import tracing.ITracer;

/**
 * An Act Instance is an occurrence of the enaction of an interaction memorized in spatio-temporal memory.
 * @author Olivier
 */
public class ActInstanceImpl implements ActInstance 
{
	private Primitive primitive;
	private Place place;
	private int clock = 0;
	private Aspect aspect = Aspect.MOVE;
	private int modality;
	private boolean focus = false;
	
	/**
	 * Create a new place 
	 * (The provided position is cloned so the place can be moved without changing the provided position).
	 * @param primitive The interaction at this place.
	 * @param position This place's position.
	 */
	public ActInstanceImpl(Primitive primitive, Point3f position){
		this.primitive = primitive;
		this.place = new PlaceImpl(position);
	}
	
	public Act getAct() {
		return ActImpl.createOrGetPrimitiveAct(primitive, this.place.getArea());
	}

	public Primitive getPrimitive() {
		return this.primitive;
	}

	public Point3f getPosition() {
		return this.place.getPosition();
	}
	
	/**
	 * Clone an Act Instance
	 * @return The cloned Act Instance
	 */
	public ActInstance clone(){
		ActInstanceImpl clonePlace = null;
		try {
			clonePlace = (ActInstanceImpl) super.clone();
		} catch(CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}

		// We must clone the objects because they are passed by reference by default
		clonePlace.place = this.place.clone();
		//clonePlace.setPosition(this.position);
		//clonePlace.setOrientation(this.orientation);

		return clonePlace;
	}
	
	public void transform(Transform3D transform)
	{
		this.place.transform(transform);
	}		
	
//	public float getDirection() 
//	{
//		return this.place.getDirection();
//	}

	public float getDistance(){
		return this.place.getDistance();
	}

	public void setPosition(Point3f position){
		this.place.setPosition(position);
	}

	public int getClock(){
		return this.clock;
	}

	public float getOrientationAngle(){
		return this.place.getOrientationAngle();
	}
	
//	public int getValue(){
//		return this.aspect.getCode();
//	}

//	public void setOrientation(Vector3f orientation){
//		this.place.setOrientation(orientation);
//	}

//	public Vector3f getOrientation(){
//		return this.getOrientation();
//	}

	public void incClock(){
		this.place.fade();
		this.clock++;
	}

	/**
	 * Act instances are equal if they have the same primitive and place and clock
	 */
	public boolean equals(Object o){
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			ActInstance other = (ActInstance)o;
			//ret  = (this.getPrimitive().equals(other.getPrimitive()) 
			ret  = (this.getDisplayLabel().equals(other.getDisplayLabel()) 
					//&& this.position.epsilonEquals(other.getPosition(), .1f)
					&& this.place.equals(other.getPlace())
					&& (this.clock == other.getClock()));
			//ret = isInCell(other.getPosition()) && other.getClock() == getClock() && other.getType() == getType();
		}		
		return ret;
	}

	public boolean isInCell(Point3f position){		
		return this.place.isInCell(position);
	}
	
	public void normalize(float scale) {
		this.place.normalize(scale);
	}
	
	public Area getArea(){
		return this.place.getArea();
	}
	
	public String getDisplayLabel(){
		return this.primitive.getLabel();
	}

	public void trace(ITracer tracer, Object e) {
		
		Object p = tracer.addSubelement(e, "place");		
		tracer.addSubelement(p, "primitive", this.primitive.getLabel());
		tracer.addSubelement(p, "position", "(" + this.getPosition().x + "," + this.getPosition().y + ")");
		tracer.addSubelement(p, "area", AreaImpl.createOrGet(this.getPosition()).getLabel());
		tracer.addSubelement(p, "modality", this.modality + "");
		tracer.addSubelement(p, "aspect", this.aspect.toString());
	}

	public Aspect getAspect() {
		return aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}

	public int getModality() {
		return modality;
	}

	public void setModality(int modality) {
		this.modality = modality;
	}

	public Place getPlace() {
		return this.place;
	}

	public int getDisplayCode() {
		return this.aspect.getCode();
	}

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}

}
