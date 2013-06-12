package spas;

import java.util.ArrayList;

import eca.Act;
import ernest.Ernest;
import ernest.ITracer;

/**
 * A bundle of interactions that may be "compresent" at the same location in space.
 * Bundles are intended to represent objects. 
 * @author Olivier
 */
public class Bundle implements IBundle 
{
	/** The duration during which checked landmarks remain not motivating  */
	public static int PERSISTENCE = 300;//Ernest 12 (in SPAS clock)
	
	int m_value;
	int m_lastTimeBundled = - PERSISTENCE;

	/** The acts attached to this bundle. */
	private ArrayList<Act> m_acts = new ArrayList<Act>();
	
	private Act m_firstAct;
	private Act m_secondAct;
	
	Bundle(Act firstAct, Act secondAct)
	{
		m_firstAct = firstAct;
		m_secondAct = secondAct;
		m_acts.add(firstAct);
		m_acts.add(secondAct);
	}
	
	public Act getOtherAct(Act act)
	{
		if (act.equals(m_firstAct))
			return m_secondAct;
		else if (act.equals(m_secondAct))
			return m_firstAct;
		else
			return null;
	}
	
	public int getValue()
	{
		return m_value;
	}
	
//	public boolean addAct(IAct act) 
//	{
//		boolean added = false;
//		int i = m_acts.indexOf(act);
//		if (i == -1)
//		{
//			// The affordance does not exist
//			m_acts.add(act);
//			added = true;
//		}
//		else 
//			// The affordance already exists: return a pointer to it.
//			act =  m_acts.get(i);
//		return added;
//	}

//	public boolean hasAct(IAct act)
//	{
//		boolean hasAct = false;
//		for (IAct a : m_acts)
//		{
//			if (a.equals(act))
//				hasAct = true;
//		}
//		return hasAct;
//	}
	
	public void setValue(int value) 
	{
		m_value = value;
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
//	public int getExtrapersonalAttractiveness(int clock) 
//	{
//		int attractiveness = Ernest.ATTRACTIVENESS_OF_UNKNOWN;
//		
//		// The proclivity of the highest interaction attached to this bundle.
//		for (IAffordance a : m_affordances)
//		{
//			if (Math.abs(a.getProclivity()) > Math.abs(attractiveness))
//				attractiveness = a.getProclivity();
//		}
//		
//		// Repulsion does not apply to extrapersonal space.
//		if (attractiveness < 0)
//			attractiveness = Ernest.ATTRACTIVENESS_OF_BACKGROUND;
//		
//		return attractiveness;
//	}

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
//	public int getPeripersonalAttractiveness(int clock) 
//	{
//		int attractiveness = Ernest.ATTRACTIVENESS_OF_UNKNOWN;
//		
//		// The proclivity of the highest interaction attached to this bundle in absolute value.
//		for (IAffordance a : m_affordances)
//		{
//			if (Math.abs(a.getProclivity()) > Math.abs(attractiveness))
//				attractiveness = a.getProclivity();
//		}
//
//		return attractiveness;
//	}

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
		
		for (Act a : m_acts)
			tracer.addSubelement(element, "act", a.getLabel());		
		
		tracer.addSubelement(element, "id", toString());
		tracer.addSubelement(element, "short_id", toString().substring(toString().length() - 6));
	}

	/**
	 * The act is consistent with this bundle if 
	 * either the schema does not belong to this bundle or the act belongs to this bundle
	 * The act is inconsistent with the bundle if 
	 * The schema belongs to the bundle with a different status. 
	 */
	public boolean isConsistent(Act act) 
	{
		boolean isConsistent = true;
		for (Act a : m_acts)
		{
			//if (a.getSchema().equals(act.getSchema()))
			if (!a.getLabel().equals(act.getLabel()))
				if (!a.equals(act))
					isConsistent = false;
		}
		return isConsistent;
	}

	/**
	 * @param act The act.
	 * @return The expected effect when enacting this act concerning this bundle.
	 */
	public String effectlabel(Act act) 
	{
		String effectLabel = "";
		for (Act a : m_acts)
		{
//			if (a.getSchema().equals(act.getSchema()))
//				effectLabel = a.getEffectLabel();
			if (a.equals(act))
				//effectLabel = a.getLabel();
				effectLabel = a.getLabel().substring(1, a.getLabel().length());
		}
		return effectLabel;
	}
	
	public Act resultingAct(Act act)
	{
		Act resultingAct = null;
		for (Act a : m_acts)
		{
			//if (a.getSchema().equals(act.getSchema()))
			if (a.equals(act))
			{
				if (!act.equals(resultingAct))
					resultingAct = a;
			}
		}
		return resultingAct;
	}


	/**
	 * This bundle affords this act
	 * if this act is in this bundle 
	 */
	public boolean afford(Act act) 
	{
		boolean afford = false;
		for (Act a : m_acts)
		{
			if (a.equals(act))
				afford = true;
		}
		if (act.equals(m_firstAct) || act.equals(m_secondAct))
			afford = true;
		return afford;
	}

//	public ArrayList<IAct> getActList() 
//	{
//		return m_acts;
//	}

	public Act getFirstAct() 
	{
		return m_firstAct;
	}

	public Act getSecondAct() 
	{
		return m_secondAct;
	}
}
