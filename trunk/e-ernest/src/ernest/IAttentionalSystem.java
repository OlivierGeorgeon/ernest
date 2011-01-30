package ernest;

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
	 * @return The primitive intended act in the current automatic loop.
	 */
	public  IAct getPrimitiveIntention();
	
	/**
	 * Ernest's central process.
	 * @param primitiveEnaction The actually enacted primitive act.
	 */
	public ISchema step(IAct primitiveEnaction); 
}
