package ernest;

import imos.IAct;
import imos.ISchema;
import imos.Imos;

import java.awt.Color;
import java.util.ArrayList;
import javax.vecmath.Vector3f;

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
	public IAct enactedAct(IAct act, boolean status) 
	{
		ArrayList<IPlace> interactionPlaces = new ArrayList<IPlace>();
		
		// The schema is null during the first cycle
		if (act == null) return null;
		
		IAct enactedAct = m_imos.addInteraction(act.getSchema().getLabel(), (status ? "t" : "f"), 0);
		
		
		// Mark the place ========
		
		m_spas.tick();
		IObservation observation = new Observation();

		//int value = 0x0FFFFFF;
		int type = Spas.PLACE_PRIMITIVE;
		int shape = Spas.SHAPE_PIE;
		float o = 0;
		if (act.getSchema().getLabel().equals(">"))
		{
			shape = Spas.SHAPE_TRIANGLE;
			if (status)
				observation.setTranslation(new Vector3f(1,0,0));
			else	
				type = Spas.PLACE_EVOKE_PHENOMENON;
		}
		if (act.getSchema().getLabel().equals("^"))
		{
			observation.setRotation((float) Math.PI / 2);
			shape = Spas.SHAPE_PIE;
			o = (float) Math.PI / 2;
		}
		if (act.getSchema().getLabel().equals("v"))
		{
			observation.setRotation((float) - Math.PI / 2);
			shape = Spas.SHAPE_PIE;
			o = (float) - Math.PI / 2;
		}
		if (act.getSchema().getLabel().equals("/"))
		{
			shape = Spas.SHAPE_SQUARE;
			type = Spas.PLACE_EVOKE_PHENOMENON;
		}
		if (act.getSchema().getLabel().equals("-"))
		{
			shape = Spas.SHAPE_SQUARE;
			type = Spas.PLACE_EVOKE_PHENOMENON;
		}
		if (act.getSchema().getLabel().equals("\\"))
		{
			shape = Spas.SHAPE_SQUARE;
			type = Spas.PLACE_EVOKE_PHENOMENON;
		}

		//IPlace place = m_spas.addPlace(new Vector3f(x,y,0), type, shape);
		IPlace place = m_spas.addPlace(enactedAct.getPosition(), Spas.PLACE_EVOKE_PHENOMENON, shape);
		
		//place.setValue(value);
		place.setValue(enactedAct.getPhenomenon());
		place.setOrientation(o);
		place.setUpdateCount(m_spas.getClock());
		interactionPlaces.add(place);
		
		m_spas.step(observation, interactionPlaces);
		
		// Pass the phenomena in the surrounding space to imos for activation of schemas
		//m_imos.setPhenomena(m_spas.getPhenomena());
		
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
	
	private boolean simulate(IAct act)
	{
		boolean consistent = false;
		ISchema s = act.getSchema();
		if (s.isPrimitive())
		{			
			if (s.getLabel().equals(">"))
			{
				if (m_spas.getValueSimulation(LocalSpaceMemory.DIRECTION_AHEAD) == Ernest.UNANIMATED_COLOR)
					consistent = true;
				else
					consistent = act.getPhenomenon() == m_spas.getValueSimulation(LocalSpaceMemory.DIRECTION_AHEAD);
			}
			if (s.getLabel().equals("-"))
			{
				if (m_spas.getValueSimulation(LocalSpaceMemory.DIRECTION_AHEAD) == Ernest.UNANIMATED_COLOR)
					consistent = true;
				else
					consistent = act.getPhenomenon() == m_spas.getValueSimulation(LocalSpaceMemory.DIRECTION_AHEAD);
			}
			if (s.getLabel().equals("\\"))
			{
				if (m_spas.getValueSimulation(LocalSpaceMemory.DIRECTION_RIGHT) == Ernest.UNANIMATED_COLOR)
					consistent = true;
				else
					consistent = act.getPhenomenon() == m_spas.getValueSimulation(LocalSpaceMemory.DIRECTION_RIGHT);
			}
			if (s.getLabel().equals("/"))
			{
				if (m_spas.getValueSimulation(LocalSpaceMemory.DIRECTION_LEFT) == Ernest.UNANIMATED_COLOR)
					consistent = true;
				else
					consistent = act.getPhenomenon() == m_spas.getValueSimulation(LocalSpaceMemory.DIRECTION_LEFT);
			}
			if (s.getLabel().equals("^") || s.getLabel().equals("v"))
				consistent = true;
			
			if (consistent)
			{
				if (act.getLabel().equals(">t") || act.getLabel().equals(">   ") )
					m_spas.translateSimulation(new Vector3f(-1, 0,0));
				if (s.getLabel().equals("^"))
					m_spas.rotateSimulation((float) - Math.PI/2);
				if (s.getLabel().equals("v"))
					m_spas.rotateSimulation((float) Math.PI/2);
			}
		}
		else 
		{
			consistent = simulate(act.getSchema().getContextAct());
			if (consistent)
				consistent = simulate(act.getSchema().getIntentionAct());
		}
		return consistent;
	}
	
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
