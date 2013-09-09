package eca;

import tracing.ITracer;
import eca.construct.Aspect;
import eca.spas.Placeable;
import eca.ss.enaction.Act;

/**
 * An Act Instance is an occurrence of the enaction of an interaction memorized in spatio-temporal memory.
 * @author Olivier
 */
public interface ActInstance extends Placeable
{	
	
	public static final int MODALITY_MOVE = 0;
	public static final int MODALITY_BUMP = 1;
	public static final int MODALITY_VISION = 2;
	public static final int MODALITY_CONSUME = 3;
	
	/**
	 * @return This Act Instance's primitive interaction.
	 */
	public Primitive getPrimitive();
	
	/**
	 * @return The act constructed from this act instance.
	 */
	public Act getAct(); 
	
	/**
	 * Normalize this act instance.
	 * @param scale The unit for normalization
	 */
	public void normalize(float scale);

	/**
	 * @return The aspect 
	 */
	public Aspect getAspect();
	
	/**
	 * @param aspect The aspect sensed in the environment
	 */
	public void setAspect(Aspect aspect);
	
	/**
	 * @return The sensory modality
	 */
	public int getModality();
	
	/**
	 * @param modality The sensory modality
	 */
	public void setModality(int modality);
	
	/**
	 * The label of the primitive interaction for display
	 */
	public String getDisplayLabel();
	
	/**
	 * @param tracer The tracer
	 * @param e The XML element that contains the trace of this act instance.
	 */
	public void trace(ITracer tracer, Object e);

}
