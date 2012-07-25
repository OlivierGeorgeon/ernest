package spas;

import imos.IAct;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import ernest.Ernest;
import ernest.ITracer;

/**
 * The spatial system.
 * Maintains the local space map and the persistence memory.
 * @author Olivier
 */
public class Spas implements ISpas 
{
	
	/** The Tracer. */
	private ITracer m_tracer = null; 
	
//	public static int PLACE_BACKGROUND = -1;
//	public static int PLACE_SEE = 0;
//	public static int PLACE_TOUCH = 1;
//	public static int PLACE_FOCUS = 10;
//	public static int PLACE_BUMP = 11;
//	public static int PLACE_EAT  = 12;
//	public static int PLACE_CUDDLE = 13;
//	public static int PLACE_PRIMITIVE = 14;
//	public static int PLACE_COMPOSITE = 15;
//	public static int PLACE_INTERMEDIARY = 16;
//	public static int PLACE_PHENOMENON = 18;
//	public static int PLACE_COPRESENCE = 19;
	
//	public static int PLACE_EVOKE_PHENOMENON = 17;
//	public static int PLACE_SIMULATION = 20;
//	public static int PLACE_UNKNOWN = 21;
//	public static int PLACE_AFFORD = 22;
	
	//public static int SHAPE_CIRCLE = 0;
	//public static int SHAPE_TRIANGLE = 1;
	//public static int SHAPE_PIE = 2;
	//public static int SHAPE_SQUARE = 3;

	/** A list of all the bundles ever identified. */
	public List<IBundle> m_bundles = new ArrayList<IBundle>(20);
	
	/** Ernest's local space memory  */
	private ISpatialMemory m_localSpaceMemory = new LocalSpaceMemory();
	
	/** The list of saliences generated by Ernest's sensory system  */
	List<IPlace> m_placeList = new ArrayList<IPlace>();
	
	IObservation m_observation;
	
	/** The clock of the spatial system. (updated on each update cycle as opposed to IMOS) */
	private int m_clock;

	public void setTracer(ITracer tracer) 
	{
		m_tracer = tracer;
		//m_localSpaceMemory = new LocalSpaceMemory(this, m_tracer);
	}

	/**
	 * The main routine of the Spatial System that is called on each interaction cycle.
	 * Maintain the local space memory.
	 * Construct bundles and affordances.
	 * Maintain the current observation that is used by IMOS. 
	 * @param observation The current observation.
	 */
	public void step(IObservation observation)//, ArrayList<IPlace> places) 
	{		

		m_observation = observation;
		
		// translate and rotate the local space memory;
		
		//Vector3f memoryTranslation = new Vector3f(observation.getTranslation());
		//memoryTranslation.scale(-1);
		//m_localSpaceMemory.update(memoryTranslation, - observation.getRotation());
		//m_localSpaceMemory.followUp(observation.getTranslation(), observation.getRotation());
		//followUp(observation.getPrimitiveAct());

		// Create and maintain phenomenon places from interaction places. 
		
		//m_localSpaceMemory.phenomenon(observation, m_clock);//(places, observation, m_clock);
		m_localSpaceMemory.copresence(this);
		
		if (m_tracer != null) m_localSpaceMemory.trace(m_tracer);

		// Construct synergies associated with bundles in the peripersonal space.		
		//synergy(interactionPlace, observation);
	}
	
	public int getValue(int i, int j)
	{
		Vector3f position = new Vector3f(1 - j, 1 - i, 0);
		if (m_localSpaceMemory != null)
			return m_localSpaceMemory.getValue(position);
		else
			return 0xFFFFFF;
	}

	public int getAttention()
	{
		int attention;
		if (m_observation == null || m_observation.getFocusPlace() == null)
			attention = Ernest.UNANIMATED_COLOR;
		else
			attention = m_observation.getFocusPlace().getBundle().getValue();

		return attention;
	}
	
	/**
	 * Set the list of saliences from the list provided by VacuumSG.
	 * @param salienceList The list of saliences provided by VacuumSG.
	 */
//	public void setPlaceList(List<IPlace> placeList)
//	{
//		m_placeList = placeList;
//	}
		
	public ArrayList<IPlace> getPlaceList()
	{
		return m_localSpaceMemory.getPlaceList();
	}

	public IPlace addPlace(Point3f position, int type) 
	{
		IPlace place = m_localSpaceMemory.addPlace(position, type);
//		place.setFirstPosition(position);
//		place.setSecondPosition(position);
		place.setType(type);
		//place.setShape(shape);
		place.setUpdateCount(m_clock);
		
		return place;
	}

	public IBundle addBundle(IAct firstAct, IAct secondAct) 
	{
		IBundle bundle = new Bundle(firstAct, secondAct);
		bundle.setValue(firstAct.getColor());
		
		int i = m_bundles.indexOf(bundle);
		if (i == -1)
		{
			m_bundles.add(bundle);
			if (m_tracer != null) {
				bundle.trace(m_tracer, "bundle");
			}
		}
		else 
			// The bundle already exists: return a pointer to it.
			bundle =  m_bundles.get(i);
		
		return bundle;
	}

//	public IBundle addBundle(int value) 
//	{
//		IBundle bundle = new Bundle(value);
//		
//		//int i = m_bundles.indexOf(bundle);
//		//if (i == -1)
//		//{
//			m_bundles.add(bundle);
//			//if (m_tracer != null) 
//			//	bundle.trace(m_tracer, "bundle");
//		//}
//		//else 
//		//	// The bundle already exists: return a pointer to it.
//		//	bundle =  m_bundles.get(i);
//		
//		return bundle;
//	}

//	public IBundle addBundle(IAct act)
//	{
//		IBundle bundle = null;
//		
//		for (IBundle b : m_bundles)
//		{
//			if (b.hasAct(act))
//				bundle = b;
//		}
//		
//		if (bundle == null)
//		{
//			bundle = new Bundle(act);
//			m_bundles.add(bundle);
//			if (m_tracer != null)
//				bundle.trace(m_tracer, "bundle");
//		}
//		return bundle;
//	}
	
//	public IBundle aggregateBundle(IBundle bundle, IAct act) 
//	{			
//		// See if this act already belongs to a different bundle
//		IBundle aggregate = null;
//		for (IBundle b : m_bundles)
//		{
//			if (b != bundle && b.hasAct(act))
//				aggregate = b;
//		}
//		
//		if (aggregate == null)
//		{
//			boolean added = bundle.addAct(act);
//			if (m_tracer != null && added)
//				bundle.trace(m_tracer, "bundle");
//		}
//		else
//		{
//			// Merge this other bundle into the bundle that was already found in the list
//			boolean added = false;
//			for (IAct a : bundle.getActList())
//			{
//				//boolean add = aggregate.addAct(a);
//				added = aggregate.addAct(a) || added;
//			}
//			if (m_tracer != null && added)
//				bundle.trace(m_tracer, "remove_bundle");
//			int i = 0; int in = -1;
//			for (IBundle bu : m_bundles)
//			{ 
//				if (bu == bundle)
//					in = i;
//				i++;	
//			}
//			if (in >= 0)
//				m_bundles.remove(in);
//			// The aggregate bundle replaces the previous bundle of this phenomenon.
//			bundle = aggregate;
//			if (m_tracer != null && added)
//				bundle.trace(m_tracer, "bundle");
//		}		
//		return bundle;
//	}

	public int getClock() 
	{
		return m_clock;
	}

//	public IPlace addPlace(IBundle bundle, Vector3f position) 
//	{
//		return m_localSpaceMemory.addPlace(bundle, position);
//	}

	public void tick() 
	{
		m_clock++;
		m_localSpaceMemory.tick();
	}

//	public ArrayList<IPlace> getPhenomena() 
//	{
//		return m_localSpaceMemory.getPhenomena();
//	}

	public boolean checkAct(IAct act) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	public int getValue(Vector3f position) 
	{
		return m_localSpaceMemory.getValue(position);
	}

//	public IPlace getPlace(Vector3f position) 
//	{
//		return m_localSpaceMemory.getPlace(position);
//	}
	
	public ISpatialMemory getSpatialMemory()
	{
		return m_localSpaceMemory;
	}

	/**
	 * Returns the first bundle found that contains this act.
	 * @param act The act to check.
	 * @return The bundle that match this act.
	 */
//	public IBundle evokeBundle(IAct act)
//	{
//		for (IBundle bundle : m_bundles)
//		{
//			if (bundle.hasAct(act))
//				return bundle;
//			// presuppose the value of phenomena
//			//if (bundle.getValue() == act.getPhenomenon())
//			//	return bundle;
//				
//		}
//		return null;
//	}

	/**
	 * Returns the list of compresences that afford this act.
	 * @param act The act to check.
	 * @return The list of compresences that match this act.
	 */
	public ArrayList<IBundle> evokeCompresences(IAct act)
	{
		ArrayList<IBundle> compresences = new ArrayList<IBundle>();

		for (IBundle bundle : m_bundles)
			if (bundle.afford(act))
				compresences.add(bundle);

		return compresences;
	}

//	/**
//	 * Returns the first bundle found form a visual stimulation.
//	 * TODO manage different bundles that have the same color.
//	 * TODO manage different bundles with more than one visual stimulation.
//	 * TODO manage bundles that have no tactile stimulation. 
//	 * @param stimulation The visual stimulation.
//	 * @return The bundle that match this stimulation.
//	 */
//	public IBundle seeBundle(int visualValue)
//	{
//		for (IBundle bundle : m_bundles)
//			// Return only bundles that have also a tactile stimulation
//			if (bundle.getVisualValue() == visualValue && bundle.getTactileValue() != Ernest.STIMULATION_TOUCH_EMPTY)
//				return bundle;
//
//		return null;
//	}

	public void followUp(IAct act) 
	{
		m_localSpaceMemory.transform(act);		
	}

	/**
	 * Returns the first bundle found form a tactile stimulation.
	 * TODO evoke different kind of bundles 
	 * @param stimulation The visual stimulation.
	 * @return The bundle that match this stimulation.
	 */
//	public IBundle touchBundle(int tactileValue)
//	{
//		for (IBundle bundle : m_bundles)
//			// So far, only consider bump and eat bundles
//			if (bundle.getTactileValue() == tactileValue && bundle.getAffordanceList().size() > 0)
//					//(bundle.getKinematicValue() != Ernest.STIMULATION_KINEMATIC_FORWARD || bundle.getGustatoryValue() != Ernest.STIMULATION_GUSTATORY_NOTHING))
//				return bundle;
//
//		return null;
//	}

}
