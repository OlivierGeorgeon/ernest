package spas;

import imos2.IEnaction;
import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import ernest.ITracer;

/**
 * The spatial system.
 * Maintains the local space map and the persistence memory.
 * @author Olivier
 */
public interface ISpas 
{

	/**
	 * @param tracer The tracer
	 */
	public void setTracer(ITracer tracer);
	
	/**
	 * The main routine of the Spatial System that is called on each interaction cycle.
	 * @param enaction The current enaction.
	 */
	public void track(IEnaction enaction);

	/**
	 * Provide a rgb code to display the local space map in the environment.
	 * @param i x coordinate.
	 * @param j y coordinate.
	 * @return The value of the bundle in this place in local space memory.
	 */
	public int getValue(int i, int j);
	
	/**
	 * Return the identifier of an area to which a point belongs.
	 * @param point The point.
	 * @return The code of the area.
	 */
	public IArea categorizePosition(Point3f point);

	/**
	 * Provide a rgb code to display the object of Ernest's attention in the environment.
	 * @return The value of the focus bundle.
	 */
	//public int getAttention();

	/**
	 * Set the place list (from Simon's local space map).
	 * @param placeList The list of place in Ernest's local space memory.
	 */
	//public void setPlaceList(List<IPlace> placeList);

	/**
	 * @return The list of places in Ernest's local space memory.
	 */
	public ArrayList<IPlace> getPlaceList();
	
	/**
	 * @return The clock of spatial memory
	 */
	public int getClock();
	
	/**
	 * @param position This place's position.
	 * @param type This place's type.
	 * @return The crated place
	 */
	//public IPlace addPlace(Point3f position, int type);

	/**
	 * Tick the clock of spatial memory
	 */
	public void tick();
	
	/**
	 * Generate a list of places that represents the phenomena in the local space.
	 * @return the list of places. 
	 */
	//public ArrayList<IPlace> getPhenomena();
	
	//public boolean checkAct(IAct act);
	
	//public IBundle addBundle(IAct firstAct, IAct secondAct); 
	//public ArrayList<IBundle> evokeCompresences(IAct act);

	//public IBundle aggregateBundle(IBundle bundle, IAct act); 

	public int getValue(Point3f position);
	
	public ISpatialMemory getSpatialMemory();
	
	//public void simulatePrimitiveAct(IEnaction enaction);
	//public IActProposition runSimulation(IAct act);
	
	public String simulateShiftLeft();
	public String simulateShiftRight();
	public String simulateShiftForward();
	public IArea getArea(String areaLabel);
}
