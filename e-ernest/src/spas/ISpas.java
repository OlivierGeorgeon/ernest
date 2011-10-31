package spas;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import ernest.ITracer;
import spas.IBundle;
import spas.IStimulation;
import imos.IAct;


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
	 * Tick Ernest's clock.
	 * Used to simulate a decay in Static memory
	 */
	public void tick();
	
	/**
	 * The anticipated observation is reset equal to the previous observation. 
	 */
	public void resetAnticipation();
	
	/**
	 * Generate the anticipated observation from the previous observation and the current intention.
	 * @param act The act whose effects we want to anticipate.
	 * @return A pointer to the anticipated observation.
	 */
	public IObservation anticipate(IAct act);
	
	/**
	 * @return The anticipated observation
	 */
	public IObservation getAnticipation();

	/**
	 * Add a stimulation to static memory if it does not already exist
	 * @param red Component of the landmark's color
	 * @param green Component of the landmark's color
	 * @param blue Component of the landmark's color
	 * @param distance The distance of the stimulation (or intensity)
	 * @return the new stimulation if created or the already existing landmark
	 */
//	public IStimulation addStimulation(int red, int green, int blue, Vector3f position);
	
	/**
	 * Add a stimulation to static memory if it does not already exist
	 * @param type The stimulation's type
	 * @param value The stimulation's value
	 * @return the new landmark if created or the already existing landmark
	 */
	public IStimulation addStimulation(int type, int value);
	
	/**
	 * Update the current Observation based on the anticipated observation and on to the sensory stimulations.
	 * @param visualCortex The set of visual stimulations in the visual cortex.
	 * @param tactileCortex The set of tactile stimulations in the tactile cortex.
	 * @param kinematicStimulation The kinematic stimulation.
	 * @param gustatoryStimulation The gustatory stimulation.
	 * @return A pointer to the current observation that has been updated.
	 */
	public IObservation adjust(IStimulation[] visualCortex, IStimulation[][] tactileCortex, IStimulation kinematicStimulation, IStimulation gustatoryStimulation);

	/**
	 * @param salienceList The list of salience in Ernest's colliculus.
	 */
	public void setSalienceList(ArrayList<ISalience> salienceList);

}
