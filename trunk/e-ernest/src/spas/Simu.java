package spas;

import imos2.IEnaction;
import ernest.Action;
import ernest.ITracer;
import ernest.Observation;

/**
 * 
 * @author Olivier
 */
public interface Simu {
	
	/**
	 * @param act The last enacted act
	 */
	public void track(IEnaction enaction);
	
	/**
	 * @param action The action to simulate
	 * @return the predicted observation
	 */
	public Observation predict(Action action);
	
	/**
	 * Trace the current state of the simulator
	 * @param tracer
	 */
	public void trace(ITracer tracer);


}
