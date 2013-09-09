package eca;


import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import eca.construct.Area;
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
	//private Point3f position = new Point3f();
	//private Vector3f orientation = new Vector3f(1,0,0);
	private Place place;
	private int clock = 0;
	private Aspect aspect = Aspect.MOVE;
	private int modality;
	
	/**
	 * Create a new place 
	 * (The provided position is cloned so the place can be moved without changing the provided position).
	 * @param primitive The interaction at this place.
	 * @param position This place's position.
	 */
	public ActInstanceImpl(Primitive primitive, Point3f position){
		this.primitive = primitive;
		this.place = new PlaceImpl(position);
		//this.position.set(position);
	}
	
	public Act getAct() {
		//return ActImpl.createOrGetPrimitiveAct(primitive, AreaImpl.createOrGet(position));
		return ActImpl.createOrGetPrimitiveAct(primitive, this.place.getArea());
	}

	public Primitive getPrimitive() {
		return this.primitive;
	}

	public Point3f getPosition() {
		return this.place.getPosition();
//		return this.position;
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
		//transform.transform(this.position);
		//transform.transform(this.orientation);
	}		
	
	public float getDirection() 
	{
		return this.place.getDirection();
		//return ErnestUtils.polarAngle(new Vector3f(this.position));
	}

	public float getDistance() 
	{
		return this.place.getDistance();
		//return this.position.distance(new Point3f());
	}
	public void setPosition(Point3f position) 
	{
		this.place.setPosition(position);
		// Create a new instance of the vector so it can be used to clone this place.
		//this.position = new Point3f(position);
	}

//	public void setClock(int clock) 
//	{
//		this.clock = clock;
//	}

	public int getClock(){
		return this.clock;
	}

//	public void setOrientation(float orientation) 
//	{
//		this.orientation.set((float) Math.cos(orientation), (float) Math.sin(orientation), 0);
//	}

	public float getOrientationAngle() 
	{
		return this.place.getOrientationAngle();
		//return ErnestUtils.polarAngle(this.orientation);
	}
	
//	public void setValue(int value) 
//	{
//		this.displayCode = value;
//	}

	public int getValue() 
	{
		return this.aspect.getCode();
		//return this.displayCode;
	}

	public void setOrientation(Vector3f orientation) 
	{
		this.place.setOrientation(orientation);
		// Create a new instance of the vector so it can be used to clone this place.
		//this.orientation = new Vector3f(orientation);
	}

	public Vector3f getOrientation() 
	{
		return this.getOrientation();
		//return orientation;
	}

	public void incClock() 
	{
		this.place.fade();
		//this.setPosition(new Point3f(this.position.x * 1.1f, this.position.y * 1.1f, 0f));
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
		
//		boolean ret;
		// Is in the same cell.
//		ret = (Math.round(this.position.x) == Math.round(position.x)) && (Math.round(this.position.y) == Math.round(position.y)); 
		
		// Is in the same cell in egocentric polar referential.
		
		// Does not work for the cell behind !!
//		if (m_position.length() < .5f && position.length() < .5f)
//			ret = true;
//		else if (Math.round(ErnestUtils.polarAngle(m_position) / (float)Math.PI * 4) ==
// 			     Math.round(ErnestUtils.polarAngle(  position) / (float)Math.PI * 4) &&
// 			     (Math.round(m_position.length()) == Math.round(position.length())))
//			ret = true;
//		else 
//			ret = false;
		
//		return ret;		
	}
	
	public void normalize(float scale) {
		
		this.place.normalize(scale);
		
		//float d = this.position.distance(new Point3f());
		//if (d > 0) this.position.scale(scale / d);
	}
	
	public Area getArea(){
		return this.place.getArea();
		//return AreaImpl.createOrGet(position);
	}
	
	public String getDisplayLabel(){
		return this.primitive.getLabel();
	}

	public void trace(ITracer tracer, Object e) {
		
		Object p = tracer.addSubelement(e, "place");		
		tracer.addSubelement(p, "primitive", this.primitive.getLabel());
		tracer.addSubelement(p, "position", "(" + this.place.getPosition().x + "," + this.place.getPosition().y + ")");
		//tracer.addSubelement(p, "position", "(" + this.position.x + "," + this.position.y + ")");
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

}
