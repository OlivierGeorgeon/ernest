package eca.construct;

import eca.construct.egomem.Area;

/**
 * An observation of a phenomenon in an area.
 * @author Olivier
 */
public interface Observation {
	
	/**
	 * @return The Observation's label
	 */
	public String getLabel();
	
	/**
	 * @return The Observation's area
	 */
	public Area getArea();
	
	/**
	 * @return The observation's phenomenon
	 */
	public Phenomenon getPhenomenon();	
}
