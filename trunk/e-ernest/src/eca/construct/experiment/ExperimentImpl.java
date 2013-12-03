package eca.construct.experiment;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import tracing.ITracer;
import eca.construct.Action;
import eca.construct.egomem.Displacement;
import eca.ss.Appearance;
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
	private int count = 0;
	private float confidence = 0.5f;

	/**
	 * @param preAppearance The Appearance.
	 * @param action The Action.
	 * @return The new or old experiment.
	 */
	public static Experiment createOrGet(Appearance preAppearance, Action action){
		String key = createKey(preAppearance, action);
		if (!EXPERIMENTS.containsKey(key))
			EXPERIMENTS.put(key, new ExperimentImpl(preAppearance, action));			
		return EXPERIMENTS.get(key);
	}

	private static String createKey(Appearance preAppearance, Action action) {
		String key = preAppearance.getLabel() + "/" + action.getLabel();
		return key;
	}
	
	private ExperimentImpl(Appearance preAppearance, Action action){
		this.label = createKey(preAppearance, action);
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
		this.count++;
	}
	
	public float getConfidence(){
		
		return this.confidence;
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
		String s = this.label + " post-appearances:";
		for (Map.Entry<Appearance, Integer> entry : postAppearances.entrySet())
			s += " " + entry.getKey().getLabel() + "(" + entry.getValue() +")";
		s += " acts:";
		for (Map.Entry<Act, Integer> entry : acts.entrySet())
			s += " " + entry.getKey().getLabel() + "(" + entry.getValue() +")";
		s += " displacements:";
		for (Map.Entry<Displacement, Integer> entry : displacements.entrySet())
			s += " " + entry.getKey().getLabel() + "(" + entry.getValue() +")";
		return s;
	}
	
	public static Collection<Experiment> getExperiments() {
		return EXPERIMENTS.values();
	}

//	public Act predictAct() {
//		int max = -1;
//		Act predictAct = null;
//		for (Map.Entry<Act, Integer> entry : acts.entrySet())
//			if (entry.getValue() > max){
//				predictAct = entry.getKey();
//				max = entry.getValue();
//			}
//		
//		return predictAct;
//	}
	
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
		Appearance predictPostAppearance = null;
		for (Map.Entry<Appearance, Integer> entry : postAppearances.entrySet())
			if (entry.getValue() > max){
				predictPostAppearance = entry.getKey();
				max = entry.getValue();
			}		
		this.confidence = (float)max / (float)count;
		return predictPostAppearance;
	}
	
	public boolean isTested(){
		return ! acts.isEmpty();
	}

	public void trace(ITracer tracer, Object e) {
		
		Object a = tracer.addSubelement(e, "experiment");
		tracer.addSubelement(a, "label", this.label);
		this.appearance.trace(tracer, a);
		this.action.trace(tracer, a);
		Object pa = tracer.addSubelement(a, "post_appearances");
		for (Map.Entry<Appearance,Integer> entry : this.postAppearances.entrySet()){
			tracer.addSubelement(pa, "post_appearance", entry.getKey().getLabel() + " weight: " + entry.getValue().intValue() );
		}
	}

}
