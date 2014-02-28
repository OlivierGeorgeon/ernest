package eca.ss;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import tracing.ITracer;
import utils.ErnestUtils;
import eca.construct.Area;
import eca.construct.PhenomenonType;
import eca.ss.enaction.Act;

/**
 * An Appearance is the observation of an instance of a phenomenonType in an area.
 * An Appearance may also be called an Observation
 * @author Olivier
 */
public class AppearanceImpl implements Appearance {
	
	/** Appearance UP */
	public static String OBSERVATION_LABEL_UP = "Up";
	/** Appearance DOWN */
	public static String OBSERVATION_LABEL_DOWN = "Down";
	/** Appearance END */
	public static String OBSERVATION_LABEL_END = "End";

    private static Map<String , Appearance> OBSERVATIONS = new HashMap<String , Appearance>() ;

	private String label;
	private List<Act> acts = new ArrayList<Act>();
	private PhenomenonType phenomenonType;
	//private Area area;
	
	/**
	 * @return The list of all the appearances known by the agent thus far
	 */
	public static Collection<Appearance> getAppearances() {
		return OBSERVATIONS.values();
	}
	
	/**
	 * Create or get an appearance from its label.
	 * @param label The action's label
	 * @return The created or retrieved action.
	 */
	public static Appearance createOrGet(Act act){
		String key = createKey(act);
		if (!OBSERVATIONS.containsKey(key))
			OBSERVATIONS.put(key, new AppearanceImpl(key));			
		return OBSERVATIONS.get(key);
	}
	
	public static Appearance evoke(Act act){
		Appearance appearance = null;
		for (Appearance a : OBSERVATIONS.values())
			if (a.contains(act))
				appearance = a;
		
		if (appearance == null){
			appearance  = createOrGet(act);
			appearance.addAct(act);
		}
			
		return appearance;
	}
	
	private static String createKey(Act act) {
		String key = "[p" + act.getLabel() + "]";
		
		if (act.getLabel().equals("-tO"))
			key = OBSERVATION_LABEL_UP;
		else if (act.getLabel().equals("-fO"))
			key = OBSERVATION_LABEL_DOWN;
		
		return key;
	}

	public static void merge(Appearance preAppearance, Appearance postAppearance, ITracer tracer){
		
		if (!postAppearance.equals(preAppearance)){
			for (Act act : preAppearance.getActs())
				postAppearance.addAct(act);
			
			OBSERVATIONS.remove(preAppearance.getLabel());
	
			if (tracer != null){
				tracer.addEventElement("merge_appearance", postAppearance.getLabel() + " absorbs " + preAppearance.getLabel());
			}
		}
	}
	
	private static String createKey(PhenomenonType phenomenonType, Area area) {
		String key = phenomenonType.getLabel() + area.getLabel();
		return key;
	}
	
	private AppearanceImpl(String label){
		this.label = label;
	}	
	
	public String getLabel() {
		return this.label;
	}
	
	public void addAct(Act act){
		if (!this.acts.contains(act))
				this.acts.add(act);
	}
	
	public List<Act> getActs(){
		return this.acts;
	}
	
	public boolean contains(Act act){
		return this.acts.contains(act);
	}

	public PhenomenonType getPhenomenonType(){
		return this.phenomenonType;
	}
	
	public Area getArea(){
		return this.acts.get(0).getArea();
		//return this.area;
	}
	
//	public void setArea(Area area){
//		this.area = area;
//	}
	
	/**
	 * Observations are equal if they have the same label. 
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
			Appearance other = (Appearance)o;
			ret = (other.getLabel().equals(this.label));
		}
		return ret;
	}
	
	public void trace(ITracer tracer, Object e) {
		
		String actList = "";
		for (Act act : this.acts)
			actList += ", " + act.getLabel();

		tracer.addSubelement(e, "appearance", this.label + actList);
	}

}
