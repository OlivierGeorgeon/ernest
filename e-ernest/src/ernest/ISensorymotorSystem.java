package ernest;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.vecmath.Vector3f;

import com.sun.tools.javac.util.List;

import spas.IObservation;
import spas.IPlace;
import spas.ISegment;
import spas.ISpas;
import spas.ISpatialMemory;
import spas.Place;
import imos.IAct;
import imos.IActProposition;
import imos.IImos;
import imos.IProposition;
import imos.ISchema;



/**
 * Generic interface for Ernest's sensorymotor system
 * Support primitive binary interactions.
 * Determine the enacted act from the selected schema and the binary feedback status.  
 * @author ogeorgeon
 */
public interface ISensorymotorSystem 
{
	
	/**
	 * Initialize the sensorymotor system with the connection to episodic memory and to the attentional system
	 * @param episodicMemory Ernest's episodic memory.
	 * @param staticSystem Ernest's static system.
	 * @param imos Ernest's motivational system.
	 * @param tracer Ernest's tracer.
	 */
	public void init(ISpas staticSystem, IImos imos , ITracer tracer);

	/**
	 * Used by the environment to set the primitive binary sensorymotor acts.
	 * @param schemaLabel The schema's label that is interpreted by the environment.
	 * @param status The act's succeed or fail status 
	 * @param satisfaction The act's satisfaction 
	 * @return the created primitive act
	 */
	public IAct addInteraction(String schemaLabel, String stimuliLabel, int satisfaction); 

	/**
	 * Determine the enacted act 
	 * @param schema The enacted primitive schema
	 * @param status The status returned as a feedback from the enacted schema
	 * @return The enacted act
	 */
	public IAct enactedAct(IAct act, boolean status);

	/**
	 * Determine the enacted act 
	 * @param schema The enacted primitive schema
	 * @param status The status returned as a feedback from the enacted schema
	 * @return The enacted act
	 */
	public IAct enactedAct(IAct act, String status); 
	
	public IAct enactedAct(IAct act, int[][] matrix);
	
	public int[] update(int[][] stimuli);
	public void sense(int[][] stimuli);
	public int impulsion(int intentionSchema);
	public void setSegmentList(ArrayList<ISegment> segmentList);
	
	public ArrayList<IPlace> getPhenomena();
	public boolean checkConsistency(IAct act);
	
	public IAct situationAct();
	public ArrayList<IProposition> getPropositionList();
	public ArrayList<IActProposition> getPropositionList(ArrayList<IAct> acts);
	public void updateSpas(IAct primitiveAct, IAct topAct);
	
	//public IAct anticipateInteraction(ISchema s, int e, ArrayList<IAct> acts);
	
	public IAct enactedAct(IAct m_primitiveAct, IObservation observation);
	
	public ISpatialMemory getSpatialSimulation();
	
	//public void setFrame(JFrame frame);

}
