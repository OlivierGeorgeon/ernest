package ernest;

import java.util.List;

/**
 * A test environment for Ernest to interact in.
 * The environment receives the schema to enact from Ernest and returns the enaction status
 * @author mcohen
 * @author ogeorgeon
 */
public interface IEnvironment 
{
	public boolean enact(String s);
}
