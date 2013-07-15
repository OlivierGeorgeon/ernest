package eca.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import eca.Primitive;
import eca.construct.egomem.AreaImpl;
import eca.construct.egomem.Displacement;
import eca.construct.egomem.DisplacementImpl;
import eca.construct.experiment.ExperimentImpl;
import eca.ss.Appearance;
import eca.ss.enaction.Act;
import eca.ss.enaction.ActImpl;

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
			for (Primitive primitive : enactedAction.getSuccessInteractions())
				intendedAction.addPrimitive(primitive);
				//primitive.setAction(intendedAction);
			ACTIONS.remove(enactedAction.getLabel());
		}
	}
	
	public static void merge(Primitive primitive, Action intendedAction){
		if (!intendedAction.contains(primitive)){
			intendedAction.addPrimitive(primitive);
			Action action = null;
			for (Action a : getACTIONS()){
				if (a.contains(primitive))
					a = action;
			}
			ACTIONS.remove(action);
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
	
	public List<Primitive> getSuccessInteractions(){
		return this.primitives;
	}
	
	public boolean contains(Primitive primitive){
		return this.primitives.contains(primitive);
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
		Act predictAct = ExperimentImpl.createOrGet(appearance, this).predictAct();
		if (predictAct == null)
			predictAct = ActImpl.createOrGetPrimitiveAct(primitives.get(0), AreaImpl.createOrGet(new Point3f()));
		return predictAct;
	}

	public Displacement predictDisplacement(Appearance appearance) {
		Displacement predictDisplacement = ExperimentImpl.createOrGet(appearance, this).predictDisplacement();
		if (predictDisplacement == null)
			predictDisplacement = DisplacementImpl.createOrGet(new Transform3D());
		return predictDisplacement;
	}

	public Appearance predictPostAppearance(Appearance preAppearance) {
		Appearance postAppearance = ExperimentImpl.createOrGet(preAppearance, this).predictPostAppearance();
		if (postAppearance == null)
			postAppearance = preAppearance; 	
		return postAppearance;
	}
}
