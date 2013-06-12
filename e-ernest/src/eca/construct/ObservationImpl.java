package eca.construct;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eca.construct.egomem.Area;
import eca.construct.egomem.AreaImpl;
import eca.ss.enaction.Act;


public class ObservationImpl implements Observation {
	
	private static Map<String , Observation> OBSERVATIONS = new HashMap<String , Observation>() ;
	private static int index = 0;

	private String label;
	private Phenomenon phenomenon;
	private Area area;
	private List<Act> acts = new ArrayList<Act>();
	
	/**
	 * @param phenomenon The observation's aspect
	 * @param area The observation's area
	 * @return The new or old observation
	 */
	public static Observation createOrGet(Phenomenon phenomenon, Area area){
		String key = createKey(phenomenon, area);
		if (!OBSERVATIONS.containsKey(key))
			OBSERVATIONS.put(key, new ObservationImpl(phenomenon, area));			
		return OBSERVATIONS.get(key);
	}

	public static Observation createOrGet(String label){
		if (!OBSERVATIONS.containsKey(label))
			OBSERVATIONS.put(label, new ObservationImpl(label));			
		return OBSERVATIONS.get(label);
	}

	private static String createKey(Phenomenon phenomenon, Area area) {
		String key = phenomenon.getLabel() + area.getLabel();
		return key;
	}
	
	public static Observation createNew(){
		index++;
		OBSERVATIONS.put(index + "", new ObservationImpl(index +""));			
		return OBSERVATIONS.get(index + "");
	}
	
	private ObservationImpl(String label){
		this.label = label;
	}	
	
	private ObservationImpl(Phenomenon phenomenon, Area area){
		this.label = phenomenon.getLabel() + area.getLabel();
		this.phenomenon = phenomenon;
		this.area = area;
	}	
	
	public String getLabel() {
		return this.label;
		//return this.aspect.getLabel() + this.area.getLabel();
	}
	
	public Phenomenon getAspect(){
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
	
	public void addAct(Act act){
		if (!this.acts.contains(act))
				this.acts.add(act);
	}
	
	public List<Act> getActs(){
		return this.acts;
	}

}
