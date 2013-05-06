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
	
	/** The clock of the spatial system. (updated on each update cycle as opposed to IMOS) */
	private int m_clock;

	/** A list of all the bundles ever identified. */
	public List<IBundle> m_bundles = new ArrayList<IBundle>(10);
	
	/** Ernest's local space memory  */
	private ISpatialMemory m_localSpaceMemory = new LocalSpaceMemory();
	
	/** The transformation used to keep track of simulation. */
	Transform3D m_transform = new Transform3D();

	/** The simulator. */
	Simu simu = new SimuImpl();
	
	/** The area manager. */
	//AreaManager areaManager = new AreaManager();
	
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
	
//	public Action getAction(Primitive interaction){
//		return this.simu.getAction(interaction);
//	}


	/**
	 * The main method of the Spatial System that is called on each interaction cycle.
	 * Track the spatial consequences of the current enaction.
	 * @param enaction The current enaction.
	 */
	public void track(IEnaction enaction) 
	{
		tick();
		
		m_localSpaceMemory.transform(enaction.getEffect().getTransformation());		
		m_localSpaceMemory.decay();
		
		if (enaction.getEffect().getLocation() != null && enaction.getEnactedPrimitiveInteraction() != null){
			addPlace(enaction.getEffect().getLocation(), Place.ENACTION_PLACE, enaction.getEffect().getColor(), enaction.getEnactedPrimitiveInteraction());			
		}
		if (enaction.getEnactedPrimitiveInteraction() != null)
			this.simu.track(enaction.getEnactedPrimitiveInteraction());
		//this.simu.track(enaction);
		if (m_tracer != null) this.simu.trace(m_tracer);
	}

	public int getValue(int i, int j)
	{
		Point3f position = new Point3f(1 - j, 1 - i, 0);
		if (m_localSpaceMemory != null)
			return m_localSpaceMemory.getValue(position);
		else
			return 0xFFFFFF;
	}
	
	public ArrayList<IPlace> getPlaceList()
	{
		return m_localSpaceMemory.getPlaceList();
	}

	private IPlace addPlace(Point3f position, int type, int value, Act act) 
	{
		IPlace place = m_localSpaceMemory.addPlace(act, position);
		place.setValue(value);
		//place.setAct(act);
//		place.setType(type);
		//place.setClock(m_clock);
//		place.setClock(0);
		
		return place;
	}

	public int getClock() 
	{
		return m_clock;
	}

	public void tick() 
	{
		m_clock++;
		m_localSpaceMemory.tick();
	}

	public int getValue(Point3f position) 
	{
		return m_localSpaceMemory.getValue(position);
	}

	public ISpatialMemory getSpatialMemory()
	{
		return m_localSpaceMemory;
	}

	public Observation predict(Action action){
		return this.simu.predict(action);
	}

}
