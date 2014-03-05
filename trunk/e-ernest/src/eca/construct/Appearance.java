package eca.construct;

import java.util.List;

import tracing.ITracer;

import eca.construct.egomem.Area;
import eca.construct.egomem.PhenomenonType;
import eca.ss.enaction.Act;

/**
 * An Appearance of a PhenomenonType in an Area.
 * An Appearance may also be called an Observation
 * @author Olivier
 */
public interface Appearance {
	
	/**
	 * @return The Observation's label
	 */
	public String getLabel();
	
	/**
	 * @param act The act to add to this action.
	 */
	public void addAct(Act act);

	/**
	 * @return The list of primitive interactions that perform this action.
	 */
	public List<Act> getActs();
	
	/**
	 * @param act The primitive to check 
	 * @return true if this primitive belongs to this action
	 */
	public boolean contains(Act act);
	
	public Act getStillAct();

	public void setStillAct(Act stillAct);

	public Action getDiscriminentAction();

	public void setDiscriminentAction(Action discriminentAction);

	/**
	 * @return The observation's phenomenon
	 */
	//public PhenomenonType getPhenomenonType();	

	/**
	 * @return The Observation's area
	 */
	//public Area getArea();
	//public void setArea(Area area);
	
	public void trace(ITracer tracer, Object e);
	
}
