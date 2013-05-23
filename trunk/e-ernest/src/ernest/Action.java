package ernest;

import java.util.List;

import spas.Transformation;

import imos2.Act;

/**
 * An modality is a set if interactions that are alternate to each other.
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
	 * Add additionnal weight to this action during the decision process.
	 * @param weight The additionnal weight to add to this action
	 */
	public void addPropositionWeight(int weight);
	
	public void addAct(Act act);
	public List<Act> getActs();

	public void setTransformation(Transformation transformation);
	public Transformation getTransformation();

}
