package spas;

import java.util.ArrayList;
import java.util.List;

import ernest.Ernest;
import ernest.ITracer;

import imos.IAct;
import imos.ISchema;


/**
 * An element of Ernest's visual system.
 * @author Olivier
 */
public class Observation implements IObservation 
{

	/** The Tracer. */
	//private ITracer m_tracer = null; 

	//private ISalience m_salience;

	/** The Direction of Ernest's interest. */
	private float m_direction = Ernest.CENTER_RETINA;
	//private float m_previousDirection = Ernest.CENTER_RETINA;

	/** The attractiveness of Ernest's interest. */
	private int m_attractiveness = 0;
	//private int m_previousAttractiveness = 0;

	/** The kinematic stimulation. */
	private IStimulation m_kinematicStimulation;
	/** The gustatory stimulation. */
	private IStimulation m_gustatoryStimulation;

	//private IBundle m_focusBundle;
	//private IBundle m_previousFocusBundle = null;

	/** The stimuli associated with the enacted interaction. */
	//private String m_stimuli  = "";

	/** The visual stimuli associated with the enacted interaction. */
	//private String m_visualStimuli  = "";
	
	/** The satisfaction associated with the enacted interaction. */
	//private int m_satisfaction = 0;
		
//	public String getStimuli() 
//	{
//		return m_stimuli;
//	}
//
//	public int getSatisfaction() 
//	{
//		return m_satisfaction;
//	}
//	
	public void setDirection(float direction) 
	{
		m_direction = direction;
	}

	public float getDirection() 
	{
		return m_direction;
	}

	public void setAttractiveness(int attractiveness) 
	{
		m_attractiveness = attractiveness;
	}

	public int getAttractiveness() 
	{
		return m_attractiveness;
	}

	public void setKinematic(IStimulation kinematicStimulation)
	{
		m_kinematicStimulation = kinematicStimulation;
	}

	public IStimulation getKinematicStimulation()
	{
		return m_kinematicStimulation;
	}

	public void setGustatory(IStimulation gustatoryStimulation)
	{
		m_gustatoryStimulation = gustatoryStimulation;
	}
	
	public IStimulation getGustatoryStimulation()
	{
		return m_gustatoryStimulation;
	}


//	public void trace(ITracer tracer, String element) 
//	{
//		if (tracer == null)
//			return;
//		Object e = tracer.addEventElement(element);
//
//		tracer.addSubelement(e, "color", getHexColor());
//		
//		if (getFocusBundle() != null)
//			getFocusBundle().trace(tracer, "focus_bundle");
//		
//		tracer.addSubelement(e, "stimuli", m_stimuli);
//		tracer.addSubelement(e, "dynamic_feature", m_visualStimuli);
//		
//		tracer.addSubelement(e, "direction", m_direction + "");
//		tracer.addSubelement(e, "attractiveness", m_attractiveness + "");
//		tracer.addSubelement(e, "satisfaction", m_satisfaction + "");
//
//		
//		if (m_kinematicStimulation != null)
//			tracer.addSubelement(e, "kinematic", m_kinematicStimulation.getHexColor());
//		if (m_gustatoryStimulation != null)
//			tracer.addSubelement(e, "gustatory", m_gustatoryStimulation.getHexColor());
//		if (m_salience != null)
//		{
//			tracer.addSubelement(e, "distance", m_salience.getDistance() + "");
//			tracer.addSubelement(e, "span", m_salience.getSpan() + "");
//		}
//		
//		if (m_focusBundle !=null && !m_focusBundle.equals(m_previousFocusBundle))
//		{
//			m_focusBundle.trace(tracer, "focus");
//		}
//		
//	}
	
//	/**
//	 * Generate the stimuli for imos.
//	 * The stimuli come from: 
//	 * - The kinematic feature.
//	 * - The variation in attractiveness and in direction of the object of interest. 
//	 * @param act The enacted act.
//	 */
//	public void setDynamicFeature(IAct act)
//	{
//		
//		String dynamicFeature = "";
//		
//		//int minFovea = Ernest.CENTER_RETINA - 30; // 25;
//		//int maxFovea = Ernest.CENTER_RETINA + 30; // 85;
//		
//		float minFovea =  - (float)Math.PI / 4 + 0.01f;
//		float maxFovea =    (float)Math.PI / 4 - 0.01f;
//		
//		int satisfaction = 0;
//
//		if (m_attractiveness >= 0)
//		{
//			// Positive attractiveness
//			{
//				// Attractiveness
//				if (m_previousAttractiveness > m_attractiveness)
//					// Farther
//					dynamicFeature = "-";		
//				else if (m_previousAttractiveness < m_attractiveness)
//					// Closer
//					dynamicFeature = "+";
//				else if (Math.abs(m_previousDirection - Ernest.CENTER_RETINA ) < Math.abs(m_direction - Ernest.CENTER_RETINA))
//					// More outward (or same direction, therefore another salience)
//					dynamicFeature = "-";
//				else if (Math.abs(m_previousDirection - Ernest.CENTER_RETINA ) > Math.abs(m_direction - Ernest.CENTER_RETINA))
//					// More inward
//					dynamicFeature = "+";
//		
//				if (dynamicFeature.equals("-"))
//					satisfaction = -100;
//				if (dynamicFeature.equals("+"))
//					satisfaction = 20;
//	
//				// Direction
//				
//				if (!dynamicFeature.equals(""))
//				{
//					if (m_direction <= minFovea)
//						dynamicFeature = "|" + dynamicFeature;
//					else if (m_direction >= maxFovea )
//						dynamicFeature = dynamicFeature + "|";
//				}		
//			}
//		}
//		else
//		{
//			// Negative attractiveness (repulsion)
//			
//			// Variation in attractiveness
//			if (m_previousAttractiveness >= 0)
//				// A wall appeared with a part of it in front of Ernest
//				dynamicFeature = "*";		
//			else if (Math.abs(m_previousDirection) < Math.abs(m_direction))
//				// The wall went more outward (Ernest closer to the edge)
//				dynamicFeature = "_";
//			else if (Math.abs(m_previousDirection) > Math.abs(m_direction))
//				// The wall went more inward (Ernest farther to the edge)
//				dynamicFeature = "*";
//	
//			if (dynamicFeature.equals("*"))
//				satisfaction = -100;
//			if (dynamicFeature.equals("_"))
//				satisfaction = 20;
//			
//			// Direction feature
//			
//			if (!dynamicFeature.equals(""))
//			{
//				if (m_direction < -0.1f ) 
//					dynamicFeature = "|" + dynamicFeature;
//				else if (m_direction > 0.1f )
//					dynamicFeature = dynamicFeature + "|";
//			}		
//		}
//		
//		// Gustatory
//		
//		if (m_gustatoryStimulation.equals(Ernest.STIMULATION_GUSTATORY_FISH))
//		{
//			dynamicFeature = "e";
//			satisfaction = 100;
//		}
//		
//		m_visualStimuli = dynamicFeature;
//		
//		// Kinematic
//		
//		boolean status = true;
//		if (m_kinematicStimulation.equals(Ernest.STIMULATION_KINEMATIC_BUMP)) status = false;
//		
//		dynamicFeature = (status ? " " : "w") + dynamicFeature;
//		if (act != null)
//		{
//			if (act.getSchema().getLabel().equals(">"))
//				satisfaction = satisfaction + (status ? 20 : -100);
//			else
//				satisfaction = satisfaction + (status ? -10 : -20);
//		}
//				
//		m_stimuli = dynamicFeature;
//		m_satisfaction = satisfaction;
//	}
	
//	public void anticipate(IObservation previousObservation)
//	{
//		if (previousObservation != null)
//		{
//			m_previousDirection = previousObservation.getDirection();
//			m_previousAttractiveness = previousObservation.getAttractiveness();
//			m_previousFocusBundle = previousObservation.getFocusBundle();
//		}		
//	}
//
//	public void setSalience(ISalience salience)
//	{
//		m_salience = salience;
//	}
//	
//	public ISalience getSalience()
//	{
//		return m_salience;
//	}
//
//	public void setFocusBundle(IBundle bundle)
//	{
//		m_focusBundle = bundle;
//	}
//	
//	public IBundle getFocusBundle()
//	{
//		return m_focusBundle;
//	}

}
