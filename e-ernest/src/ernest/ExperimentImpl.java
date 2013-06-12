package ernest;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eca.enaction.Act;

/**
 * An experiment is an Action performed on a Phenomenon.
 * Experiments record the resulting interactions. 
 * @author Olivier
 */
public class ExperimentImpl implements Experiment {
	
	private static Map<String , Experiment> EXPERIMENTS = new HashMap<String , Experiment>() ;
	private String label;
	private Action action;
	private Observation observation;
	private Map<Act , Integer> acts = new HashMap<Act , Integer>() ;

	/**
	 * @param action The Action.
	 * @param aspect The Phenomenon.
	 * @return The new or old experiment.
	 */
	public static Experiment createOrGet(Action action, Observation observation){
		String key = createKey(action, observation);
		if (!EXPERIMENTS.containsKey(key))
			EXPERIMENTS.put(key, new ExperimentImpl(action, observation));			
		return EXPERIMENTS.get(key);
	}

	private static String createKey(Action action, Observation observation) {
		String key = action.getLabel() + "/" + observation.getLabel();
		return key;
	}
	
	private ExperimentImpl(Action action, Observation observation){
		this.label = createKey(action, observation);
		this.action = action;
		this.observation = observation;
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
