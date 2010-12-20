package ernest;

/**
 * The interface for dealing with an Ernest agent. 
 * @author ogeorgeon
 */
public interface IErnest 
{

	/**
	 * Reset Ernest by clearing all of its long-term memory.
	 */
	public void clear();
	
    /**
	 * Initialize the tracer that generates Ernest's activity trace.
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer tracer);

	/**
	 * Set Ernest's fundamental learning parameters.
	 * Use null to leave a value unchanged.
	 * @param regularityThreshold The Regularity Sensibility Threshold.
	 * @param activationThreshold The Activation Threshold.
	 * @param schemaMaxLength The Maximum Schema Length
	 */
	public void setParameters(Integer regularityThreshold, Integer activationThreshold, Integer schemaMaxLength); 
	
	/**
	 * Add a primitive schema and its two resulting acts that represent a primitive possibility 
	 * of interaction between Ernest and its environment.
	 * @param label The schema's string identifier that can be interpreted by the environment
	 * @param successSatisfaction The satisfaction in case of success.
	 * @param failureSatisfaction The satisfaction in case of failure.
	 * @return The created primitive schema.
	 */
	public ISchema addPrimitive(String label, int successSatisfaction, int failureSatisfaction, int module);

	/**
	 * Add a primitive sensory schema
	 * @param label The primitive schema's string description
	 * @param matrix The sensor matrix 
	 * @return The created primitive schema
	 */
	public IAct addIconicPrimitive(String label, int[][] matrix); 

	/**
	 * Add a composite schema and its succeeding act that represent a composite possibility 
	 * of interaction between Ernest and its environment. 
	 * @param contextAct The context Act.
	 * @param intentionAct The intention Act.
	 * @return The schema made of the two specified acts, whether it has been created or it already existed. 
	 */
	public ISchema addCompositeInteraction(IAct contextAct, IAct intentionAct);
    
	/**
	 * Get a description of Ernest's internal state.
	 * This is used to display Ernest's internal state in the environment
	 * @return A representation of Ernest's internal state
	 */
	public String internalState();
	
	/**
	 * Run Ernest one step.
	 * @param status The status received in return from the previous schema enaction.
	 * @return The next primitive schema to enact.
	 */
	public String step(boolean status);
	
	/**
	 * Run Ernest one step. Exploit the matrix sensed in the environment.
	 * @param status The status received in return from the previous schema enaction.
	 * @param matrix The matrix sensed in the environment to inform the iconic module.
	 * @return The next primitive schema to enact.
	 */
	public String step(boolean status, int[][] matrix);
}