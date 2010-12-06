package ernest;

import java.util.List;

/**
 * The interface that defines the Ernest class. 
 * @author ogeorgeon
 *
 */
public interface IErnest 
{

	public void addPrimitiveInteraction(String tag, int valSucceed, int valFail);
	public void setTracer(ITracer tracer);
	public void clear();
	public String getState();
	public String step(boolean status);
	
}