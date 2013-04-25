package spas;

/**
 * An area of the agent's surrounding space.
 * @author Olivier
 */
public class Area implements IArea {

	private String label;
	
	Area(String label)
	{
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * areas are equal if they have the same label. 
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
			IArea other = (IArea)o;
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}


}
