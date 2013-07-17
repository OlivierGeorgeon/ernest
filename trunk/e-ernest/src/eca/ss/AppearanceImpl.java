package eca.ss;


import java.util.HashMap;
import java.util.Map;

import eca.construct.Area;
import eca.construct.PhenomenonType;

/**
 * An Appearance is the observation of an instance of a phenomenonType in an area.
 * An Appearance may also be called an Observation
 * @author Olivier
 */
public class AppearanceImpl implements Appearance {
	
	private static Map<String , Appearance> OBSERVATIONS = new HashMap<String , Appearance>() ;
	private static int index = 0;
	private String label;
	private PhenomenonType phenomenonType;
	private Area area;
	
	/**
	 * @param phenomenonType The observation's aspect
	 * @param area The observation's area
	 * @return The new or old observation
	 */
	public static Appearance createOrGet(PhenomenonType phenomenonType, Area area){
		String label = createKey(phenomenonType, area);
		if (!OBSERVATIONS.containsKey(label))
			OBSERVATIONS.put(label, new AppearanceImpl(phenomenonType, area));			
		return OBSERVATIONS.get(label);
	}

	private static String createKey(PhenomenonType phenomenonType, Area area) {
		String key = phenomenonType.getLabel() + area.getLabel();
		return key;
	}
	
	private AppearanceImpl(PhenomenonType phenomenonType, Area area){
		this.label = phenomenonType.getLabel() + area.getLabel();
		this.phenomenonType = phenomenonType;
		this.area = area;
	}	
	
	public String getLabel() {
		return this.label;
		//return this.aspect.getLabel() + this.area.getLabel();
	}
	
	public PhenomenonType getPhenomenonType(){
		return this.phenomenonType;
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
			Appearance other = (Appearance)o;
			ret = (other.getLabel().equals(this.getLabel()));
		}
		return ret;
	}
	
}
