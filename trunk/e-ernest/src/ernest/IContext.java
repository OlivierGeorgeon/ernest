package ernest;

import java.util.ArrayList;
import java.util.List;

/**
 * A context is Ernest's internal representation of its situation at a given point in time 
 * @author ogeorgeon
 */
public interface IContext 
{

	public  List<IAct> getContextList();
	public  List<IAct> getFocusList();
	public  IAct getCoreAct();

	public void setCoreAct(IAct act);
	public void addFocusAct(IAct act);
	public void addContextAct(IAct act);
}
