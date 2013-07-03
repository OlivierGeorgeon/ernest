package eca.construct.experiment;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eca.construct.Action;
import eca.construct.Appearance;
import eca.ss.enaction.Act;

/**
 * An experiment is an Action performed on a Phenomenon.
 * Experiments record the resulting interactions. 
 * @author Olivier
 */
public class ExperimentImpl implements Experiment {
	
	private static Map<String , Experiment> EXPERIMENTS = new HashMap<String , Experiment>() ;
	private String label;
	private Action action;
	private Appearance appearance;
	private Map<Act , Integer> acts = new HashMap<Act , Integer>() ;

	/**
	 * @param action The Action.
	 * @param appearance The Appearance.
	 * @return The new or old experiment.
	 */
	public static Experiment createOrGet(Action action, Appearance appearance){
		String key = createKey(action, appearance);
		if (!EXPERIMENTS.containsKey(key))
			EXPERIMENTS.put(key, new ExperimentImpl(action, appearance));			
		return EXPERIMENTS.get(key);
	}

	private static String createKey(Action action, Appearance appearance) {
		String key = action.getLabel() + "/" + appearance.getLabel();
		return key;
	}
	
	private ExperimentImpl(Action action, Appearance appearance){
		this.label = createKey(action, appearance);
		this.action = action;
		this.appearance = appearance;
	}	
	
	public String getLabel() {
		return this.label;
	}
	
	public void addAct(Act act){
		if (acts.containsKey(act))
			acts.put(act, acts.get(act) + 1);
		else
			acts.put(act, 1);
	}

	public Act predictAct() {
		int max = 0;
		Act predictAct = null;
		for (Map.Entry<Act, Integer> entry : acts.entrySet())
			if (entry.getValue() > max){
				predictAct = entry.getKey();
				max = entry.getValue();
			}
		
		return predictAct;
	}
	
	/**
	 * Experiments are equal if they have the same label. 
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			Experiment other = (Experiment)o;
			ret = (other.getLabel().equals(this.getLabel()));
		}
		return ret;
	}
	
	public String toString(){
		String s = this.label;
		for (Map.Entry<Act, Integer> entry : acts.entrySet())
			s += " " + entry.getKey().getLabel() + "(" + entry.getValue() +")";
		return s;
	}
	
	public static Collection<Experiment> getExperiments() {
		return EXPERIMENTS.values();
	}

}
