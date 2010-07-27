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
	public IAct getSuccessAct();
	public IAct getFailureAct();
	public void setSuccessAct(IAct a);
	public void setFailureAct(IAct a);
	
	public IAct getContextAct();
	public IAct getIntentionAct();
	public void setContextAct(IAct a);
	public void setIntentionAct(IAct a);
	
	public int getWeight();
	public boolean isPrimitive();
	public String getId();
	public void updateSuccessSatisfaction();
	public void incWeight();
}
