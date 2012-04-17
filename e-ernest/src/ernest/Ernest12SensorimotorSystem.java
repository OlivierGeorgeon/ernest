package ernest;

import imos.IAct;
import imos.ISchema;
import imos.Imos;

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
				act.setPosition(LocalSpaceMemory.DIRECTION_HERE);
			}
			else
			{
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
				act.setPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			}
		}

		if (schemaLabel.equals("^"))
		{
			act.setRotation((float) - Math.PI / 2);
			act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
			act.setPosition(LocalSpaceMemory.DIRECTION_HERE);
		}
		
		if (schemaLabel.equals("v"))
		{
			act.setRotation((float) Math.PI / 2);
			act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
			act.setPosition(LocalSpaceMemory.DIRECTION_HERE);
		}
		
		if (act.getSchema().getLabel().equals("/") )
		{
			if (stimuliLabel.equals("f") || stimuliLabel.equals("  "))
			{
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setPosition(LocalSpaceMemory.DIRECTION_LEFT);
			}
			else
			{
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
				act.setPosition(LocalSpaceMemory.DIRECTION_LEFT);
			}
		}
		
		if (act.getSchema().getLabel().equals("-"))
		{
			if (stimuliLabel.equals("f") || stimuliLabel.equals("  "))
			{
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			}
			else
			{
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
				act.setPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			}
		}
		
		if (act.getSchema().getLabel().equals("\\"))
		{
			if (stimuliLabel.equals("f") || stimuliLabel.equals("  "))
			{
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setPosition(LocalSpaceMemory.DIRECTION_RIGHT);
			}
			else
			{
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
				act.setPosition(LocalSpaceMemory.DIRECTION_RIGHT);
			}
		}

		return act;
	}

	public IAct enactedAct(IAct act, boolean status) 
	{
		// Add the interaction in IMOS ==================
		
		// The schema is null during the first cycle
		if (act == null) return null;
		
		IAct enactedAct = m_imos.addInteraction(act.getSchema().getLabel(), (status ? "t" : "f"), 0);
		
		
		// Mark the place of the interaction in SPAS ====
		
		m_spas.tick();
		IObservation observation = new Observation();		
		observation.setTranslation(enactedAct.getTranslation());
		observation.setRotation(enactedAct.getRotation());

//		int shape = Spas.SHAPE_PIE;
//		float orientation = 0;
//
//		if (act.getSchema().getLabel().equals(">"))
//			shape = Spas.SHAPE_TRIANGLE;
//
//		if (act.getSchema().getLabel().equals("^"))
//		{
//			shape = Spas.SHAPE_PIE;
//			orientation = (float) Math.PI / 2;
//		}
//		
//		if (act.getSchema().getLabel().equals("v"))
//		{
//			shape = Spas.SHAPE_PIE;
//			orientation = (float) - Math.PI / 2;
//		}
//		
//		if (act.getSchema().getLabel().equals("/"))
//			shape = Spas.SHAPE_SQUARE;
//
//		if (act.getSchema().getLabel().equals("-"))
//			shape = Spas.SHAPE_SQUARE;
//
//		if (act.getSchema().getLabel().equals("\\"))
//			shape = Spas.SHAPE_SQUARE;

		IPlace place = m_spas.addPlace(enactedAct.getPosition(), Spas.PLACE_EVOKE_PHENOMENON, Spas.SHAPE_PIE);
		
		
		// The way a place's value is computed determines the construction of phenomena.
		
		place.setValue(enactedAct.getPhenomenon());

//		place.setOrientation(orientation);
		place.setUpdateCount(m_spas.getClock());
		place.setAct(enactedAct);

		// Update the spatial system to place phenomena ====
		
		ArrayList<IPlace> interactionPlaces = new ArrayList<IPlace>();
		interactionPlaces.add(place);
		m_spas.step(observation, interactionPlaces);
		
		return enactedAct;
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
			m_spas.translateSimulation(act.getTranslation());
			m_spas.rotateSimulation(act.getRotation());

			if (m_spas.getValueSimulation(act.getPosition()) == Ernest.UNANIMATED_COLOR)
				consistent = true;
			else
				consistent = act.getPhenomenon() == m_spas.getValueSimulation(act.getPosition());
//			IBundle bundle = m_spas.getBundleSimulation(act.getPosition());
//			if (bundle == null)	
//				consistent = true;
//			else
//				consistent =  m_spas.getBundleSimulation(act.getPosition()).isConsistent(act);
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
	public IAct situationAct() 
	{
		IAct situationAct = null;
		int left = m_spas.getValue(LocalSpaceMemory.DIRECTION_LEFT);
		int front = m_spas.getValue(LocalSpaceMemory.DIRECTION_AHEAD);
		int right = m_spas.getValue(LocalSpaceMemory.DIRECTION_RIGHT);
		
		IAct frontWall = m_imos.addInteraction("-", "t", 0);
		IAct leftWall = m_imos.addInteraction("/", "t", 0);
		IAct leftEmpty = m_imos.addInteraction("/", "f", 0);

		// Left Corner
		if (front == Ernest.PHENOMENON_WALL && left == Ernest.PHENOMENON_WALL && right == Ernest.PHENOMENON_EMPTY)
		{
			situationAct = m_imos.addCompositeInteraction(frontWall, leftWall);
			situationAct.getSchema().incWeight(6);
			if (m_tracer != null && situationAct.getConfidence() == Imos.RELIABLE)
				m_tracer.addEventElement("situation", "left-corner");
		}
		
		// right Corner
		if (front == Ernest.PHENOMENON_WALL && right == Ernest.PHENOMENON_WALL && left == Ernest.PHENOMENON_EMPTY )
		{
			situationAct = m_imos.addCompositeInteraction(frontWall, leftEmpty);
			situationAct.getSchema().incWeight(6);
			if (m_tracer != null && situationAct.getConfidence() == Imos.RELIABLE)
				m_tracer.addEventElement("situation", "right-corner");
		}
		if (situationAct != null && situationAct.getConfidence() == Imos.RELIABLE)
			return situationAct;
		else 
			return null;
	}

}
