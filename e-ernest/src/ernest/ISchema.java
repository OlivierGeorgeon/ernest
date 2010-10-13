package ernest;

/**
 * A Schema is a pattern of interaction between the agent and its environment. 
 * Specifically, schemas can either succeed of fail when the agent tries to enact 
 * them in the environment.
 * @author mcohen
 *
 */
public interface ISchema 
{
	public IAct getSucceedingAct();
	public IAct getFailingAct();
	public void setSucceedingAct(IAct a);
	public void setFailingAct(IAct a);
	
	public void initSucceedingAct();
	public IAct initFailingAct(int Satisfaction);
	
	public IAct getContextAct();
	public IAct getIntentionAct();
	public void setContextAct(IAct a);
	public void setIntentionAct(IAct a);
	
	public String getTag(); 
	public int getWeight();
	public boolean isPrimitive();
	public void incWeight();
}
