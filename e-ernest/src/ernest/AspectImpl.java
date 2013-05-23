package ernest;

import imos2.Act;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An Aspect is the element of an observation that can be situated in space
 * @author Olivier
 */
public class AspectImpl implements Aspect {
	
	private static Map<String , Aspect> ASPECTS = new HashMap<String , Aspect>() ;
	private static int index = 0;
	
	private String label;
	private List<Primitive> acts = new ArrayList<Primitive>();

	/**
	 * @param label The aspect's label
	 * @return The aspect
	 */
	public static Aspect createOrGet(String label){
		if (!ASPECTS.containsKey(label))
			ASPECTS.put(label, new AspectImpl(label));			
		return ASPECTS.get(label);
	}
	
	/**
	 * Creates a new aspect using an incremental label
	 * @return The new created aspect
	 */
	public static Aspect createNew(){
		index++;
		ASPECTS.put(index + "", new AspectImpl(index +""));			
		return ASPECTS.get(index + "");
	}
	
	/**
	 * Merge the enacted aspect into the intended aspect.
	 * The interactions attached to the enactedAction are transferred to the intendedAction and the enactedAction is removed
	 * @param enactedAspect The first action from which to merge (removed). 
	 * @param intendedAspect The second action to which to merge (kept).
	 */
	public static void merge(Aspect enactedAspect, Aspect intendedAspect){
		if (!enactedAspect.equals(intendedAspect)){
			for (Primitive act : enactedAspect.getPrimitives())
				act.setAspect(intendedAspect);
			ASPECTS.remove(enactedAspect.getLabel());
		}
	}
	
	private AspectImpl(String label){
		this.label = label;
	}

	public String getLabel(){
		return this.label;
	}
	
	public void addPrimitive(Primitive act){
		if (!this.acts.contains(act))
				this.acts.add(act);
	}
	
	public List<Primitive> getPrimitives(){
		return this.acts;
	}
	
	/**
	 * Features are equal if they have the same label. 
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
			Aspect other = (Aspect)o;
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}

	public static Collection<Aspect> getAspects() {
		return ASPECTS.values();
	}

}
