package ernest;

import spas.IArea;

/**
 * An observation of a feature in an area.
 * @author Olivier
 */
public interface Observation {
	
	public String getLabel();
	
	public IArea getArea();
	
	public Aspect getAspect();	
}
