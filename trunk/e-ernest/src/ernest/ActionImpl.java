package ernest;

import imos2.IProposition;

/**
 * An action represents the active part of an interaction.
 * @author Olivier
 */
public class ActionImpl implements Action {

	private String label;
	private int propositionWeight;
	
	ActionImpl(String label){
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
