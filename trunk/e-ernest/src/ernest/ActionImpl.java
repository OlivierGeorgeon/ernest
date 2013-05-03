package ernest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imos2.IProposition;

/**
 * An action represents the active part of an interaction.
 * @author Olivier
 */
public class ActionImpl implements Action {

	/** Step forward action */
	public static final Action STEP = new ActionImpl(">");
	
	/** Turn left action */
	public static final Action TURN_LEFT = new ActionImpl("^");
	
	/** Turn right action */
	public static final Action TURN_RIGHT = new ActionImpl("v");

	private static Map<String , Action> ACTIONS = new HashMap<String , Action>() ;

	private String label;
	private int propositionWeight;
	
	static{
		ACTIONS.put(STEP.getLabel(), STEP);
		ACTIONS.put(TURN_LEFT.getLabel(), TURN_LEFT);
		ACTIONS.put(TURN_RIGHT.getLabel(), TURN_RIGHT);
	}
	
	public static Action createOrGet(String label){
		if (!ACTIONS.containsKey(label))
			ACTIONS.put(label, new ActionImpl(label));			
		return ACTIONS.get(label);
	}
	
	public static Collection<Action> getACTIONS(){
		// Oddly, i could not directly cast ACTIONS.values() to List<Action>
//		List<Action> listActions = new ArrayList<Action>();
//		for (Action a : ACTIONS.values())
//			listActions.add(a);
		return ACTIONS.values();
	}
	
	public static Action categorize(Primitive interaction) {
		// The action of a primitive interaction is given by the first character of its label
		// TODO learn actions without using assumption about the interaction's label.
		String actionLabel = interaction.getLabel().substring(0, 1);

		if (!ACTIONS.containsKey(actionLabel))
			ACTIONS.put(actionLabel, new ActionImpl(actionLabel));
		return ACTIONS.get(actionLabel); 
	}
	
	private ActionImpl(String label){
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
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
	public int compareTo(Object modality) 
	{
		Action m = (Action)modality;
		//Transferred propositions are smaller 
		int c = - new Integer(this.propositionWeight).compareTo(new Integer(m.getPropositionWeight()));
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
}
