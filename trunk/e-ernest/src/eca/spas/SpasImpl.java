package eca.spas;


import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import tracing.ITracer;
import eca.ActInstance;
import eca.Primitive;
import eca.construct.Area;
import eca.construct.AreaImpl;
import eca.construct.PhenomenonInstance;
import eca.construct.PhenomenonInstanceImpl;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.spas.egomem.SpatialMemory;
import eca.spas.egomem.SpatialMemoryImpl;
import eca.ss.enaction.Enaction;

/**
 * The spatial system.
 * @author Olivier
 */
public class SpasImpl implements Spas 
{
	
	/** The Tracer. */
	private ITracer m_tracer = null; 
	
	/** Ernest's local space memory  */
	private SpatialMemory spacialMemory = new SpatialMemoryImpl();
	
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

		for (ActInstance p : enaction.getEnactedPlaces())
			p.normalize(3);
		
		ActInstance salientPlace = enaction.getSalientPlace();	
		Transform3D transform = enaction.getTransform3D();
		
		if (salientPlace != null){
	
			Primitive enactedPrimitive = salientPlace.getPrimitive();
			Area enactedArea = salientPlace.getArea();
			
			PhenomenonType actualPhenomenonType = PhenomenonTypeImpl.evoke(salientPlace.getPrimitive());
			//Area previousArea = null;
			Area projectedArea = null;
//			if (intendedPhenomenonInstance == null){
//				//previousArea = AreaImpl.createOrGet(new Point3f());
//				intendedPhenomenonInstance = new PhenomenonInstanceImpl(actualPhenomenonType, salientPlace.getPosition());
//				this.spacialMemory.addPlaceable(intendedPhenomenonInstance);
//			}
//			else{
//				//previousArea = intendedPhenomenonInstance.getArea();
//				//intendedPhenomenonInstance.transform(transform);
//				//projectedArea = intendedPhenomenonInstance.getArea();
//			}
			
			//Appearance preAppearance = AppearanceImpl.createOrGet(intendedPhenomenonInstance.getPhenomenonType(), previousArea);
	
			// Update spatial memory
			
			this.spacialMemory.tick();
			this.spacialMemory.transform(transform);
			PhenomenonInstance intendedPhenomenonInstance = this.getFocusPhenomenon();
			if (intendedPhenomenonInstance == null){
				//previousArea = AreaImpl.createOrGet(new Point3f());
				intendedPhenomenonInstance = new PhenomenonInstanceImpl(actualPhenomenonType, salientPlace.getPosition());
				this.spacialMemory.addPlaceable(intendedPhenomenonInstance);
			}
			projectedArea = intendedPhenomenonInstance.getArea();
			this.spacialMemory.forgetOldPlaces();		
			for (ActInstance p : enaction.getEnactedPlaces())
				this.spacialMemory.addPlaceable(p);
			
			// Empty phenomenon instance
			if (enactedArea.getLabel().equals(AreaImpl.O)  ){
				PhenomenonTypeImpl.merge(enactedPrimitive, PhenomenonTypeImpl.EMPTY);
				intendedPhenomenonInstance.setPhenomenonType(PhenomenonTypeImpl.EMPTY);
				intendedPhenomenonInstance.setPosition(salientPlace.getPosition());
				//intendedPhenomenonInstance = new PhenomenonInstanceImpl(PhenomenonTypeImpl.EMPTY, salientPlace.getPosition());
				if (m_tracer != null ){
					if (!actualPhenomenonType.equals(PhenomenonTypeImpl.EMPTY)){
						PhenomenonTypeImpl.EMPTY.trace(m_tracer, phenomenonInstElemnt);
						m_tracer.addSubelement(phenomenonInstElemnt, "merge", actualPhenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "area", enactedArea.getLabel());
					}
					else //if (!previousArea.getLabel().equals(AreaImpl.O))
					{
						PhenomenonTypeImpl.EMPTY.trace(m_tracer, phenomenonInstElemnt);
						m_tracer.addSubelement(phenomenonInstElemnt, "shift", actualPhenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "area", enactedArea.getLabel());
					}
				}
				actualPhenomenonType = PhenomenonTypeImpl.EMPTY;
			}
			// Follow the phenomenon instance
			else if (enactedArea.equals(projectedArea)){
				PhenomenonType previousPhenomenonType = intendedPhenomenonInstance.getPhenomenonType();
				intendedPhenomenonInstance.setPosition(salientPlace.getPosition()); //Update the position
				if (!previousPhenomenonType.equals(actualPhenomenonType)){
					PhenomenonTypeImpl.merge(enactedPrimitive, previousPhenomenonType);
					if (salientPlace.getModality() == ActInstance.MODALITY_VISION)
						previousPhenomenonType.setAspect(salientPlace.getAspect());
					if (m_tracer != null){
						previousPhenomenonType.trace(m_tracer, phenomenonInstElemnt);
						m_tracer.addSubelement(phenomenonInstElemnt, "merge", actualPhenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "area", enactedArea.getLabel());
						}
					actualPhenomenonType = previousPhenomenonType;
					//preAppearance = AppearanceImpl.createOrGet(actualPhenomenonType, previousArea);
				}
			}
			// Shift to another phenomenon instance
			else {
				intendedPhenomenonInstance.setPhenomenonType(actualPhenomenonType);
				intendedPhenomenonInstance.setPosition(salientPlace.getPosition());
				//intendedPhenomenonInstance = new PhenomenonInstanceImpl(actualPhenomenonType, salientPlace.getPosition());
				if (m_tracer != null){
					actualPhenomenonType.setAspect(salientPlace.getAspect());
					actualPhenomenonType.trace(m_tracer, phenomenonInstElemnt);
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

		//enaction.setPhenomenonInstance(intendedPhenomenonInstance);
	}

	public int getValue(int i, int j)
	{
		Point3f position = new Point3f(1 - j, 1 - i, 0);
		if (spacialMemory != null)
			return spacialMemory.getDisplayCode(position);
		else
			return 0xFFFFFF;
	}
	
	public ArrayList<Placeable> getPlaceList()	{
		return this.spacialMemory.clonePlaceList();
	}

	public int getValue(Point3f position) 
	{
		return this.spacialMemory.getDisplayCode(position);
	}
	
	public PhenomenonInstance getFocusPhenomenon(){
		PhenomenonInstance phenomenonInstance = null;
		for (Placeable placeable : this.spacialMemory.getPlaceables()){
			if (placeable instanceof PhenomenonInstance){
				phenomenonInstance = (PhenomenonInstance)placeable;
				break;
			}
		}
		return phenomenonInstance;
	}
}
