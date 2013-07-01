package eca.construct;

import tracing.ITracer;
import eca.ss.enaction.Enaction;

/**
 * 
 * @author Olivier
 */
public interface Simu {
	
	/**
	 * Updates the simulator according to the last enacted act
	 * @param enaction The current enaction.
	 */
	//public void track(Enaction enaction);
	
	/**
	 * Predict the layout resulting from an action.
	 * @param action The action to simulate
	 * @return the predicted layout
	 */
	//public Layout predict(Action action);
	
	/**
	 * Trace the current state of the simulator
	 * @param tracer The tracer
	 */
	//public void trace(ITracer tracer);

}
