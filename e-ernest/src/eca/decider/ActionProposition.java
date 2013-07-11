package eca.decider;

import eca.construct.Action;
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
		
	/**
	 * @return The anticipated act.
	 */
	public Act getAnticipatedAct();

	/**
	 * @param anticipatedAct The anticipated act.
	 */
	public void setAnticipatedAct(Act anticipatedAct);
	
	/**
	 * Two propositions are equal if they propose the same action. 
	 */
	public boolean equals(Object o);	
	
	public void setSSAnticipatedAct(Act ssAnticipatedAct);
	public Act getSSAnticipatedAct();
	public void setSSActWeight(int ssActWeight);
	public int getSSActWeight();
	
}
