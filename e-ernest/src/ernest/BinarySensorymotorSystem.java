package ernest;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.vecmath.Vector3f;

import spas.IPlace;
import spas.ISegment;
import spas.ISpas;
import spas.ISpatialMemory;
import spas.LocalSpaceMemory;
import spas.Place;
import imos.IAct;
import imos.IActProposition;
import imos.IImos;
import imos.IProposition;
import imos.ISchema;


/**
 * The binary sensorymotor system can only sense a binary feedback from the environment.
 * This sensorymotor system is provided as an example for the SimpleMaze environment,
 * and as a parent class for more complex sensorymotor systems.
 * @author ogeorgeon
 */
public class BinarySensorymotorSystem implements ISensorymotorSystem 
{
	protected IImos m_imos;
	protected ITracer m_tracer;
	protected spas.ISpas m_spas;
    private ISpatialMemory m_spatialMemory;

	public void init(ISpas spas, IImos imos, ITracer tracer)
	{
		m_spas = spas;
		m_spatialMemory = m_spas.getSpatialMemory();
		m_imos = imos;
		m_tracer = tracer;
		// TODO clean this up.
		if (tracer == null) System.out.println("The method Ernest.setTracer() must be called before the method Ernest.setSensorymotorSystem.");
	}
	
	public IAct addInteraction(String schemaLabel, String stimuliLabel, int satisfaction)
	{
		return m_imos.addInteraction(schemaLabel, stimuliLabel, satisfaction);
	}

	/**
	 * Determine the enacted act 
	 * This implementation does not assume that the resulting act already exists
	 * If the resulting act did not exist then it is created with a satisfaction value of 0.
	 * @param status The status returned as a feedback from the enacted schema
	 * @return The enacted act
	 */
	public IAct enactedAct(IAct act, boolean status) 
	{
		// The schema is null during the first cycle
		if (act == null) return null;
		
		IAct enactedAct = m_imos.addInteraction(act.getSchema().getLabel(), (status ? "t" : "f"), 0);
		
		return enactedAct;
	}

	public IAct enactedAct(IAct act, String status) 
	{
		// The schema is null during the first cycle
		if (act == null) return null;
		
		IAct enactedAct = m_imos.addInteraction(act.getSchema().getLabel(), status, 0);
		
		return enactedAct;
	}

	public IAct enactedAct(IAct act, int[][] matrix) { return null; }

	public int impulsion(int intentionSchema) { return 0; }

	public void sense(int[][] stimuli) { }

	public int[] update(int[][] stimuli) { return null; }
	
	public void setSegmentList(ArrayList<ISegment> segmentList) {}

	public ArrayList<IPlace> getPhenomena() 
	{
		return new ArrayList<IPlace>();
	}

}
