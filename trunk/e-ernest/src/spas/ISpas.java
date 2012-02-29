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
	 * Processes the impact of the enacted interaction on the local space system.
	 * @param act The enacted act.
	 * @param visualCortex The set of visual stimulations in the visual cortex.
	 * @param tactileCortex The set of tactile stimulations in the tactile cortex.
	 * @param kinematicStimulation The kinematic stimulation.
	 * @param gustatoryStimulation The gustatory stimulation.
	 * @return An observation to compute the impact on the imos.
	 */
	public void step(IPlace interactionPlace, IObservation observation);

	/**
	 * Add a stimulation to static memory if it does not already exist.
	 * @param type The stimulation's type.
	 * @param value The stimulation's value.
	 * @return the new stimulation if created or the already existing stimulation.
	 */
	//public IStimulation addStimulation(int type, int value);
	
	/**
	 * Provide a rgb code to display the local space map in the environment.
	 * @param i x coordinate.
	 * @param j y coordinate.
	 * @return The value of the bundle in this place in local space memory.
	 */
	public int getValue(int i, int j);

	/**
	 * Provide a rgb code to display the the object of Ernest's attention in the environment.
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
	
	//public void addAffordance(IObservation initialObservation, IObservation finalObservation);

}
