package spas;

import imos.IAct;

import java.util.ArrayList;
import javax.vecmath.Vector3f;
import ernest.ITracer;

/**
 * A Spatial Memory is a memory of a spatial configuration
 * @author Olivier
 */
public interface ISpatialMemory 
{
	/**
	 * @return A clone of this spatial memory
	 */
	public ISpatialMemory clone();
	
	/**
	 * Simulates an act in spatial memory to check its consistency with the current state of spatial memory.
	 * TODO Create simulated phenomena to check for internal consistency of composite acts.
	 * @param act The act to simulate
	 * @param doubt consistency in case of doubt.
	 */
	public boolean simulate(IAct act, boolean doubt);
	
	/**
	 * Tick this spatial memory's clock (to compute decay)
	 */
	public void tick();
	
	/**
	 * Trace the content of this spatial memory
	 * @param tracer The tracer
	 */
	public void trace(ITracer tracer);
	
	/**
	 * Add a place in this spatial memory
	 * @param bundle A possible bundle attached to this place
	 * @param position This place's position
	 * @return The created place
	 */
	public IPlace addPlace(IBundle bundle, Vector3f position);

	/**
	 * Apply a geometrical transformation to this spatial memory
	 * @param translation The translation vector (opposite of the agent's movement)
	 * @param rotation The rotation value (opposite of the agent's rotation)
	 */
	public void followUp(Vector3f translation, float rotation);
	
	/**
	 * Get the first place found at a given position
	 * @param position The position
	 * @return The place
	 */
	public IPlace getPlace(Vector3f position);
	
	/**
	 * Remove places that are older than the decay laps
	 */
	public void clear();

	/**
	 * @return The list of places in this spatial memory
	 */
	public ArrayList<IPlace> getPlaceList();
	public void setPlaceList(ArrayList<IPlace> places);

	public int getValue(Vector3f position);

	/**
	 * Compute a compresence of places in this spatial memory
	 * @param observation
	 * @param spas
	 */
	public void copresence(IObservation observation, ISpas spas);

}
