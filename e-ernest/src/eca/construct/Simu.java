package eca.construct;

import tracing.ITracer;
import eca.enaction.Enaction;

/**
 * 
 * @author Olivier
 */
public interface Simu {
	
	/**
	 * Updates the simulator according to the last enacted act
	 * @param act The last enacted act
	 */
	public void track(Enaction enaction);
	
	/**
	 * Predict the observation resulting from an action.
	 * @param action The action to simulate
	 * @return the predicted observation
	 */
	public Observation predict(Action action);
	
	/**
	 * Trace the current state of the simulator
	 * @param tracer The tracer
	 */
	public void trace(ITracer tracer);

}
