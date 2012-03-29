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
	/** Temporary places.  */
	ArrayList<IPlace> m_places = new ArrayList<IPlace>();

	public IAct enactedAct(IAct act, boolean status) 
	{
		// The schema is null during the first cycle
		if (act == null) return null;
		
		IAct enactedAct = m_imos.addInteraction(act.getSchema().getLabel(), (status ? "t" : "f"), 0);
		
		// Mark the place
		
		m_spas.tick();
		IObservation observation = new Observation();
		m_places.clear();
		int type = Spas.PLACE_PRIMITIVE;
		int value = 0x0FFFFFF;
		int shape = Spas.SHAPE_PIE;
		float x = 0; 
		float y = 0; 
		float o = 0;
		if (act.getSchema().getLabel().equals(">"))
		{
			shape = Spas.SHAPE_TRIANGLE;
			x = .3f;
			if (status)
				observation.setTranslation(new Vector3f(1,0,0));
			else				
				value = 0x0FF0000;
		}
		if (act.getSchema().getLabel().equals("^"))
		{
			observation.setRotation((float) Math.PI / 2);
			shape = Spas.SHAPE_PIE;
			y = .3f;
			o = (float) Math.PI / 2;
			//if (!status)
			//	value = 0x008000;
		}
		if (act.getSchema().getLabel().equals("v"))
		{
			observation.setRotation((float) - Math.PI / 2);
			shape = Spas.SHAPE_PIE;
			y = -.3f;
			o = (float) - Math.PI / 2;
			//if (!status)
			//	value = 0x008000;
		}
		if (act.getSchema().getLabel().equals("/"))
		{
			shape = Spas.SHAPE_SQUARE;
			y = .5f;
			if (status)
				value = 0x008000;
		}
		if (act.getSchema().getLabel().equals("-"))
		{
			shape = Spas.SHAPE_SQUARE;
			x = .3f; 
			if (status)
				value = 0x008000;
		}
		if (act.getSchema().getLabel().equals("\\"))
		{
			shape = Spas.SHAPE_SQUARE;
			y = -.5f;
			if (status)
				value = 0x008000;
		}

		//IPlace place = m_spas.addPlace(new Vector3f(x,y,0), type, shape);
		IPlace place = m_spas.addPlace(enactedAct.getPosition(), type, shape);
		
		place.setValue(value);
		place.setOrientation(o);
		place.setUpdateCount(m_spas.getClock());
		//m_places.add(place);
		
		m_spas.step(observation, m_places);
		
		return enactedAct;
	}
}
