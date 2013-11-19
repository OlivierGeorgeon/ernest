package eca.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.media.j3d.Transform3D;
import eca.construct.egomem.Displacement;
import eca.construct.egomem.DisplacementImpl;
import eca.construct.experiment.ExperimentImpl;
import eca.ss.Appearance;
import eca.ss.enaction.Act;

/**
 * An Action that can be performed in the external world.
 * An Action is intended to be performed on an Appearance
 *   It maintains the list of appearance to which it applies and provides the expected Acts and Displacements. 
 * An action conflates primitive interactions based on the fact that they are alternative to each other.
 * @author Olivier
 */
public class ActionImpl implements Action {

	private static Map<String , Action> ACTIONS = new LinkedHashMap<String , Action>() ;
	//private static int index = 0;

	private String label;
	private List<Act> succeedingActs = new ArrayList<Act>();
	private List<Act> failingActs = new ArrayList<Act>();

	/**
	 * Create or get an action from its label.
	 * @param label The action's label
	 * @return The created or retrieved action.
	 */
	public static Action createOrGet(String label){
		if (!ACTIONS.containsKey(label))
			ACTIONS.put(label, new ActionImpl(label));			
		return ACTIONS.get(label);
	}
	
	/**
	 * Creates a new action using an incremental label
	 * @return The new created action
	 */
//	public static Action createNew(){
//		index++;
//		String key = index + "";
//		ACTIONS.put(key, new ActionImpl(key));			
//		return ACTIONS.get(key);
//	}
	
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
				for (Act p : action.getSucceedingActs())
					intendedAction.addSucceedingAct(p);
				ACTIONS.remove(action.getLabel());
			}
			intendedAction.addSucceedingAct(act);
		}
	}
	
	private ActionImpl(String label){
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void addSucceedingAct(Act act){
		if (!this.succeedingActs.contains(act))
				this.succeedingActs.add(act);
	}
	
	public List<Act> getSucceedingActs(){
		return this.succeedingActs;
	}
	
	public void addFailingAct(Act act){
		if (!this.failingActs.contains(act))
				this.failingActs.add(act);
	}
	
	public List<Act> getFailingActs(){
		return this.failingActs;
	}
	
	public boolean contains(Act act){
		return this.succeedingActs.contains(act);
	}

	public boolean containsAct(Act act){
		return this.succeedingActs.contains(act) || this.failingActs.contains(act);
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

	public Act predictAct(Appearance appearance) {
		Act predictAct = null; 
		predictAct = ExperimentImpl.createOrGet(appearance, this).predictAct();
		if (predictAct == null)
			predictAct = succeedingActs.get(0);
		return predictAct;
	}

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
	
	public String toString(){
		String label = getLabel();
		for (Act primitive : this.succeedingActs)
			label += " " + primitive.getLabel();
		return label;
	}
}
