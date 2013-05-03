package spas;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;

import ernest.Action;
import ernest.ActionImpl;
import ernest.Aspect;
import ernest.AspectImpl;
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
		if (action.equals(ActionImpl.STEP)){
			return simulateShiftForward();
		}
		else if (action.equals(ActionImpl.TURN_LEFT)){
			return simulateShiftRight();
		}
		else{
			return simulateShiftLef();
		}
	}

	public Observation simulateShiftLef() {
		Observation observation = null;
		if (AreaImpl.A.isOccupied()){
			observation = ObservationImpl.createOrGet(AspectImpl.DISAPPEAR, AreaImpl.A); 
		}
		else if (AreaImpl.B.isOccupied()){
			observation = ObservationImpl.createOrGet(AspectImpl.FARTHER, AreaImpl.A); 
		}
		else if (AreaImpl.C.isOccupied()){
			observation = ObservationImpl.createOrGet(AspectImpl.MOVE, AreaImpl.A); 
		}
		else{
			observation = ObservationImpl.createOrGet(AspectImpl.UNCHANGED, AreaImpl.B); 
		}
		return observation;
	}

	public Observation simulateShiftRight() {
		Observation observation = null;
		if (AreaImpl.C.isOccupied()){
			observation = ObservationImpl.createOrGet(AspectImpl.DISAPPEAR, AreaImpl.C); 
		}
		else if (AreaImpl.B.isOccupied()){
			observation = ObservationImpl.createOrGet(AspectImpl.FARTHER, AreaImpl.C); 
		}
		else if (AreaImpl.A.isOccupied()){
			observation = ObservationImpl.createOrGet(AspectImpl.MOVE, AreaImpl.C); 
		}
		else{
			observation = ObservationImpl.createOrGet(AspectImpl.UNCHANGED, AreaImpl.B); 
		}
		return observation;
	}

	public Observation simulateShiftForward() {
		Observation observation = null;
		if (AreaImpl.A.isOccupied()){
			observation = ObservationImpl.createOrGet(AspectImpl.CLOSER, AreaImpl.A); 
		}
		else if (AreaImpl.B.isOccupied()){
			observation = ObservationImpl.createOrGet(AspectImpl.CLOSER, AreaImpl.B); 
		}
		else if (AreaImpl.C.isOccupied()){
			observation = ObservationImpl.createOrGet(AspectImpl.UNCHANGED, AreaImpl.C); 
		}
		return observation;
	}

	public void shiftLef() {
		AreaImpl.A.setOccupied(AreaImpl.B.isOccupied());
		AreaImpl.B.setOccupied(AreaImpl.C.isOccupied());
		AreaImpl.C.setOccupied(false);
	}

	public void shiftRight() {
		AreaImpl.C.setOccupied(AreaImpl.B.isOccupied());
		AreaImpl.B.setOccupied(AreaImpl.A.isOccupied());
		AreaImpl.A.setOccupied(false);
	}

	public void trace(ITracer tracer)
	{
		if (tracer != null)
		{
			Object localSpace = tracer.addEventElement("local_space");
			tracer.addSubelement(localSpace, "position_8", "FFFFFF");
			tracer.addSubelement(localSpace, "position_7", "FFFFFF");
			tracer.addSubelement(localSpace, "position_6", "FFFFFF");
			
			if (AreaImpl.A.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_5", "A0E000");
				tracer.addSubelement(localSpace, "position_4", "A0E000");
			}
			else if (AreaImpl.A.getEvent().equals("o")){
				tracer.addSubelement(localSpace, "position_5", "808080");
				tracer.addSubelement(localSpace, "position_4", "808080");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (AreaImpl.B.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_3", "A0E000");
			}
			else if (AreaImpl.B.getEvent().equals("o")){
				tracer.addSubelement(localSpace, "position_3", "808080");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (AreaImpl.C.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_2", "A0E000");
				tracer.addSubelement(localSpace, "position_1", "A0E000");
			}
			else if (AreaImpl.C.getEvent().equals("o")){
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
			
			if (AreaImpl.A.isOccupied()){
				tracer.addSubelement(localSpace, "position_5", "9680FF");
				tracer.addSubelement(localSpace, "position_4", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (AreaImpl.B.isOccupied()){
				tracer.addSubelement(localSpace, "position_3", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (AreaImpl.C.isOccupied()){
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
