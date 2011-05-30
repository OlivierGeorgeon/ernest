package ernest;

import java.awt.Color;


/**
 * The interface through which the environment can use an Ernest agent. 
 * @author ogeorgeon
 */
public interface IErnest 
{

	/**
	 * Set Ernest's fundamental learning parameters.
	 * Use null to leave a value unchanged.
	 * @param regularityThreshold The Regularity Sensibility Threshold.
	 * @param activationThreshold The Activation Threshold.
	 * @param schemaMaxLength The Maximum Schema Length.
	 */
	public void setParameters(Integer regularityThreshold, Integer activationThreshold, Integer schemaMaxLength); 
	
	/**
	 * Set Ernest's sensorymotor system.
	 * (The sensorymotor system is instantiated by the environment so that the environment can choose the suitable sensorymotor system)
	 * @param sensorymotorSystem Ernest's sensorymotor system
	 */
	public void setSensorymotorSystem(ISensorymotorSystem sensorymotorSystem);
	
    /**
	 * Initialize the tracer that generates Ernest's activity trace.
	 * (The tracer is instantiated by the environment so that the environment can choose the suitable tracer and also use it to trace things) 
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer tracer);

	/**
	 * Provide access to Ernest's episodic memory
	 * (The environment can populate Ernest's episodic memory with inborn composite schemas) 
	 * @return Ernest's episodic memory. 
	 */
    public EpisodicMemory getEpisodicMemory();
    
	/**
	 * Get a description of Ernest's internal state.
	 * (This is used to display Ernest's internal state in the environment)
	 * @return A representation of Ernest's internal state
	 */
	public String internalState();
	
	/**
	 * Run Ernest one step.
	 * (All environments return at least a boolean feedback from Ernest's actions) 
	 * @param status The status received as feedback from the previous schema enaction.
	 * @return The next primitive schema to enact.
	 */
	public String step(boolean status);
	
	/**
	 * Run Ernest one step.
	 * @param matrix The matrix of stimuli received from the environment.
	 * @return The next primitive schema to enact.
	 */
	public String step(int[][] matrix);
	
	/**
	 * @return Ernest observation used to draw the local map in the environment 
	 */
	public IObservation getObservation();
	
	/**
	 * @return a pointer to Ernest's static system to be used by other modules
	 */
	public StaticSystem getStaticSystem();

}