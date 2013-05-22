package imos2;

import java.util.ArrayList;

import spas.Area;
import ernest.Action;
import ernest.Aspect;
import ernest.Observation;
import ernest.Primitive;

/**
 * A sensorimotor pattern of interaction of Ernest with its environment 
 * @author Olivier
 */
public interface Act 
{
	/**
	 * @return The label of this interaction (unique identifier)
	 */
	public String getLabel();
	
	/**
	 * @return true if primitive, false if composite
	 */
	public boolean isPrimitive();
	
	/**
	 * @return The value of enacting this interaction
	 */
	public int getEnactionValue();
	
	/**
	 * @param enactionWeight The weight of this interaction.
	 */
	public void setWeight(int enactionWeight);
	
	/**
	 * @return The weight of this interaction.
	 */
	public int getWeight();
	
	/**
	 * @return This interaction's pre-interaction (null if primitive).
	 */
	public Act getPreAct();
	
	/**
	 * @return This interaction's post-interaction (null if primitive).
	 */
	public Act getPostAct();
	
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
	public void setPrescriber(Act prescriber);
	
	/**
	 * @return The interaction that prescribes the enaction of this interaction.
	 */
	public Act getPrescriber();
	
	/**
	 * Prescribe this interaction's preInteraction
	 * This method applies recursively to all this interaction's sub interactions.
	 * @return The primitive interaction at the bottom of the hierarchy
	 */
	public Act prescribe();

	/**
	 * Update the prescriber if this interaction was enacted
	 * @return The next top level interaction to enact or null if the current enaction is over.
	 */
	public Act updatePrescriber();
	
	/**
	 * Clear the prescriber hierarchy
	 */
	public void terminate();
	
	/**
	 * @return The action corresponding to this act
	 */
	public Action getAction();
	public void setAction(Action action);
	
	public Aspect getAspect();
	public void setAspect(Aspect aspect);

	public Area getArea();
	public void setArea(Area area);
	
	/**
	 * @return The aspect of the phenomenon observed through this act.
	 */
//	public Observation getObservation();
//	public void setObservation(Observation observation);
	
}
