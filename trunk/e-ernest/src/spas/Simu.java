package spas;

import javax.vecmath.Point3f;
import ernest.Action;
import ernest.ITracer;
import ernest.Observation;
import ernest.Primitive;

/**
 * 
 * @author Olivier
 */
public interface Simu {
	
	public Observation predict(Action action);
	
	//public Action getAction(Primitive interaction);

	public void trace(ITracer tracer);


}
