package ernest;

import imos2.IProposition;

/**
 * An modality is a set if interactions that are alternate to each other.
 * @author Olivier
 */
public class Modality implements IModality {

	private String label;
	private IPrimitive prototypeInteraction;
	private int propositionWeight;
	
	Modality(String label)
	{
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void setPrototypeInteraction(IPrimitive interaction){
		this.prototypeInteraction = interaction;
	}
	
	public IPrimitive getPrototypeInteraction(){
		return this.prototypeInteraction;
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
	 * Modalities are compared according to their proposition weight. 
	 */
	public int compareTo(Object modality) 
	{
		IModality m = (IModality)modality;
		//Transferred propositions are smaller 
		int c = - new Integer(this.propositionWeight).compareTo(new Integer(m.getPropositionWeight()));
		return c; 
	}

	/**
	 * Modalities are equal if they have the same label. 
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
			IModality other = (IModality)o;
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}
}
