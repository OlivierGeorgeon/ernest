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
				act.setTranslation(new Vector3f(-1,0,0));
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				//act.setEndPosition(LocalSpaceMemory.DIRECTION_HERE);
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
	
	public void stepSpas(IAct act)
	{
		// Add this act into spatial memory
		
		m_spas.tick();
		IPlace place = m_spas.addPlace(act.getEndPosition(), Spas.PLACE_EVOKE_PHENOMENON, Spas.SHAPE_PIE);
		place.setValue(act.getPhenomenon());
		place.setUpdateCount(m_spas.getClock());
		place.setAct(act);
	}
	
	public void updateSpas(IAct act)
	{
		// Update the spatial system to construct phenomena ==
		
		IObservation observation = new Observation();		
		observation.setTranslation(act.getTranslation());
		observation.setRotation(act.getRotation());
		m_spas.step(observation);

	}
	
	public ArrayList<IPlace> getPhenomena()
	{
		return m_spas.getPhenomena();
	}

	public boolean checkConsistency(IAct act) 
	{
		m_spas.initSimulation();
		
		return simulate(act);		
	}
	
	public Vector3f situate(IAct act) 
	{
		//IPlace flag = new Place(null, new Vector3f());
		IPlace flag = m_spas.addPlace(null, new Vector3f());
		m_spas.initSimulation();
		sit(act);
		//return  flag.getPosition();
		return  flag.getSimulatedPosition();
	}
	
	/**
	 * The recursive function that tracks back the origin of an act.
	 */
	private void sit(IAct act)
	{
		ISchema s = act.getSchema();
		if (s.isPrimitive())
		{			
			m_spas.translateSimulation(act.getTranslation());
			m_spas.rotateSimulation(act.getRotation());
		}
		else 
		{
			sit(s.getIntentionAct());
			sit(s.getContextAct());
		}
	}
	
	/**
	 * Simulates an act in spatial memory to check its consistency with the current state of spatial memory.
	 * TODO Create simulated phenomena to check for internal consistency of composite acts.
	 */
	private boolean simulate(IAct act)
	{
		boolean consistent = false;
		ISchema s = act.getSchema();
		if (s.isPrimitive())
		{			
			IBundle bundle = m_spas.getBundleSimulation(act.getEndPosition());
			if (bundle == null)	
				consistent = true;
			else
				consistent =  bundle.isConsistent(act);
			m_spas.translateSimulation(act.getTranslation());
			m_spas.rotateSimulation(act.getRotation());
		}
		else 
		{
			consistent = simulate(act.getSchema().getContextAct());
			if (consistent)
				consistent = simulate(act.getSchema().getIntentionAct());
		}
		return consistent;
	}
	
	/**
	 * Generates a composite act that represents a useful situation recognized in local space memory.
	 * TODO Should learn to categorize useful situations autonomously.
	 */
//	public IAct situationAct() 
//	{
//		IAct situationAct = null;
//		int left = m_spas.getValue(LocalSpaceMemory.DIRECTION_LEFT);
//		int front = m_spas.getValue(LocalSpaceMemory.DIRECTION_AHEAD);
//		int right = m_spas.getValue(LocalSpaceMemory.DIRECTION_RIGHT);
//		
//		IAct frontWall = m_imos.addInteraction("-", "t", 0);
//		IAct leftWall = m_imos.addInteraction("/", "t", 0);
//		IAct leftEmpty = m_imos.addInteraction("/", "f", 0);
//		IAct leftTurn = m_imos.addInteraction("^", "f", 0);
//		IAct rightTurn = m_imos.addInteraction("v", "f", 0);
//
//		// Left Corner
//		if (front == Ernest.PHENOMENON_WALL && left == Ernest.PHENOMENON_WALL && right == Ernest.PHENOMENON_EMPTY)
//		{
//			situationAct = m_imos.addCompositeInteraction(frontWall, leftWall);
//			situationAct.getSchema().incWeight(6);
//			//IAct leftCorner = m_imos.addCompositeInteraction(situationAct, rightTurn);
//			//leftCorner.getSchema().incWeight(1);
//			if (m_tracer != null && situationAct.getConfidence() == Imos.RELIABLE)
//				m_tracer.addEventElement("situation", "left-corner");
//		}
//		
//		// right Corner
//		if (front == Ernest.PHENOMENON_WALL && right == Ernest.PHENOMENON_WALL && left == Ernest.PHENOMENON_EMPTY )
//		{
//			situationAct = m_imos.addCompositeInteraction(frontWall, leftEmpty);
//			situationAct.getSchema().incWeight(6);
//			//IAct rightCorner =m_imos.addCompositeInteraction(situationAct, leftTurn);
//			//rightCorner.getSchema().incWeight(1);
//			if (m_tracer != null && situationAct.getConfidence() == Imos.RELIABLE)
//				m_tracer.addEventElement("situation", "right-corner");
//		}
//		if (situationAct != null && situationAct.getConfidence() == Imos.RELIABLE)
//			return situationAct;
//		else 
//			return null;
//	}
	
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

		// Bundle ahead
		IPlace frontPlace = m_spas.getPlace(LocalSpaceMemory.DIRECTION_AHEAD);		
		if (frontPlace == null)
		{
			IProposition p = new Proposition(touchAhead.getSchema(), UNKNOWN_WEIGHT, 1001);
			propositionList.add(p);
			if (m_tracer != null)
				m_tracer.addSubelement(activations, "propose", touchAhead.getLabel() + " weight: " + UNKNOWN_WEIGHT);
		}
		else
		{
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
		
		// Bundle left
		IPlace leftPlace = m_spas.getPlace(LocalSpaceMemory.DIRECTION_LEFT);		
		if (leftPlace == null)
		{
			IProposition p = new Proposition(touchLeft.getSchema(), UNKNOWN_WEIGHT, 1001);
			propositionList.add(p);
			if (m_tracer != null)
				m_tracer.addSubelement(activations, "propose", touchLeft.getLabel() + " weight: " + UNKNOWN_WEIGHT);
		}
		else
		{
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
				if (a.getLabel().equals("/f"))
				{
					int w = 1001;
					IProposition p = new Proposition(turnLeft.getSchema(), w, PHENOMENA_WEIGHT);
					//propositionList.add(p);
//					if (m_tracer != null)
//						m_tracer.addSubelement(activations, "propose", turnLeft.getLabel() + " weight: " + w);
				}
			}
		}
		
		// Bundle right
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
				if (a.getLabel().equals("\\f"))
				{
					int w = 1001;
					IProposition p = new Proposition(turnRight.getSchema(), w, PHENOMENA_WEIGHT);
					//propositionList.add(p);
//					if (m_tracer != null)
//						m_tracer.addSubelement(activations, "propose", turnRight.getLabel() + " weight: " + w);
				}
			}
		}
		
		return propositionList;
	}

//	public ArrayList<IProposition> getPropositionList()
//	{
//		ArrayList<IProposition> propositionList = new ArrayList<IProposition>();
//		IAct leftTurn = m_imos.addInteraction("^", "f", 0);
//		IAct rightTurn = m_imos.addInteraction("v", "f", 0);
//		IAct step = m_imos.addInteraction(">", "t", 0);
//
//		Object activations = null;
//		if (m_tracer != null)
//			activations = m_tracer.addEventElement("phenomena_activations", true);
//
//		boolean frontWall = false;
//		for (IPlace place : m_spas.getPhenomena())
//		{
//			if (place.isInCell(LocalSpaceMemory.DIRECTION_AHEAD) && place.getValue() == Ernest.PHENOMENON_EMPTY)
//			{
//				IProposition p = new Proposition(step.getSchema(), 1001, 1001);
//				int i = propositionList.indexOf(p);
//				if (i == -1)
//					propositionList.add(p);
//				else
//					propositionList.get(i).update(1001, 1001);
//				if (m_tracer != null)
//					m_tracer.addSubelement(activations, "activation", "front_empty");
//			}
//			if (place.isInCell(LocalSpaceMemory.DIRECTION_AHEAD) && place.getValue() == Ernest.PHENOMENON_WALL)
//			{
//				if (m_tracer != null)
//					m_tracer.addSubelement(activations, "activation", "front_wall");
//				frontWall = true;
//			}
//		}
////		if (frontWall)
//		{
//			for (IPlace place : m_spas.getPhenomena())
//			{
//				{
//					if (place.isInCell(LocalSpaceMemory.DIRECTION_LEFT) && place.getValue() == Ernest.PHENOMENON_EMPTY)
//					{
//						IProposition p = new Proposition(leftTurn.getSchema(), 1001, 1001);
//						int i = propositionList.indexOf(p);
//						if (i == -1)
//							propositionList.add(p);
//						else
//							propositionList.get(i).update(1001, 1001);
//						if (m_tracer != null)
//							m_tracer.addSubelement(activations, "activation", "right_corner");
//					}
//					if (place.isInCell(LocalSpaceMemory.DIRECTION_RIGHT) && place.getValue() == Ernest.PHENOMENON_EMPTY)
//					{
//						IProposition p = new Proposition(rightTurn.getSchema(), 1001, 1001);
//						int i = propositionList.indexOf(p);
//						if (i == -1)
//							propositionList.add(p);
//						else
//							propositionList.get(i).update(1001, 1001);
//						if (m_tracer != null)
//							m_tracer.addSubelement(activations, "activation", "left_corner");
//					}
//				}
//			}
//		}
//		return propositionList;
//	}
}
