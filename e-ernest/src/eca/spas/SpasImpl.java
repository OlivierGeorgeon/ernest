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
	
	private PhenomenonInstance focusPhenomenonInstance = PhenomenonInstance.EMPTY;
	
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

		Object pie = null;
		if (m_tracer != null)
			pie = m_tracer.addEventElement("construct", true);

		//for (ActInstance p : enaction.getEnactedPlaces())
			//p.normalize(3);
		
		// Update spatial memory
		
		this.spacialMemory.tick();
		this.spacialMemory.transform(enaction.getTransform3D());
		this.spacialMemory.forgetOldPlaces();		
		for (ActInstance actInstance : enaction.getEnactedPlaces()){
			this.spacialMemory.addPlaceable(actInstance);
		
			if (actInstance.getModality() == ActInstance.MODALITY_MOVE){
				this.spacialMemory.clearPhenomenonInstanceFront();
			}
			else{
				PhenomenonInstance phenomenonInstance = this.spacialMemory.getPhenomenonInstance(actInstance.getPosition());
				if (phenomenonInstance == null){
					// create a new phenomenon type with this act
					PhenomenonType  phenomenonType = PhenomenonTypeImpl.evoke(actInstance.getAspect());
					if (phenomenonType == null){
						phenomenonType = PhenomenonTypeImpl.createNew();
						phenomenonType.setAspect(actInstance.getAspect());
						phenomenonType.addPrimitive(actInstance.getPrimitive());
						phenomenonInstance = new PhenomenonInstanceImpl(phenomenonType, actInstance.getPosition());
						this.spacialMemory.addPlaceable(phenomenonInstance);
					}
					else{
						phenomenonType.addPrimitive(actInstance.getPrimitive());
						phenomenonInstance = new PhenomenonInstanceImpl(phenomenonType, actInstance.getPosition());
						this.spacialMemory.addPlaceable(phenomenonInstance);
					}
					if (m_tracer != null ){
						phenomenonInstance.trace(m_tracer, pie);
						m_tracer.addSubelement(pie, "create", actInstance.getDisplayLabel());
					}
				}
				else{
					phenomenonInstance.setClock(0);
					// add this act to the phenomenon type of this place
					PhenomenonType phenomenonType = phenomenonInstance.getPhenomenonType();
					if (!phenomenonType.contains(actInstance.getPrimitive())){
//						if (!phenomenonInstance.equals(this.focusPhenomenonInstance)){
//							if (m_tracer != null ){
//								phenomenonInstance.trace(m_tracer, pie);
//								m_tracer.addSubelement(pie, "shift", "");
//							}
//						}
//					}
//					else{
						phenomenonType.addPrimitive(actInstance.getPrimitive());
						if (m_tracer != null ){
							phenomenonInstance.trace(m_tracer, pie);
							m_tracer.addSubelement(pie, "add", actInstance.getDisplayLabel());
						}
					}
				}
				if (actInstance.getModality() == ActInstance.MODALITY_CONSUME || actInstance.getModality() == ActInstance.MODALITY_BUMP)
					phenomenonInstance.getPhenomenonType().setAttractiveness(actInstance.getPrimitive().getValue());
				//this.focusPhenomenonInstance = phenomenonInstance;
			}
		}
		
		// The focus phenomenon is the one that has the highest attractiveness or that is the closest or with which there is an interaction
		
		PhenomenonInstance phenomenonInstance = PhenomenonInstance.EMPTY;
		if (enaction.getSalientActInstance() != null){
			PhenomenonInstance salientPhenomenonInstance = this.spacialMemory.getPhenomenonInstance(enaction.getSalientActInstance().getPosition());
			if (salientPhenomenonInstance != null)
				 phenomenonInstance = salientPhenomenonInstance;
		}
		float distance = 10000;
		int attractiveness = -200;
		for (PhenomenonInstance p : this.spacialMemory.getPhenomenonInstances())
			if (p.getPhenomenonType().getAttractiveness() > attractiveness && p.getPosition().x >= 0){
				phenomenonInstance = p;
				distance = phenomenonInstance.getDistance();
				attractiveness = phenomenonInstance.getPhenomenonType().getAttractiveness();
			}
			else if (p.getDistance() < distance  && p.getPosition().x >= 0){
				phenomenonInstance = p;
				distance = phenomenonInstance.getDistance();
				attractiveness = phenomenonInstance.getPhenomenonType().getAttractiveness();
			}
		if (!this.focusPhenomenonInstance.equals(phenomenonInstance)){
			this.focusPhenomenonInstance = phenomenonInstance;
			if (m_tracer != null ){
				phenomenonInstance.trace(m_tracer, pie);
				m_tracer.addSubelement(pie, "shift", "");
			}			
		}
			
		
		// Merge phenomenon types
		
		//this.mergePhenomenonTypes(enaction.getSalientPlace());
	}

	public ArrayList<Placeable> getPlaceableClones(){
		ArrayList<Placeable> placeableClones = new ArrayList<Placeable>();
		for (Placeable placeable : this.spacialMemory.getPlaceables()){
			Placeable placeableClone = placeable.clone();
			if (placeableClone.getPosition().epsilonEquals(this.getFocusPhenomenonInstance().getPosition(), .1f))
				placeableClone.setFocus(true);
			placeableClones.add(placeableClone);
		}
		return placeableClones;
	}

	public int getDisplayCode(){
		int displayCode = Ernest.UNANIMATED_COLOR;
		PhenomenonInstance forcusPhenomenonInstance = getFocusPhenomenonInstance();
		if (forcusPhenomenonInstance != null)
			displayCode = forcusPhenomenonInstance.getPhenomenonType().getAspect().getCode();
		
		return displayCode;
	}
	
	public PhenomenonInstance getFocusPhenomenonInstance(){
		return this.focusPhenomenonInstance;
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
