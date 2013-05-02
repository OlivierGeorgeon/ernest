package ernest;

/**
 * A feature of a phenomenon observed in an area.
 * @author Olivier
 */
public interface Aspect {
	
	public static final Aspect APPEAR = new AspectImpl("*");
	public static final Aspect CLOSER = new AspectImpl("+");
	public static final Aspect DISAPPEAR = new AspectImpl("o");
	public static final Aspect FARTHER = new AspectImpl("-");
	public static final Aspect MOVE = new AspectImpl("=");
	public static final Aspect UNCHANGED = new AspectImpl("_");

	public String getLabel();

}
