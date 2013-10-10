package ernest;

/**
 * A decider decides what interaction to try to enact next
 * when the previous decision cycle is over
 * based on the current state of sequential and spatial memory
 * @author Olivier
 */
public interface IDecider 
{
	/**
	 * @param maxSchemaLength The maximum length of a schema for being enacted.
	 */
	public void setMaxSchemaLength(int maxSchemaLength);
	
	/**
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer tracer);

	/**
	 * @param enaction The current enaction.
	 * @return The next enaction.
	 */
	public IEnaction decide(IEnaction enaction);
	
	/**
	 * @param enaction The current enaction.
	 */
	public void carry(IEnaction enaction);
}
