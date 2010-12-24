package ernest;

/**
 * A noème is an element of awareness in the agent's phenomenological experience.  
 * (http://fr.wikipedia.org/wiki/No%C3%A8me_%28philosophie%29)
 * As implemented, it is an element of Ernest's internal context.
 * Noèmes can activate schemas when they match the schema's context.
 * Noèmes can be pushed into Ernest's situation awareness.
 * Primitive sensorymotor noèmes relate to primitive enaction if they are not inhibited (later).
 * Primitive iconic noèmes relate to the iconic sensory system if they are not inhibited.
 * Noèmes are chained through schemas.
 * @author ogeorgeon
 */
public interface INoeme 
{
	
	/**
	 * @return A unique string representation of that noème.
	 */
	public String getLabel();
	
	/**
	 * @return The type of that noème. So far, only SENSORYMOTOR or ICON.
	 */
	public int getModule();
}
