package spas;

import imos2.Act;
import imos2.IEnaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ernest.Action;
import ernest.Primitive;
import ernest.ITracer;
import ernest.Observation;

/**
 * The spatial system.
 * Maintains the local space map and the persistence memory.
 * @author Olivier
 */
public class Spas implements ISpas 
{
	
	/** The Tracer. */
	private ITracer m_tracer = null; 
	
	/** A list of all the bundles ever identified. */
	public List<IBundle> m_bundles = new ArrayList<IBundle>(10);
	
	/** Ernest's local space memory  */
	private ISpatialMemory m_localSpaceMemory = new LocalSpaceMemory();
	
	/** The simulator. */
	Simu simu = new SimuImpl();
	
	/** The transformation to apply to spatial memory */
	Transform3D transform = new Transform3D();
	
//	private static Spas INSTANCE = null; 
//	
//	private Spas() {
//		// TODO Auto-generated constructor stub
//	}
//	
//	public static Spas instance(){
//		if (INSTANCE == null)
//			INSTANCE = new Spas();
//		return INSTANCE;
//	}

	public void setTracer(ITracer tracer) {
		m_tracer = tracer;
	}
	
	public Area categorizePosition(Point3f point){
		return SimuImpl.getArea(point);
	}
	
	/**
	 * The main method of the Spatial System that is called on each interaction cycle.
	 * Track the spatial consequences of the current enaction.
	 * @param enaction The current enaction.
	 */
	public void track(IEnaction enaction) 
	{
		tick();
		if (enaction.getIntendedPrimitiveInteraction() != null)
			this.transform = SimuImpl.spasTransform(enaction.getIntendedPrimitiveInteraction().getPrimitive().getAction().getTransformation());

		if (enaction.getEnactedPrimitiveInteraction().getPrimitive().getAspect().equals(SimuImpl.EMPTY)){				
			if (enaction.getArea().equals(SimuImpl.O)){
				enaction.getEnactedPrimitiveInteraction().getPrimitive().setAspect(SimuImpl.NONE);
				if (m_tracer != null){
					m_tracer.addEventElement("phenomenon", "none");}
			}
			else{ 
				enaction.getEnactedPrimitiveInteraction().getPrimitive().setAspect(SimuImpl.ANYTHING); 
				if (m_tracer != null){
					m_tracer.addEventElement("phenomenon", "anything");}
			}
		}


		m_localSpaceMemory.transform(this.transform);		
		m_localSpaceMemory.forgetOldPlaces();
		
		if (enaction.getEffect().getLocation() != null && enaction.getEnactedPrimitiveInteraction() != null){
			addPlace(enaction.getEnactedPrimitiveInteraction(), SimuImpl.spasPoint(enaction.getArea()), enaction.getEffect().getColor());			
		}

		this.simu.track(enaction);

		if (m_tracer != null) this.simu.trace(m_tracer);
	}

	public int getValue(int i, int j)
	{
		Point3f position = new Point3f(1 - j, 1 - i, 0);
		if (m_localSpaceMemory != null)
			return m_localSpaceMemory.getDisplayCode(position);
		else
			return 0xFFFFFF;
	}
	
	public ArrayList<IPlace> getPlaceList()
	{
		// return m_localSpaceMemory.getPlaceList();
		return m_localSpaceMemory.clonePlaceList();
	}

	private IPlace addPlace(Act act, Point3f position,int value) 
	{
		IPlace place = m_localSpaceMemory.addPlace(act, position);
		place.setValue(value);
		return place;
	}

	public void tick() 
	{
		m_localSpaceMemory.tick();
	}

	public int getValue(Point3f position) 
	{
		return m_localSpaceMemory.getDisplayCode(position);
	}

	public ISpatialMemory getSpatialMemory()
	{
		return m_localSpaceMemory;
	}

	public Observation predict(Action action){
		return this.simu.predict(action);
	}

	public Transform3D getTransformToAnim() {
		return this.transform;
	}

}
