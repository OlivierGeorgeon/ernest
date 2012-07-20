package spas;

import imos.IAct;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import utils.ErnestUtils;
import ernest.Ernest;
import ernest.ITracer;

/**
 * A bundle of sensory stimulations. 
 * So far, a bundle is defined by its visual and its tactile stimulation. 
 * Other stimulations are optional.
 * A bundle may correspond to a physical object according to Hume's bundle theory (http://en.wikipedia.org/wiki/Bundle_theory)
 * @author Olivier
 */
public class Bundle implements IBundle 
{
	int m_value;
	int m_tactileValue = 0;
	int m_lastTimeBundled = - Ernest.PERSISTENCE;

	/** The acts attached to this bundle. */
	private ArrayList<IAct> m_acts = new ArrayList<IAct>();
	
	private IAct m_firstAct;
	private IAct m_secondAct;
	
	/** The affordances attached to this bundle. */
	private ArrayList<IAffordance> m_affordances = new ArrayList<IAffordance>();
	
	Bundle(int value)
	{
		m_value = value;
	}
	
	Bundle(IAct act)
	{
		m_value = act.getColor();
		m_acts.add(act);
	}
	
	Bundle(IAct firstAct, IAct secondAct)
	{
		m_firstAct = firstAct;
		m_secondAct = secondAct;
		m_acts.add(firstAct);
		m_acts.add(secondAct);
	}
	
	public IAct getOtherAct(IAct act)
	{
		if (act.equals(m_firstAct))
			return m_secondAct;
		else if (act.equals(m_secondAct))
			return m_firstAct;
		else
			return null;
	}
	
	Bundle(int visualValue, int tactileValue)
	{
		m_value = visualValue;
		m_tactileValue = tactileValue;
		// When created, the bundle is not yet confirmed visited.
		m_lastTimeBundled = - Ernest.PERSISTENCE;
	}
	
	public int getValue()
	{
//		int value = 0;
//		if (m_value == Ernest.STIMULATION_VISUAL_UNSEEN)
//			value = m_tactileValue;
//		else 
//			value = m_value;		
//		return value;
		
		return m_value;
	}
	
	public boolean addAct(IAct act) 
	{
		boolean added = false;
		int i = m_acts.indexOf(act);
		if (i == -1)
		{
			// The affordance does not exist
			m_acts.add(act);
			added = true;
		}
		else 
			// The affordance already exists: return a pointer to it.
			act =  m_acts.get(i);
		return added;
	}

	public boolean hasAct(IAct act)
	{
		boolean hasAct = false;
		for (IAct a : m_acts)
		{
			if (a.equals(act))
				hasAct = true;
		}
		return hasAct;
	}
	
	public int getVisualValue() 
	{
		return m_value;
	}
	
	public void setVisualValue(int visualValue) 
	{
		m_value = visualValue;
	}
	
	public void setTactileValue(int tactileValue) 
	{
		m_value = tactileValue;
	}
	
	public int getTactileValue() 
	{
		return m_tactileValue;
	}
	public void setLastTimeBundled(int clock)
	{
		m_lastTimeBundled = clock;
	}

	/**
	 * Get the attractiveness of object in the extrapersonal space (out of reach by touch).
	 * ATTRACTIVENESS_OF_FISH (400) if this bundle's gustatory stimulation is STIMULATION_TASTE_FISH.
	 * ATTRACTIVENESS_OF_UNKNOWN (200) if this bundle has been forgotten,
	 * ATTRACTIVENESS_OF_BACKGROUND (0) if this bundle is known and not fish.
	 * @param clock Ernest's current clock value.
	 * @return This bundle's attractiveness at the given time.
	 */
	public int getExtrapersonalAttractiveness(int clock) 
	{
		int attractiveness = Ernest.ATTRACTIVENESS_OF_UNKNOWN;
		
		// The proclivity of the highest interaction attached to this bundle.
		for (IAffordance a : m_affordances)
		{
			if (Math.abs(a.getProclivity()) > Math.abs(attractiveness))
				attractiveness = a.getProclivity();
		}
		
		// Repulsion does not apply to extrapersonal space.
		if (attractiveness < 0)
			attractiveness = Ernest.ATTRACTIVENESS_OF_BACKGROUND;
		
		// If the bundle has a gustatory stimulation of fish 
//		if (m_gustatoryValue == Ernest.STIMULATION_GUSTATORY_FISH)
//			return Ernest.ATTRACTIVENESS_OF_FISH;	
//		
//		// If the bundle has a gustatory stimulation of cuddle 
//		if (m_gustatoryValue == Ernest.STIMULATION_GUSTATORY_CUDDLE)
//			return Ernest.ATTRACTIVENESS_OF_CUDDLE;	

//		// if the bundle is forgotten
//		if (clock - m_lastTimeBundled > Ernest.PERSISTENCE)
//			attractiveness = Ernest.ATTRACTIVENESS_OF_UNKNOWN ;

		return attractiveness;
	}

	/**
	 * Get the attractiveness of object in the peripersonal space (within the reach of touch).
	 * ATTRACTIVENESS_OF_BUMP (-500) if the bundle has a kinematic stimulation of bump.
	 * ATTRACTIVENESS_OF_HARD - 10 (-210) if the bundle is hard but not yet bump.  
	 * ATTRACTIVENESS_OF_FISH + 10 (410) if this bundle's gustatory stimulation is STIMULATION_TASTE_FISH.
	 * ATTRACTIVENESS_OF_UNKNOWN (200) if this bundle has been forgotten,
	 * ATTRACTIVENESS_OF_BACKGROUND (0) otherwise.
	 * @param clock Ernest's current clock value.
	 * @return This bundle's attractiveness at the given time.
	 */
	public int getPeripersonalAttractiveness(int clock) 
	{
		int attractiveness = Ernest.ATTRACTIVENESS_OF_UNKNOWN;
		
		// The proclivity of the highest interaction attached to this bundle in absolute value.
		for (IAffordance a : m_affordances)
		{
			if (Math.abs(a.getProclivity()) > Math.abs(attractiveness))
				attractiveness = a.getProclivity();
		}

//		// If the bundle has a gustatory stimulation of cuddle 
//		if (m_gustatoryValue == Ernest.STIMULATION_GUSTATORY_CUDDLE)
//			return Ernest.ATTRACTIVENESS_OF_CUDDLE;	
//		
//		// If the bundle has a kinematic stimulation of bump.
//		if (m_kinematicValue == Ernest.STIMULATION_KINEMATIC_BUMP)
//			return Ernest.ATTRACTIVENESS_OF_BUMP;
//
//		// If the bundle has a tactile stimulation of hard.
//		if (m_tactileValue == Ernest.STIMULATION_TOUCH_WALL)
//			return Ernest.ATTRACTIVENESS_OF_HARD - 10; // prefer a bundle salience than a mere touch salience.
//
//		// If the bundle has a gustatory stimulation of fish 
//		if (m_gustatoryValue == Ernest.STIMULATION_GUSTATORY_FISH)
//			// Fish that are touched are more attractive 
//			return Ernest.ATTRACTIVENESS_OF_FISH + 10;

		//		// if the bundle is forgotten
//		if (clock - m_lastTimeBundled > Ernest.PERSISTENCE)
//			attractiveness = Ernest.ATTRACTIVENESS_OF_UNKNOWN ;
		
		return attractiveness;
	}

	/**
	 * Bundles are equal if they have the same first and second acts. 
	 * (only used for dual act bundles)
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			IBundle other = (IBundle)o;
			//ret = (other.getValue() == getValue());
			//ret = (other.getVisualValue() == m_value) && 	
				  //(other.getTactileValue() == m_tactileValue);
				  //(other.getKinematicValue() == m_kinematicValue) &&
				  //(other.getGustatoryValue() == m_gustatoryValue);
			if (m_firstAct != null && m_firstAct.equals(other.getFirstAct())
					&& m_secondAct != null && m_secondAct.equals(other.getSecondAct()))
				ret = true;
			if (m_firstAct != null && m_firstAct.equals(other.getSecondAct())
					&& m_secondAct != null && m_secondAct.equals(other.getFirstAct()))
				ret = true;
			
//			IAct otherAct =  other.getOtherAct(m_firstAct);
//			if (otherAct != null && otherAct.equals(m_secondAct))
//				ret = true;
		}
		return ret;
	}

	/**
	 * Traces the bundle 
	 */
	public void trace(ITracer tracer, String label)
	{
		Object element = tracer.addEventElement(label);
		
		for (IAct a : m_acts)
			tracer.addSubelement(element, "act", a.getLabel());		
		
//		tracer.addSubelement(element, "visual", ErnestUtils.hexColor(m_value));		
//		tracer.addSubelement(element, "tactile", ErnestUtils.hexColor(m_tactileValue));
//		String id = this.toString();
		tracer.addSubelement(element, "id", toString());
		tracer.addSubelement(element, "short_id", toString().substring(toString().length() - 6));
//		tracer.addSubelement(element, "last_time_bundled", m_lastTimeBundled + "");
	}

	public void addAffordance(IAct act, IPlace place, Vector3f relativePosition, float relativeOrientation, int proclivity, int value) 
	{
		
		
		IPlace relativePlace = new Place(place.getBundle(), relativePosition);
		if (place.getBundle() != null)
			relativePlace.setValue(place.getBundle().getValue());
		relativePlace.setOrientation(place.getOrientation() - relativeOrientation);
		relativePlace.setType(place.getType());
		relativePlace.setShape(place.getShape());
		
		IAffordance affordance = new Affordance(act, relativePlace, proclivity, value);
		int i = m_affordances.indexOf(affordance);
		if (i == -1)
			// The affordance does not exist
			m_affordances.add(affordance);
		else 
			// The affordance already exists: return a pointer to it.
			affordance =  m_affordances.get(i);
	}

	public ArrayList<IAffordance> getAffordanceList() 
	{
		return m_affordances;
	}

	public IAct activateAffordance(Vector3f relativePosition) 
	{
		IAct act = null;
		
		for (IAffordance affordance : m_affordances)
		{
			Vector3f compare = new Vector3f(relativePosition);
			compare.add(affordance.getPlace().getPosition());
			if (compare.length() < .1f && affordance.getProclivity() > 0)
				act = affordance.getAct();
		}

		return act;
	}

	/**
	 * The act is consistent with this bundle if 
	 * either the schema does not belong to this bundle or the act belongs to this bundle
	 * The act is inconsistent with the bundle if 
	 * The schema belongs to the bundle with a different status. 
	 */
	public boolean isConsistent(IAct act) 
	{
		boolean isConsistent = true;
		for (IAct a : m_acts)
		{
			if (a.getSchema().equals(act.getSchema()))
				//if (!a.getLabel().equals(act.getLabel()))
				if (!a.equals(act))
					isConsistent = false;
		}
		return isConsistent;
	}

	/**
	 * This bundle affords this act
	 * if this act is in this bundle 
	 */
	public boolean afford(IAct act) 
	{
		boolean afford = false;
		for (IAct a : m_acts)
		{
			if (a.equals(act))
				afford = true;
		}
		if (act.equals(m_firstAct) || act.equals(m_secondAct))
			afford = true;
		return afford;
	}

	public ArrayList<IAct> getActList() 
	{
		return m_acts;
	}

	public IAct getFirstAct() 
	{
		return m_firstAct;
	}

	public IAct getSecondAct() 
	{
		return m_secondAct;
	}
}
