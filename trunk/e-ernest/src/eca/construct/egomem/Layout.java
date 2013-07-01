package eca.construct.egomem;

import eca.construct.Observation;
import eca.construct.Phenomenon;

/**
 * A spatial configuration of the surroundings of the agent.
 * @author Olivier
 */
public interface Layout {
	
	/**
	 * @return The layout's identifier
	 */
	public String getLabel();
	
	/**
	 * @param area the area
	 * @return The phenomenon present in this area.
	 */
	public Phenomenon getPhenomenon(Area area);
	
	/**
	 * @param area The area
	 * @return true if this area is empty
	 */
	public boolean isEmpty(Area area);
	
	/**
	 * @return true if this layout is empty
	 */
	public boolean isEmpty();

	/**
	 * @return The observation produced by this layout
	 */
	public Observation observe();

}
