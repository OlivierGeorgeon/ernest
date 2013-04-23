package imos2;

import java.util.ArrayList;
import ernest.IEffect;
import ernest.ITracer;


/**
 * A structure used to manage the enaction of a scheme in the real world
 * or the simulation of the enaction of a scheme in spatial memory.
 * @author ogeorgeon
 */
public interface IEnaction 
{
	/**
	 * @param effect The effect resulting from the last enacted interaction.
	 */
	public void setEffect(IEffect effect);
	
	public void setSlice(String slice);
	
	/**
	 * @return The effect resulting from the last enacted interaction.
	 */
	public IEffect getEffect();
	
	/**
	 * @param interaction The last primitive intended interaction
	 */
	public void setIntendedPrimitiveInteraction(IInteraction interaction);
	
	/**
	 * @return The last primitive intended interaction
	 */
	public IInteraction getIntendedPrimitiveInteraction();	

	/**
	 * @param interaction The last primitive enacted interaction
	 */
	public void setEnactedPrimitiveInteraction(IInteraction interaction);
	
	/**
	 * @return The last primitive enacted interaction
	 */
	public IInteraction getEnactedPrimitiveInteraction();	

	/**
	 * @param interaction The composite interaction to be enacted
	 */
	public void setTopInteraction(IInteraction interaction);
	
	/**
	 * @return The composite interaction to be enacted
	 */
	public IInteraction getTopInteraction();	

	/**
	 * @param interaction The highest-level composite interaction enacted thus far.
	 */
	public void setTopEnactedInteraction(IInteraction interaction);
	
	/**
	 * @return The highest-level composite interaction enacted thus far.
	 */
	public IInteraction getTopEnactedInteraction();	

	/**
	 * @param interaction The remaining highest-level composite interaction to enact.
	 */
	public void setTopRemainingInteraction(IInteraction interaction);
	
	/**
	 * @return The remaining highest-level composite interaction to enact.
	 */
	public IInteraction getTopRemainingInteraction();	

	/**
	 * @param step The rank of the primitive act in the current enaction 
	 */
	public void setStep(int step);

	/**
	 * @return The rank of the primitive act in the current enaction 
	 */
	public int getStep();
	
	/**
	 * @param correct false if the top intention was not correctly enacted
	 */
	public void setCorrect(boolean correct);
	
	/**
	 * @return The status of simulating this enaction in spatial memory.
	 */
	//public int getSimulationStatus();

	/**
	 * @param simulationStatus The status of simulating this enaction in spatial memory.
	 */
	//public void setSimulationStatus(int simulationStatus);
	
	/**
	 * @return True if this enaction is terminated.
	 */
	public boolean isOver();
	
	public void setFinalContext(IInteraction enactedInteraction, IInteraction performedInteraction, ArrayList<IInteraction> contextList);
	public ArrayList<IInteraction> getFinalLearningContext();
	public ArrayList<IInteraction> getFinalActivationContext();
	public void setInitialLearningContext(ArrayList<IInteraction> learningContext);
	public ArrayList<IInteraction>  getInitialLearningContext();
	public void setPreviousLearningContext(ArrayList<IInteraction> learningContext);
	public ArrayList<IInteraction>  getPreviousLearningContext();
	public void setNbActLearned(int nbActLearned);
	

	/**
	 * Trace the tracking of the enaction (just after a primitive interaction being enacted)
	 * @param tracer The tracer
	 */
	public void traceTrack(ITracer tracer);
	
	/**
	 * Trace the carrying out of an enaction (just before the next intended primitive interaction)
	 * @param tracer The tracer
	 */
	public void traceCarry(ITracer tracer);

	/**
	 * Trace the termination of an enaction
	 * @param tracer The tracer
	 */
	public void traceTerminate(ITracer tracer);
	//public void trace(ITracer tracer);
	
	//public void addOngoingInteraction(IInteraction intraction);
	//public ArrayList<IInteraction> getOngoingInteractions();
	
}
