package eca.spas;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import spas.IBundle;
import spas.Place;
import spas.Simu;
import spas.SimuImpl;

import eca.Primitive;
import eca.enaction.Act;
import eca.enaction.Enaction;
import eca.spas.egomem.Area;
import eca.spas.egomem.ISpatialMemory;
import eca.spas.egomem.LocalSpaceMemory;
import ernest.Action;
import ernest.Phenomenon;
import ernest.PhenomenonImpl;
import ernest.Experiment;
import ernest.ExperimentImpl;
import ernest.ObservationImpl;
import ernest.ITracer;
import ernest.Observation;

/**
 * The spatial system.
 * Maintains the local space map and the persistence memory.
 * @author Olivier
 */
public class Spas implements ISpas 
{
	
	/** The Tracer. */
	private ITracer m_tracer = null; 
	
	/** A list of all the bundles ever identified. */
	public List<IBundle> m_bundles = new ArrayList<IBundle>(10);
	
	/** Ernest's local space memory  */
	private ISpatialMemory m_localSpaceMemory = new LocalSpaceMemory();
	
	/** The simulator. */
	Simu simu = new SimuImpl();
	
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
	
	public Area categorizePosition(Point3f point){
		return SimuImpl.getArea(point);
	}
	
	/**
	 * The main method of the Spatial System that is called on each interaction cycle.
	 * Track the spatial consequences of the current enaction.
	 * @param enaction The current enaction.
	 */
	public void track(Enaction enaction) 
	{
		tick();
		
		Area area = enaction.getEnactedPrimitiveAct().getArea();
		
		// Update spatial memory
		
		this.simu.track(enaction);

		this.transform = SimuImpl.spasTransform(enaction.getEnactedPrimitiveAct().getPrimitive().getAction().getTransformation());

		m_localSpaceMemory.transform(this.transform);		
		m_localSpaceMemory.forgetOldPlaces();
		
		if ( enaction.getEnactedPrimitiveAct() != null){
			addPlace(enaction.getEnactedPrimitiveAct(), SimuImpl.spasPoint(area), enaction.getEnactedPrimitiveAct().getColor());			
		}

		// Merge phenomena
		
		Phenomenon newAspect = enaction.getEnactedPrimitiveAct().getPrimitive().getPhenomenon();
		if (area.equals(SimuImpl.O) && enaction.getIntendedPrimitiveAct() != null){
			PhenomenonImpl.merge(newAspect, SimuImpl.EMPTY);
			if (m_tracer != null && !newAspect.equals(SimuImpl.EMPTY)){
				m_tracer.addEventElement("empty", newAspect.getLabel() + " merged to " + SimuImpl.EMPTY.getLabel());}
		}
		else{ 
			Place previousPlace = m_localSpaceMemory.getPreviousPlace();
			if (previousPlace != null){
				System.out.println("previous place " + previousPlace.getValue());
				Area previousArea = SimuImpl.getArea(previousPlace.getPosition());
				if (previousArea.equals(area)){
					Phenomenon previousAspect = previousPlace.getAct().getPrimitive().getPhenomenon();
					if (!previousAspect.equals(newAspect)){
						PhenomenonImpl.merge(newAspect, previousAspect);
						if (m_tracer != null){
							m_tracer.addEventElement("phenomenon", newAspect.getLabel() + " merged to " + previousAspect.getLabel());}
					}
				}
			}
		}
		
		// Record the experiment
		Observation observation = ObservationImpl.createOrGet(newAspect, area);
		Experiment newExp = ExperimentImpl.createOrGet(enaction.getEnactedPrimitiveAct().getPrimitive().getAction(), observation);
		newExp.addAct(enaction.getEnactedPrimitiveAct());

		if (m_tracer != null) this.simu.trace(m_tracer);
	}

	public int getValue(int i, int j)
	{
		Point3f position = new Point3f(1 - j, 1 - i, 0);
		if (m_localSpaceMemory != null)
			return m_localSpaceMemory.getDisplayCode(position);
		else
			return 0xFFFFFF;
	}
	
	public ArrayList<Place> getPlaceList()
	{
		// return m_localSpaceMemory.getPlaceList();
		return m_localSpaceMemory.clonePlaceList();
	}

	private Place addPlace(Act act, Point3f position,int value) 
	{
		Place place = m_localSpaceMemory.addPlace(act, position);
		place.setValue(value);
		return place;
	}

	public void tick() 
	{
		m_localSpaceMemory.tick();
	}

	public int getValue(Point3f position) 
	{
		return m_localSpaceMemory.getDisplayCode(position);
	}

	public ISpatialMemory getSpatialMemory()
	{
		return m_localSpaceMemory;
	}

	public Observation predict(Action action){
		return this.simu.predict(action);
	}

	public Transform3D getTransformToAnim() {
		return this.transform;
	}

}
