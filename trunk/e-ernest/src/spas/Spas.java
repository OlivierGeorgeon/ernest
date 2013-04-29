package spas;

import imos2.IAct;
import imos2.IEnaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ernest.IPrimitive;
import ernest.ITracer;

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

	/** The area manager. */
	IAreaManager areaManager = new AreaManager();

	public void setTracer(ITracer tracer) 
	{
		m_tracer = tracer;
	}
	
	public IArea categorizePosition(Point3f point)
	{
		return this.areaManager.categorize(point);
	}

	/**
	 * The main method of the Spatial System that is called on each interaction cycle.
	 * Maintain the local space memory.
	 * Construct compresences.
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
		
		this.areaManager.clearAll();
		if (enaction.getEffect().equals("*") || 
			enaction.getEffect().equals("+") ||
			enaction.getEffect().equals("=") || 
			enaction.getEffect().equals("-")){
			enaction.getArea().setOccupied(true);
		}				
		
		if (m_tracer != null) enaction.traceSpace(m_tracer);
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

	private IPlace addPlace(Point3f position, int type, int value, IAct act) 
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

	public String simulateShiftLeft() {
		return this.areaManager.simulateShiftLef();
	}

	public String simulateShiftRight() {
		return this.areaManager.simulateShiftRight();
	}
	public String simulateShiftForward(){
		return this.areaManager.simulateShiftForward();
	}
	public IArea getArea(String areaLabel) {
		return this.areaManager.getArea(areaLabel);
	}


}
