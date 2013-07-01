package eca.construct.egomem;

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
	 * Gives the area to which a point belongs.
	 * @param point The point
	 * @return The area of interest
	 */
	public static Area getArea(Point3f point) 
	{
		if (point.epsilonEquals(new Point3f(), .1f))
			return O;
		else if (ErnestUtils.polarAngle(point) > .1f)
			return A; 
		else if (ErnestUtils.polarAngle(point) >= -.1f)
			return B; 
		else
			return C; 
	}
	
	public static Point3f spasPoint(Area area){
		Point3f spasPoint = new Point3f(1, 0, 0);
		if (area.equals(A))
			spasPoint.set((float)Math.cos(Math.PI/4), (float)Math.sin(Math.PI/4), 0);
		else if (area.equals(C))
			spasPoint.set((float)Math.cos(Math.PI/4),-(float)Math.sin(Math.PI/4), 0);
		else if (area.equals(O))
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
