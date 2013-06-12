package eca.decider;

import eca.Enaction;
import ernest.ITracer;

/**
 * A decider decides what interaction to try to enact next
 * when the previous decision cycle is over
 * based on the current state of sequential and spatial memory
 * @author Olivier
 */
public interface IDecider 
{
	/**
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer tracer);

	/**
	 * @param enaction The current enaction.
	 * @return The next enaction.
	 */
	public Enaction decide(Enaction enaction);
	
	/**
	 * @param enaction The current enaction.
	 */
	public void carry(Enaction enaction);
}
