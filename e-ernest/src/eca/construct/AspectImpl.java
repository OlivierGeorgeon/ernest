package eca.construct;

import java.util.HashMap;
import java.util.Map;
import utils.ErnestUtils;

/**
 * A visual aspect of a phenomenon type.
 * @author Olivier
 */
public class AspectImpl implements Aspect {

	private static Map<String , Aspect> ASPECTS = new HashMap<String , Aspect>() ;
	private int displayCode;

	/**
	 * @param displayCode this aspect's label
	 * @return The aspect
	 */
	public static Aspect createOrGet(int displayCode){
		String key = ErnestUtils.hexColor(displayCode);
		if (!ASPECTS.containsKey(key))
			ASPECTS.put(key, new AspectImpl(displayCode));			
		return ASPECTS.get(key);
	}
	
	private AspectImpl(int displayCode){
		this.displayCode = displayCode;
	}
	
	public int getCode() {
		return displayCode;
	}

	/**
	 * Aspects are equal if they have the same display code. 
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
			ret = (other.getCode() == this.displayCode);
		}
		
		return ret;
	}
	
	public String toString(){
		return ErnestUtils.hexColor(this.displayCode);
	}

}
