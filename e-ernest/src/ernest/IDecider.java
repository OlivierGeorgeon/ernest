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
	 * @param enaction The current enaction.
	 */
	public void decide(IEnaction enaction);
}
