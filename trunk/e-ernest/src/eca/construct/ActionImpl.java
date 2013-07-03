package eca.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import eca.Primitive;
import eca.construct.egomem.Displacement;
import eca.construct.experiment.Experiment;
import eca.construct.experiment.ExperimentImpl;
import eca.ss.enaction.Act;

/**
 * An Action that can be performed in the external world.
 * An Action is intended to be performed on an Appearance
 *   It maintains the list of appearance to which it applies and provides the expected Acts and Displacements. 
 * An action conflates primitive interactions based on the fact that they are alternative to each other.
 * @author Olivier
 */
public class ActionImpl implements Action {

	private static Map<String , Action> ACTIONS = new HashMap<String , Action>() ;
	private static int index = 0;

	private String label;
	private List<Primitive> primitives = new ArrayList<Primitive>();

	private Map<Appearance , Displacement> experiences = new HashMap<Appearance , Displacement>() ;

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
	public static Action createNew(){
		index++;
		String key = index + "";
		ACTIONS.put(key, new ActionImpl(key));			
		return ACTIONS.get(key);
	}
	
	/**
	 * @return The collection of all actions known by the agent.
	 */
	public static Collection<Action> getACTIONS(){
		return ACTIONS.values();
	}
	
	/**
	 * Merge the enacted action into the intended action.
	 * The interactions attached to the enactedAction are transferred to the intendedAction and the enactedAction is removed
	 * @param enactedAction The first action from which to merge (removed). 
	 * @param intendedAction The second action to which to merge (kept).
	 */
	public static void merge(Action enactedAction, Action intendedAction){
		if (!enactedAction.equals(intendedAction)){
			for (Primitive primitive : enactedAction.getPrimitives())
				primitive.setAction(intendedAction);
			ACTIONS.remove(enactedAction.getLabel());
		}
	}
	
	private ActionImpl(String label){
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void addPrimitive(Primitive primitive){
		if (!this.primitives.contains(primitive))
				this.primitives.add(primitive);
	}
	
	public List<Primitive> getPrimitives(){
		return this.primitives;
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
		return ExperimentImpl.createOrGet(appearance, this).predictAct();
	}

	public Displacement predictDisplacement(Appearance appearance) {
		return ExperimentImpl.createOrGet(appearance, this).predictDisplacement();
	}

	public Appearance predictPostAppearance(Appearance preAppearance) {
		return ExperimentImpl.createOrGet(preAppearance, this).predictPostAppearance();
	}

	public void recordExperiment(Appearance appearance, Act act, Displacement displacement) {
		Experiment ex = ExperimentImpl.createOrGet(appearance, this);
		ex.incActCounter(act);
		ex.incDisplacementCounter(displacement);
	}
}
