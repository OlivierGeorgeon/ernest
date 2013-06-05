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
	 * @return The primitive interaction's value (multiplied by 10)
	 */
	public int getValue();
	
	/**
	 * @return The action performed by this primitive interaction
	 */
	public Action getAction();
	
	/**
	 * @param action The action performed by this primitive interaction.
	 */
	public void setAction(Action action);
	
	/**
	 * @return The phenomenon observed by this primitive interaction.
	 */
	public Phenomenon getPhenomenon();
	
	/**
	 * @param phenomenon The phenomenon observed by this primitive interaction.
	 */
	public void setAspect(Phenomenon phenomenon);
}
