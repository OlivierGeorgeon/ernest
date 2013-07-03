package eca.construct;

import eca.construct.egomem.Area;

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
	 * @return The observation's phenomenon
	 */
	public PhenomenonType getPhenomenonType();	

	/**
	 * @return The Observation's area
	 */
	public Area getArea();
}
