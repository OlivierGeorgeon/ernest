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
	
	public void addPrimitive(Primitive primitive);
	public List<Primitive> getPrimitives();
}
