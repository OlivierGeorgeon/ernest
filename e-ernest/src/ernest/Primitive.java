package ernest;

/**
 * A primitive interaction.
 * @author Olivier
 */
public interface Primitive 
{
	/**
	 * @return The primitive interaction's label
	 */
	public String getLabel();
	/**
	 * @return The primitive interaction's value
	 */
	public int getValue();
	
	/**
	 * @return The action corresponding to this primitive interaction.
	 */
	public Action getAction();
	
	/**
	 * @param action The action corresponding to this primitive interaction.
	 */
	public void setAction(Action action);
	
	/**
	 * @return The aspect of a phenomenon observed through this primitive interaction.
	 */
	public Aspect getAspect();
	
	/**
	 * @param aspect The aspect of a phenomenon observed through this primitive interaction.
	 */
	public void setAspect(Aspect aspect);
}
