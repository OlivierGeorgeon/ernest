package spas;

import imos2.Act;
import javax.vecmath.Point3f;
import utils.ErnestUtils;
import ernest.Action;
import ernest.Aspect;
import ernest.AspectImpl;
import ernest.ITracer;
import ernest.Observation;
import ernest.ObservationImpl;
import ernest.Primitive;

public class SimuImpl implements Simu {

	/** Predefined actions */
//	public static Action STEP = ActionImpl.createOrGet(">");
//	public static Action TURN_LEFT = ActionImpl.createOrGet("^");
//	public static Action TURN_RIGHT = ActionImpl.createOrGet("v");

	/** Predefined aspects */
	public static Aspect APPEAR = AspectImpl.createOrGet("*");
	public static Aspect CLOSER = AspectImpl.createOrGet("+");
	public static Aspect DISAPPEAR = AspectImpl.createOrGet("o");
	public static Aspect FARTHER = AspectImpl.createOrGet("-");
	public static Aspect MOVE = AspectImpl.createOrGet("=");
	public static Aspect UNCHANGED = AspectImpl.createOrGet("_");
	
	/** Predefined areas */
	public static Area A = AreaImpl.createOrGet("A");
	public static Area B = AreaImpl.createOrGet("B");
	public static Area C = AreaImpl.createOrGet("C");

	/** Predefined observations */
	static{
		for(Aspect aspect : AspectImpl.getAspects())
			for(Area area : AreaImpl.getAREAS())
				ObservationImpl.createOrGet(aspect, area);
	}
	
	/**
	 * Gives the area to which a point belongs.
	 * @param point The point
	 * @return The area of interest
	 */
	public static Area getArea(Point3f point) 
	{
		if (ErnestUtils.polarAngle(point) > .1f)
			return A; 
		else if (ErnestUtils.polarAngle(point) >= -.1f)
			return B; 
		else
			return C; 
	}
	
//	public static Action getAction(Primitive interaction) {
//		// The action of a primitive interaction is given by the first character of its label
//		// TODO learn actions without using assumption about the interaction's label.
//		String actionLabel = interaction.getLabel().substring(0, 1);
//		
//		return ActionImpl.createOrGet(actionLabel);
//	}
	
	public static Aspect getAspect(Primitive interaction) {
		// The aspect of a primitive interaction is given by the first character of its label
		// TODO learn aspect without using assumption about the interaction's label.
		String aspectLabel = interaction.getLabel().substring(1, 2);
		
		return AspectImpl.createOrGet(aspectLabel);
	}

	public void track(Act act){
		AreaImpl.clearAll();
//		if (act.getAspect().equals(APPEAR) ||
//			act.getAspect().equals(CLOSER) ||
//			act.getAspect().equals(MOVE) ||
//			act.getAspect().equals(FARTHER)){
//			act.getArea().setOccupied(true);
//		}
	}
	
	public Observation predict(Action action){
		Observation observation = ObservationImpl.createOrGet(UNCHANGED, B);
//		if (action.equals(STEP)){
//			observation = simulateShiftForward();
//		}
//		else if (action.equals(TURN_LEFT)){
//			observation = simulateShiftRight();
//		}
//		else if (action.equals(TURN_RIGHT)){
//			observation = simulateShiftLef();
//		}
		return observation;
	}

	private Observation simulateShiftLef() {
		Observation observation = ObservationImpl.createOrGet(UNCHANGED, B); 
		if (A.isOccupied()){
			observation = ObservationImpl.createOrGet(DISAPPEAR, A); 
		}
		else if (B.isOccupied()){
			observation = ObservationImpl.createOrGet(FARTHER, A); 
		}
		else if (C.isOccupied()){
			observation = ObservationImpl.createOrGet(MOVE, A); 
		}

		return observation;
	}

	private Observation simulateShiftRight() {
		Observation observation = ObservationImpl.createOrGet(UNCHANGED, B); 
		if (C.isOccupied()){
			observation = ObservationImpl.createOrGet(DISAPPEAR, C); 
		}
		else if (B.isOccupied()){
			observation = ObservationImpl.createOrGet(FARTHER, C); 
		}
		else if (A.isOccupied()){
			observation = ObservationImpl.createOrGet(MOVE, C); 
		}

		return observation;
	}

	private Observation simulateShiftForward() {
		Observation observation = ObservationImpl.createOrGet(UNCHANGED, B); 
		if (A.isOccupied()){
			observation = ObservationImpl.createOrGet(CLOSER, A); 
		}
		else if (B.isOccupied()){
			observation = ObservationImpl.createOrGet(CLOSER, B); 
		}
		else if (C.isOccupied()){
			observation = ObservationImpl.createOrGet(UNCHANGED, C); 
		}
		return observation;
	}

	public void shiftLef() {
		A.setOccupied(B.isOccupied());
		B.setOccupied(C.isOccupied());
		C.setOccupied(false);
	}

	public void shiftRight() {
		C.setOccupied(B.isOccupied());
		B.setOccupied(A.isOccupied());
		A.setOccupied(false);
	}

	public void trace(ITracer tracer)
	{
		if (tracer != null)
		{
			Object localSpace = tracer.addEventElement("local_space");
			tracer.addSubelement(localSpace, "position_8", "FFFFFF");
			tracer.addSubelement(localSpace, "position_7", "FFFFFF");
			tracer.addSubelement(localSpace, "position_6", "FFFFFF");
			
			if (A.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_5", "A0E000");
				tracer.addSubelement(localSpace, "position_4", "A0E000");
			}
			else if (A.getEvent().equals("o")){
				tracer.addSubelement(localSpace, "position_5", "808080");
				tracer.addSubelement(localSpace, "position_4", "808080");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (B.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_3", "A0E000");
			}
			else if (B.getEvent().equals("o")){
				tracer.addSubelement(localSpace, "position_3", "808080");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (C.getEvent().equals("*")){
				tracer.addSubelement(localSpace, "position_2", "A0E000");
				tracer.addSubelement(localSpace, "position_1", "A0E000");
			}
			else if (C.getEvent().equals("o")){
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
			
			if (A.isOccupied()){
				tracer.addSubelement(localSpace, "position_5", "9680FF");
				tracer.addSubelement(localSpace, "position_4", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (B.isOccupied()){
				tracer.addSubelement(localSpace, "position_3", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (C.isOccupied()){
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
