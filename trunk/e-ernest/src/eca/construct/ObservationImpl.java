package eca.construct;


import java.util.HashMap;
import java.util.Map;
import eca.construct.egomem.Area;

public class ObservationImpl implements Observation {
	
	private static Map<String , Observation> OBSERVATIONS = new HashMap<String , Observation>() ;
	private static int index = 0;
	private String label;
	private Phenomenon phenomenon;
	private Area area;
	
	/**
	 * @param phenomenon The observation's aspect
	 * @param area The observation's area
	 * @return The new or old observation
	 */
	public static Observation createOrGet(Phenomenon phenomenon, Area area){
		String label = createKey(phenomenon, area);
		if (!OBSERVATIONS.containsKey(label))
			OBSERVATIONS.put(label, new ObservationImpl(phenomenon, area));			
		return OBSERVATIONS.get(label);
	}

//	public static Observation createOrGet(String label){
//		if (!OBSERVATIONS.containsKey(label))
//			OBSERVATIONS.put(label, new ObservationImpl(label));			
//		return OBSERVATIONS.get(label);
//	}

	private static String createKey(Phenomenon phenomenon, Area area) {
		String key = phenomenon.getLabel() + area.getLabel();
		return key;
	}
	
//	public static Observation createNew(){
//		index++;
//		return createOrGet(index + "");
//		OBSERVATIONS.put(index + "", new ObservationImpl(index +""));			
//		return OBSERVATIONS.get(index + "");
//	}
	
//	private ObservationImpl(String label){
//		this.label = label;
//	}	
	
	private ObservationImpl(Phenomenon phenomenon, Area area){
		this.label = phenomenon.getLabel() + area.getLabel();
		this.phenomenon = phenomenon;
		this.area = area;
	}	
	
	public String getLabel() {
		return this.label;
		//return this.aspect.getLabel() + this.area.getLabel();
	}
	
	public Phenomenon getPhenomenon(){
		return this.phenomenon;
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
