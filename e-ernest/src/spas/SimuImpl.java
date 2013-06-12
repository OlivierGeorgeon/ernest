package spas;


import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import utils.ErnestUtils;
import eca.Act;
import eca.ActImpl;
import eca.Enaction;
import eca.spas.egomem.Area;
import eca.spas.egomem.AreaImpl;
import eca.spas.egomem.Transformation;
import eca.spas.egomem.TransformationImpl;
import ernest.Action;
import ernest.IEffect;
import ernest.Phenomenon;
import ernest.PhenomenonImpl;
import ernest.Experiment;
import ernest.ExperimentImpl;
import ernest.ITracer;
import ernest.Layout;
import ernest.LayoutImpl;
import ernest.Observation;
import ernest.Primitive;
import ernest.PrimitiveImpl;

public class SimuImpl implements Simu {

	public static int SCALE = 3;
	
	/** Predefined areas */
	public static Area A = AreaImpl.createOrGet("A");
	public static Area B = AreaImpl.createOrGet("B");
	public static Area C = AreaImpl.createOrGet("C");
	public static Area O = AreaImpl.createOrGet("O");

	/** Predefined aspects */
	public static Phenomenon EMPTY = PhenomenonImpl.createOrGet("_");
	
	/** Predefined transformations */
	public static Transformation UNKNOWN = TransformationImpl.createOrGet("?");
	public static Transformation IDENTITY = TransformationImpl.createOrGet("<");
	public static Transformation SHIFT_LEFT = TransformationImpl.createOrGet("^");
	public static Transformation SHIFT_RIGHT = TransformationImpl.createOrGet("v");
	
	private Layout layout  = LayoutImpl.createOrGet(EMPTY, EMPTY, EMPTY);
	
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
	
	public static Transform3D spasTransform(Transformation transformation){
		Transform3D spasTransform = new Transform3D();
		spasTransform.setIdentity();
		if (!transformation.equals(UNKNOWN)){
			if (transformation.equals(SHIFT_LEFT))		
				spasTransform.rotZ(Math.PI/2);
			else if (transformation.equals(SHIFT_RIGHT))		
				spasTransform.rotZ(-Math.PI/2);
			else
				spasTransform.setTranslation(new Vector3f(0,0,0));
		}
		
		return spasTransform;
	}
	
	public static Act getAct(Action action, Observation observation){
		
		Experiment exp = ExperimentImpl.createOrGet(action, observation);
		Act act = exp.predictAct();
		
		if (act == null){
			Primitive interaction = action.getPrimitives().get(0);
			for (Primitive i : PrimitiveImpl.getINTERACTIONS()){
				if (i.getAction().equals(action) && i.getPhenomenon().equals(observation.getAspect()))
					interaction = i;
			}
			act = ActImpl.createOrGetPrimitiveAct(interaction, observation.getArea());
		}

		return act;
	}

	public void track(Enaction enaction){
		Area area = enaction.getEnactedPrimitiveAct().getArea();
		Phenomenon aspectA = EMPTY;
		Phenomenon aspectB = EMPTY;
		Phenomenon aspectC = EMPTY;
		
		//Transformation transformation = transformation(enaction.getEffect());
		Transformation transformation = enaction.getTransformation();
		//previousLayout = LayoutImpl.transform(layout, transformation);

		if (enaction.getEnactedPrimitiveAct() != null){
			if (area.equals(A)){
				aspectA = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenon();
			}
			else if (area.equals(B)){
				aspectB = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenon();
			}
			else if (area.equals(C)){
				aspectC = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenon();
			}
		}

		layout = LayoutImpl.createOrGet(aspectA, aspectB, aspectC);
		
		enaction.getEnactedPrimitiveAct().getPrimitive().getAction().setTransformation(enaction.getTransformation());
		//enaction.setTransformation(transformation);
	}
	
	public static Transformation transformation(IEffect effect){
		Transformation transform = SimuImpl.UNKNOWN;

		transform = SimuImpl.IDENTITY;
		Transform3D t = effect.getTransformation();
		float angle = ErnestUtils.angle(t);
		if (Math.abs(angle) > .1){
			if ( angle > 0)		
				transform = SimuImpl.SHIFT_LEFT;
			else 		
				transform = SimuImpl.SHIFT_RIGHT;
		}

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
