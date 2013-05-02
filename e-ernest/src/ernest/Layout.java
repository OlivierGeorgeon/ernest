package ernest;

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
	 * @param action
	 * @return The observation resulting from this action in this layout.
	 */
	public Observation observe(Action action); 

}
