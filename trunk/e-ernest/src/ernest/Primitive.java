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
	
	public Action getAction();
	public void setAction(Action action);
	
	public Phenomenon getAspect();
	public void setAspect(Phenomenon phenomenon);
}
