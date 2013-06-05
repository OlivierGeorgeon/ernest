package imos2;

/**
 * A proposition that Ernest enacts an interaction. 
 * @author ogeorgeon
 */
public class Proposition implements IProposition 
{
	private Act act;
	private int weight = 0; 
	
	/**
	 * Constructor. 
	 * @param a The proposed interaction.
	 * @param w The proposal's weight.
	 */
	public Proposition(Act a, int w){
		act = a;
		weight = w;
	}

	public int compareTo(IProposition o){
		return new Integer(o.getWeight()).compareTo(weight);
	}

	public Act getAct(){
		return act;
	}

	public int getWeight(){
		return weight;
	}
	
	public void addWeight(int w){
		weight += w;
	}

	/**
	 * Two propositions are equal if they propose the same interaction. 
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
			Proposition other = (Proposition)o;
			ret = other.act == act;
		}
		
		return ret;
	}

	/**
	 * Generate a textual representation of the proposition for debug.
	 * @return A string that represents the proposition. 
	 */
	public String toString(){
		return act + " with weight = " + weight/10;
	}

}
