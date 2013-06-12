package ernest;


import java.util.List;

import eca.enaction.Act;
import eca.spas.egomem.Area;


/**
 * An observation of a feature in an area.
 * @author Olivier
 */
public interface Observation {
	
	public String getLabel();
	
	public Area getArea();
	
	public Phenomenon getAspect();	
	
	public void addAct(Act act);
	public List<Act> getActs();

}
