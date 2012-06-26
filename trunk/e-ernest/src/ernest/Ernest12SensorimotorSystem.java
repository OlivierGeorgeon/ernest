package ernest;

import imos.IAct;
import imos.IProposition;
import imos.ISchema;
import imos.Imos;
import imos.Proposition;

import java.awt.Color;
import java.util.ArrayList;
import javax.vecmath.Vector3f;

import spas.IBundle;
import spas.IObservation;
import spas.IPlace;
import spas.LocalSpaceMemory;
import spas.Observation;
import spas.Place;
import spas.Spas;

/**
 * Implement Ernest 12.0's sensorimotor system.
 * The binary sensorimotor system plus local space memory tracking.
 * @author ogeorgeon
 */
public class Ernest12SensorimotorSystem extends BinarySensorymotorSystem 
{
	/**
	 * The agent's self model is hard coded in the interactions.
	 * TODO The phenomenon code, position, and spatial transformation should be learned rather than hard coded.
	 */
	public IAct addInteraction(String schemaLabel, String stimuliLabel, int satisfaction)
	{
		// Create the act in imos
		
		IAct act = m_imos.addInteraction(schemaLabel, stimuliLabel, satisfaction);
		
		// Add the spatial properties ========
		
		if (schemaLabel.equals(">"))
		{
			if (stimuliLabel.equals("t") || stimuliLabel.equals("  "))
			{
				//act.setTranslation(new Vector3f(-1,0,0));
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				//act.setEndPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_HERE);
			}
			else
			{
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			}
		}

		if (schemaLabel.equals("^"))
		{
			act.setRotation((float) - Math.PI / 2);
			act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
			act.setStartPosition(LocalSpaceMemory.DIRECTION_HERE);
			act.setEndPosition(LocalSpaceMemory.DIRECTION_HERE);
		}
		
		if (schemaLabel.equals("v"))
		{
			act.setRotation((float) Math.PI / 2);
			act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
			act.setStartPosition(LocalSpaceMemory.DIRECTION_HERE);
			act.setEndPosition(LocalSpaceMemory.DIRECTION_HERE);
		}
		
		if (act.getSchema().getLabel().equals("/") )
		{
			if (stimuliLabel.equals("f") || stimuliLabel.equals("  "))
			{
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_LEFT);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_LEFT);
			}
			else
			{
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_LEFT);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_LEFT);
			}
		}
		
		if (act.getSchema().getLabel().equals("-"))
		{
			if (stimuliLabel.equals("f") || stimuliLabel.equals("  "))
			{
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			}
			else
			{
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			}
		}
		
		if (act.getSchema().getLabel().equals("\\"))
		{
			if (stimuliLabel.equals("f") || stimuliLabel.equals("  "))
			{
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_RIGHT);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_RIGHT);
			}
			else
			{
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_RIGHT);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_RIGHT);
			}
		}

		return act;
	}

	public IAct enactedAct(IAct act, boolean status) 
	{
		// Add the interaction in IMOS =======================
		
		// The schema is null during the first cycle
		if (act == null) return null;
		
		IAct enactedAct = m_imos.addInteraction(act.getSchema().getLabel(), (status ? "t" : "f"), 0);

		return enactedAct;
	}
	
	public void updateSpas(IAct primitiveAct, IAct topAct)
	{
		// Add this act into spatial memory
		
		m_spas.tick();
		if (primitiveAct != null)
		{
			// Apply the spatial transformation to spatial memory
			m_spas.followUp(primitiveAct);
			
			// Place the act in spatial memory
			
			IPlace place = m_spas.addPlace(topAct.getEndPosition(), Spas.PLACE_EVOKE_PHENOMENON, Spas.SHAPE_PIE);
			place.setValue(topAct.getPhenomenon());
			place.setUpdateCount(m_spas.getClock());
			place.setAct(topAct);
			
			// TODO place intermediary acts of higher level acts too?
			if (!topAct.getSchema().isPrimitive())
			{
				IPlace place2 = m_spas.addPlace(primitiveAct.getEndPosition(), Spas.PLACE_EVOKE_PHENOMENON, Spas.SHAPE_PIE);
				place2.setValue(primitiveAct.getPhenomenon());
				place2.setUpdateCount(m_spas.getClock());
				place2.setAct(primitiveAct);
			}
			// Update the spatial system to construct phenomena ==
			
			IObservation observation = new Observation();
			observation.setPrimitiveAct(primitiveAct);
			//observation.setTranslation(primitiveAct.getTranslation());
			//observation.setRotation(primitiveAct.getRotation());
			m_spas.step(observation);
		}

	}
	
	public ArrayList<IPlace> getPhenomena()
	{
		return m_spas.getPhenomena();
	}

	public boolean checkConsistency(IAct act) 
	{
		m_spas.initSimulation();
		return simulate(act, true);		
	}
	
	/**
	 * Simulates an act in spatial memory to check its consistency with the current state of spatial memory.
	 * TODO Create simulated phenomena to check for internal consistency of composite acts.
	 * @param act The act to simulate
	 * @param doubt consistency in case of doubt.
	 */
	private boolean simulate(IAct act, boolean doubt)
	{
		boolean consistent = false;
		ISchema s = act.getSchema();
		if (s.isPrimitive())
		{			
			IBundle bundle = m_spas.getBundleSimulation(act.getStartPosition());
			if (bundle == null)	
				consistent = doubt;
			else
			{
				if (doubt)
					consistent =  bundle.isConsistent(act);
				else 
					consistent = bundle.afford(act);
			}
			m_spas.translateSimulation(act.getTranslation());
			m_spas.rotateSimulation(act.getRotation());
		}
		else 
		{
			consistent = simulate(act.getSchema().getContextAct(), doubt);
			if (consistent)
				consistent = simulate(act.getSchema().getIntentionAct(), doubt);
		}
		return consistent;
	}
	
	/**
	 * Propose all acts that match the spatial context
	 */
	public ArrayList<IProposition> getPropositionList(ArrayList<IAct> acts)
	{
		ArrayList<IProposition> propositionList = new ArrayList<IProposition>();
		int  PHENOMENA_WEIGHT = 11;
		int UNKNOWN_WEIGHT = 2001;
		
		Object activations = null;
		if (m_tracer != null)
			activations = m_tracer.addEventElement("phenomena_propositions", true);

		for (IAct a : acts)
		{
			// propose acts that are afforded by the spatial memory context
			m_spas.initSimulation();
			if (simulate(a, false))
			{
				int w = PHENOMENA_WEIGHT * a.getSatisfaction();
				IProposition p = new Proposition(a.getSchema(), w, PHENOMENA_WEIGHT);
				propositionList.add(p);
				if (m_tracer != null)
					m_tracer.addSubelement(activations, "propose", a.getLabel() + " weight: " + w);
			}
			
			// Propose primitive acts that inform about unknown places
			if (a.getSchema().getLabel().equals("-") || a.getSchema().getLabel().equals("/") || a.getSchema().getLabel().equals("\\"))
			{
				IPlace concernedPlace = m_spas.getPlace(a.getStartPosition());	
				if (concernedPlace == null)
				{
					IProposition p = new Proposition(a.getSchema(), UNKNOWN_WEIGHT, 1001);
					propositionList.add(p);
					if (m_tracer != null)
						m_tracer.addSubelement(activations, "propose", a.getLabel() + " weight: " + UNKNOWN_WEIGHT);
				}
			}
		}
		
		return propositionList;
	}
	
	public ArrayList<IProposition> getPropositionList()
	{
		int  PHENOMENA_WEIGHT = 11;
		int UNKNOWN_WEIGHT = 2001;
		
		ArrayList<IProposition> propositionList = new ArrayList<IProposition>();
		IAct touchAhead = m_imos.addInteraction("-", "f", 0);
		IAct touchLeft = m_imos.addInteraction("/", "f", 0);
		IAct touchRight = m_imos.addInteraction("\\", "t", 0);
		IAct turnLeft = m_imos.addInteraction("^", "f", 0);
		IAct turnRight = m_imos.addInteraction("v", "f", 0);

		Object activations = null;
		if (m_tracer != null)
			activations = m_tracer.addEventElement("phenomena_propositions", true);

		// Place ahead
		IPlace frontPlace = m_spas.getPlace(LocalSpaceMemory.DIRECTION_AHEAD);		
		if (frontPlace == null)
		{
			// If no phenomenon then propose touching ahead
			IProposition p = new Proposition(touchAhead.getSchema(), UNKNOWN_WEIGHT, 1001);
			propositionList.add(p);
			if (m_tracer != null)
				m_tracer.addSubelement(activations, "propose", touchAhead.getLabel() + " weight: " + UNKNOWN_WEIGHT);
		}
		else
		{
			// If phenomenon ahead then all of its acts whose start position is ahead are proposed
			for (IAct a : frontPlace.getBundle().getActList())
			{
				if (a.getStartPosition().equals(LocalSpaceMemory.DIRECTION_AHEAD))
				{
					int w = PHENOMENA_WEIGHT * a.getSatisfaction();
					IProposition p = new Proposition(a.getSchema(), w, PHENOMENA_WEIGHT);
					propositionList.add(p);
					if (m_tracer != null)
						m_tracer.addSubelement(activations, "propose", a.getLabel() + " weight: " + w);
				}
			}
		}
		
		// Place left
		IPlace leftPlace = m_spas.getPlace(LocalSpaceMemory.DIRECTION_LEFT);		
		if (leftPlace == null)
		{
			// If no phenomenon then propose touching left
			IProposition p = new Proposition(touchLeft.getSchema(), UNKNOWN_WEIGHT, 1001);
			propositionList.add(p);
			if (m_tracer != null)
				m_tracer.addSubelement(activations, "propose", touchLeft.getLabel() + " weight: " + UNKNOWN_WEIGHT);
		}
		else
		{
			// If phenomenon left then all acts whose start position is left are proposed
			for (IAct a : leftPlace.getBundle().getActList())
			{
				if (a.getStartPosition().equals(LocalSpaceMemory.DIRECTION_LEFT))
				{
					int w = PHENOMENA_WEIGHT * a.getSatisfaction();
					IProposition p = new Proposition(a.getSchema(), w, PHENOMENA_WEIGHT);
					propositionList.add(p);
					if (m_tracer != null)
						m_tracer.addSubelement(activations, "propose", a.getLabel() + " weight: " + w);
				}
			}
		}
		
		// place right
		IPlace rightPlace = m_spas.getPlace(LocalSpaceMemory.DIRECTION_RIGHT);		
		if (rightPlace == null)
		{
			IProposition p = new Proposition(touchRight.getSchema(), UNKNOWN_WEIGHT, 1001);
			propositionList.add(p);
			if (m_tracer != null)
				m_tracer.addSubelement(activations, "propose", touchRight.getLabel() + " weight: " + UNKNOWN_WEIGHT);
		}
		else
		{
			for (IAct a : rightPlace.getBundle().getActList())
			{
				if (a.getStartPosition().equals(LocalSpaceMemory.DIRECTION_RIGHT))
				{
					int w = PHENOMENA_WEIGHT * a.getSatisfaction();
					IProposition p = new Proposition(a.getSchema(), w, PHENOMENA_WEIGHT);
					propositionList.add(p);
					if (m_tracer != null)
						m_tracer.addSubelement(activations, "propose", a.getLabel() + " weight: " + w);
				}
			}
		}
		
		return propositionList;
	}
}
