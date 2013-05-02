package spas;

/**
 * An area of the agent's surrounding space.
 * @author Olivier
 */
public interface IArea 
{
	public static final IArea A = new Area("A");
	public static final IArea B = new Area("B");
	public static final IArea C = new Area("C");
	
	public String getLabel();
	public boolean isOccupied();
	public void setOccupied(boolean occupied);
	public void clear();
	public String getEvent();

}
