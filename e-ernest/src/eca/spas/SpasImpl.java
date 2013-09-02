package eca.spas;


import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import tracing.ITracer;
import eca.Primitive;
import eca.construct.Area;
import eca.construct.AreaImpl;
import eca.construct.PhenomenonInstance;
import eca.construct.PhenomenonInstanceImpl;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.construct.egomem.Displacement;
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
	//Transform3D transform = new Transform3D();
	
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

		for (Place p : enaction.getEnactedPlaces())
			p.normalize(3);
		Place salientPlace = enaction.getSalientPlace();	

		PhenomenonInstance intendedPhenomenonInstance = enaction.getPhenomenonInstance();
		
		//this.transform.set(enaction.getTransform3D());
		Transform3D transform = enaction.getTransform3D();
		
		if (salientPlace != null){
	
			Primitive enactedPrimitive = salientPlace.getPrimitive();
			Area enactedArea = salientPlace.getArea();
			
			//PhenomenonType actualPhenomenonType = PhenomenonTypeImpl.evoke(enaction.getEnactedPrimitiveAct().getPrimitive());
			PhenomenonType actualPhenomenonType = PhenomenonTypeImpl.evoke(salientPlace.getPrimitive());
			Area previousArea = null;
			Area projectedArea = null;
			if (intendedPhenomenonInstance == null){
				previousArea = AreaImpl.createOrGet(new Point3f());
				intendedPhenomenonInstance = new PhenomenonInstanceImpl(actualPhenomenonType, salientPlace.clone());
			}
			else{
				previousArea = intendedPhenomenonInstance.getPlace().getArea();
				//System.out.println("pre-position " + phenomenonInstance.getPlace().getPosition().x + "," + phenomenonInstance.getPlace().getPosition().y);
				intendedPhenomenonInstance.getPlace().transform(transform);
				//System.out.println("projected position " + phenomenonInstance.getPlace().getPosition().x + "," + phenomenonInstance.getPlace().getPosition().y);
				projectedArea = intendedPhenomenonInstance.getPlace().getArea();
			}
			
			Appearance preAppearance = AppearanceImpl.createOrGet(intendedPhenomenonInstance.getPhenomenonType(), previousArea);
	
			//Area area = enaction.getEnactedPrimitiveAct().getArea();
			//Primitive enactedPrimitive = enaction.getEnactedPrimitiveAct().getPrimitive();
			
			// Update spatial memory
			
			this.spacialMemory.tick();
			this.spacialMemory.transform(transform);		
			this.spacialMemory.forgetOldPlaces();		
			for (Place p : enaction.getEnactedPlaces())
				this.spacialMemory.addPlace(p);
	
			// Merge phenomenon types	
		
			if (enactedArea.getLabel().equals(AreaImpl.O)  ){
				PhenomenonTypeImpl.merge(enactedPrimitive, PhenomenonTypeImpl.EMPTY);
				intendedPhenomenonInstance = new PhenomenonInstanceImpl(PhenomenonTypeImpl.EMPTY, salientPlace.clone());
				if (m_tracer != null ){
					if (!actualPhenomenonType.equals(PhenomenonTypeImpl.EMPTY)){
						m_tracer.addSubelement(phenomenonInstElemnt, "type", PhenomenonTypeImpl.EMPTY.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "merge", actualPhenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "area", enactedArea.getLabel());
					}
					else if (!previousArea.getLabel().equals(AreaImpl.O)){
						m_tracer.addSubelement(phenomenonInstElemnt, "type", PhenomenonTypeImpl.EMPTY.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "shift", actualPhenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "area", enactedArea.getLabel());
					}
				}
				actualPhenomenonType = PhenomenonTypeImpl.EMPTY;
			}
			else if (enactedArea.equals(projectedArea)){
				PhenomenonType previousPhenomenonType = intendedPhenomenonInstance.getPhenomenonType();
				intendedPhenomenonInstance.getPlace().setPosition(salientPlace.getPosition()); //Update the position
				if (!previousPhenomenonType.equals(actualPhenomenonType)){
					PhenomenonTypeImpl.merge(enactedPrimitive, previousPhenomenonType);
					if (m_tracer != null){
						m_tracer.addSubelement(phenomenonInstElemnt, "type", previousPhenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "merge", actualPhenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "area", enactedArea.getLabel());
						}
					actualPhenomenonType = previousPhenomenonType;
					preAppearance = AppearanceImpl.createOrGet(actualPhenomenonType, previousArea);
				}
			}
			else {
				intendedPhenomenonInstance = new PhenomenonInstanceImpl(actualPhenomenonType, salientPlace.clone());
				if (m_tracer != null){
					m_tracer.addSubelement(phenomenonInstElemnt, "type", actualPhenomenonType.getLabel());
					m_tracer.addSubelement(phenomenonInstElemnt, "shift", "");
					m_tracer.addSubelement(phenomenonInstElemnt, "area", intendedPhenomenonInstance.getPlace().getArea().getLabel());
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

		enaction.setPhenomenonInstance(intendedPhenomenonInstance);
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

//	public Transform3D getTransformToAnim() {
//		return this.transform;
//	}

}
