package spas;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;

import utils.ErnestUtils;

/**
 * Categorize the surrounding space into areas left(A)/front(B)/right(C).
 * @author Olivier
 */
public class AreaManager implements IAreaManager {
	
	public final String A = "A";
	public final String B = "B";
	public final String C = "C";
	
	private Map<String , IArea> areas = new HashMap<String , IArea>() ;
	
	public IArea categorize(Point3f point) 
	{
		if (ErnestUtils.polarAngle(point) > .1f)
		{
			// Area A: left
			if (!this.areas.containsKey(A))
				this.areas.put(A, new Area(A));
			return areas.get(A); 
		}
		else if (ErnestUtils.polarAngle(point) >= -.1f)
		{
			// Area B: front
			if (!this.areas.containsKey(B))
				this.areas.put(B, new Area(B));
			return areas.get(B); 
		}
		else
		{
			// Area C: right
			if (!this.areas.containsKey(C))
				this.areas.put(C, new Area(C));
			return areas.get(C); 
		}
	}
	
	public void clearAll(){
		for (IArea a : areas.values())
			a.setOccupied(false);
	}

	public String simulateShiftLef() {
		String stimulusLabel = "_";
		String areaLabel ="";
		if (areas.get(A).isOccupied()){
			stimulusLabel = "o";
			areaLabel = A;
		}
		else if (areas.get(B).isOccupied()){
			stimulusLabel = "-";
			areaLabel = A;
		}
		else if (areas.get(C).isOccupied()){
			stimulusLabel = "=";
			areaLabel = A;			
		}
		else{
			stimulusLabel = "_";
			areaLabel = B;
		}
		return stimulusLabel + areaLabel;
	}

	public String simulateShiftRight() {
		String stimulusLabel = "_";
		String areaLabel ="";
		if (areas.get(C).isOccupied()){
			stimulusLabel = "o";
			areaLabel = C;
		}
		else if (areas.get(B).isOccupied()){
			stimulusLabel = "-";
			areaLabel = C;
		}
		else if (areas.get(A).isOccupied()){
			stimulusLabel = "=";
			areaLabel = C;			
		}
		else{
			stimulusLabel = "_";
			areaLabel = B;
		}
		return stimulusLabel + areaLabel;
	}

	public String simulateShiftForward() {
		String stimulusLabel = "_";
		String areaLabel ="";
		if (areas.get(A).isOccupied()){
			stimulusLabel = "+";
			areaLabel = A;
		}
		else if (areas.get(B).isOccupied()){
			stimulusLabel = "+";
			areaLabel = B;
		}
		else if (areas.get(C).isOccupied()){
			stimulusLabel = "+";
			areaLabel = C;
		}
		return stimulusLabel + areaLabel;
	}

	public void shiftLef() {
		areas.get(A).setOccupied(areas.get(B).isOccupied());
		areas.get(B).setOccupied(areas.get(C).isOccupied());
		areas.get(C).setOccupied(false);
	}

	public void shiftRight() {
		areas.get(C).setOccupied(areas.get(B).isOccupied());
		areas.get(B).setOccupied(areas.get(A).isOccupied());
		areas.get(A).setOccupied(false);
	}

	public IArea getArea(String areaLabel) {
		return areas.get(areaLabel);
	}
}
