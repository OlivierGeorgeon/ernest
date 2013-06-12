package ernest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eca.Primitive;
import eca.spas.egomem.Transformation;
import spas.SimuImpl;

/**
 * An action that may be performed by interactions.
 * @author Olivier
 */
public class ActionImpl implements Action {

	private static Map<String , Action> ACTIONS = new HashMap<String , Action>() ;
	private static int index = 0;

	private String label;
	private int propositionWeight;
	private Transformation transformation = SimuImpl.UNKNOWN;
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
			for (Primitive primitive : enactedAction.getPrimitives())
				primitive.setAction(intendedAction);
			if (!enactedAction.getTransformation().equals(SimuImpl.UNKNOWN))
				intendedAction.setTransformation(enactedAction.getTransformation());
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
	
	public int getPropositionWeight() {
		return this.propositionWeight;
	}

	public void setPropositionWeight(int propositionWeight) {
		this.propositionWeight = propositionWeight;
	}

	public void addPropositionWeight(int weight){
		this.propositionWeight += weight;
	}

	/**
	 * Actions are compared according to their proposition weight. 
	 */
	public int compareTo(Object action) 
	{
		Action a = (Action)action;
		int c = - new Integer(this.propositionWeight).compareTo(new Integer(a.getPropositionWeight()));
		return c; 
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

	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}

	public Transformation getTransformation() {
		return this.transformation;
	}
}
