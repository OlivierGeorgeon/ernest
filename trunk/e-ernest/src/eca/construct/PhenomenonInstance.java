package eca.construct;

import tracing.ITracer;
import eca.spas.Placeable;

/**
 * An instance of phenomenon known to be present in the surrounding environment
 * @author Olivier
 */
public interface PhenomenonInstance extends Placeable {
	
	/**
	 * @return The primitive interaction
	 */
	public PhenomenonType getPhenomenonType();
	
	/**
	 * @param phenomenonType The type of this phenomenon 
	 */
	public void setPhenomenonType(PhenomenonType phenomenonType);	
	
	/**
	 * @param tracer The tracer
	 * @param e the xml element that contains the trace of this phenomenon instance
	 */
	public void trace(ITracer tracer, Object e);
}
