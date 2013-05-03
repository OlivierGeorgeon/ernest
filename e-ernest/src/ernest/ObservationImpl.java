package ernest;

import java.util.HashMap;
import java.util.Map;

import spas.AreaImpl;
import spas.Area;

public class ObservationImpl implements Observation {
	
	Aspect aspect;
	Area area;
	
	private static Map<String , Observation> OBSERVATIONS = new HashMap<String , Observation>() ;

	/**
	 * @param aspect The observation's aspect
	 * @param area The observation's area
	 * @return The new or old observation
	 */
	public static Observation createOrGet(Aspect aspect, Area area){
		String key = createKey(aspect, area);
		if (!OBSERVATIONS.containsKey(key))
			OBSERVATIONS.put(key, new ObservationImpl(aspect, area));			
		return OBSERVATIONS.get(key);
	}

	private static String createKey(Aspect aspect, Area area) {
		String key = aspect.getLabel() + area.getLabel();
		return key;
	}
	
	private ObservationImpl(Aspect aspect, Area area){
		this.aspect = aspect;
		this.area = area;
	}	
	
	public String getLabel() {
		return this.aspect.getLabel() + this.area.getLabel();
	}
	
	public Aspect getAspect(){
		return this.aspect;
	}
	
	public Area getArea(){
		return this.area;
	}
	
	/**
	 * Observations are equal if they have the same label. 
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
			Observation other = (Observation)o;
			ret = (other.getLabel().equals(this.getLabel()));
		}
		return ret;
	}

}
