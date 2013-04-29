package ernest;

/**
 * A primitive interaction.
 * @author Olivier
 */
public class Primitive implements IPrimitive {

	private String label = "";
	private int value = 0;
	Primitive(String label, int value)
	{
		this.label = label;
		this.value = value;
	}
	
	public String getLabel() 
	{
		return this.label;
	}

	public int getValue() 
	{
		return this.value;
	}
	
	/**
	 * Interactions are equal if they have the same label. 
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
			IPrimitive other = (IPrimitive)o;
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}

	public String toString()
	{
		return this.label + "(" + this.value / 10 + ")";
	}
}
