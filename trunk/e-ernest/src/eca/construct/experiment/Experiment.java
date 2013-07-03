package eca.construct.experiment;

import eca.ss.enaction.Act;

/**
 * An experiment is an Action performed on an Appearance.
 * Experiments record the resulting acts, 
 * so as to predict what act will result from an Action performed on an Appearance. 
 * @author Olivier
 */
public interface Experiment {
	
	/**
	 * @return This experiment's label.
	 */
	public String getLabel();
	
	/**
	 * @param act the act to add to this experiment 
	 */
	public void addAct(Act act);
	
	/**
	 * @return The act most probably resulting from this experiment.
	 */
	public Act predictAct();

}
