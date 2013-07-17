package eca.construct;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Point3f;
import utils.ErnestUtils;

/**
 * An area of the agent's surrounding space.
 * @author Olivier
 */
public class AreaImpl implements Area {

	/** Predefined areas */
	public static String A = "A";
	public static String B = "B";
	public static String C = "C";
	public static String O = "O";

	private static Map<String , Area> AREAS = new HashMap<String , Area>() ;

	private String label;
	
	/**
	 * @param point The point from which to create or get the area
	 * @return The area
	 */
	public static Area createOrGet(Point3f point){
		String label = createKey(point);
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
	
	private static String createKey(Point3f point) {
		String key = "";
		if (point.epsilonEquals(new Point3f(), .1f) )//|| point.x < 0)
			key = O;
		else if (ErnestUtils.polarAngle(point) > .1f)
			key = A; 
		else if (ErnestUtils.polarAngle(point) >= -.1f)
			key = B; 
		else
			key = C; 
		return key;
	}
	/**
	 * @return The point prototypical of this area
	 */
	public Point3f getPoint(){
		Point3f spasPoint = new Point3f(1, 0, 0);
		if (label.equals(A))
			spasPoint.set((float)Math.cos(Math.PI/4), (float)Math.sin(Math.PI/4), 0);
		else if (label.equals(C))
			spasPoint.set((float)Math.cos(Math.PI/4),-(float)Math.sin(Math.PI/4), 0);
		else if (label.equals(O))
			spasPoint.set(0,0, 0);
		spasPoint.scale(3);
		return spasPoint;
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
