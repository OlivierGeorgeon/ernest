package eca;

import eca.construct.Area;
import eca.construct.egomem.Displacement;

/**
 * A primitive interaction.
 * @author Olivier
 */
public interface Primitive 
{
	/**
	 * @return The primitive interaction's label
	 */
	public String getLabel();
	/**
	 * @return The primitive interaction's value (multiplied by 10)
	 */
	public int getValue();
	
	/**
	 * @param displacement The displacement to record to this experiment
	 */
	public void incDisplacementCounter(Displacement displacement);
	
	/**
	 * @param area The area from which we want to predict the displacement
	 * @return The displacement most probably resulting from this experiment
	 */
	public Displacement predictDisplacement(Area area);
	
	public String getDisplacementLabels();
	
}
