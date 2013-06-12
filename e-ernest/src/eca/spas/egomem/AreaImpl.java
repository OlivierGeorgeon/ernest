package eca.spas.egomem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;

import ernest.Phenomenon;
import ernest.PhenomenonImpl;

import utils.ErnestUtils;

/**
 * An area of the agent's surrounding space.
 * @author Olivier
 */
public class AreaImpl implements Area {

	private static Map<String , Area> AREAS = new HashMap<String , Area>() ;

	private String label;
	
	public static Area createOrGet(String label){
		if (!AREAS.containsKey(label))
			AREAS.put(label, new AreaImpl(label));			
		return AREAS.get(label);
	}
	
	/**
	 * @return A collection of the existing areas.
	 */
	public static Collection<Area> getAREAS() {
		return AREAS.values();
	}
	
	/**
	 * @param label The area's label.
	 */
	public AreaImpl(String label){
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * areas are equal if they have the same label. 
	 */
	public boolean equals(Object o){
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			Area other = (Area)o;
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}

}
