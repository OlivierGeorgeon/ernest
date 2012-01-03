package ernest;

import spas.ISpas;
import imos.IAct;
import imos.IImos;



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
	//public void init(EpisodicMemory episodicMemory, StaticSystem staticSystem, IImos attentionalSystem , ITracer tracer);
	public void init(ISpas staticSystem, IImos imos , ITracer tracer);

	/**
	 * Used by the environment to set the primitive binary sensorymotor acts.
	 * @param schemaLabel The schema's label that is interpreted by the environment.
	 * @param status The act's succeed or fail status 
	 * @param satisfaction The act's satisfaction 
	 * @return the created primitive act
	 */
	//public IAct addPrimitiveAct(String schemaLabel, boolean status, int satisfaction); 

	public IAct addInteraction(String schemaLabel, String stimuliLabel, int satisfaction); 

	/**
	 * Determine the enacted act 
	 * @param schema The enacted primitive schema
	 * @param status The status returned as a feedback from the enacted schema
	 * @return The enacted act
	 */
	public IAct enactedAct(IAct act, boolean status);
	
	public IAct enactedAct(IAct act, int[][] matrix);
	
	public int impulsion(int intentionSchema);
	
}
