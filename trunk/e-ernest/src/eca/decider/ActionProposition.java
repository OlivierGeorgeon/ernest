package eca.decider;

import eca.construct.Action;
import eca.construct.experiment.Experiment;
import eca.ss.Appearance;
import eca.ss.enaction.Act;

/**
 * A proposition to perform an action. 
 * @author ogeorgeon
 */
public interface ActionProposition extends Comparable<ActionProposition> {
	/**
	 * @return The interaction proposed by this proposition.
	 */
	public Action getAction();
	
	/**
	 * @return The proposition's weight according to the Spatial System.
	 */
	public int getSSWeight();
	
	/**
	 * @param ssWeight The weight to add to the proposition.
	 */
	public void addSSWeight(int ssWeight);
		
	
	public void setExperiment(Experiment experiment);
	public Experiment getExperiment();
	
	/**
	 * @return The anticipated act.
	 */
	//public Appearance getAnticipatedAppearance();

	/**
	 * @param anticipatedAct The anticipated act.
	 */
	//public void setAnticipatedAppearance(Appearance appearance);
	
	//public float getConfidence();
	//public void setConfidence(float confidence);
	
	/**
	 * Two propositions are equal if they propose the same action. 
	 */
	public boolean equals(Object o);	
	
	public void setSSAnticipatedAct(Act ssAnticipatedAct);
	public Act getSSAnticipatedAct();
	public void setSSActWeight(int ssActWeight);
	public int getSSActWeight();
	
}
