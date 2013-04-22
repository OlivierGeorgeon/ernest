package imos2;

import java.util.ArrayList;

/**
 * A sensorimotor pattern of interaction of Ernest with its environment 
 * @author Olivier
 */
public interface IInteraction 
{
	/**
	 * @return The label of the move of this interaction
	 */
	//public String getMoveLabel();
	
	/**
	 * @return The label of this interaction (unique identifier)
	 */
	public String getLabel();
	
	/**
	 * @return true if primitive, false if composite
	 */
	public boolean getPrimitive();
	
	/**
	 * @return The value of enacting this interaction
	 */
	public int getEnactionValue();
	
	/**
	 * @param enactionWeight The weight of this interaction.
	 */
	public void setEnactionWeight(int enactionWeight);
	
	/**
	 * @return The weight of this interaction.
	 */
	public int getEnactionWeight();
	
	/**
	 * @return This interaction's pre-interaction (null if primitive).
	 */
	public IInteraction getPreInteraction();
	
	/**
	 * @return This interaction's post-interaction (null if primitive).
	 */
	public IInteraction getPostInteraction();
	
	/**
	 * @return The number of primitive interactions that compose this interaction
	 */
	public int getLength();
	
	/**
	 * @param step The current step during the enaction of this interaction.
	 */
	public void setStep(int step);
	
	/**
	 * @return The current step during the enaction of this interaction.
	 */
	public int getStep();
	
	/**
	 * @param prescriber The interaction that prescribes the enaction of this interaction.
	 */
	public void setPrescriber(IInteraction prescriber);
	
	/**
	 * @return The interaction that prescribes the enaction of this interaction.
	 */
	public IInteraction getPrescriber();
	
	/**
	 * Prescribe this interaction's preInteraction
	 * This method applies recursively to all this interaction's sub interactions.
	 * @return The primitive interaction at the bottom of the hierarchy
	 */
	public IInteraction prescribe();

	/**
	 * Update the prescriber if this interaction was enacted
	 * @return The next top level interaction to enact or null if the current enaction is over.
	 */
	public IInteraction updatePrescriber();
	
	/**
	 * Clear the prescriber hierarchy
	 */
	public void terminate();
	
	/**
	 * @param alternateInteraction The actually enacted interaction
	 * @return true if the alternate interaction is new 
	 */
	public boolean addAlternateInteraction(IInteraction alternateInteraction);
	
	/**
	 * @return This list of this interaction's alternative interactions
	 */
	public ArrayList<IInteraction> getAlternateInteractions();
	
	public void setPreAct(IAct act);
	public void setPostAct(IAct act);
	public IAct getPreAct();
	public IAct getPostAct();
	
	
}
