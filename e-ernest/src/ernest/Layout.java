package ernest;

import spas.Area;

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
	 * @return The aspect present in this area.
	 */
	public Phenomenon getAspect(Area area);
	
	/**
	 * @param area The area
	 * @return true if this area is empty
	 */
	public boolean isEmpty(Area area);
	
	/**
	 * @return true if this layout is empty
	 */
	public boolean isEmpty();

	public Observation observe();

}
