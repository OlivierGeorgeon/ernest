package spas;

/**
 * An area of the agent's surrounding space.
 * @author Olivier
 */
public interface Area 
{
	public String getLabel();
	public boolean isOccupied();
	public void setOccupied(boolean occupied);
	public void clear();
	public String getEvent();
}
