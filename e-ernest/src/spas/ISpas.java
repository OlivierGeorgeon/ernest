package spas;

import imos.IAct;

import java.util.ArrayList;

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
	public IObservation step(IAct act, IStimulation[] visualCortex, IStimulation[] tactileCortex, IStimulation kinematicStimulation, IStimulation gustatoryStimulation);

	/**
	 * Add a stimulation to static memory if it does not already exist.
	 * @param type The stimulation's type.
	 * @param value The stimulation's value.
	 * @return the new stimulation if created or the already existing stimulation.
	 */
	public IStimulation addStimulation(int type, int value);
	
	/**
	 * Provide a rgb code to display the local space map in the environment.
	 * @param i x coordinate.
	 * @param j y coordinate.
	 * @return The value of the bundle in this place in local space memory.
	 */
	public int getValue(int i, int j);

	/**
	 * Set the salience list (from Simon's local space map).
	 * @param salienceList The list of salience in Ernest's colliculus.
	 */
	public void setSalienceList(ArrayList<ISalience> salienceList);

}
