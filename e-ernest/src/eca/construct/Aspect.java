package eca.construct;

/**
 * A visual aspect of a phenomenon type.
 * @author Olivier
 */
public interface Aspect 
{

	/** Predefined aspects */
	public static Aspect DEFAULT_ASPECT = AspectImpl.createOrGet(0xFFFFFF);
	
	/**
	 * @return The area's label
	 */
	public int getCode();	
}
