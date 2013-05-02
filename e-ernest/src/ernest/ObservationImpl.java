package ernest;

import java.util.HashMap;
import java.util.Map;

import spas.Area;
import spas.IArea;

public class ObservationImpl implements Observation {
	
	Aspect aspect;
	IArea area;
	
	private static Map<String , Observation> OBSERVATIONS = new HashMap<String , Observation>() ;

	static{
		for(Aspect a : AspectImpl.getAspects())
			for(IArea area : Area.getAREAS())
				OBSERVATIONS.put(createKey(a,area), new ObservationImpl(a,area));
	}
	
	private ObservationImpl(Aspect aspect, IArea area){
		this.aspect = aspect;
		this.area = area;
	}

	public static Observation createOrGet(Aspect aspect, IArea area){
		String key = createKey(aspect, area);
		if (!OBSERVATIONS.containsKey(key))
			OBSERVATIONS.put(key, new ObservationImpl(aspect, area));			
		return OBSERVATIONS.get(key);
	}

	private static String createKey(Aspect aspect, IArea area) {
		String key = aspect.getLabel() + area.getLabel();
		return key;
	}
	
	
	
	public String getLabel() {
		return this.aspect.getLabel() + this.area.getLabel();
	}
	
	public Aspect getAspect(){
		return this.aspect;
	}
	
	public IArea getArea(){
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
