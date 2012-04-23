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
	public void step(IObservation observation);//, ArrayList<IPlace> places);

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
	//public void setPlaceList(List<IPlace> placeList);

	/**
	 * @return The list of places in Ernest's local space memory.
	 */
	public ArrayList<IPlace> getPlaceList();
	
	public int getClock();
	public void traceLocalSpace();
	
	public IPlace getFocusPlace();
	
	/**
	 * @param place The place to add in local space memory.
	 */
	public IPlace addPlace(Vector3f position, int type, int shape);
	public IPlace addPlace(IBundle bundle, Vector3f position);
	
	//public IPlace addOrReplacePlace(IBundle bundle, Vector3f position);
	
	public IBundle seeBundle(int value);
	
	//public IPlace seePlace(float direction);
	
	public IBundle addBundle(int visualValue, int tactileValue);
	
	public void tick();
	
	/**
	 * Generate a list of places that represents the phenomena in the local space.
	 * @return the list of places. 
	 */
	public ArrayList<IPlace> getPhenomena();
	
	public boolean checkAct(IAct act);
	
	public IBundle addBundle(int value);
	public IBundle addBundle(IAct act);
	public IBundle evokeBundle(IAct act);
	public void aggregateBundle(IBundle bundle, IAct act); 

	public int getValue(Vector3f position);
	public IPlace getPlace(Vector3f position);
	
	public void initSimulation();
	public void translateSimulation(Vector3f translation);
	public void rotateSimulation(float angle);
	public int getValueSimulation(Vector3f position);
	public IBundle getBundleSimulation(Vector3f position);

}
