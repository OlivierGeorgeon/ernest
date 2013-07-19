package eca;

import eca.construct.Area;
import eca.construct.PhenomenonType;
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
	 * @return The type of phenomenon observed through this primitive interaction.
	 */
	public PhenomenonType getPhenomenonType();
	
	/**
	 * @param phenomenonType The type of phenomenon observed through this primitive interaction.
	 */
	public void setPhenomenonType(PhenomenonType phenomenonType);
	
	/**
	 * @param displacement The displacement to record to this experiment
	 */
	public void incDisplacementCounter(Displacement displacement);
	
	/**
	 * @return The displacement most probably resulting from this experiment
	 */
	public Displacement predictDisplacement(Area area);
	
}
