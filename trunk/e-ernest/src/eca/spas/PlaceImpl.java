package eca.spas;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import utils.ErnestUtils;
import eca.construct.egomem.Area;
import eca.construct.egomem.AreaImpl;

/**
 * A place in egocentric spatial memory where an ActInstance or a PhenomenonInstance is located.
 * @author Olivier
 */
public class PlaceImpl implements Place {

	private Point3f position = new Point3f();
	private Vector3f orientation = new Vector3f(1,0,0);	
	
	/**
	 * Create a new place 
	 * (The provided position is cloned so the place can be moved without changing the provided position).
	 * @param position This place's position.
	 */
	public PlaceImpl(Point3f position){
		this.position.set(position);
	}
	
	/**
	 * Clone a place
	 * Warning: the bundle and act that this place contain are not cloned 
	 * @return The cloned place
	 */
	public PlaceImpl clone(){
		PlaceImpl clonePlace = null;
		try {
			clonePlace = (PlaceImpl) super.clone();
		} catch(CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}

		// We must clone the objects because they are passed by reference by default
		clonePlace.setPosition(this.position);
		clonePlace.setOrientation(this.orientation);

		return clonePlace;
	}
	
	public void transform(Transform3D transform) {
		transform.transform(this.position);
		transform.transform(this.orientation);
	}

	public void setPosition(Point3f position) {
		// Create a new instance of the vector so it can be used to clone this place.
		this.position = new Point3f(position);
	}

	public Point3f getPosition() {
		return this.position;
	}

	public boolean isInCell(Point3f position) {
		boolean ret;
		// Is in the same cell.
		ret = (Math.round(this.position.x) == Math.round(position.x)) && (Math.round(this.position.y) == Math.round(position.y)); 
		
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
		
		return ret;		
	}

	public void setOrientation(Vector3f orientation) {
		this.orientation = new Vector3f(orientation);
	}

	public Vector3f getOrientation() {
		return this.orientation;
	}

	public float getDirection() {
		return ErnestUtils.polarAngle(new Vector3f(this.position));
	}

	public float getDistance() {
		return this.position.distance(new Point3f());
	}

	public float getOrientationAngle() {
		return ErnestUtils.polarAngle(this.orientation);
	}

	public void normalize(float scale) {
		float d = this.position.distance(new Point3f());
		if (d > 0) this.position.scale(scale / d);
	}

	public Area getArea() {
		return AreaImpl.createOrGet(position);
	}
	
	/**
	 * Places are equal if they are in the same position
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
			Place other = (Place)o;
			ret  = this.position.epsilonEquals(other.getPosition(), .1f);
		}		
		return ret;
	}

	public void fade() {
		this.setPosition(new Point3f(this.position.x * 1.1f, this.position.y * 1.1f, 0f));
	}

}
