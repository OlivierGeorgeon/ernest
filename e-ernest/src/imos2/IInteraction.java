package imos2;

/**
 * A sensorimotor pattern of interaction of Ernest with its environment 
 * @author Olivier
 */
public interface IInteraction 
{
	public String getMoveLabel();
	public String getLabel();
	public boolean getPrimitive();
	public int getEnactionValue();
	public void setEnactionWeight(int enactionWeight);
	public int getEnactionWeight();
	public IInteraction getPreInteraction();
	public IInteraction getPostInteraction();
	public void setfailPostValue(int failPostValue);
	public int getfailPostValue();
	public void setfailPostWeight(int failPostWeight);
	public int getfailPostWeight();
	public int getLength();
	public void setStep(int step);
	public void setPrescriber(IInteraction prescriber);
	public IInteraction getPrescriber();
	public int getStep();

	/**
	 * Update the prescriber if this interaction was enacted
	 * @return The next top level interaction to enact or null if the current enaction is over.
	 */
	public IInteraction updatePrescriber();
	
	/**
	 * Clear the prescriber hierarchy
	 */
	public void terminate();
}
