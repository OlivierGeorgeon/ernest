package ernest;

/**
 * Interface for an Environment suitable to Ernest.
 * @author mcohen
 * @author ogeorgeon
 */
public interface IEnvironment 
{
	/**
	 * Enact a primitive schema and return the enaction status.
	 * @param s The primitive schema that Ernest has chosen to enact.
	 * @return The status that results from the enaction of the primitive schema in the environment.
	 */
	public boolean enact(String s);
}
