package ernest;

import imos2.Act;

import java.util.List;

/**
 * An aspect of a phenomenon observed in an area.
 * @author Olivier
 */
public interface Aspect {
	
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
