package ernest;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AspectImpl implements Aspect {
	
	private static Map<String , Aspect> ASPECTS = new HashMap<String , Aspect>() ;
	
	private String label;

	public static Aspect createOrGet(String label){
		if (!ASPECTS.containsKey(label))
			ASPECTS.put(label, new AspectImpl(label));			
		return ASPECTS.get(label);
	}
	
	public static Aspect getAspect(String label){
		return ASPECTS.get(label);
	}
	
	public static void addAspect(Aspect aspect){
		ASPECTS.put(aspect.getLabel(), aspect);
	}
	
	private AspectImpl(String label){
		this.label = label;
	}

	public String getLabel(){
		return this.label;
	}
	
//	public static Aspect categorize(Primitive interaction) {
//		// The action of a primitive interaction is given by the first character of its label
//		// TODO learn actions without using assumption about the interaction's label.
//		String aspectLabel = interaction.getLabel().substring(1, 2);
//
//		if (!ASPECTS.containsKey(aspectLabel))
//			ASPECTS.put(aspectLabel, new AspectImpl(aspectLabel));
//		return ASPECTS.get(aspectLabel); 
//	}
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
