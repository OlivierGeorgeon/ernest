package eca.spas;


import java.util.ArrayList;
import java.util.List;
import tracing.ITracer;
import eca.ActInstance;
import eca.Primitive;
import eca.construct.Area;
import eca.construct.PhenomenonInstance;
import eca.construct.PhenomenonInstanceImpl;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.spas.egomem.SpatialMemory;
import eca.spas.egomem.SpatialMemoryImpl;
import eca.ss.enaction.Enaction;
import ernest.Ernest;

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

		//for (ActInstance p : enaction.getEnactedPlaces())
			//p.normalize(3);
		
		// Update spatial memory
		
		this.spacialMemory.tick();
		this.spacialMemory.transform(enaction.getTransform3D());
		this.spacialMemory.forgetOldPlaces();		
		for (ActInstance actInstance : enaction.getEnactedPlaces()){
			this.spacialMemory.addPlaceable(actInstance);
		
			if (actInstance.getModality() != ActInstance.MODALITY_MOVE){
				PhenomenonInstance phenomenonInstance = this.spacialMemory.getPhenomenonInstance(actInstance.getPosition());
				if (phenomenonInstance == null){
					// create a new phenomenon type with this act
					PhenomenonType phenomenonType = PhenomenonTypeImpl.createNew();
					phenomenonType.addPrimitive(actInstance.getPrimitive());
					phenomenonType.setAspect(actInstance.getAspect());
					// create a new phenomenon instance at this place
					phenomenonInstance = new PhenomenonInstanceImpl(phenomenonType, actInstance.getPosition());
					this.spacialMemory.addPlaceable(phenomenonInstance);
				}
				else{
					// add this act to the phenomenon type of this place
					PhenomenonType phenomenonType = phenomenonInstance.getPhenomenonType();
					phenomenonType.addPrimitive(actInstance.getPrimitive());
				}
				if (m_tracer != null ){
					phenomenonInstance.trace(m_tracer, phenomenonInstElemnt);
					m_tracer.addSubelement(phenomenonInstElemnt, "merge", actInstance.getDisplayLabel());
					m_tracer.addSubelement(phenomenonInstElemnt, "area", actInstance.getArea().getLabel());
				}
			}
		}
		// Merge phenomenon types
		
		//this.mergePhenomenonTypes(enaction.getSalientPlace());
	}

	public ArrayList<Placeable> getPlaceList()	{
		return this.spacialMemory.clonePlaceList();
	}

	public int getDisplayCode(){
		int displayCode = Ernest.UNANIMATED_COLOR;
		PhenomenonInstance forcusPhenomenonInstance = getFocusPhenomenonInstance();
		if (forcusPhenomenonInstance != null)
			displayCode = forcusPhenomenonInstance.getPhenomenonType().getAspect().getCode();
		
		return displayCode;
	}
	
	public PhenomenonInstance getFocusPhenomenonInstance(){
		
		List<PhenomenonInstance> phenomenonInstances = this.spacialMemory.getPhenomenonInstances();
		
		PhenomenonInstance phenomenonInstance = null;
		if (phenomenonInstances.size() > 0){
			phenomenonInstance = phenomenonInstances.get(0);
			for (PhenomenonInstance p : phenomenonInstances)
				if (p.getDistance() < phenomenonInstance.getDistance())
					phenomenonInstance = p;
		}
		
		return phenomenonInstance;
	}
	
	private void mergePhenomenonTypes(ActInstance salientPlace){
		
		if (salientPlace != null){
			
			Object phenomenonInstElemnt = null;
			if (m_tracer != null)
				phenomenonInstElemnt = m_tracer.addEventElement("phenomenonInstance", true);
	
			Primitive enactedPrimitive = salientPlace.getPrimitive();
			Area enactedArea = salientPlace.getArea();
			
			PhenomenonType actualPhenomenonType = PhenomenonTypeImpl.evoke(salientPlace.getPrimitive());
			actualPhenomenonType.setAspect(salientPlace.getAspect());
	
			PhenomenonInstance focusPhenomenonInstance = this.getFocusPhenomenonInstance();
			if (focusPhenomenonInstance == null){
				focusPhenomenonInstance = new PhenomenonInstanceImpl(actualPhenomenonType, salientPlace.getPosition());
				this.spacialMemory.addPlaceable(focusPhenomenonInstance);
			}
			Area projectedArea = focusPhenomenonInstance.getArea();
	
			if (salientPlace.getModality() == ActInstance.MODALITY_MOVE){
				PhenomenonTypeImpl.merge(enactedPrimitive, PhenomenonTypeImpl.EMPTY);
				focusPhenomenonInstance.setPosition(salientPlace.getPosition());
				if (!focusPhenomenonInstance.getPhenomenonType().equals(PhenomenonTypeImpl.EMPTY)){
					focusPhenomenonInstance.setPhenomenonType(PhenomenonTypeImpl.EMPTY);
					if (m_tracer != null ){
						if (!actualPhenomenonType.equals(PhenomenonTypeImpl.EMPTY)){
							PhenomenonTypeImpl.EMPTY.trace(m_tracer, phenomenonInstElemnt);
							m_tracer.addSubelement(phenomenonInstElemnt, "merge", actualPhenomenonType.getLabel());
							m_tracer.addSubelement(phenomenonInstElemnt, "area", enactedArea.getLabel());
						}
						else{
							PhenomenonTypeImpl.EMPTY.trace(m_tracer, phenomenonInstElemnt);
							m_tracer.addSubelement(phenomenonInstElemnt, "shift", actualPhenomenonType.getLabel());
							m_tracer.addSubelement(phenomenonInstElemnt, "area", enactedArea.getLabel());
						}
					}
				}
			}
			// Follow the phenomenon instance
			else if (enactedArea.equals(projectedArea)){
				PhenomenonType previousPhenomenonType = focusPhenomenonInstance.getPhenomenonType();
				focusPhenomenonInstance.setPosition(salientPlace.getPosition()); 
				if (!previousPhenomenonType.equals(actualPhenomenonType)){
					PhenomenonTypeImpl.merge(enactedPrimitive, previousPhenomenonType);
					//if (salientPlace.getModality() == ActInstance.MODALITY_VISION)
						previousPhenomenonType.setAspect(salientPlace.getAspect());
					if (m_tracer != null){
						previousPhenomenonType.trace(m_tracer, phenomenonInstElemnt);
						m_tracer.addSubelement(phenomenonInstElemnt, "merge", actualPhenomenonType.getLabel());
						m_tracer.addSubelement(phenomenonInstElemnt, "area", enactedArea.getLabel());
						}
					//preAppearance = AppearanceImpl.createOrGet(actualPhenomenonType, previousArea);
				}
			}
			// Shift to another phenomenon instance
			else {
				focusPhenomenonInstance.setPhenomenonType(actualPhenomenonType);
				focusPhenomenonInstance.setPosition(salientPlace.getPosition());
				if (m_tracer != null){
					actualPhenomenonType.trace(m_tracer, phenomenonInstElemnt);
					m_tracer.addSubelement(phenomenonInstElemnt, "shift", "");
					m_tracer.addSubelement(phenomenonInstElemnt, "area", focusPhenomenonInstance.getPlace().getArea().getLabel());
				}
			}		
		}
	}
}
