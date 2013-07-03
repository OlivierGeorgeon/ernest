package eca;

import eca.construct.Action;
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
	 * @return The action performed by this primitive interaction
	 */
	public Action getAction();
	
	/**
	 * @param action The action performed by this primitive interaction.
	 */
	public void setAction(Action action);
	
	/**
	 * @return The type of phenomenon observed by this primitive interaction.
	 */
	public PhenomenonType getPhenomenonType();
	
	/**
	 * @param phenomenonType The type phenomenon observed by this primitive interaction.
	 */
	public void setPhenomenonType(PhenomenonType phenomenonType);
	
	public void setDisplace(Displacement trans);
	public Displacement getDisplace();
}
