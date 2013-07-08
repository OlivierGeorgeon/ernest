package eca.construct;

import java.util.List;
import eca.Primitive;

/**
 * A PhenomenonType is intended to represent a type of phenomenon that can be observed in the external world.
 * A PhenomenonType conflates primitive interactions based on the fact that their spatial location consistently overlaps.
 * A PhenomenonType may also be called a Bundle.
 * @author Olivier
 */
public interface PhenomenonType {
	
	/** Predefined phenomena */
	public static PhenomenonType EMPTY = PhenomenonTypeImpl.createOrGet("_");
	
	/**
	 * @return This PhenomenonType's label.
	 */
	public String getLabel();
	
	/**
	 * @param primitive The primitive interaction to add to this phenomenon type
	 */
	public void addPrimitive(Primitive primitive);
	
	/**
	 * @return The list of primitive interactions attached to this phenomenon type
	 */
	public List<Primitive> getPrimitives();
}
