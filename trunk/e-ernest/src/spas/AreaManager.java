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
	
	public IArea categorize(Point3f point) 
	{
		if (ErnestUtils.polarAngle(point) > .1f)
			return Area.A; 
		else if (ErnestUtils.polarAngle(point) >= -.1f)
			return Area.B; 
		else
			return Area.C; 
	}
	
	public void clearAll(){
		for (IArea a : Area.getAREAS())
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
		if (IArea.A.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.DISAPPEAR, IArea.A); 
		}
		else if (IArea.B.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.FARTHER, IArea.A); 
		}
		else if (IArea.C.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.MOVE, IArea.A); 
		}
		else{
			observation = ObservationImpl.createOrGet(Aspect.UNCHANGED, IArea.B); 
		}
		return observation;
	}

	public Observation simulateShiftRight() {
		Observation observation = null;
		if (IArea.C.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.DISAPPEAR, IArea.C); 
		}
		else if (IArea.B.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.FARTHER, IArea.C); 
		}
		else if (IArea.A.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.MOVE, IArea.C); 
		}
		else{
			observation = ObservationImpl.createOrGet(Aspect.UNCHANGED, IArea.B); 
		}
		return observation;
	}

	public Observation simulateShiftForward() {
		Observation observation = null;
		if (IArea.A.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.CLOSER, IArea.A); 
		}
		else if (IArea.B.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.CLOSER, IArea.B); 
		}
		else if (IArea.C.isOccupied()){
			observation = ObservationImpl.createOrGet(Aspect.UNCHANGED, IArea.C); 
		}
		return observation;
	}

	public void shiftLef() {
		IArea.A.setOccupied(IArea.B.isOccupied());
		IArea.B.setOccupied(IArea.C.isOccupied());
		IArea.C.setOccupied(false);
	}

	public void shiftRight() {
		IArea.C.setOccupied(IArea.B.isOccupied());
		IArea.B.setOccupied(IArea.A.isOccupied());
		IArea.A.setOccupied(false);
	}

	public void trace(ITracer tracer)
	{
		if (tracer != null)
		{
			Object localSpace = tracer.addEventElement("local_space");
			tracer.addSubelement(localSpace, "position_8", "FFFFFF");
			tracer.addSubelement(localSpace, "position_7", "FFFFFF");
			tracer.addSubelement(localSpace, "position_6", "FFFFFF");
			
			if (IArea.A.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_5", "A0E000");
				tracer.addSubelement(localSpace, "position_4", "A0E000");
			}
			else if (IArea.A.getEvent().equals("o")){
				tracer.addSubelement(localSpace, "position_5", "808080");
				tracer.addSubelement(localSpace, "position_4", "808080");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (IArea.B.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_3", "A0E000");
			}
			else if (IArea.B.getEvent().equals("o")){
				tracer.addSubelement(localSpace, "position_3", "808080");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (IArea.C.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_2", "A0E000");
				tracer.addSubelement(localSpace, "position_1", "A0E000");
			}
			else if (IArea.C.getEvent().equals("o")){
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
			
			if (IArea.A.isOccupied()){
				tracer.addSubelement(localSpace, "position_5", "9680FF");
				tracer.addSubelement(localSpace, "position_4", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (IArea.B.isOccupied()){
				tracer.addSubelement(localSpace, "position_3", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (IArea.C.isOccupied()){
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
