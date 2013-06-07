package ernest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;

import spas.Area;
import spas.AreaImpl;
import spas.Place;
import spas.ISpas;
import spas.ISpatialMemory;
import spas.SimuImpl;
import spas.Spas;
import spas.Transformation;
import imos2.EnactionImpl;
import imos2.Enaction;
import imos2.IImos;
import imos2.Act;
import imos2.Imos;
import imos2.Decider;
import imos2.IDecider;
import imos2.ActImpl;

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
	private ISpas m_spas = new Spas();

	/** Ernest's Intrinsically motivated Schema Mechanism. */
	private IImos m_imos = new Imos();
	
	/** Ernest's tracing system. */
	private ITracer m_tracer = null;
	
	/** The list of primitive interactions available to Ernest */
	//private final Map<String , IPrimitive> interactions = new TreeMap<String , IPrimitive>() ;
//	private final Map<String , Primitive> interactions = new HashMap<String , Primitive>() ;
	
	/** Ernest's decisional Mechanism. */
	private IDecider m_decider = new Decider(m_imos, m_spas); // Regular decider for Ernest 7.
	
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
            m_tracer.startNewEvent(m_imos.getCounter());
			m_tracer.addEventElement("clock", m_imos.getCounter() + "");
			input.trace(m_tracer);		
		}                

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

	public int getValue(int i, int j)
	{
		return m_spas.getValue(i,j);
	}
	
	public Primitive addInteraction(String label, int value)
	{
		return PrimitiveImpl.createOrGet(label, value * 10);
	}

	public ArrayList<Place> getPlaceList()
	{
		return m_spas.getPlaceList();
	}

	public int getCounter() 
	{
		if (m_imos == null)
			return 0;
		else
			return m_imos.getCounter();
	}

	public int getUpdateCount() 
	{
		if (m_imos == null)
			return 0;
		else
			return m_imos.getCounter();
		//return m_spas.getClock();
	}

	public ISpatialMemory getSpatialSimulation() 
	{
		return m_spas.getSpatialMemory();
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
		return m_spas.getTransformToAnim();
	}
		
}
