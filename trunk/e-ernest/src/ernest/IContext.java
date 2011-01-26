package ernest;

import java.util.List;

/**
 * Give access to Ernest's Working Memory.
 * Used to store and retrieve elements of Ernest's internal representation of its situation at a given point in time.
 * @author ogeorgeon
 */
public interface IContext 
{

	/**
	 * Give the context to learn new schemas with the first learning mechanism.
	 * @return The list of acts in the context list.
	 */
	public  List<IAct> getContextList();

	/**
	 * Give the base context to learn new schemas with the second learning mechanism.
	 * @return The list of acts in the base context list.
	 */
	public  List<IAct> getBaseContextList();

	/**
	 * Give the context to select the next schemas to enact in the next decision cycle.
	 * @return The list of acts in the activation list.
	 */
	public  List<IAct> getActivationList();

	/**
	 * @return The primitive intended act in the current automatic loop.
	 */
	public  IAct getPrimitiveIntention();
	
	/**
	 * @return The primitive actually enacted act
	 */
	public  IAct getPrimitiveEnaction();

	/**
	 * @return The intention act decided during the last decision cycle.
	 */
	public  IAct getIntentionAct();
	
	/**
	 * @param act the primitive intended act. 
	 */
	public void setPrimitiveIntention(IAct act);
	
	/**
	 * @param act The primitive actually enacted act.
	 */
	public void setPrimitiveEnaction(IAct act);

	/**
	 * @param act The intention act decided during the decision cycle.
	 */
	public void setIntentionAct(IAct act);
	
	/**
	 * Add an act to the context and to the activation list. 
	 * @param act The act that will be added to the context list and to the focus list.
	 */
	public void addActivationAct(IAct act);
	
	/**
	 * Add an act to the context list. 
	 * @param act The act to add.
	 */
	public void addContextAct(IAct act);

	/**
	 * Add a list of acts to the context list (scope). 
	 * This list is used to learn new schemas in the next decision cycle.
	 * @param actList The context to append in this context.
	 */
	public void addContextList(List<IAct> actList);

	/**
	 * Shift the context when a decision cycle terminates and the next begins.
	 * The context list is passed to the base context list.
	 * The activation list is reinitialized from the enacted act and the performed act.
	 * The context list is reinitialized from the activation list an the additional list provided as a parameter. 
	 * @param enactedAct The act that was actually enacted during the terminating decision cycle.
	 * @param performedAct The act that was performed during the terminating decision cycle.
	 * @param contextList The additional acts to add to the new context list
	 */
	public void shiftDecisionCycle(IAct enactedAct, IAct performedAct, List<IAct> contextList);

}
