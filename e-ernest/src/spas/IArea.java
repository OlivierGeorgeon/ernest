package spas;

/**
 * An area of the agent's surrounding space.
 * @author Olivier
 */
public interface IArea 
{
	public String getLabel();
	public boolean isOccupied();
	public void setOccupied(boolean occupied);

}
