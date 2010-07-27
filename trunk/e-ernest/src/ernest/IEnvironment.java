package ernest;

import java.util.List;

/**
 * The environment that ernest interact in.
 * @author mcohen
 *
 */
public interface IEnvironment 
{
	public boolean enactSchema(ISchema s);
	public List<ISchema> getPrimitiveSchema();
}
