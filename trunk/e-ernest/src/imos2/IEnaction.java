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
	 * @param effect The effect produced by the last move.
	 */
	public void setEffect(IEffect effect);
	
	/**
	 * @return The effect produced by the last move.
	 */
	public IEffect getEffect();
	
	/**
	 * @param act The last primitive act enacted
	 */
	public void setIntendedPrimitiveInteraction(IInteraction interaction);
	
	/**
	 * @return The last primitive act enacted
	 */
	public IInteraction getIntendedPrimitiveInteraction();	

	/**
	 * @param act The last primitive act enacted
	 */
	public void setEnactedPrimitiveAct(IInteraction interaction);
	
	/**
	 * @return The last primitive act enacted
	 */
	public IInteraction getEnactedPrimitiveInteraction();	

	/**
	 * @param act The last highest-level act enacted
	 */
	public void setTopInteraction(IInteraction interaction);
	
	/**
	 * @return The last highest-level act enacted
	 */
	public IInteraction getTopInteraction();	

	/**
	 * @param act The last highest-level act enacted
	 */
	public void setTopEnactedInteraction(IInteraction interaction);
	
	/**
	 * @return The last highest-level act enacted
	 */
	public IInteraction getTopEnactedInteraction();	

	/**
	 * @param act The last highest-level act enacted
	 */
	public void setTopRemainingInteraction(IInteraction interaction);
	
	/**
	 * @return The last highest-level act enacted
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
	public int getSimulationStatus();

	/**
	 * @param simulationStatus The status of simulating this enaction in spatial memory.
	 */
	public void setSimulationStatus(int simulationStatus);
	
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
	 * @param tracer The tracer
	 */
	public void traceTrack(ITracer tracer);
	public void traceCarry(ITracer tracer);
	public void traceTerminate(ITracer tracer);
	public void trace(ITracer tracer);
}
