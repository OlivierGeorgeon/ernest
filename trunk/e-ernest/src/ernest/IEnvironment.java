package ernest;

import java.util.List;

/**
 * The environment that ernest interact in.
 *  The environment needs to process schemas and return a boolean status
 * @author mcohen
 * @author ogeorgeon
 */
public interface IEnvironment 
{
	public boolean enact(String s);
}
