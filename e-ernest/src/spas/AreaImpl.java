package spas;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * An area of the agent's surrounding space.
 * @author Olivier
 */
public class AreaImpl implements Area {

	public static final Area A = new AreaImpl("A");
	public static final Area B = new AreaImpl("B");
	public static final Area C = new AreaImpl("C");
	
	private String label;
	private boolean occupied;
	private boolean previousOccupied;
	
	private static Map<String , Area> AREAS = new HashMap<String , Area>() ;
	
	static{
		AREAS.put(A.getLabel(), A);
		AREAS.put(B.getLabel(), B);
		AREAS.put(C.getLabel(), C);
	}
	
	public AreaImpl(String label){
		this.label = label;
	}
	
	public static Collection<Area> getAREAS() {
		return AREAS.values();
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public boolean isOccupied() {
		return occupied;
	}

	public void clear(){
		this.occupied = false;
	}
	
	public void setOccupied(boolean occupied) {
		this.previousOccupied = this.occupied;
		this.occupied = occupied;
	}

	public String getEvent(){
		if (this.previousOccupied == this.occupied)
			return "_"; // unchanged
		else if (this.previousOccupied)
			return "o"; // disappear
		else 
			return "*"; // appear
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
