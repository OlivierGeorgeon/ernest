package ernest;

/**
 * A pattern of interaction between Ernest and its environment. 
 * Specifically, schemas can either succeed of fail when the agent tries to enact 
 * them in the environment.
 * @author mcohen
 *
 */
public interface ISchema 
{
	public IAct getSucceedingAct();
	public IAct getFailingAct();
	public IAct getResultingAct(boolean s);
	public void setSucceedingAct(IAct a);
	public void setFailingAct(IAct a);
		
	public IAct getContextAct();
	public IAct getIntentionAct();
	public void setContextAct(IAct a);
	public void setIntentionAct(IAct a);
	
	public void setPrescriberAct(IAct a);
	public IAct getPrescriberAct();
	public void setPrescriberSchema(ISchema s);
	public ISchema getPrescriberSchema();
	public void setPointer(int p);
	public int  getPointer();
	public int getLength();
	
	public String getTag(); 
	public int  getWeight();
	public boolean isPrimitive();
	public void incWeight();
	public void setWeight(int weight);
	
	public boolean isActivated();
	public void setActivated(boolean b);
}
