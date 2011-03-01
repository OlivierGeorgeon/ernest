package ernest;

import java.awt.Color;

/**
 * The main Ernest class used to create an Ernest agent in the environment.
 * @author ogeorgeon
 */
public class Ernest implements IErnest 
{
	/** A big value that can represent infinite for diverse purpose. */
	public static final int INFINITE = 1000;
	
	/** Color of regular wall  */
	public static Color WALL_COLOR   = new Color(0, 128, 0); // Color.getHSBColor(1/3f, 1f, 0.5f)

	public static Color BUMP_COLOR   = new Color(255, 0, 0);
	public static Color WATER_COLOR  = new Color(150, 128, 255);
	public static Color FOOD_COLOR   = new Color(227, 124, 255);
	
	/** Hypothetical act (Cannot be chosen as an intention. Cannot support higher-level learning). */
	public static final int HYPOTHETICAL = 1;

	/** Reliable act (Can be chosen as an intention and can support higher-level learning). */
	public static final int RELIABLE = 2;

	/** Regularity sensibility threshold (The weight threshold for an act to become reliable). */
	public static int REG_SENS_THRESH = 5;

	/** Activation threshold (The weight threshold for higher-level learning with the second learning mechanism). */
	public static int ACTIVATION_THRESH = 1;

	/** Maximum length of a schema (For the schema to be chosen as an intention) */
	public static int SCHEMA_MAX_LENGTH = INFINITE;
	
	/** A threshold for maturity that reduces exploration after a certain age to make demos nicer */
	public static int MATURITY = 150;
	
	/** Ernest's primitive schema currently enacted */
	private ISchema m_primitiveSchema = null;
	
	/** Ernest's episodic memory. */
	private EpisodicMemory m_episodicMemory = new EpisodicMemory();

	/** Ernest's attentional system. */
	private IAttentionalSystem m_attentionalSystem = new AttentionalSystem(m_episodicMemory);
	
	/** Ernest's sensorymotor system. */
	private ISensorymotorSystem m_sensorymotorSystem;

	/** Ernest's tracing system. */
	private ITracer m_tracer = null;

	/**
	 * Set Ernest's fundamental learning parameters.
	 * Use null to leave a value unchanged.
	 * @param regularityThreshold The Regularity Sensibility Threshold.
	 * @param activationThreshold The Activation Threshold.
	 * @param schemaMaxLength The Maximum Schema Length
	 */
	public void setParameters(Integer regularityThreshold, Integer activationThreshold, Integer schemaMaxLength) 
	{
		if (regularityThreshold != null)
			REG_SENS_THRESH = regularityThreshold.intValue();
		
		if (activationThreshold != null)
			ACTIVATION_THRESH = activationThreshold.intValue();
		
		if (schemaMaxLength != null)
			SCHEMA_MAX_LENGTH = schemaMaxLength.intValue();
	}

	/**
	 * Let the environment set the sensorymotor system.
	 * @param sensor The sensorymotor system.
	 */
	public void setSensorymotorSystem(ISensorymotorSystem sensor) 
	{
		m_sensorymotorSystem = sensor;
		m_sensorymotorSystem.init(m_episodicMemory, m_attentionalSystem, m_tracer);
	};
	
	/**
	 * Let the environment set the tracer.
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer tracer) 
	{ 
		m_tracer = tracer;
		m_attentionalSystem.setTracer(m_tracer); 
		m_episodicMemory.setTracer(m_tracer);
	}

	/**
	 * Provide access to Ernest's episodic memory
	 * (The environment can populate Ernest's episodic memory with inborn composite schemas) 
	 * @return Ernest's episodic memory. 
	 */
    public EpisodicMemory getEpisodicMemory()
    {
    	return m_episodicMemory;
    }

	/**
	 * Get a description of Ernest's internal state.
	 * @return A representation of Ernest's internal state
	 */
	public String internalState() 
	{
		return m_attentionalSystem.getInternalState();
	}
		
	/**
	 * Ernest's main process.
	 * (All environments return at least a boolean feedback from Ernest's actions) 
	 * @param status The status received as a feedback from the previous primitive enaction.
	 * @return The next primitive schema to enact.
	 */
	public String step(boolean status) 
	{
		// Determine the primitive enacted act from the enacted schema and the data sensed in the environment.
		// (May use additional sensory data from the environment, e.g., received via the sensorymotorSystem.senseMatrix() method)
		
		IAct enactedPrimitiveAct = m_sensorymotorSystem.enactedAct(m_primitiveSchema, status);
		
		// Let Ernest decide for the next primitive schema to enact.
		
		m_primitiveSchema = m_attentionalSystem.step(enactedPrimitiveAct);
		
		// Return the schema to enact.
		
		return m_primitiveSchema.getLabel();
	}

	public IAttentionalSystem getAttentionalSystem() 
	{
		return m_attentionalSystem;
	}
		
}
