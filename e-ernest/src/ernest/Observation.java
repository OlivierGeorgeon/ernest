package ernest;

import spas.Area;

/**
 * An observation of a feature in an area.
 * @author Olivier
 */
public interface Observation {
	
	public String getLabel();
	
	public Area getArea();
	
	public Aspect getAspect();	
}
