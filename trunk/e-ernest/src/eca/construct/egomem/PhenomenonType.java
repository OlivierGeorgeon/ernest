package eca.construct.egomem;

import java.util.List;

import tracing.ITracer;
import eca.Primitive;

/**
 * A PhenomenonType is intended to represent a type of phenomenon that can be observed in the external world.
 * A PhenomenonType conflates primitive interactions based on the fact that their spatial location consistently overlaps.
 * A PhenomenonType may also be called a Bundle.
 * @author Olivier
 */
public interface PhenomenonType {
	
	/** Predefined phenomenon type */
	public static PhenomenonType EMPTY = PhenomenonTypeImpl.createOrGet("0");
	
	/**
	 * @return This PhenomenonType's label.
	 */
	public String getLabel();
	
	/**
	 * @param aspect The visual aspect
	 */
	public void setAspect(Aspect aspect);
	
	/**
	 * @return The visual aspect
	 */
	public Aspect getAspect();
	
	/**
	 * @param primitive The primitive interaction to add to this phenomenon type
	 */
	public void addPrimitive(Primitive primitive);
	
	/**
	 * @return The list of primitive interactions attached to this phenomenon type
	 */
	public List<Primitive> getPrimitives();
	
	/**
	 * @param primitive The primitive to check 
	 * @return true if this primitive belongs to this phenomenon type.
	 */
	public boolean contains(Primitive primitive);
	
	/**
	 * Trace this phenomenon type
	 * @param tracer The tracer
	 * @param e The xml tag that contains the trace
	 */
	public void trace(ITracer tracer, Object e);
	
	public void setAttractiveness(int attractiveness);
	public int getAttractiveness();

}
