package ernest;

import imos2.Act;

import java.util.List;

import spas.Area;

/**
 * An observation of a feature in an area.
 * @author Olivier
 */
public interface Observation {
	
	public String getLabel();
	
	public Area getArea();
	
	public Aspect getAspect();	
	
	public void addAct(Act act);
	public List<Act> getActs();

}
