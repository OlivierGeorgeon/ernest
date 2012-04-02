package ernest;

import imos.IAct;

import java.awt.Color;
import java.util.ArrayList;
import javax.vecmath.Vector3f;

import spas.IObservation;
import spas.IPlace;
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
		m_imos.setPhenomena(m_spas.getPhenomena());
		
		return enactedAct;
	}
}
