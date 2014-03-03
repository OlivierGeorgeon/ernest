package eca.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tracing.ITracer;
import eca.construct.experiment.ExperimentImpl;
import eca.ss.Appearance;
import eca.ss.AppearanceImpl;
import eca.ss.enaction.Act;

/**
 * An Action that can be performed in the external world.
 * An Action is intended to be performed on an Appearance
 *   It maintains the list of appearance to which it applies and provides the expected Acts and Displacements. 
 * An action conflates primitive interactions based on the fact that they are alternative to each other.
 * @author Olivier
 */
public class ActionImpl implements Action {

	/** Action Feel */
	public static String ACTION_FEEL = "Feel";
	/** Action Step */
	public static String ACTION_STEP = "Step";
	/** Action Swap */
	public static String ACTION_SWAP = "Swap";

	private static Map<String , Action> ACTIONS = new LinkedHashMap<String , Action>() ;
	//private static int index = 0;

	private String label;
	private List<Act> acts = new ArrayList<Act>();

	/**
	 * Create or get an action from its act.
	 * @param act The action's act
	 * @return The created or retrieved action.
	 */
	public static Action createOrGet(Act act){
		String key = createKey(act);
		if (!ACTIONS.containsKey(key))
			ACTIONS.put(key, new ActionImpl(key));			
		return ACTIONS.get(key);
	}
	
	private static String createKey(Act act) {
		String key = "[a" + act.getLabel() + "]";
		
		if (act.getLabel().equals(">t"))
			key = ACTION_STEP;
		else if (act.getLabel().equals("-t"))
			key = ACTION_FEEL;
		else if (act.getLabel().equals("it"))
			key = ACTION_SWAP;
		
		return key;
	}

	/**
	 * @return The collection of all actions known by the agent.
	 */
	public static Collection<Action> getACTIONS(){
		return ACTIONS.values();
	}
	
	/**
	 * Merge the action of the enacted act into the intended action.
	 * The interactions attached to the enacted act's action are transferred to the intendedAction and the enacted Action is removed
	 * @param act The act to merge. 
	 * @param intendedAction The second action to which to merge (kept).
	 */
	public static void merge(Act act, Action intendedAction){
		if (!intendedAction.contains(act)){
			Action action = null;
			for (Action a : getACTIONS()){
				if (a.contains(act))
					action = a;
			}
			// TODO more complex merge of actions.
			if (action != null){
				for (Act p : action.getActs())
					intendedAction.addAct(p);
				ACTIONS.remove(action.getLabel());
			}
			intendedAction.addAct(act);
		}
	}
	
	/**
	 * The intendedAction absorbs the other action that contains the same acts.
	 * @param intendedAction The intended action
	 * @param tracer The tracer
	 */
	public static void absorbIdenticalAction(Action intendedAction, ITracer tracer){
		Action action = null;
		for (Action a : getACTIONS()){
			if (a!=intendedAction)	
				//if (a.getActs().containsAll(intendedAction.getActs()))
				if (intendedAction.getActs().containsAll(a.getActs()))
					action = a;				
		}
		if (action != null){
			for (Act p : action.getActs())
				intendedAction.addAct(p);
			if (tracer != null){
				tracer.addEventElement("merge_action", intendedAction.getLabel() + " absorbs " + action.getLabel());
			}
			ACTIONS.remove(action.getLabel());
		}		
	}
	
	private ActionImpl(String label){
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

	/**
	 * Actions are equal if they have the same label. 
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
			Action other = (Action)o;
			ret = (other.getLabel().equals(this.label));
		}
		return ret;
	}

	public Appearance predictPostAppearance(Appearance preAppearance) {
		Appearance postAppearance = null;
		if (preAppearance != null)
			postAppearance = ExperimentImpl.createOrGet(preAppearance, this).predictPostAppearance();
		if (postAppearance == null)
			postAppearance = AppearanceImpl.evoke(acts.get(0));
		return postAppearance;
	}


//	public Act predictAct(Appearance appearance) {
//		Act predictAct = null;
//		if (appearance != null)
//			predictAct = ExperimentImpl.createOrGet(appearance, this).predictAct();
//		if (predictAct == null)
//			predictAct = acts.get(0);
//		return predictAct;
//	}

//	public Displacement predictDisplacement(Appearance appearance) {
//		Displacement predictDisplacement = ExperimentImpl.createOrGet(appearance, this).predictDisplacement();
//		if (predictDisplacement == null)
//			predictDisplacement = DisplacementImpl.createOrGet(new Transform3D());
//		return predictDisplacement;
//	}
//
//	public Appearance predictPostAppearance(Appearance preAppearance) {
//		Appearance postAppearance = ExperimentImpl.createOrGet(preAppearance, this).predictPostAppearance();
//		if (postAppearance == null)
//			postAppearance = preAppearance; 	
//		return postAppearance;
//	}
	
	public void trace(ITracer tracer, Object e) {
		
		String actList = "";
		for (Act act : this.acts)
			actList += ", " + act.getLabel();

		tracer.addSubelement(e, "action", this.label + actList);
	}

	public String toString(){
		String label = getLabel();
		for (Act primitive : this.acts)
			label += ", " + primitive.getLabel();
//		label += " failing: ";
//		for (Act primitive : this.failingActs)
//			label += " " + primitive.getLabel();
		return label;
	}
}
