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
	/** The observation */
    private IObservation m_observation ;
    
    private int m_satisfaction = 0;
    
	public IAct enactedAct(IAct act, IObservation observation) 
	{
		// The schema is null during the first cycle
		if (act == null) return null;
		
		// Computes the resulting interaction from the visual observation
		if (m_observation != null)
		{
			m_satisfaction = 0;
	        String rightFeature  = sensePixel(m_observation.getVisualDistance()[0], observation.getVisualDistance()[0]);
	        String leftFeature  = sensePixel(m_observation.getVisualDistance()[1], observation.getVisualDistance()[1]);
	        if (leftFeature.equals(" ") && rightFeature.equals(" "))
	        	{leftFeature = ""; rightFeature = "";}
	    	
	        if (act.getSchema().getLabel().equals(">"))
	        	m_satisfaction += (observation.getStimuli().equals("t") ? 5 : -10);
	        else if (act.getSchema().getLabel().equals("^") || act.getSchema().getLabel().equals("v"))
	        	m_satisfaction -= 3;
	        else
	        	m_satisfaction -= 1;
	        
	        observation.setStimuli(leftFeature + rightFeature + observation.getStimuli());
	        observation.setSatisfaction(m_satisfaction);
		}
        m_observation = observation;
        
 		IAct enactedAct = addInteraction(act.getSchema().getLabel(), observation.getStimuli(), m_satisfaction);
		
		return enactedAct;
	}
	
    private String sensePixel(int previousPixel, int currentPixel) 
    {
            String feature = " ";
            int satisfaction = 0;
            
            // arrived
            if (previousPixel > currentPixel && currentPixel == 0)
            {
                    feature = "x";
                    satisfaction = 10;
            }
            
            // closer
            else if (previousPixel < Ernest.INFINITE && currentPixel < previousPixel)
            {
                    feature = "+";
                    satisfaction = 10;
            }

            // appear
            else if (previousPixel == Ernest.INFINITE && currentPixel < Ernest.INFINITE)
            {
                    feature = "*";
                    satisfaction = 15;
            }
            
            // disappear
            else if (previousPixel < Ernest.INFINITE && currentPixel == Ernest.INFINITE)
            {
                    feature = "o";
                    satisfaction = -15;
            }

            System.out.println("Sensed " + "prev=" + previousPixel + "cur=" + currentPixel + " feature " + feature);
            
            m_satisfaction += satisfaction;

            return feature;
    }
    
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
			if (stimuliLabel.indexOf("f") >= 0)
			{
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			}
			else if (stimuliLabel.equals("++t"))
			{
				act.setPhenomenon(Ernest.PHENOMENON_FISH);
				act.setStartPosition(new Vector3f(4,0,0));
				act.setEndPosition(new Vector3f(3,0,0));
			}
			else if (stimuliLabel.equals(" +t"))
			{
				act.setPhenomenon(Ernest.PHENOMENON_FISH);
				act.setStartPosition(new Vector3f(3,-3,0));
				act.setEndPosition(new Vector3f(2,-3,0));
			}
			else if (stimuliLabel.equals("+ t"))
			{
				act.setPhenomenon(Ernest.PHENOMENON_FISH);
				act.setStartPosition(new Vector3f(3,3,0));
				act.setEndPosition(new Vector3f(2,3,0));
			}
			else 
			{
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_HERE);
			}
		}

		if (schemaLabel.equals("<"))
		{
			if (stimuliLabel.indexOf("t") >= 0 || stimuliLabel.equals("  "))
			{
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_BEHIND);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_HERE);
			}
			else
			{
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_BEHIND);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_BEHIND);
			}
		}

		if (act.getLabel().equals("^* f"))
		{
			act.setRotation((float) - Math.PI / 2);
			act.setPhenomenon(Ernest.PHENOMENON_FISH);
			act.setStartPosition(new Vector3f(3,3,0));
			act.setEndPosition(new Vector3f(3,3,0));
		}
		else if (schemaLabel.equals("^"))
		{
			act.setRotation((float) - Math.PI / 2);
			act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
			act.setStartPosition(LocalSpaceMemory.DIRECTION_HERE);
			act.setEndPosition(LocalSpaceMemory.DIRECTION_HERE);
		}
		
		if (act.getLabel().equals("v *f"))
		{
			act.setRotation((float) Math.PI / 2);
			act.setPhenomenon(Ernest.PHENOMENON_FISH);
			act.setStartPosition(new Vector3f(3,-3,0));
			act.setEndPosition(new Vector3f(3,-3,0));
		}
		else if (schemaLabel.equals("v"))
		{
			act.setRotation((float) Math.PI / 2);
			act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
			act.setStartPosition(LocalSpaceMemory.DIRECTION_HERE);
			act.setEndPosition(LocalSpaceMemory.DIRECTION_HERE);
		}
		
		if (schemaLabel.equals("/") )
		{
			if (stimuliLabel.indexOf("f") >= 0 || stimuliLabel.equals("  "))
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
		
		if (schemaLabel.equals("-"))
		{
			if (stimuliLabel.indexOf("f") >= 0 || stimuliLabel.equals("  "))
			{
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
				act.setStartPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				act.setEndPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			}
			else if (stimuliLabel.indexOf("a") >= 0 )
			{
				act.setPhenomenon(Ernest.PHENOMENON_ALGA);
				//act.setPhenomenon(Ernest.PHENOMENON_FISH);
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
		
		if (schemaLabel.equals("\\"))
		{
			act.setStartPosition(LocalSpaceMemory.DIRECTION_RIGHT);
			act.setEndPosition(LocalSpaceMemory.DIRECTION_RIGHT);
			//act.setStartPosition(new Vector3f(1,-1,0));
			//act.setEndPosition(new Vector3f(1,-1,0));
			if (stimuliLabel.indexOf("f") >= 0 || stimuliLabel.equals("  "))
				act.setPhenomenon(Ernest.PHENOMENON_EMPTY);
			else
				act.setPhenomenon(Ernest.PHENOMENON_WALL);
		}

		return act;
	}

//	public IAct enactedAct(IAct act, boolean status) 
//	{
//		// Add the interaction in IMOS =======================
//		
//		// The schema is null during the first cycle
//		if (act == null) return null;
//		
//		IAct enactedAct = m_imos.addInteraction(act.getSchema().getLabel(), (status ? "t" : "f"), 0);
//
//		return enactedAct;
//	}
//	
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
	
//	public ArrayList<IPlace> getPhenomena()
//	{
//		return m_spas.getPhenomena();
//	}

	public boolean checkConsistency(IAct act) 
	{
		m_spas.initSimulation();
		return simulate(act, true);
		//return true;
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
	 * Tells the interaction that is likely to result from the enaction of this schema.
	 * If the schema has no succeeding or failing act defined, 
	 * then pick a random interaction attached to this schema.
	 * TODO Simulate the action to get a better anticipation.
	 * @param s The schema. 
	 * @return The anticipated resulting interaction.
	 */
	public IAct anticipateInteraction(ISchema s, int e, ArrayList<IAct> acts)
	{
		IAct anticipateInteraction = null;
		boolean status = (e >= 0);
		anticipateInteraction = (status ? s.getSucceedingAct() : s.getFailingAct());
		
		// if the schema has no succeeding or failing act, then pick an act randomly
		if (anticipateInteraction==null)
		{
			for (IAct a : acts)
			{
				//if (a.getSchema().equals(s) && (a.getStatus() == true))
				if (a.getSchema().equals(s) )
					anticipateInteraction = a;
			}
		}
		return anticipateInteraction;
	}

	/**
	 * Propose all acts that are afforded by the spatial context
	 * and primitive acts that inform about unknown places.
	 */
	public ArrayList<IProposition> getPropositionList(ArrayList<IAct> acts)
	{
		ArrayList<IProposition> propositionList = new ArrayList<IProposition>();
		int  PHENOMENA_WEIGHT = 10;
		int UNKNOWN_WEIGHT = 10;
		
		Object activations = null;
		if (m_tracer != null)
			activations = m_tracer.addEventElement("copresence_propositions", true);

		for (IAct a : acts)
		{
			// Propose acts that are afforded by the spatial memory context
			m_spas.initSimulation();
			if (a.getConfidence() == Imos.RELIABLE && a.getSchema().getLength() <= 4 && simulate(a, false))
			{
				int w = PHENOMENA_WEIGHT * a.getSatisfaction();
				IProposition p = new Proposition(a.getSchema(), w, PHENOMENA_WEIGHT * (a.getStatus() ? 1 : -1));
				propositionList.add(p);
				if (m_tracer != null)
					m_tracer.addSubelement(activations, "propose", p.toString());
			}
			
			// Propose primitive acts that inform about unknown places
			if (a.getSchema().getLabel().equals("-") || a.getSchema().getLabel().equals("/") || a.getSchema().getLabel().equals("\\"))
			{
				IPlace concernedPlace = m_spas.getPlace(a.getStartPosition());	
				if (concernedPlace == null)
				{
					IProposition p = new Proposition(a.getSchema(), UNKNOWN_WEIGHT, UNKNOWN_WEIGHT * (a.getStatus() ? 1 : -1));
					propositionList.add(p);
					if (m_tracer != null)
						m_tracer.addSubelement(activations, "poke", p.toString());
				}
			}
			
			// Propose acts that lead to a place and orientation that affords acts.
			// 
			
		}	
		return propositionList;
	}	
}
