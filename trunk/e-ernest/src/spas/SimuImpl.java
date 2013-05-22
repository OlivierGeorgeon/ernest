package spas;

import imos2.IEnaction;
import javax.vecmath.Point3f;
import utils.ErnestUtils;
import ernest.Action;
import ernest.Aspect;
import ernest.AspectImpl;
import ernest.ITracer;
import ernest.Layout;
import ernest.LayoutImpl;
import ernest.Observation;
import ernest.ObservationImpl;

public class SimuImpl implements Simu {

	/** Predefined actions */
//	public static Action STEP = ActionImpl.createOrGet(">");
//	public static Action TURN_LEFT = ActionImpl.createOrGet("^");
//	public static Action TURN_RIGHT = ActionImpl.createOrGet("v");

	/** Predefined transformation */
	public static Transformation IDENTITY = TransformationImpl.createOrGet("_");
	public static Transformation SHIFT_LEFT = TransformationImpl.createOrGet("^");
	public static Transformation SHIFT_RIGHT = TransformationImpl.createOrGet("v");
	public static Transformation UNKNOWN = TransformationImpl.createOrGet("?");
	
	/** Predefined aspects */
	public static Aspect EMPTY = AspectImpl.createOrGet("_");
//	public static Aspect APPEAR = AspectImpl.createOrGet("*");
//	public static Aspect CLOSER = AspectImpl.createOrGet("+");
//	public static Aspect DISAPPEAR = AspectImpl.createOrGet("o");
//	public static Aspect FARTHER = AspectImpl.createOrGet("-");
//	public static Aspect MOVE = AspectImpl.createOrGet("=");
//	public static Aspect UNCHANGED = AspectImpl.createOrGet("_");
	
	/** Predefined areas */
	public static Area A = AreaImpl.createOrGet("A");
	public static Area B = AreaImpl.createOrGet("B");
	public static Area C = AreaImpl.createOrGet("C");

	/** Predefined observations */
//	static{
//		for(Aspect aspect : AspectImpl.getAspects())
//			for(Area area : AreaImpl.getAREAS())
//				ObservationImpl.createOrGet(aspect, area);
//	}
	
	Layout previousLayout = LayoutImpl.createOrGet(EMPTY, EMPTY, EMPTY);
	Layout currentLayout = LayoutImpl.createOrGet(EMPTY, EMPTY, EMPTY);
	
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
	
	public void setLayout(Aspect aspectA, Aspect aspectB, Aspect aspectC){
		currentLayout = LayoutImpl.createOrGet(aspectA, aspectB, aspectC);
	}
	
	public void track(IEnaction enaction){
		previousLayout = currentLayout;
		Aspect aspectA = EMPTY;
		Aspect aspectB = EMPTY;
		Aspect aspectC = EMPTY;
		if (enaction.getEnactedPrimitiveInteraction() != null){
			if (enaction.getArea().equals(A)){
				aspectA = enaction.getEnactedPrimitiveInteraction().getAspect();
			}
			else if (enaction.getArea().equals(B)){
				aspectB = enaction.getEnactedPrimitiveInteraction().getAspect();
			}
			else if (enaction.getArea().equals(C)){
				aspectC = enaction.getEnactedPrimitiveInteraction().getAspect();
			}
		}
		currentLayout = LayoutImpl.createOrGet(aspectA, aspectB, aspectC);
		enaction.setTransformation(trackTransformation());
	}
	
	private Transformation trackTransformation(){
		Transformation trans = UNKNOWN;
		
		if (currentLayout.isEmpty(A)== previousLayout.isEmpty(A) &&
			currentLayout.isEmpty(B)== previousLayout.isEmpty(B) &&
			currentLayout.isEmpty(C)== previousLayout.isEmpty(C))
			trans = IDENTITY;
		
		else if (!currentLayout.isEmpty(A)){
			if (!previousLayout.isEmpty(B) || !previousLayout.isEmpty(C))
				trans = SHIFT_LEFT;
		}
		else if (!currentLayout.isEmpty(C)){
			if (!previousLayout.isEmpty(A) || !previousLayout.isEmpty(B))
				trans = SHIFT_RIGHT;
		}
		else if (!currentLayout.isEmpty(B)){
			if (!previousLayout.isEmpty(C))
				trans = SHIFT_LEFT;
			if (!previousLayout.isEmpty(A))
				trans = SHIFT_RIGHT;
		}
		
		return trans;
	}
	
	public Observation predict(Action action){
		Aspect aspect = AspectImpl.getAspects().iterator().next();
		Observation observation = ObservationImpl.createOrGet(aspect, B);
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

//	private Observation simulateShiftLef() {
//		Observation observation = ObservationImpl.createOrGet(UNCHANGED, B); 
//		if (A.isOccupied()){
//			observation = ObservationImpl.createOrGet(DISAPPEAR, A); 
//		}
//		else if (B.isOccupied()){
//			observation = ObservationImpl.createOrGet(FARTHER, A); 
//		}
//		else if (C.isOccupied()){
//			observation = ObservationImpl.createOrGet(MOVE, A); 
//		}
//
//		return observation;
//	}
//
//	private Observation simulateShiftRight() {
//		Observation observation = ObservationImpl.createOrGet(UNCHANGED, B); 
//		if (C.isOccupied()){
//			observation = ObservationImpl.createOrGet(DISAPPEAR, C); 
//		}
//		else if (B.isOccupied()){
//			observation = ObservationImpl.createOrGet(FARTHER, C); 
//		}
//		else if (A.isOccupied()){
//			observation = ObservationImpl.createOrGet(MOVE, C); 
//		}
//
//		return observation;
//	}
//
//	private Observation simulateShiftForward() {
//		Observation observation = ObservationImpl.createOrGet(UNCHANGED, B); 
//		if (A.isOccupied()){
//			observation = ObservationImpl.createOrGet(CLOSER, A); 
//		}
//		else if (B.isOccupied()){
//			observation = ObservationImpl.createOrGet(CLOSER, B); 
//		}
//		else if (C.isOccupied()){
//			observation = ObservationImpl.createOrGet(UNCHANGED, C); 
//		}
//		return observation;
//	}

//	public void shiftLef() {
//		A.setOccupied(B.isOccupied());
//		B.setOccupied(C.isOccupied());
//		C.setOccupied(false);
//	}
//
//	public void shiftRight() {
//		C.setOccupied(B.isOccupied());
//		B.setOccupied(A.isOccupied());
//		A.setOccupied(false);
//	}

//	public void trace(ITracer tracer)
//	{
//		if (tracer != null)
//		{
//			Object localSpace = tracer.addEventElement("local_space");
//			tracer.addSubelement(localSpace, "position_8", "FFFFFF");
//			tracer.addSubelement(localSpace, "position_7", "FFFFFF");
//			tracer.addSubelement(localSpace, "position_6", "FFFFFF");
//			
//			if (A.getEvent().equals("*")){
//				tracer.addSubelement(localSpace, "position_5", "A0E000");
//				tracer.addSubelement(localSpace, "position_4", "A0E000");
//			}
//			else if (A.getEvent().equals("o")){
//				tracer.addSubelement(localSpace, "position_5", "808080");
//				tracer.addSubelement(localSpace, "position_4", "808080");
//			}
//			else {
//				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
//				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
//			}
//			if (B.getEvent().equals("*")){
//				tracer.addSubelement(localSpace, "position_3", "A0E000");
//			}
//			else if (B.getEvent().equals("o")){
//				tracer.addSubelement(localSpace, "position_3", "808080");
//			}
//			else {
//				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
//			}
//			if (C.getEvent().equals("*")){
//				tracer.addSubelement(localSpace, "position_2", "A0E000");
//				tracer.addSubelement(localSpace, "position_1", "A0E000");
//			}
//			else if (C.getEvent().equals("o")){
//				tracer.addSubelement(localSpace, "position_2", "808080");
//				tracer.addSubelement(localSpace, "position_1", "808080");
//			}
//			else {
//				tracer.addSubelement(localSpace, "position_2", "FFFFFF");
//				tracer.addSubelement(localSpace, "position_1", "FFFFFF");
//			}
//			tracer.addSubelement(localSpace, "position_0", "FFFFFF");
//		}
//	}

	public void trace(ITracer tracer)
	{
		if (tracer != null)
		{
			Object localSpace = tracer.addEventElement("local_space");
			tracer.addSubelement(localSpace, "position_8", "FFFFFF");
			tracer.addSubelement(localSpace, "position_7", "FFFFFF");
			tracer.addSubelement(localSpace, "position_6", "FFFFFF");
			
			if (!currentLayout.isEmpty(A)){
				tracer.addSubelement(localSpace, "position_5", "9680FF");
				tracer.addSubelement(localSpace, "position_4", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (!currentLayout.isEmpty(B)){
				tracer.addSubelement(localSpace, "position_3", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (!currentLayout.isEmpty(C)){
				tracer.addSubelement(localSpace, "position_2", "9680FF");
				tracer.addSubelement(localSpace, "position_1", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_2", "FFFFFF");
				tracer.addSubelement(localSpace, "position_1", "FFFFFF");
			}
			tracer.addSubelement(localSpace, "position_0", "FFFFFF");
			
			Object layoutElmt = tracer.addEventElement("layout");
			tracer.addSubelement(layoutElmt, "Aspect_A", currentLayout.getAspect(A).getLabel());
			tracer.addSubelement(layoutElmt, "Aspect_B", currentLayout.getAspect(B).getLabel());
			tracer.addSubelement(layoutElmt, "Aspect_C", currentLayout.getAspect(C).getLabel());
		}
	}

}
