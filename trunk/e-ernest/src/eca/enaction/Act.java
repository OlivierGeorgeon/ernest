package eca.enaction;

import eca.Primitive;
import eca.construct.Action;
import eca.construct.egomem.Area;

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
	 * @return The primitive interaction enacted by this act.
	 */
	public Primitive getPrimitive();
	
	/**
	 * @param primitive The primitive interaction enacted by this act.
	 */
	public void setPrimitive(Primitive primitive);
	
	/**
	 * @return The area where this act is enacted.
	 */
	public Area getArea();
	
	/**
	 * @param area The area where this act is enacted.
	 */
	public void setArea(Area area);
	
	/**
	 * @return The action performed by the primitive interaction of this act
	 */
	public Action getAction();
	
	public void setColor(int color);
	public int getColor();
}
