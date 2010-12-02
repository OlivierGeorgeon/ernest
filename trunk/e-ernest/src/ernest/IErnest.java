package ernest;

import java.util.List;

/**
 * The interface that defines the Ernest class. 
 * @author ogeorgeon
 *
 */
public interface IErnest 
{
	public void addPrimitiveSchema(String tag, int valSucceed, int valFail);
	public void setLogger(ILogger logger);
	public void clear();
	public String step(boolean status);
}