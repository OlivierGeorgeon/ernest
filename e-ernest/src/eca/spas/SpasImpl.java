package eca.spas;


import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import tracing.ITracer;
import eca.construct.Appearance;
import eca.construct.AppearanceImpl;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.construct.egomem.Area;
import eca.construct.egomem.AreaImpl;
import eca.construct.egomem.Displacement;
import eca.construct.experiment.Experiment;
import eca.construct.experiment.ExperimentImpl;
import eca.spas.egomem.Place;
import eca.spas.egomem.SpatialMemory;
import eca.spas.egomem.SpatialMemoryImpl;
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
	
//	private static Spas INSTANCE = null; 
//	
//	private Spas() {
//		// TODO Auto-generated constructor stub
//	}
//	
//	public static Spas instance(){
//		if (INSTANCE == null)
//			INSTANCE = new Spas();
//		return INSTANCE;
//	}

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

		//Appearance previousAppearance = spacialMemory.getLastAppearance();
		Appearance previousAppearance = enaction.getAppearance();
		Displacement displacement = enaction.getEnactedPrimitiveAct().getPrimitive().getDisplacement();
		this.transform.set(displacement.getTransform3D());
		
		// Update spatial memory
		
		this.spacialMemory.tick();
		this.spacialMemory.transform(this.transform);		
		this.spacialMemory.forgetOldPlaces();
		
		this.spacialMemory.addPlace(enactedPlace);

		// Merge phenomena
		
		Area area = enaction.getEnactedPrimitiveAct().getArea();
		PhenomenonType newPhenomenonType = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenonType();
		if (area.getLabel().equals(AreaImpl.O) && enaction.getIntendedPrimitiveAct() != null){
			PhenomenonTypeImpl.merge(newPhenomenonType, PhenomenonTypeImpl.EMPTY);
			if (m_tracer != null && !newPhenomenonType.equals(PhenomenonTypeImpl.EMPTY)){
				m_tracer.addEventElement("empty", newPhenomenonType.getLabel() + " merged to " + PhenomenonTypeImpl.EMPTY.getLabel());}
		}
		else{ 
			if (previousAppearance != null){
				if (previousAppearance.getArea().equals(area)){
					PhenomenonType previousPhenomenonType = previousAppearance.getPhenomenonType();
					if (!previousPhenomenonType.equals(newPhenomenonType)){
						PhenomenonTypeImpl.merge(newPhenomenonType, previousPhenomenonType);
						//enactedPlace.setPhenomenonType(previousPhenomenonType);
						if (m_tracer != null){
							m_tracer.addEventElement("phenomenon", newPhenomenonType.getLabel() + " merged to " + previousPhenomenonType.getLabel());}
						newPhenomenonType = previousPhenomenonType;
					}
				}
			}
		}
		
		// Record the experiment
		Appearance appearance = AppearanceImpl.createOrGet(newPhenomenonType, area);
		if (previousAppearance != null){
			Experiment newExp = ExperimentImpl.createOrGet(previousAppearance, enaction.getEnactedPrimitiveAct().getPrimitive().getAction());
			newExp.incActCounter(enaction.getEnactedPrimitiveAct());
			newExp.incDisplacementCounter(displacement);
			newExp.incPostAppearanceCounter(appearance);
			if (m_tracer != null){
				m_tracer.addEventElement("experiment", newExp.toString());}
		}
		enaction.setAppearance(appearance);
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

	public SpatialMemory getSpatialMemory()
	{
		return this.spacialMemory;
	}

	public Transform3D getTransformToAnim() {
		return this.transform;
	}

//	public Appearance getLastAppearance() {
//		return this.spacialMemory.getLastAppearance();
//	}

}
