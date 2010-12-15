package ernest;

import java.util.List;

/**
 * The interface for dealing with an Ernest agent. 
 * @author ogeorgeon
 */
public interface IErnest 
{

	public void clear();
	public void setTracer(ITracer tracer);
	public void setParameters(Integer RegularityThreshold, Integer ActivationThreshold, Integer schemaMaxLength); 
	
	public ISchema addPrimitiveInteraction(String tag, int valSucceed, int valFail);
    public ISchema addCompositeInteraction(IAct contextAct, IAct intentionAct);
    
	public String getState();
	public IIcon setSensor(int[][] matrix); 
	public String step(boolean status);
	
}