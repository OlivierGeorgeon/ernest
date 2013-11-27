package eca.ss;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tracing.ITracer;
import eca.construct.Area;
import eca.construct.PhenomenonType;
import eca.ss.enaction.Act;

/**
 * An Appearance is the observation of an instance of a phenomenonType in an area.
 * An Appearance may also be called an Observation
 * @author Olivier
 */
public class AppearanceImpl implements Appearance {
	
	private static Map<String , Appearance> OBSERVATIONS = new HashMap<String , Appearance>() ;
	private static int index = 0;

	private String label;
	private List<Act> acts = new ArrayList<Act>();
	private PhenomenonType phenomenonType;
	private Area area;
	
	/**
	 * @return The list of all types of phenomenon known by the agent thus far
	 */
	public static Collection<Appearance> getAppearances() {
		return OBSERVATIONS.values();
	}
	
	/**
	 * @param phenomenonType The observation's aspect
	 * @param area The observation's area
	 * @return The new or old observation
	 */
	public static Appearance createOrGet(PhenomenonType phenomenonType, Area area){
		String label = createKey(phenomenonType, area);
		if (!OBSERVATIONS.containsKey(label))
			OBSERVATIONS.put(label, new AppearanceImpl(phenomenonType, area));			
		return OBSERVATIONS.get(label);
	}

	/**
	 * Create or get an action from its label.
	 * @param label The action's label
	 * @return The created or retrieved action.
	 */
	public static Appearance createOrGet(String label){
		if (!OBSERVATIONS.containsKey(label))
			OBSERVATIONS.put(label, new AppearanceImpl(label));			
		return OBSERVATIONS.get(label);
	}
	
	private static String createKey(PhenomenonType phenomenonType, Area area) {
		String key = phenomenonType.getLabel() + area.getLabel();
		return key;
	}
	
	private AppearanceImpl(PhenomenonType phenomenonType, Area area){
		this.label = phenomenonType.getLabel() + area.getLabel();
		this.phenomenonType = phenomenonType;
		this.area = area;
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
		return this.area;
	}
	
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
		
		String actList = getLabel();
		for (Act act : this.acts)
			actList += act.getLabel() + ", ";

		tracer.addSubelement(e, "appearance", this.label + actList);
	}

}
