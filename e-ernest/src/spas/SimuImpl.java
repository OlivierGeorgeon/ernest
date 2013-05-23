package spas;

import imos2.IEnaction;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import utils.ErnestUtils;
import ernest.Action;
import ernest.Aspect;
import ernest.AspectImpl;
import ernest.ITracer;
import ernest.Layout;
import ernest.LayoutImpl;
import ernest.Observation;

public class SimuImpl implements Simu {

	/** Predefined areas */
	public static Area A = AreaImpl.createOrGet("A");
	public static Area B = AreaImpl.createOrGet("B");
	public static Area C = AreaImpl.createOrGet("C");
	public static Area O = AreaImpl.createOrGet("O");

	/** Predefined aspects */
	public static Aspect EMPTY = AspectImpl.createOrGet("?");
	public static Aspect NONE = AspectImpl.createOrGet("0");
	public static Aspect ANYTHING = AspectImpl.createOrGet("1");
	
	/** Predefined transformations */
	public static Transformation UNKNOWN = TransformationImpl.createOrGet("?");
	public static Transformation IDENTITY = TransformationImpl.createOrGet("<");
	public static Transformation SHIFT_LEFT = TransformationImpl.createOrGet("^");
	public static Transformation SHIFT_RIGHT = TransformationImpl.createOrGet("v");
	
	//private Layout previousLayout = LayoutImpl.createOrGet(EMPTY, EMPTY, EMPTY);
	private Layout layout  = LayoutImpl.createOrGet(NONE, NONE, NONE);
	
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
		if (area.equals(C))
			spasPoint.set((float)Math.cos(Math.PI/4),-(float)Math.sin(Math.PI/4), 0);
		spasPoint.scale(4);
		return spasPoint;
	}
	
	public static Transform3D spasTransform(Transformation transformation){
		Transform3D spasTransform = new Transform3D();
		spasTransform.setIdentity();
		if (!transformation.equals(UNKNOWN)){
			if (transformation.equals(SHIFT_LEFT))		
				spasTransform.rotZ(Math.PI/2);
			else if (transformation.equals(SHIFT_RIGHT))		
				spasTransform.rotZ(-Math.PI/2);
			else
				spasTransform.setTranslation(new Vector3f(-1,0,0));
		}
		
		return spasTransform;
	}
	
	public void track(IEnaction enaction){
		Aspect aspectA = NONE;
		Aspect aspectB = NONE;
		Aspect aspectC = NONE;
		
		//previousLayout = layout;

		if (enaction.getEnactedPrimitiveInteraction() != null){
			if (enaction.getArea().equals(A)){
				aspectA = enaction.getEnactedPrimitiveInteraction().getPrimitive().getAspect();
			}
			else if (enaction.getArea().equals(B)){
				aspectB = enaction.getEnactedPrimitiveInteraction().getPrimitive().getAspect();
			}
			else if (enaction.getArea().equals(C)){
				aspectC = enaction.getEnactedPrimitiveInteraction().getPrimitive().getAspect();
			}
		}
		layout = LayoutImpl.createOrGet(aspectA, aspectB, aspectC);
		Transformation transformation = transformation(enaction);
		
		//if (!transformation.equals(SimuImpl.UNKNOWN))
		enaction.getEnactedPrimitiveInteraction().getPrimitive().getAction().setTransformation(transformation);
		enaction.setTransformation(transformation);
	}
	
	private Transformation transformation(IEnaction enaction){
		Transformation transform = SimuImpl.UNKNOWN;

		//if (!layout.isEmpty()){
			transform = SimuImpl.IDENTITY;
			Transform3D t = enaction.getEffect().getTransformation();
			float angle = ErnestUtils.angle(t);
			if (Math.abs(angle) > .1){
				if ( angle > 0)		
					transform = SimuImpl.SHIFT_LEFT;
				else 		
					transform = SimuImpl.SHIFT_RIGHT;
			}
		//}
		return transform;
	}
	
	public Observation predict(Action action){
		
		Layout nextLayout = LayoutImpl.transform(layout, action.getTransformation()); 
		Observation observation = nextLayout.observe();		
		return observation;
	}

	public void trace(ITracer tracer)
	{
		if (tracer != null)
		{
			Object localSpace = tracer.addEventElement("local_space");
			if (layout.isEmpty())
				tracer.addSubelement(localSpace, "position_8", "9680FF");
			else
				tracer.addSubelement(localSpace, "position_8", "FFFFFF");
	
			tracer.addSubelement(localSpace, "position_7", "FFFFFF");
			tracer.addSubelement(localSpace, "position_6", "FFFFFF");
			
			if (!layout.isEmpty(A)){
				tracer.addSubelement(localSpace, "position_5", "9680FF");
				tracer.addSubelement(localSpace, "position_4", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
			}
			if (!layout.isEmpty(B)){
				tracer.addSubelement(localSpace, "position_3", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
			}
			if (!layout.isEmpty(C)){
				tracer.addSubelement(localSpace, "position_2", "9680FF");
				tracer.addSubelement(localSpace, "position_1", "9680FF");
			}
			else {
				tracer.addSubelement(localSpace, "position_2", "FFFFFF");
				tracer.addSubelement(localSpace, "position_1", "FFFFFF");
			}
			tracer.addSubelement(localSpace, "position_0", "FFFFFF");
			
			Object layoutElmt = tracer.addEventElement("layout");
			tracer.addSubelement(layoutElmt, "Aspect_A", layout.getAspect(A).getLabel());
			tracer.addSubelement(layoutElmt, "Aspect_B", layout.getAspect(B).getLabel());
			tracer.addSubelement(layoutElmt, "Aspect_C", layout.getAspect(C).getLabel());
		}
	}
}
