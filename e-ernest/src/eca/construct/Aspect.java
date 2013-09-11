package eca.construct;

/**
 * A visual aspect of a phenomenon type.
 * @author Olivier
 */
public interface Aspect 
{

	/** Predefined aspects */
	public static Aspect MOVE = AspectImpl.createOrGet(0xFFFFFF);
	//public static Aspect WALL = AspectImpl.createOrGet(0x646464);
	public static Aspect BUMP = AspectImpl.createOrGet(0xFF0000);
	public static Aspect CONSUME = AspectImpl.createOrGet(0x9680FF);
	
	/**
	 * @return The area's label
	 */
	public int getCode();	
}
