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
	public  List<IAct> getFocusList();
	public  IAct getCoreAct();
	public  IAct getPrimitiveIntention();
	public  IAct getIntentionAct();
	
	public void setCoreAct(IAct a);
	public void setPrimitiveIntention(IAct a);
	public void setIntentionAct(IAct a);
	
	public void addFocusAct(IAct a);
	public void addContextAct(IAct a);
	public void addContext(IContext context);

	/**
	 * @param a The sensor act that represents the sensory system context.
	 */
//	public void setSensorAct(IAct a);
	/**
	 * @return The sensor act that represents the sensory system context.
	 */
// 	public IAct getSensorAct();
}
