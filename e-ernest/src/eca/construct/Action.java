package eca.construct;

import java.util.List;
import eca.Primitive;
import eca.construct.egomem.Displacement;
import eca.ss.enaction.Act;

/**
 * An Action that can be performed in the external world.
 * An action conflates primitive interactions based on the fact that they are alternative to each other.
 * @author Olivier
 */
public interface Action
{
	/**
	 * @return This action's label
	 */
	public String getLabel();
	
	/**
	 * @param primitive The primitive interaction to add to this action.
	 */
	public void addPrimitive(Primitive primitive);

	/**
	 * @return The list of primitive interactions that perform this action.
	 */
	public List<Primitive> getPrimitives();
	
	/**
	 * Record the experiment of performing this action on this appearance.
	 * @param appearance The appearance on which this action is performed.
	 * @param act The act enacted during this experiment.
	 * @param displacement The displacement enacted during this experiment.
	 */
	public void recordExperiment(Appearance appearance, Act act, Displacement displacement);
	
	/**
	 * @param appearance The appearance on which the action is performed.
	 * @return The Act that will likely result from performing this action on this appearance.
	 */
	public Act predictAct(Appearance appearance);
	
	/**
	 * @param appearance The appearance on which the action is performed.
	 * @return The Displacement that will likely result from performing this action on this appearance.
	 */
	public Displacement predictDisplacement(Appearance appearance);

	public Appearance predictPostAppearance(Appearance preAppearance);

}
