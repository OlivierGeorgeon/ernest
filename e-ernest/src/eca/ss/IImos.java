package eca.ss;


import java.util.ArrayList;

import tracing.ITracer;

import eca.decider.IProposition;
import eca.enaction.Enaction;

/**
 * The sequential system of the Enactive Cognitive Architecture.
 * @author ogeorgeon
 */
public interface IImos 
{
	/**
	 * @param regularityThreshold The regularity sensibility threshold.
	 */
	public void setRegularityThreshold(int regularityThreshold);
	
	/**
	 * @param maxSchemaLength The maximum length of acts
	 */
	public void setMaxSchemaLength(int maxSchemaLength);

	/**
	 * @param tracer The tracer used to generate the activity traces
	 */
	public void setTracer(ITracer<Object> tracer);
	
	/**
	 * Track the enaction at hand. 
	 * @param enaction The current enaction.
	 */
	public void track(Enaction enaction); 
	
	/**
	 * Terminates the enaction at hand
	 * Record and reinforce new schemas and construct the final context.
	 * @param enaction The current enaction that is being terminated.
	 */
	public void terminate(Enaction enaction);
	
	/**
	 * Generates a list of propositions based on the enaction's activation context.
	 * @param enaction The previous enaction
	 * @return The next list of propositions
	 */
	public ArrayList<IProposition> propose(Enaction enaction);

	/**
	 * The counter of interaction cycles.
	 * @return The current interaction cycle number.
	 */
	public int getCounter();	
}
