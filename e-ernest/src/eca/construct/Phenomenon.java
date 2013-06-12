package eca.construct;


import java.util.List;

import eca.Primitive;
import eca.enaction.Act;

/**
 * An aspect of a phenomenon observed in an area.
 * @author Olivier
 */
public interface Phenomenon {
	
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
