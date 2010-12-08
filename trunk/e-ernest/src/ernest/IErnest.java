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
	public void setParameters(Integer RegularityThreshold, Integer ActivationThreshold, Integer schemaMaxLength); 
	public void clear();
	public String getState();
	public void setSensor(String p); 
	public String step(boolean status);
	
}