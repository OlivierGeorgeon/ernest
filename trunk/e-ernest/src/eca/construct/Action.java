package eca.construct;

import java.util.List;

import eca.Primitive;
import eca.construct.egomem.Displacement;

/**
 * An Action is intended to represent an action performed in the external world.
 * An action conflates primitive interactions based on the fact that they are alternative to each other.
 * @author Olivier
 */
public interface Action extends Comparable
{
	/**
	 * @return This action's label
	 */
	public String getLabel();
	
	/**
	 * @return The list of primitive interactions that perform this action.
	 */
	public List<Primitive> getPrimitives();
	
	/**
	 * @param displacement The displacement associated with this interaction.
	 */
	public void setDisplacement(Displacement displacement);
	
	/**
	 * @return The displacement associated with this interaction.
	 */
	public Displacement getDisplacement();

	/**
	 * Provide the weight of this action for the decider to choose the most weighted action
	 * @return The weight of this action. 
	 */
	public int getPropositionWeight();
	
	/**
	 * Let the decider set the weight of this action on each decision cycle
	 * @param propositionWeight The weight of this action
	 */
	public void setPropositionWeight(int propositionWeight);
	
	/**
	 * Add additional weight to this action during the decision process.
	 * @param weight The additional weight to add to this action
	 */
	public void addPropositionWeight(int weight);
	
	/**
	 * @param primitive The primitive interaction to add to this action.
	 */
	public void addPrimitive(Primitive primitive);
	
}
