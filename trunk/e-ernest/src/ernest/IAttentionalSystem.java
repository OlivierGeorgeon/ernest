package ernest;

import java.awt.Color;


/**
 * Ernest's attentional system.
 * Maintain lists of acts that represent Ernest's current situation
 * Control the current enaction.
 * @author ogeorgeon
 */
public interface IAttentionalSystem 
{

	/**
	 * @param tracer The tracer used to generate Ernest's activity traces
	 */
	public void setTracer(ITracer tracer);
	
	/**
	 * Get a description of Ernest's internal state.
	 * @return A representation of Ernest's internal state
	 */
	public String getInternalState();
	
	/**
	 * Ernest's central process.
	 * Choose the intentions to enact and control their enaction. 
	 * @param primitiveEnaction The actually enacted primitive act.
	 * @return The primitive schema to enact in the environment 
	 */
	public IAct step(IAct primitiveEnaction); 
	
}
