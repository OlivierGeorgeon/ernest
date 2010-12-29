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
	public  List<IAct> getActivationList();
	public  IAct getCoreAct();
	public  IAct getPrimitiveIntention();
	public  IAct getIntentionAct();
	public  IAct getSensedIcon();	

	
	public void setCoreAct(IAct a);
	public void setPrimitiveIntention(IAct a);
	public void setIntentionAct(IAct a);
	
	public void addActivationAct(IAct a);
	public void addContextAct(IAct a);
	public void addContext(IContext context);
	public void addSensedIcon(IAct icon);

	public void removeIcons();
	
	public void setAnimationNoeme(IAct n);
	public IAct getAnimationNoeme();
}
