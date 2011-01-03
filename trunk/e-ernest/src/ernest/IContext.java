package ernest;

import java.util.ArrayList;
import java.util.List;

/**
 * A context is Ernest's internal representation of its situation at a given point in time. 
 * @author ogeorgeon
 */
public interface IContext 
{

	public  List<IAct> getContextList();
	public List<IAct> getBaseContextList();
	public  List<IAct> getActivationList();
	public  IAct getPrimitiveIntention();
	public  IAct getPrimitiveEnaction();
	public  IAct getIntentionAct();

	/**
	 * Get the sensed icon that was set by the IconicModule.senseMatrix() method during the step's beginning
	 * @return The sensed icon
	 */
	public  IAct getSensedIcon();	

	
	public void setPrimitiveIntention(IAct a);
	public void setPrimitiveEnaction(IAct a);
	public void setIntentionAct(IAct a);
	
	public void addActivationAct(IAct a);
	public void addContextAct(IAct a);
	public void addContext(IContext context);
	public void addContextList(List<IAct> actList);
	public void addSensedIcon(IAct icon);

	public void removeIcons();
	
	/**
	 * Set the animation noème in the context.
	 * @param n The animation noème. 
	 */
	public void setAnimationNoeme(IAct n);
	
	/**
	 * @return the animation noème.
	 */
	public IAct getAnimationNoeme();
	
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

	/**
	 * Shift the context to terminate a step and begin a new one.
	 * The context list is passed to the base context list.
	 * The activation list is reinitialized from the enacted act and the performed act.
	 * The context list is reinitialized from the activation list an the additional list provided as a parameter. 
	 * @param icon The sensed icon.
	 */
	public void shiftStep(IAct[] pixelMatrix);

	
	public void setHomeostaticNoeme(IAct n);
	public IAct getHomeostaticNoeme();



}
