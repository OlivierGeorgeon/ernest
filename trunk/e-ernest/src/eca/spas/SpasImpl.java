package eca.spas;


import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import tracing.ITracer;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.construct.egomem.Area;
import eca.construct.egomem.AreaImpl;
import eca.construct.egomem.Displacement;
import eca.construct.experiment.Experiment;
import eca.construct.experiment.ExperimentImpl;
import eca.spas.egomem.PhenomenonInstance;
import eca.spas.egomem.PhenomenonInstanceImpl;
import eca.spas.egomem.Place;
import eca.spas.egomem.SpatialMemory;
import eca.spas.egomem.SpatialMemoryImpl;
import eca.ss.Appearance;
import eca.ss.AppearanceImpl;
import eca.ss.enaction.Enaction;

/**
 * The spatial system.
 * Maintains the local space map and the persistence memory.
 * @author Olivier
 */
public class SpasImpl implements Spas 
{
	
	/** The Tracer. */
	private ITracer m_tracer = null; 
	
	/** Ernest's local space memory  */
	private SpatialMemory spacialMemory = new SpatialMemoryImpl();
	
	/** The transformation to apply to spatial memory */
	Transform3D transform = new Transform3D();
	
	public void setTracer(ITracer tracer) {
		m_tracer = tracer;
	}
	
	/**
	 * The main method of the Spatial System that is called on each interaction cycle.
	 * Track the spatial consequences of the current enaction.
	 * @param enaction The current enaction.
	 */
	public void track(Enaction enaction) 
	{
		Place enactedPlace = enaction.getEnactedPlaces().get(0);	
		enactedPlace.normalize(3);
		Displacement displacement = enaction.getEnactedPrimitiveAct().getPrimitive().getDisplacement();
		this.transform.set(displacement.getTransform3D());

		PhenomenonInstance phenomenonInstance = enaction.getPhenomenonInstance();
		Area previousArea = null;
		if (phenomenonInstance == null){
			previousArea = AreaImpl.createOrGet(new Point3f());
			phenomenonInstance = new PhenomenonInstanceImpl(enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenonType(), enactedPlace.clone());
		}
		else{
			previousArea = phenomenonInstance.getPlace().getArea();
			phenomenonInstance.getPlace().transform(this.transform); // 
		}
		
		// Update spatial memory
		
		this.spacialMemory.tick();
		this.spacialMemory.transform(this.transform);		
		this.spacialMemory.forgetOldPlaces();		
		this.spacialMemory.addPlace(enactedPlace);

		// Merge phenomenon types	
		
		Area area = enaction.getEnactedPrimitiveAct().getArea();
		PhenomenonType phenomenonType = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenonType();
		
		if (enaction.getIntendedPrimitiveAct() != null){
			if (area.getLabel().equals(AreaImpl.O)  ){
				PhenomenonTypeImpl.merge(phenomenonType, PhenomenonTypeImpl.EMPTY);
				phenomenonInstance = new PhenomenonInstanceImpl(PhenomenonTypeImpl.EMPTY, enactedPlace.clone());
				if (m_tracer != null && !phenomenonType.equals(PhenomenonTypeImpl.EMPTY)){
					m_tracer.addEventElement("empty", phenomenonType.getLabel() + " merged to " + PhenomenonTypeImpl.EMPTY.getLabel());}
				phenomenonType = PhenomenonTypeImpl.EMPTY;
			}
			else if (phenomenonInstance.getPlace().getArea().equals(area)){
				PhenomenonType previousPhenomenonType = phenomenonInstance.getPhenomenonType();
				if (!previousPhenomenonType.equals(phenomenonType)){
					PhenomenonTypeImpl.merge(phenomenonType, previousPhenomenonType);
					if (m_tracer != null){
						m_tracer.addEventElement("phenomenon", phenomenonType.getLabel() + " merged to " + previousPhenomenonType.getLabel() + " in area " + area.getLabel());}
					phenomenonType = previousPhenomenonType;
				}
			}
			else {
				phenomenonInstance = new PhenomenonInstanceImpl(phenomenonType, enactedPlace.clone());
			}
		}
		
		// Record the experiment
		//if (phenomenonInstance != null){
			Appearance preAppearance = AppearanceImpl.createOrGet(phenomenonType, previousArea);
			Appearance postAppearance = AppearanceImpl.createOrGet(phenomenonType, area);
			Experiment newExp = ExperimentImpl.createOrGet(preAppearance, enaction.getEnactedPrimitiveAct().getPrimitive().getAction());
			newExp.incActCounter(enaction.getEnactedPrimitiveAct());
			newExp.incDisplacementCounter(displacement);
			newExp.incPostAppearanceCounter(postAppearance);
			if (m_tracer != null){
				m_tracer.addEventElement("experiment", newExp.toString());}
		//}
		enaction.setPhenomenonInstance(phenomenonInstance);
	}

	public int getValue(int i, int j)
	{
		Point3f position = new Point3f(1 - j, 1 - i, 0);
		if (spacialMemory != null)
			return spacialMemory.getDisplayCode(position);
		else
			return 0xFFFFFF;
	}
	
	public ArrayList<Place> getPlaceList()	{
		return this.spacialMemory.clonePlaceList();
	}

	public int getValue(Point3f position) 
	{
		return this.spacialMemory.getDisplayCode(position);
	}

//	public SpatialMemory getSpatialMemory()
//	{
//		return this.spacialMemory;
//	}

	public Transform3D getTransformToAnim() {
		return this.transform;
	}

}
