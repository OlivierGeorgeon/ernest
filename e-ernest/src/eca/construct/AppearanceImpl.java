package eca.construct;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tracing.ITracer;
import eca.construct.egomem.Area;
import eca.construct.egomem.PhenomenonType;
import eca.ss.enaction.Act;

/**
 * An Appearance is the observation of an instance of a phenomenonType in an area.
 * An Appearance may also be called an Observation
 * @author Olivier
 */
public class AppearanceImpl implements Appearance {
	
	/** Appearance UP */
	public static String OBSERVATION_LABEL_UP = "UP";
	/** Appearance DOWN */
	public static String OBSERVATION_LABEL_DOWN = "DOWN";
	/** Appearance END */
	public static String OBSERVATION_LABEL_END = "END";

    private static Map<String , Appearance> OBSERVATIONS = new HashMap<String , Appearance>() ;

	private String label;
	private Act flowAct;
	private List<Act> evokingActs = new ArrayList<Act>();
	private List<Act> affordedActs = new ArrayList<Act>();

	private Act stillAct = null;
	private Action discriminentAction = null; 
	
	//private PhenomenonType phenomenonType;
	//private Area area;
	
	/**
	 * @return The list of all the appearances known by the agent thus far
	 */
	public static Collection<Appearance> getAppearances() {
		return OBSERVATIONS.values();
	}
	
	/**
	 * Create or get an appearance from its act.
	 * @param act The appearance's act
	 * @return The created or retrieved action.
	 */
	public static Appearance createOrGet(Act act){
		String key = createKey(act);
		if (!OBSERVATIONS.containsKey(key))
			OBSERVATIONS.put(key, new AppearanceImpl(key));			
		return OBSERVATIONS.get(key);
	}
	
//	public static Appearance evoke(Act act){
//		Appearance appearance = null;
//		for (Appearance a : OBSERVATIONS.values())
//			if (a.contains(act))
//				appearance = a;
//		
//		if (appearance == null){
//			appearance  = createOrGet(act);
//			appearance.addAct(act);
//		}
//			
//		return appearance;
//	}
	
	private static String createKey(Act act) {
		String key = act.getLabel();
		
		if (act.getLabel().equals("-t"))
			key = OBSERVATION_LABEL_UP;
		else if (act.getLabel().equals("-f"))
			key = OBSERVATION_LABEL_DOWN;
		
		return key;
	}

	/**
	 * @param act The act whose appearance we are searching for
	 * @return The first appearance found that contains act. Null if no appearance found.
	 */
	public static List<Appearance> getEvokedAppeareances(Act act){
		List<Appearance> appearances = new ArrayList<Appearance>(2);
		for (Appearance appearance : OBSERVATIONS.values())
			if (appearance.isEvokedBy(act))
				appearances.add(appearance);
		return appearances;
	}
	
	/**
	 * @param act The act whose appearance we are searching for
	 * @return The first appearance found that contains act. Null if no appearance found.
	 */
	public static List<Appearance> getFlowAppeareances(Act act){
		List<Appearance> appearances = new ArrayList<Appearance>(2);
		for (Appearance appearance : OBSERVATIONS.values())
			if (act.equals(appearance.getFlowAct()))
				appearances.add(appearance);
		return appearances;
	}
	
	/**
	 * Merges two appearances
	 * @param preAppearance The appearance before
	 * @param postAppearance The appearance after
	 * @param tracer The tracer
	 */
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
		if (!this.evokingActs.contains(act))
				this.evokingActs.add(act);
	}
	
	public List<Act> getActs(){
		return this.evokingActs;
	}
	
	public boolean isEvokedBy(Act act){
		return this.evokingActs.contains(act);
	}

//	public PhenomenonType getPhenomenonType(){
//		return this.phenomenonType;
//	}
	
//	public Area getArea(){
//		return this.acts.get(0).getArea();
//		//return this.area;
//	}
	
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
		
		String evokingActsList = "";
		for (Act act : this.evokingActs)
			evokingActsList +=  act.getLabel() + ", ";
		String affordedActList = "";
		for (Act act : this.affordedActs)
			affordedActList +=  act.getLabel() + ", ";

		tracer.addSubelement(e, "observation", this.label + " / flows as: " + flowAct.getLabel() + " / evoked by: "+ evokingActsList + " / affords: " + affordedActList);
	}

	public Act getStillAct() {
		return stillAct;
	}

	public void setStillAct(Act stillAct) {
		this.stillAct = stillAct;
	}

	public Action getDiscriminentAction() {
		return discriminentAction;
	}

	public void setDiscriminentAction(Action discriminentAction) {
		this.discriminentAction = discriminentAction;
	}

	public List<Act> getAffordedActs() {
		return this.affordedActs;
	}

	public void addAffordedAct(Act act) {
		if (!this.affordedActs.contains(act))
			this.affordedActs.add(act);
	}

	public Act getFlowAct() {
		return flowAct;
	}

	public void setFlowAct(Act flowAct) {
		this.flowAct = flowAct;
	}

}
