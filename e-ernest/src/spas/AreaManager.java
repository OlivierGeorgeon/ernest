package spas;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;

import ernest.ITracer;

import utils.ErnestUtils;

/**
 * Categorize the surrounding space into areas left(A)/front(B)/right(C).
 * @author Olivier
 */
public class AreaManager implements IAreaManager {
	
	public final String A = "A"; // Left
	public final String B = "B"; // Front
	public final String C = "C"; // Right
	
	private Map<String , IArea> areas = new HashMap<String , IArea>() ;
	
	AreaManager(){
		this.areas.put(A, new Area(A));
		this.areas.put(B, new Area(B));
		this.areas.put(C, new Area(C));
	}
	
	public IArea categorize(Point3f point) 
	{
		if (ErnestUtils.polarAngle(point) > .1f)
			return areas.get(A); 
		else if (ErnestUtils.polarAngle(point) >= -.1f)
			return areas.get(B); 
		else
			return areas.get(C); 
	}
	
	public void clearAll(){
		for (IArea a : areas.values())
			a.setOccupied(false);
	}

	public String simulateShiftLef() {
		String stimulusLabel = "+";
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
		String stimulusLabel = "+";
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
		String areaLabel = B;
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
	
	public void trace(ITracer tracer)
	{
		if (tracer != null)
		{
			Object localSpace = tracer.addEventElement("local_space");
			tracer.addSubelement(localSpace, "position_8", "FFFFFF");
			tracer.addSubelement(localSpace, "position_7", "FFFFFF");
			tracer.addSubelement(localSpace, "position_6", "FFFFFF");
			
			if (areas.get(A).isOccupied()){
				tracer.addSubelement(localSpace, "position_5", "9680FF");
				tracer.addSubelement(localSpace, "position_4", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (areas.get(B).isOccupied()){
				tracer.addSubelement(localSpace, "position_3", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (areas.get(C).isOccupied()){
				tracer.addSubelement(localSpace, "position_2", "9680FF");
				tracer.addSubelement(localSpace, "position_1", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_2", "FFFFFF");
				tracer.addSubelement(localSpace, "position_1", "FFFFFF");
			}
			tracer.addSubelement(localSpace, "position_0", "FFFFFF");
		}
	}
}
