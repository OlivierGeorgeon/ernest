package ernest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;

import tracing.ITracer;
import eca.ActInstance;
import eca.Primitive;
import eca.PrimitiveImpl;
import eca.construct.Action;
import eca.construct.ActionImpl;
import eca.construct.AreaImpl;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.decider.DeciderImpl;
import eca.decider.Decider;
import eca.spas.Placeable;
import eca.spas.Spas;
import eca.spas.SpasImpl;
import eca.spas.egomem.SpatialMemory;
import eca.ss.IImos;
import eca.ss.Imos;
import eca.ss.enaction.Act;
import eca.ss.enaction.ActImpl;
import eca.ss.enaction.Enaction;
import eca.ss.enaction.EnactionImpl;


/**
 * The main Ernest class used to create an Ernest agent in the environment.
 * @author ogeorgeon
 */
public class Ernest implements IErnest 
{
	/** A big value that can represent infinite for diverse purpose. */	
	public static final int INFINITE = 1000 ;//* INT_FACTOR;

	/** Color unanimated */
	public static int UNANIMATED_COLOR = 0x808080;

	/** Ernest's current enaction */
	private Enaction m_enaction = new EnactionImpl();
	
	/** Ernest's spatial system. */
	private Spas m_spas = new SpasImpl();

	/** Ernest's Intrinsically motivated Schema Mechanism. */
	private IImos m_imos = new Imos();
	
	/** Ernest's tracing system. */
	private ITracer m_tracer = null;
	
	private static int clock = 0;
	
	private Transform3D transformToAnim = new Transform3D();
	
	/** Ernest's decisional Mechanism. */
	private Decider m_decider = new DeciderImpl(m_imos, m_spas); // Regular decider for Ernest 7.
	
	/**
	 * Set Ernest's fundamental learning parameters.
	 * @param regularityThreshold The Regularity Sensibility Threshold.
	 * @param maxSchemaLength The Maximum Schema Length
	 */
	public void setParameters(int regularityThreshold, int maxSchemaLength) 
	{
		m_imos.setRegularityThreshold(regularityThreshold);
		m_imos.setMaxSchemaLength(maxSchemaLength);
	}

	/**
	 * Let the environment set the tracer.
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer tracer) 
	{ 
		m_tracer = tracer;
		m_imos.setTracer(m_tracer); 
		m_spas.setTracer(m_tracer);
		m_decider.setTracer(m_tracer);
	}

	public String step(IEffect input) 
	{
		
		// Trace a new interaction cycle.
		if (m_tracer != null){
            //m_tracer.startNewEvent(m_imos.getCounter());
			//m_tracer.addEventElement("clock", m_imos.getCounter() + "");
            m_tracer.startNewEvent(this.clock);
			m_tracer.addEventElement("clock", this.clock + "");
			input.trace(m_tracer);		
		}                

		this.clock++;

		// track the enaction 
		
		m_enaction.track(input);
		m_imos.track(m_enaction);
		m_spas.track(m_enaction);			
		m_enaction.traceTrack(m_tracer);

		
		// Decision cycle
		if (m_enaction.isOver()){
			m_imos.terminate(m_enaction);
			m_enaction = m_decider.decide(m_enaction);
		}

		// Carry out the current enaction
		
		m_decider.carry(m_enaction);
		
		return m_enaction.getIntendedPrimitiveAct().getLabel();		
	}
	
	public String step(List<ActInstance> actInstances, Transform3D transform){
		
		// Trace a new interaction cycle.
		if (m_tracer != null){
            //m_tracer.startNewEvent(m_imos.getCounter());
			//m_tracer.addEventElement("clock", m_imos.getCounter() + "");
            m_tracer.startNewEvent(this.clock);
			m_tracer.addEventElement("clock", this.clock + "");

			Object ep = m_tracer.addEventElement("enacted_places");
			for (ActInstance p : actInstances){
				p.trace(m_tracer, ep);
			}
		}                

		this.clock++;

		// track the enaction 
		
		this.transformToAnim.set(transform);
		m_enaction.track(actInstances, transform);
		m_imos.track(m_enaction);
		m_spas.track(m_enaction);			
		m_enaction.traceTrack(m_tracer);

		
		// Decision cycle
		if (m_enaction.isOver()){
			m_imos.terminate(m_enaction);
			m_enaction = m_decider.decide(m_enaction);
		}

		// Carry out the current enaction
		
		m_decider.carry(m_enaction);
		
		return m_enaction.getIntendedPrimitiveAct().getLabel();		
		
	}
	

	public int getValue(int i, int j)
	{
		return m_spas.getValue(i,j);
	}
	
	public Primitive addInteraction(String label, int value)
	{
		Primitive primitive = PrimitiveImpl.createOrGet(label, value * 10);
		
		Act act = ActImpl.createOrGetPrimitiveAct(primitive, AreaImpl.createOrGet(new Point3f(1,0,0)));
		Action action = ActionImpl.createOrGet("[a" + act.getLabel() + "]");
		action.addAct(act);
		PhenomenonType phenomenonType = PhenomenonTypeImpl.createOrGet("[p" + label +"]");
		phenomenonType.addPrimitive(primitive);	

		return primitive;
	}

	public ArrayList<Placeable> getPlaceList()
	{
		return m_spas.getPlaceList();
	}

	public int getClock(){
		return this.clock;
	}

	public int getUpdateCount() 
	{
//		if (m_imos == null)
//			return 0;
//		else
//			return m_imos.getCounter();
		return this.clock;
	}

	public Collection<Primitive> getPrimitives() 
	{
		return PrimitiveImpl.getINTERACTIONS();
	}
	
	/**
	 * Get a description of Ernest's internal state (to display in the environment).
	 * @return A representation of Ernest's internal state
	 */
	public String internalState() 
	{
		return ""; //m_imos.getInternalState();
	}

	public Transform3D getTransformToAnim() {
		//Transform3D transform = new Transform3D(this.m_enaction.getTransform3D());
		return this.transformToAnim;
		//return m_spas.getTransformToAnim();
	}
		
}
