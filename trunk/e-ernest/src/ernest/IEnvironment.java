package ernest;

import java.util.List;

/**
 * The environment that ernest interact in.
 * @author mcohen
 *
 */
public interface IEnvironment 
{
	public boolean enact(String s);
	public List<ISchema> getPrimitiveSchema();
}
