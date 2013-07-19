package eca.spas;


import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import tracing.ITracer;
import eca.construct.Area;
import eca.construct.AreaImpl;
import eca.construct.PhenomenonInstance;
import eca.construct.PhenomenonInstanceImpl;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.construct.egomem.Displacement;
import eca.construct.experiment.Experiment;
import eca.construct.experiment.ExperimentImpl;
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
		Object phenomenonInstElemnt = null;
		if (m_tracer != null)
			phenomenonInstElemnt = m_tracer.addEventElement("phenomenonInstance", true);

		Place enactedPlace = enaction.getEnactedPlaces().get(0);	
		enactedPlace.normalize(3);
		System.out.println("enacted position " + enactedPlace.getPosition().x + "," + enactedPlace.getPosition().y);
		//Displacement displacement = enaction.getEnactedPrimitiveAct().getDisplacement();
		//Displacement displacement = enaction.getDisplacement();
		this.transform.set(enaction.getTransform3D());

		PhenomenonInstance phenomenonInstance = enaction.getPhenomenonInstance();
		Area previousArea = null;
		Area projectedArea = null;
		if (phenomenonInstance == null){
			previousArea = AreaImpl.createOrGet(new Point3f());
			phenomenonInstance = new PhenomenonInstanceImpl(enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenonType(), enactedPlace.clone());
		}
		else{
			previousArea = phenomenonInstance.getPlace().getArea();
			//System.out.println("pre-position " + phenomenonInstance.getPlace().getPosition().x + "," + phenomenonInstance.getPlace().getPosition().y);
			phenomenonInstance.getPlace().transform(this.transform);
			//System.out.println("projected position " + phenomenonInstance.getPlace().getPosition().x + "," + phenomenonInstance.getPlace().getPosition().y);
			projectedArea = phenomenonInstance.getPlace().getArea();
		}
		
		Appearance preAppearance = AppearanceImpl.createOrGet(phenomenonInstance.getPhenomenonType(), previousArea);

		Area area = enaction.getEnactedPrimitiveAct().getArea();
		PhenomenonType phenomenonType = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenonType();
		
		// Update spatial memory
		
		this.spacialMemory.tick();
		this.spacialMemory.transform(this.transform);		
		this.spacialMemory.forgetOldPlaces();		
		this.spacialMemory.addPlace(enactedPlace);

		// Merge phenomenon types	
		
		if (enaction.getIntendedPrimitiveAct() != null){
			if (area.getLabel().equals(AreaImpl.O)  ){
				PhenomenonTypeImpl.merge(phenomenonType, PhenomenonTypeImpl.EMPTY);
				phenomenonInstance = new PhenomenonInstanceImpl(PhenomenonTypeImpl.EMPTY, enactedPlace.clone());
				if (m_tracer != null ){
					if (!phenomenonType.equals(PhenomenonTypeImpl.EMPTY)){
						m_tracer.addSubelement(phenomenonInstElemnt, "type", PhenomenonTypeImpl.EMPTY.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "merge", phenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "area", area.getLabel());
					}
					else if (!previousArea.getLabel().equals(AreaImpl.O)){
						m_tracer.addSubelement(phenomenonInstElemnt, "type", PhenomenonTypeImpl.EMPTY.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "shift", phenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "area", area.getLabel());
					}
				}
				phenomenonType = PhenomenonTypeImpl.EMPTY;
			}
			else if (projectedArea.equals(area)){
				PhenomenonType previousPhenomenonType = phenomenonInstance.getPhenomenonType();
				phenomenonInstance.getPlace().setPosition(enactedPlace.getPosition()); //Update the position
				if (!previousPhenomenonType.equals(phenomenonType)){
					PhenomenonTypeImpl.merge(phenomenonType, previousPhenomenonType);
					if (m_tracer != null){
						m_tracer.addSubelement(phenomenonInstElemnt, "type", previousPhenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "merge", phenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "area", area.getLabel());
						}
					phenomenonType = previousPhenomenonType;
					preAppearance = AppearanceImpl.createOrGet(phenomenonType, previousArea);
				}
			}
			else {
				phenomenonInstance = new PhenomenonInstanceImpl(phenomenonType, enactedPlace.clone());
				if (m_tracer != null){
					m_tracer.addSubelement(phenomenonInstElemnt, "type", phenomenonType.getLabel());
					m_tracer.addSubelement(phenomenonInstElemnt, "shift", phenomenonType.getLabel());
					m_tracer.addSubelement(phenomenonInstElemnt, "area", phenomenonInstance.getPlace().getArea().getLabel());
				}
			}
		}
		
		// Record the experiment
//		Appearance postAppearance = AppearanceImpl.createOrGet(phenomenonType, area);
//		Experiment newExp = ExperimentImpl.createOrGet(preAppearance, enaction.getEnactedPrimitiveAct().getPrimitive().getAction());
//		newExp.incActCounter(enaction.getEnactedPrimitiveAct());
//		newExp.incDisplacementCounter(displacement);
//		newExp.incPostAppearanceCounter(postAppearance);
//		if (m_tracer != null){
//			m_tracer.addEventElement("experiment", newExp.toString());}

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
