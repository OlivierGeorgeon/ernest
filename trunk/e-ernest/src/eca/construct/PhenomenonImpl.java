package eca.construct;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eca.Primitive;
import eca.ss.enaction.Act;

/**
 * An Aspect is the element of an observation that can be situated in space
 * @author Olivier
 */
public class PhenomenonImpl implements Phenomenon {
	
	private static Map<String , Phenomenon> ASPECTS = new HashMap<String , Phenomenon>() ;
	private static int index = 0;
	
	private String label;
	private List<Primitive> primitives = new ArrayList<Primitive>();

	/**
	 * @param label The aspect's label
	 * @return The aspect
	 */
	public static Phenomenon createOrGet(String label){
		if (!ASPECTS.containsKey(label))
			ASPECTS.put(label, new PhenomenonImpl(label));			
		return ASPECTS.get(label);
	}
	
	/**
	 * Creates a new aspect using an incremental label
	 * @return The new created aspect
	 */
	public static Phenomenon createNew(){
		index++;
		ASPECTS.put(index + "", new PhenomenonImpl(index +""));			
		return ASPECTS.get(index + "");
	}
	
	/**
	 * Merge the new aspect into the previous aspect.
	 * The interactions attached to the new aspect are transferred to the previous aspect and the new aspect is removed
	 * @param enactedAspect The first action from which to merge (removed). 
	 * @param previousAspect The second action to which to merge (kept).
	 */
	public static void merge(Phenomenon enactedAspect, Phenomenon previousAspect){
		if (!enactedAspect.equals(previousAspect)){
			for (Primitive act : enactedAspect.getPrimitives())
				act.setPhenomenonType(previousAspect);
			ASPECTS.remove(enactedAspect.getLabel());
		}
	}
	
	private PhenomenonImpl(String label){
		this.label = label;
	}

	public String getLabel(){
		return this.label;
	}
	
	public void addPrimitive(Primitive act){
		if (!this.primitives.contains(act))
				this.primitives.add(act);
	}
	
	public List<Primitive> getPrimitives(){
		return this.primitives;
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
			Phenomenon other = (Phenomenon)o;
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}

	public static Collection<Phenomenon> getAspects() {
		return ASPECTS.values();
	}
	
	public String toString(){
		String s = this.label;
		for (Primitive i : primitives)
			s += " " + i.getLabel();
		return s;
	}

}
