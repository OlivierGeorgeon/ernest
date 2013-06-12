package ernest;

import java.util.List;

import eca.spas.egomem.Transformation;

/**
 * An action that may be performed by interactions.
 * @author Olivier
 */
public interface Action extends Comparable
{
	/**
	 * @return This action's label
	 */
	public String getLabel();
	
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
	
	/**
	 * @return The list of primitive interactions that perform this action.
	 */
	public List<Primitive> getPrimitives();

	/**
	 * @param transformation The transformation performed by this action
	 */
	public void setTransformation(Transformation transformation);
	
	/**
	 * @return The transformation performed by this action. 
	 */
	public Transformation getTransformation();

}
