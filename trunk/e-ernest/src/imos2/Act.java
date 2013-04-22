package imos2;

import javax.vecmath.Point3f;

/**
 * A structure used to handle the enaction of an interaction
 * @author ogeorgeon
 */
public class Act implements IAct 
{
	private IInteraction interaction;
	private Point3f position = new Point3f();
	
	/**
	 * @param interaction This act's interaction
	 * @param position This act's position relative to the agent
	 */
	public  Act(IInteraction interaction, Point3f position)
	{
		this.interaction = interaction;
		this.position.set(position);
	}
	
	public String getLabel()
	{
		return this.interaction.getLabel() + "(" + Math.round(this.position.x) + "," + Math.round(this.position.y) +")";
	}
	
	/**
	 * Acts are equal if they have the same interaction and the same label. 
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
			IAct other = (IAct)o;
			ret = (other.getLabel().equals(getLabel()));
		}
		
		return ret;
	}


}
