package eca.construct;

import java.util.List;
import eca.Primitive;

/**
 * A type of phenomenon.
 * @author Olivier
 */
public interface Phenomenon {
	
	/** Predefined phenomena */
	public static Phenomenon EMPTY = PhenomenonImpl.createOrGet("_");
	
	/**
	 * @return This aspect's label.
	 */
	public String getLabel();
	
	/**
	 * @param primitive The primitive interaction to add to this aspect
	 */
	public void addPrimitive(Primitive primitive);
	
	/**
	 * @return The list of primitive interactions attached to this aspect
	 */
	public List<Primitive> getPrimitives();
}
