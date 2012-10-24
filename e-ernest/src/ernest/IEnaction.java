package ernest;

import java.util.ArrayList;
import java.util.List;

import imos.IAct;

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
	public void setIntendedPrimitiveAct(IAct act);
	
	/**
	 * @return The last primitive act enacted
	 */
	public IAct getIntendedPrimitiveAct();	

	/**
	 * @param act The last primitive act enacted
	 */
	public void setEnactedPrimitiveAct(IAct act);
	
	/**
	 * @return The last primitive act enacted
	 */
	public IAct getEnactedPrimitiveAct();	

	/**
	 * @param act The last highest-level act enacted
	 */
	public void setTopAct(IAct act);
	
	/**
	 * @return The last highest-level act enacted
	 */
	public IAct getTopAct();	

	/**
	 * @param act The last highest-level act enacted
	 */
	public void setTopEnactedAct(IAct act);
	
	/**
	 * @return The last highest-level act enacted
	 */
	public IAct getTopEnactedAct();	

	/**
	 * @param act The last highest-level act enacted
	 */
	public void setTopRemainingAct(IAct act);
	
	/**
	 * @return The last highest-level act enacted
	 */
	public IAct getTopRemainingAct();	

	/**
	 * @param step The rank of the primitive act in the current enaction 
	 */
	public void setStep(int step);

	/**
	 * @return The rank of the primitive act in the current enaction 
	 */
	public int getStep();
	
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
	
	public void setFinalContext(IAct enactedAct, IAct performedAct, ArrayList<IAct> contextList);
	public ArrayList<IAct> getFinalLearningContext();
	public ArrayList<IAct> getFinalActivationContext();
	public void setInitialLearningContext(ArrayList<IAct> learningContext);
	public ArrayList<IAct>  getInitialLearningContext();
	public void setPreviousLearningContext(ArrayList<IAct> learningContext);
	public ArrayList<IAct>  getPreviousLearningContext();
	public void setNbActLearned(int nbActLearned);
	

	/**
	 * @param tracer The tracer
	 */
	//public void traceInitialize(ITracer tracer);
	public void traceCarry(ITracer tracer);
	public void traceTerminate(ITracer tracer);
	public void trace(ITracer tracer);
}
