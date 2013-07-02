package eca.construct;

import eca.construct.egomem.Area;

/**
 * An observation of a phenomenon in an area.
 * @author Olivier
 */
public interface Appearance {
	
	/**
	 * @return The Observation's label
	 */
	public String getLabel();
	
	/**
	 * @return The observation's phenomenon
	 */
	public Phenomenon getPhenomenon();	

	/**
	 * @return The Observation's area
	 */
	public Area getArea();
}
