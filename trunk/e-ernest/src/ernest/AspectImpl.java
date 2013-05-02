package ernest;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AspectImpl implements Aspect {
	
	String label;
	private static Map<String , Aspect> ASPECTS = new HashMap<String , Aspect>() ;
	
	static{
		ASPECTS.put(Aspect.APPEAR.getLabel(), Aspect.APPEAR);
		ASPECTS.put(Aspect.CLOSER.getLabel(), Aspect.CLOSER);
		ASPECTS.put(Aspect.DISAPPEAR.getLabel(), Aspect.DISAPPEAR);
		ASPECTS.put(Aspect.FARTHER.getLabel(), Aspect.FARTHER);
		ASPECTS.put(Aspect.MOVE.getLabel(), Aspect.MOVE);
		ASPECTS.put(Aspect.UNCHANGED.getLabel(), Aspect.UNCHANGED);
	}
	
	public static Aspect getAspect(String label){
		return ASPECTS.get(label);
	}
	
	public static void addAspect(Aspect aspect){
		ASPECTS.put(aspect.getLabel(), aspect);
	}
	
	public AspectImpl(String label){
		this.label = label;
	}

	public String getLabel() {
		return this.label;
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
