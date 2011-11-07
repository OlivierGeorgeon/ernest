package ernest;

import java.util.ArrayList;

import spas.IObservation;
import spas.ISalience;
import spas.ISpas;
import spas.IStimulation;
import spas.Spas;
import spas.Stimulation;
import imos.IAct;
import imos.IImos;
import imos.Imos;


/**
 * The main Ernest class used to create an Ernest agent in the environment.
 * @author ogeorgeon
 */
public class Ernest implements IErnest 
{
	/** A big value that can represent infinite for diverse purpose. */
	public static final int INFINITE = 1000;
	
	/** Ernest's retina resolution  */
	public static int RESOLUTION_RETINA = 12;
	public static int CENTER_RETINA = 0; //55; 
		
	/** Ernest's colliculus resolution  */
	public static int RESOLUTION_COLLICULUS = 24;
	
	/** Ernest's number of rows in the retina */
	public static int ROW_RETINA = 1;
	
	/** The duration during which checked landmarks remain not motivating  */
	public static int PERSISTENCE = 30; // (50 Ernest 9.3)
	
	/** 200 Base attractiveness of bundles that are not edible  */
	public static int ATTRACTIVENESS_OF_UNKNOWN =  200;

	/** 400 Top attractiveness of bundles that are edible  */
	public static int ATTRACTIVENESS_OF_FISH  =  400;
	
	/** -600 Attractiveness of bumping in a wall  */
	public static int ATTRACTIVENESS_OF_BUMP  =  -500;
	
	/** -300 Attractiveness of hard  */
	public static int ATTRACTIVENESS_OF_HARD  =  -200;
	
	/** A threshold for maturity that reduces exploration after a certain age to make demos nicer */
	public static int MATURITY = 1500; // not used currently.
	
	/** A gustatory stimulation */
	public static int STIMULATION_GUSTATORY = 0;
	
	/** A kinematic stimulation */
	public static int STIMULATION_KINEMATIC = 1;
	
	/** A visual stimulation */
	public static int MODALITY_VISUAL = 2;
	
	/** A tactile stimulation */
	public static int MODALITY_TACTILE = 3;
	
	/** A circadian stimulation */
	public static int STIMULATION_CIRCADIAN = 4;
	
	/** The taste of food */
	public static int STIMULATION_FOOD = 2;
	
	/** Stimulation of seeing a regular wall  */
	public static IStimulation STIMULATION_VISUAL_WALL = new Stimulation( MODALITY_VISUAL, 128 * 256 );

	/** Visual stimulation of touching an alga */
	public static IStimulation STIMULATION_VISUAL_UNSEEN = new Stimulation(MODALITY_VISUAL, 255 * 65536 + 255 * 256 + 255);

	/** Touch empty */
	public static IStimulation STIMULATION_TOUCH_EMPTY = new Stimulation(MODALITY_TACTILE, 11842740); // 180 * 65536 + 180 * 256 + 180  = B4B4B4
	
	/** Touch soft */
	public static IStimulation STIMULATION_TOUCH_SOFT = new Stimulation(MODALITY_TACTILE, 100 * 65536 + 100 * 256 + 100);
	
	/** Touch hard */
	public static IStimulation STIMULATION_TOUCH_WALL = new Stimulation(MODALITY_TACTILE, 0);
	
	/** Touch fish */
	public static IStimulation STIMULATION_TOUCH_FISH = new Stimulation(MODALITY_TACTILE, 100 * 65536 + 100 * 256 + 101);
	
	/** Kinematic Stimulation succeed */	
	public static IStimulation STIMULATION_KINEMATIC_FORWARD = new Stimulation(STIMULATION_KINEMATIC, 255 * 65536 + 255 * 256 + 255);

	/** Kinematic Stimulations fail*/
	public static IStimulation STIMULATION_KINEMATIC_BUMP = new Stimulation(STIMULATION_KINEMATIC, 255 * 65536);
		
	/** Kinematic Stimulations turn left toward empty square */
	public static IStimulation STIMULATION_KINEMATIC_LEFT_EMPTY = new Stimulation(STIMULATION_KINEMATIC, 2);
		
	/** Kinematic Stimulations turn left toward wall */
	public static IStimulation STIMULATION_KINEMATIC_LEFT_WALL = new Stimulation(STIMULATION_KINEMATIC, 3);
		
	/** Kinematic Stimulations turn left toward empty square */
	public static IStimulation STIMULATION_KINEMATIC_RIGHT_EMPTY = new Stimulation(STIMULATION_KINEMATIC, 4);
		
	/** Kinematic Stimulations turn left toward wall */
	public static IStimulation STIMULATION_KINEMATIC_RIGHT_WALL = new Stimulation(STIMULATION_KINEMATIC, 5);
		
	/** Gustatory Stimulation nothing */	
	public static IStimulation STIMULATION_GUSTATORY_NOTHING = new Stimulation(STIMULATION_GUSTATORY, 255 * 65536 + 255 * 256 + 255);

	/** Gustatory Stimulation fish */	
	public static IStimulation STIMULATION_GUSTATORY_FISH = new Stimulation(STIMULATION_GUSTATORY, 255 * 65536 + 255 * 256);
	
	/** Circadian stimulation (daytime) */	
	public static int STIMULATION_CIRCADIAN_DAY = 0;
	
	/** Ernest's primitive schema currently enacted */
	private IAct m_primitiveAct = null;
	
	/** Ernest's intention is being inhibited by the anticipated observation */
	private boolean m_inhibited = false;
	
	/** Ernest's static system. */
	private ISpas m_spas = new Spas();

	/** Ernest's motivational system. */
	private IImos m_imos ;
	
	/** Ernest's sensorymotor system. */
	private ISensorymotorSystem m_sensorymotorSystem;

	/** Ernest's tracing system. */
	private ITracer m_tracer = null;

	/**
	 * Set Ernest's fundamental learning parameters.
	 * @param regularityThreshold The Regularity Sensibility Threshold.
	 * @param schemaMaxLength The Maximum Schema Length
	 */
	public void setParameters(int regularityThreshold, int schemaMaxLength) 
	{
		m_imos = new Imos(regularityThreshold, schemaMaxLength);
	}

	/**
	 * Let the environment set the sensorymotor system.
	 * @param sensor The sensorymotor system.
	 */
	public void setSensorymotorSystem(ISensorymotorSystem sensor) 
	{
		m_sensorymotorSystem = sensor;
		m_sensorymotorSystem.init(m_spas, m_imos, m_tracer);
	};
	
	/**
	 * Let the environment set the tracer.
	 * @param tracer The tracer.
	 */
	public void setTracer(ITracer tracer) 
	{ 
		m_tracer = tracer;
		m_imos.setTracer(m_tracer); 
		m_spas.setTracer(m_tracer);
	}

	/**
	 * Get a description of Ernest's internal state (to display in the environment).
	 * @return A representation of Ernest's internal state
	 */
	public String internalState() 
	{
		return m_imos.getInternalState();
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
		
		IAct enactedPrimitiveAct = m_sensorymotorSystem.enactedAct(m_primitiveAct, status);
		
		// Let Ernest decide for the next primitive schema to enact.
		
		//m_spas.tick();
		m_primitiveAct = m_imos.step(enactedPrimitiveAct);
		
		// Return the schema to enact.
		
		return m_primitiveAct.getSchema().getLabel();
	}

	/**
	 * Ernest's main process in the case of an environment that provides a matrix of stimuli.
	 * @param stimuli The matrix of stimuli privided by the environment.
	 * @return The next primitive schema to enact.
	 */
	public String step(int[][] stimuli) 
	{
		String primitiveSchema = "";
		IAct enactedPrimitiveAct;
		
		// Determine the primitive enacted act from the enacted schema and the stimuli received from the environment.		
		
		enactedPrimitiveAct = m_sensorymotorSystem.enactedAct(m_primitiveAct, stimuli);
		
		// Let Ernest decide for the next primitive schema to enact.
		
		m_primitiveAct = m_imos.step(enactedPrimitiveAct);
		
		primitiveSchema = m_primitiveAct.getSchema().getLabel();

		// Return the schema to enact.
		
		return primitiveSchema;
	}

	public int getValue(int i, int j)
	{
		return m_spas.getValue(i,j);
	}


	public IAct addInteraction(String schemaLabel, String stimuliLabel, int satisfaction)
	{
		return m_imos.addInteraction(schemaLabel, stimuliLabel, satisfaction);
	}

	public void setSalienceList(ArrayList<ISalience> salienceList)
	{
		m_spas.setSalienceList(salienceList);
	}

}
