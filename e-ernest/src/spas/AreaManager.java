package spas;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;

import ernest.Action;
import ernest.ActionMemoryImpl;
import ernest.Aspect;
import ernest.ITracer;
import ernest.Observation;
import ernest.ObservationImpl;

import utils.ErnestUtils;

/**
 * Categorize the surrounding space into areas left(A)/front(B)/right(C).
 * @author Olivier
 */
public class AreaManager implements IAreaManager {
	
	public Area categorize(Point3f point) 
	{
		if (ErnestUtils.polarAngle(point) > .1f)
			return AreaImpl.A; 
		else if (ErnestUtils.polarAngle(point) >= -.1f)
			return AreaImpl.B; 
		else
			return AreaImpl.C; 
	}
	
	public void clearAll(){
		for (Area a : AreaImpl.getAREAS())
			a.setOccupied(false);
			//a.clear();
	}
	
	public Observation predict(Action action){
		if (action.equals(Action.STEP)){
			return simulateShiftForward();
		}
		else if (action.equals(Action.TURN_LEFT)){
			return simulateShiftRight();
		}
		else{
			return simulateShiftLef();
		}
	}

	public Observation simulateShiftLef() {
		Observation observation = null;
		if (Area.A.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.DISAPPEAR, Area.A); 
		}
		else if (Area.B.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.FARTHER, Area.A); 
		}
		else if (Area.C.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.MOVE, Area.A); 
		}
		else{
			observation = ObservationImpl.createOrGet(Aspect.UNCHANGED, Area.B); 
		}
		return observation;
	}

	public Observation simulateShiftRight() {
		Observation observation = null;
		if (Area.C.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.DISAPPEAR, Area.C); 
		}
		else if (Area.B.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.FARTHER, Area.C); 
		}
		else if (Area.A.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.MOVE, Area.C); 
		}
		else{
			observation = ObservationImpl.createOrGet(Aspect.UNCHANGED, Area.B); 
		}
		return observation;
	}

	public Observation simulateShiftForward() {
		Observation observation = null;
		if (Area.A.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.CLOSER, Area.A); 
		}
		else if (Area.B.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.CLOSER, Area.B); 
		}
		else if (Area.C.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.UNCHANGED, Area.C); 
		}
		return observation;
	}

	public void shiftLef() {
		Area.A.setOccupied(Area.B.isOccupied());
		Area.B.setOccupied(Area.C.isOccupied());
		Area.C.setOccupied(false);
	}

	public void shiftRight() {
		Area.C.setOccupied(Area.B.isOccupied());
		Area.B.setOccupied(Area.A.isOccupied());
		Area.A.setOccupied(false);
	}

	public void trace(ITracer tracer)
	{
		if (tracer != null)
		{
			Object localSpace = tracer.addEventElement("local_space");
			tracer.addSubelement(localSpace, "position_8", "FFFFFF");
			tracer.addSubelement(localSpace, "position_7", "FFFFFF");
			tracer.addSubelement(localSpace, "position_6", "FFFFFF");
			
			if (Area.A.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_5", "A0E000");
				tracer.addSubelement(localSpace, "position_4", "A0E000");
			}
			else if (Area.A.getEvent().equals("o")){
				tracer.addSubelement(localSpace, "position_5", "808080");
				tracer.addSubelement(localSpace, "position_4", "808080");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (Area.B.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_3", "A0E000");
			}
			else if (Area.B.getEvent().equals("o")){
				tracer.addSubelement(localSpace, "position_3", "808080");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (Area.C.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_2", "A0E000");
				tracer.addSubelement(localSpace, "position_1", "A0E000");
			}
			else if (Area.C.getEvent().equals("o")){
				tracer.addSubelement(localSpace, "position_2", "808080");
				tracer.addSubelement(localSpace, "position_1", "808080");
			}
			else {
				tracer.addSubelement(localSpace, "position_2", "FFFFFF");
				tracer.addSubelement(localSpace, "position_1", "FFFFFF");
			}
			tracer.addSubelement(localSpace, "position_0", "FFFFFF");
		}
	}

	public void traceObject(ITracer tracer)
	{
		if (tracer != null)
		{
			Object localSpace = tracer.addEventElement("local_space");
			tracer.addSubelement(localSpace, "position_8", "FFFFFF");
			tracer.addSubelement(localSpace, "position_7", "FFFFFF");
			tracer.addSubelement(localSpace, "position_6", "FFFFFF");
			
			if (Area.A.isOccupied()){
				tracer.addSubelement(localSpace, "position_5", "9680FF");
				tracer.addSubelement(localSpace, "position_4", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (Area.B.isOccupied()){
				tracer.addSubelement(localSpace, "position_3", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (Area.C.isOccupied()){
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
