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

	private static Map<String , Action> ACTIONS = new HashMap<String , Action>() ;

	private String label;
	private int propositionWeight;
	
	public static Action createOrGet(String label){
		if (!ACTIONS.containsKey(label))
			ACTIONS.put(label, new ActionImpl(label));			
		return ACTIONS.get(label);
	}
	
	public static Collection<Action> getACTIONS(){
		return ACTIONS.values();
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
