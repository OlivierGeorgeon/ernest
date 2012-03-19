package spas;

import imos.IAct;

import java.util.ArrayList;
import java.util.List;

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
	 * @param interactionPlace The place where the ongoing interaction started.
	 * @param observation The current observation.
	 */
	public void step(IPlace interactionPlace, IObservation observation);

	/**
	 * Provide a rgb code to display the local space map in the environment.
	 * @param i x coordinate.
	 * @param j y coordinate.
	 * @return The value of the bundle in this place in local space memory.
	 */
	public int getValue(int i, int j);

	/**
	 * Provide a rgb code to display the object of Ernest's attention in the environment.
	 * @return The value of the focus bundle.
	 */
	public int getAttention();

	/**
	 * Set the place list (from Simon's local space map).
	 * @param placeList The list of place in Ernest's local space memory.
	 */
	public void setPlaceList(List<IPlace> placeList);

	/**
	 * @return The list of places in Ernest's local space memory.
	 */
	public ArrayList<IPlace> getPlaceList();
	
	public void count();
	public void traceLocalSpace();
	public void setSegmentList(ArrayList<ISegment> segmentList);
	
	public IPlace getFocusPlace();
	
	/**
	 * @param place The place to add in local space memory.
	 */
	public IPlace addPlace(Vector3f position, int type, int shape);
	
}
