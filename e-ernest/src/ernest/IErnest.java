package ernest;

import java.util.List;

/**
 * Defines the Ernest agent class. 
 * @author ogeorgeon
 */
public interface IErnest 
{

	public ISchema addPrimitiveInteraction(String tag, int valSucceed, int valFail);
    public ISchema addCompositeInteraction(IAct contextAct, IAct intentionAct);
	public void setTracer(ITracer tracer);
	public void setParameters(Integer RegularityThreshold, Integer ActivationThreshold, Integer schemaMaxLength); 
	public void clear();
	public String getState();
	public void setSensor(int[][] matrix); 
	public String step(boolean status);
	
}