package spas;

/**
 * An area of the agent's surrounding space.
 * @author Olivier
 */
public interface Area 
{
	public static final Area A = new AreaImpl("A");
	public static final Area B = new AreaImpl("B");
	public static final Area C = new AreaImpl("C");
	
	public String getLabel();
	public boolean isOccupied();
	public void setOccupied(boolean occupied);
	public void clear();
	public String getEvent();

}
