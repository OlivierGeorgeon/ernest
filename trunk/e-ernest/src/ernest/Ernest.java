package ernest;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import spas.IObservation;
import spas.IPlace;
import spas.ISegment;
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
	/** A multiplication factor when we need decimal precision but still use integers. */		
	public static final int INT_FACTOR = 1000;
	
	/** A big value that can represent infinite for diverse purpose. */	
	public static final int INFINITE = 1000 * INT_FACTOR;

	/** Ernest's retina resolution  */
	public static int RESOLUTION_RETINA = 12;
	public static int CENTER_RETINA = 0; //55; 
		
	/** Ernest's colliculus resolution  */
	//public static int RESOLUTION_COLLICULUS = 24;
	
	/** Ernest's number of rows in the retina */
	public static int ROW_RETINA = 1;
	
	/** The duration during which checked landmarks remain not motivating  */
	public static int PERSISTENCE = 30; // (50 Ernest 9.3)
	
	/** 200 Base attractiveness of bundles that are not edible  */
	public static int ATTRACTIVENESS_OF_UNKNOWN =  200;

	/** 400 Top attractiveness of bundles that are edible  */
	public static int ATTRACTIVENESS_OF_FISH  =  400;
	
	/** 300 Top attractiveness of cuddling another fish  */
	public static int ATTRACTIVENESS_OF_CUDDLE  =  300;
	
	/** -350 Attractiveness of bumping in a wall  */
	public static int ATTRACTIVENESS_OF_BUMP  =  -350;//-500;
	
	/** -200 Attractiveness of hard  */
	public static int ATTRACTIVENESS_OF_HARD  =  -200;
	
	/** 0 Attractiveness of background in the extrapersonal space  */
	public static int ATTRACTIVENESS_OF_BACKGROUND  =  0;
	
	/** A threshold for maturity that reduces exploration after a certain age to make demos nicer */
	public static int MATURITY = 1500; // not used currently.
	
	/** The radius of tactile sensors */
	public static float TACTILE_RADIUS = 1;
	
	/** Ernest physical diameter (for eat and bump) */
	public static float BOUNDING_RADIUS = .4f;
	
	/** A gustatory stimulation */
	public static int STIMULATION_GUSTATORY = 0;
	
	/** A kinematic stimulation */
	public static int STIMULATION_KINEMATIC = 1;
	
	/** A spatial stimulation */
	public static int MODALITY_SPATIAL = 1;
	
	/** A visual stimulation */
	public static int MODALITY_VISUAL = 2;

	/** A tactile stimulation */
	public static int MODALITY_TACTILE = 3;
	
	/** A circadian stimulation */
	public static int STIMULATION_CIRCADIAN = 4;
	
	/** The taste of food */
	public static int STIMULATION_FOOD = 2;
	
	/** Visual stimulation of seeing nothing */
	public static int STIMULATION_VISUAL_UNSEEN = 0xFFFFFF;//255 * 65536 + 255 * 256 + 255;

	/** Color unanimated */
	public static int UNANIMATED_COLOR = 0x808080;

	/** Touch empty */
	public static int STIMULATION_TOUCH_EMPTY = 0xB4B4B4;//11842740;
	
	/** Touch soft */
	public static int STIMULATION_TOUCH_SOFT = 0x646464;//100 * 65536 + 100 * 256 + 100 = 6579300
	
	/** Touch hard */
	public static int STIMULATION_TOUCH_WALL = 0x000000;
	
	/** Touch fish */
	public static int STIMULATION_TOUCH_FISH = 0x646465;//100 * 65536 + 100 * 256 + 101 = 6579301
	
	/** Touch other agent */
	public static int STIMULATION_TOUCH_AGENT = 0x646466;
	
	/** Kinematic Stimulation move forward */	
	public static int STIMULATION_KINEMATIC_FORWARD = 0xFFFFFF;//255 * 65536 + 255 * 256 + 255 = 16777215

	/** Kinematic Stimulations bump */
	public static int STIMULATION_KINEMATIC_BUMP = 0xFF0000;//255 * 65536 = 16711680
		
	/** Kinematic Stimulations turn left toward empty square */
	public static int STIMULATION_KINEMATIC_LEFT_EMPTY = 2;
		
	/** Kinematic Stimulations turn left toward wall */
	public static int STIMULATION_KINEMATIC_LEFT_WALL = 3;
		
	/** Kinematic Stimulations turn right toward empty square */
	public static int STIMULATION_KINEMATIC_RIGHT_EMPTY = 4;
		
	/** Kinematic Stimulations turn right toward wall */
	public static int STIMULATION_KINEMATIC_RIGHT_WALL = 5;
		
	/** Gustatory Stimulation nothing */	
	public static int STIMULATION_GUSTATORY_NOTHING = 0xFFFFFF;//255 * 65536 + 255 * 256 + 255;

	/** Gustatory Stimulation fish */	
	public static int STIMULATION_GUSTATORY_FISH = 0xFFFF00;//255 * 65536 + 255 * 256;
	
	/** Social Stimulation cuddle */	
	public static int STIMULATION_GUSTATORY_CUDDLE = 0xFF8080;
	
	/** Social Stimulation nothing */	
	public static int STIMULATION_SOCIAL_NOTHING = 0x000000;
	
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
	 * Ernest's main cognitive loop in the case of an environment that provides a matrix of stimuli.
	 * @param stimuli The matrix of stimuli privided by the environment.
	 * @return The next primitive schema to enact.
	 */
	public int[] step(int[][] stimuli) 
	{
		int primitiveSchema[] = new int[2];
		IAct enactedPrimitiveAct;
		
		// Determine the primitive enacted act from the enacted schema and the stimuli received from the environment.		
		
		enactedPrimitiveAct = m_sensorymotorSystem.enactedAct(m_primitiveAct, stimuli);
		
		// Let Ernest decide for the next primitive schema to enact.
		
		m_primitiveAct = m_imos.step(enactedPrimitiveAct);
		primitiveSchema[0] = m_primitiveAct.getSchema().getLabel().toCharArray()[0];
		
		// Once the decision is made, compute the intensity.
		
		primitiveSchema[1] = m_sensorymotorSystem.impulsion(primitiveSchema[0]);
		
		// Return the schema to enact and its intensity.
		
		return primitiveSchema;
	}

	/**
	 * Update Ernest when the environment is refreshed.
	 * May trigger a new cognitive loop
	 * @param stimuli The matrix of stimuli provided by the environment.
	 * @return The next primitive schema to enact.
	 */
	public int[] update(int[][] stimuli) 
	{
		return m_sensorymotorSystem.update(stimuli);
	}
	
	public int getValue(int i, int j)
	{
		return m_spas.getValue(i,j);
	}
	public int getAttention()
	{
		return m_spas.getAttention();
	}

	public IAct addInteraction(String schemaLabel, String stimuliLabel, int satisfaction)
	{
		return m_imos.addInteraction(schemaLabel, stimuliLabel, satisfaction);
	}

	public void setPlaceList(ArrayList<IPlace> placeList)
	{
		m_spas.setPlaceList(placeList);
	}

	public ArrayList<IPlace> getPlaceList()
	{
		return m_spas.getPlaceList();
	}

	public void setSegmentList(ArrayList<ISegment> segmentList) 
	{
		m_spas.setSegmentList(segmentList);
	}

	public int getCounter() 
	{
		return m_imos.getCounter();
	}

	public IPlace getFocusPlace() 
	{
		return m_spas.getFocusPlace();
	}
}
