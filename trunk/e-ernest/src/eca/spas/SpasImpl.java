package eca.spas;


import java.util.ArrayList;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import tracing.ITracer;
import eca.Primitive;
import eca.construct.Action;
import eca.construct.Observation;
import eca.construct.ObservationImpl;
import eca.construct.Phenomenon;
import eca.construct.PhenomenonImpl;
import eca.construct.egomem.Area;
import eca.construct.egomem.AreaImpl;
import eca.construct.experiment.Experiment;
import eca.construct.experiment.ExperimentImpl;
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
				
		// Update spatial memory
		
		tick();
		//this.transform.set(enaction.getEnactedPrimitiveAct().getPrimitive().getAction().getTransformation().getTransform3D());
		this.transform.set(enaction.getEnactedPrimitiveAct().getPrimitive().getDisplace().getTransform3D());
		this.spacialMemory.transform(this.transform);		
		this.spacialMemory.forgetOldPlaces();
		this.spacialMemory.addPlace(enactedPlace);

		// Merge phenomena
		
		Area area = enaction.getEnactedPrimitiveAct().getArea();
		Phenomenon newPhenomenonType = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenonType();
		if (area.getLabel().equals(AreaImpl.O) && enaction.getIntendedPrimitiveAct() != null){
			PhenomenonImpl.merge(newPhenomenonType, PhenomenonImpl.EMPTY);
			if (m_tracer != null && !newPhenomenonType.equals(PhenomenonImpl.EMPTY)){
				m_tracer.addEventElement("empty", newPhenomenonType.getLabel() + " merged to " + PhenomenonImpl.EMPTY.getLabel());}
		}
		else{ 
			Place previousPlace = spacialMemory.getPreviousPlace();
			if (previousPlace != null){
				System.out.println("previous place " + previousPlace.getValue());
				Area previousArea = AreaImpl.createOrGet(previousPlace.getPosition());
				if (previousArea.equals(area)){
					//Phenomenon previousPhenomenonType = previousPlace.getPrimitive().getPhenomenonType();
					Phenomenon previousPhenomenonType = previousPlace.getPhenomenonType();
					if (!previousPhenomenonType.equals(newPhenomenonType)){
						PhenomenonImpl.merge(newPhenomenonType, previousPhenomenonType);
						enactedPlace.setPhenomenonType(previousPhenomenonType);
						if (m_tracer != null){
							m_tracer.addEventElement("phenomenon", newPhenomenonType.getLabel() + " merged to " + previousPhenomenonType.getLabel());}
					}
				}
			}
		}
		
		// Record the experiment
		Observation observation = ObservationImpl.createOrGet(newPhenomenonType, area);
		Experiment newExp = ExperimentImpl.createOrGet(enaction.getEnactedPrimitiveAct().getPrimitive().getAction(), observation);
		newExp.addAct(enaction.getEnactedPrimitiveAct());

		//if (m_tracer != null) this.simu.trace(m_tracer);
	}

	public int getValue(int i, int j)
	{
		Point3f position = new Point3f(1 - j, 1 - i, 0);
		if (spacialMemory != null)
			return spacialMemory.getDisplayCode(position);
		else
			return 0xFFFFFF;
	}
	
	public ArrayList<Place> getPlaceList()
	{
		// return m_localSpaceMemory.getPlaceList();
		return this.spacialMemory.clonePlaceList();
	}

//	private Place addPlace(Primitive primitive, Point3f position,int value) 
//	{
//		Place place = this.spacialMemory.addPlace(primitive, position);
//		place.setValue(value);
//		return place;
//	}

	public void tick() {
		this.spacialMemory.tick();
	}

	public int getValue(Point3f position) 
	{
		return this.spacialMemory.getDisplayCode(position);
	}

	public SpatialMemory getSpatialMemory()
	{
		return this.spacialMemory;
	}

	public Observation predictPhenomenonInst(Action action){
		Observation observation = ObservationImpl.createOrGet(PhenomenonImpl.EMPTY, AreaImpl.createOrGet(new Point3f()));
		if (this.spacialMemory.getPreviousPlace() != null){
			Place lastPlace = this.spacialMemory.getPreviousPlace().clone();
			if (action.getPrimitives().get(0).getDisplace() != null)
				lastPlace.transform(action.getPrimitives().get(0).getDisplace().getTransform3D()); // TODO manage simultaneously the displacement and the phenomenon instance
//					.getTransformation().getTransform3D());
			observation = ObservationImpl.createOrGet(lastPlace.getPrimitive().getPhenomenonType(), AreaImpl.createOrGet(lastPlace.getPosition()));
		}
		return observation;
		
	}

	public Transform3D getTransformToAnim() {
		return this.transform;
	}

}
