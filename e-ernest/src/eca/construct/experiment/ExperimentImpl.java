package eca.construct.experiment;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;

import eca.construct.Action;
import eca.construct.Appearance;
import eca.construct.AppearanceImpl;
import eca.construct.PhenomenonTypeImpl;
import eca.construct.egomem.AreaImpl;
import eca.construct.egomem.Displacement;
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
	private Map<Displacement , Integer> displacements = new HashMap<Displacement , Integer>() ;
	private Map<Appearance , Integer> postAppearances = new HashMap<Appearance , Integer>() ;

	/**
	 * @param preAppearance The Appearance.
	 * @param action The Action.
	 * @return The new or old experiment.
	 */
	public static Experiment createOrGet(Appearance preAppearance, Action action){
		String key = createKey(action, preAppearance);
		if (!EXPERIMENTS.containsKey(key))
			EXPERIMENTS.put(key, new ExperimentImpl(preAppearance, action));			
		return EXPERIMENTS.get(key);
	}

	private static String createKey(Action action, Appearance appearance) {
		String key = action.getLabel() + "/" + appearance.getLabel();
		return key;
	}
	
	private ExperimentImpl(Appearance preAppearance, Action action){
		this.label = createKey(action, preAppearance);
		this.action = action;
		this.appearance = preAppearance;
	}	
	
	public String getLabel() {
		return this.label;
	}
	
	public void incActCounter(Act act){
		if (acts.containsKey(act))
			acts.put(act, acts.get(act) + 1);
		else
			acts.put(act, 1);
	}

	public void incDisplacementCounter(Displacement displacement){
		if (displacements.containsKey(displacement))
			displacements.put(displacement, displacements.get(displacement) + 1);
		else
			displacements.put(displacement, 1);
	}

	public void incPostAppearanceCounter(Appearance appearance){
		if (postAppearances.containsKey(appearance))
			postAppearances.put(appearance, postAppearances.get(appearance) + 1);
		else
			postAppearances.put(appearance, 1);
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

	public Displacement predictDisplacement() {
		int max = 0;
		Displacement predictDisplacement = null;
		for (Map.Entry<Displacement, Integer> entry : displacements.entrySet())
			if (entry.getValue() > max){
				predictDisplacement = entry.getKey();
				max = entry.getValue();
			}
		
		return predictDisplacement;
	}

	public Appearance predictPostAppearance() {
		int max = 0;
		Appearance predictPostAppearance = AppearanceImpl.createOrGet(PhenomenonTypeImpl.EMPTY, AreaImpl.createOrGet(new Point3f()));
		for (Map.Entry<Appearance, Integer> entry : postAppearances.entrySet())
			if (entry.getValue() > max){
				predictPostAppearance = entry.getKey();
				max = entry.getValue();
			}
		
		return predictPostAppearance;
	}

}
