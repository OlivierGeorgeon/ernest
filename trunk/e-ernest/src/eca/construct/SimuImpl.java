package eca.construct;


import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import utils.ErnestUtils;
import eca.Primitive;
import eca.PrimitiveImpl;
import eca.construct.egomem.Area;
import eca.construct.egomem.AreaImpl;
import eca.construct.egomem.Transformation;
import eca.construct.egomem.TransformationImpl;
import eca.construct.experiment.Experiment;
import eca.construct.experiment.ExperimentImpl;
import eca.ss.enaction.Act;
import eca.ss.enaction.ActImpl;
import ernest.IEffect;

public class SimuImpl implements Simu {

	//public static int SCALE = 3;
	
	//private Layout layout  = LayoutImpl.createOrGet(EMPTY, EMPTY, EMPTY);
	
//	public static Transform3D spasTransform(Transformation transformation){
//		Transform3D spasTransform = new Transform3D();
//		spasTransform.setIdentity();
//		if (!transformation.equals(UNKNOWN)){
//			if (transformation.equals(SHIFT_LEFT))		
//				spasTransform.rotZ(Math.PI/2);
//			else if (transformation.equals(SHIFT_RIGHT))		
//				spasTransform.rotZ(-Math.PI/2);
//			else
//				spasTransform.setTranslation(new Vector3f(0,0,0));
//		}
//		
//		return spasTransform;
//	}
	
//	public static Act getAct(Action action, Observation observation){
//		
//		Experiment exp = ExperimentImpl.createOrGet(action, observation);
//		Act act = exp.predictAct();
//		
//		if (act == null){
//			Primitive interaction = action.getPrimitives().get(0);
//			for (Primitive i : PrimitiveImpl.getINTERACTIONS()){
//				if (i.getAction().equals(action) && i.getPhenomenonType().equals(observation.getPhenomenon()))
//					interaction = i;
//			}
//			act = ActImpl.createOrGetPrimitiveAct(interaction, observation.getArea());
//		}
//
//		return act;
//	}

//	public void track(Enaction enaction){
//		Area area = enaction.getEnactedPrimitiveAct().getArea();
//		Phenomenon aspectA = EMPTY;
//		Phenomenon aspectB = EMPTY;
//		Phenomenon aspectC = EMPTY;
//		
//		if (enaction.getEnactedPrimitiveAct() != null){
//			if (area.equals(A)){
//				aspectA = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenonType();
//			}
//			else if (area.equals(B)){
//				aspectB = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenonType();
//			}
//			else if (area.equals(C)){
//				aspectC = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenonType();
//			}
//		}
//
//		layout = LayoutImpl.createOrGet(aspectA, aspectB, aspectC);
//		
//		enaction.getEnactedPrimitiveAct().getPrimitive().getAction().setTransformation(enaction.getTransformation());
//	}
	
//	public Layout predict(Action action){
//		return LayoutImpl.transform(layout, action.getTransformation()); 
//		
//		Layout nextLayout = LayoutImpl.transform(layout, action.getTransformation()); 
//		Observation observation = nextLayout.observe();		
//		return observation;
//	}

//	public void trace(ITracer tracer)
//	{
//		if (tracer != null)
//		{
//			Object localSpace = tracer.addEventElement("local_space");
//			if (layout.isEmpty())
//				tracer.addSubelement(localSpace, "position_8", "9680FF");
//			else
//				tracer.addSubelement(localSpace, "position_8", "FFFFFF");
//	
//			tracer.addSubelement(localSpace, "position_7", "FFFFFF");
//			tracer.addSubelement(localSpace, "position_6", "FFFFFF");
//			
//			if (!layout.isEmpty(A)){
//				tracer.addSubelement(localSpace, "position_5", "9680FF");
//				tracer.addSubelement(localSpace, "position_4", "9680FF");
//			}
//			else {
//				tracer.addSubelement(localSpace, "position_5", "FFFFFF");
//				tracer.addSubelement(localSpace, "position_4", "FFFFFF");
//			}
//			if (!layout.isEmpty(B)){
//				tracer.addSubelement(localSpace, "position_3", "9680FF");
//			}
//			else {
//				tracer.addSubelement(localSpace, "position_3", "FFFFFF");
//			}
//			if (!layout.isEmpty(C)){
//				tracer.addSubelement(localSpace, "position_2", "9680FF");
//				tracer.addSubelement(localSpace, "position_1", "9680FF");
//			}
//			else {
//				tracer.addSubelement(localSpace, "position_2", "FFFFFF");
//				tracer.addSubelement(localSpace, "position_1", "FFFFFF");
//			}
//			tracer.addSubelement(localSpace, "position_0", "FFFFFF");
//			
//			Object layoutElmt = tracer.addEventElement("layout");
//			tracer.addSubelement(layoutElmt, "Aspect_A", layout.getPhenomenon(A).getLabel());
//			tracer.addSubelement(layoutElmt, "Aspect_B", layout.getPhenomenon(B).getLabel());
//			tracer.addSubelement(layoutElmt, "Aspect_C", layout.getPhenomenon(C).getLabel());
//		}
//	}
}
