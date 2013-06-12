package eca.construct;


import java.util.List;

import eca.construct.egomem.Area;
import eca.ss.enaction.Act;


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
