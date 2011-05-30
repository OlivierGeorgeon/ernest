package ernest;

import org.w3c.dom.Element;

/**
 * Implement Ernest 10.0's sensorymotor system.
 * Ernest 10.0 has a visual resolution of 2x12 pixels and a kinesthetic resolution of 3x3 pixels.
 * @author ogeorgeon
 */
public class Visual100SensorymotorSystem  extends BinarySensorymotorSystem
{

	public IAct enactedAct(IAct act, int[][] stimuli) 
	{
		IStimulation kinematicStimulation;
		
		// Vision =====
		
		IStimulation[] visualCortex = new Stimulation[Ernest.RESOLUTION_RETINA];
		for (int i = 0; i < Ernest.RESOLUTION_RETINA; i++)
			visualCortex[i] = m_staticSystem.addStimulation(stimuli[i][1], stimuli[i][2], stimuli[i][3], stimuli[i][0]);

		if (m_tracer != null) {
			Object retinaElmt = m_tracer.addEventElement("retina");

			for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0 ; i--)
			{
				m_tracer.addSubelement(retinaElmt, "pixel_0_" + i, visualCortex[i].getHexColor());
			}
		}
		
		// Touch =====
		
		Object s = null;
		if (m_tracer != null)
			s = m_tracer.addEventElement("tactile");
		IStimulation [][] somatoCortex = new IStimulation[3][3];
		for (int j = 0; j < 3; j++)
			for (int i = 0; i < 3; i++)
			{
				somatoCortex[i][j] = m_staticSystem.addStimulation(Ernest.STIMULATION_TACTILE, stimuli[i][9 + j]);
				if (m_tracer != null)
					m_tracer.addSubelement(s, "cell_" + i + "_" + j, somatoCortex[i][j].getValue() + "");
			}
		
		// Kinematic ====
		
		kinematicStimulation = m_staticSystem.addStimulation(Ernest.STIMULATION_KINEMATIC, stimuli[1][8]);

		// Taste =====
		
		IStimulation gustatoryStimulation = m_staticSystem.addStimulation(Ernest.STIMULATION_GUSTATORY, stimuli[0][8]); 

		// Circadian ====
		
		IStimulation circadianStimulation = m_staticSystem.addStimulation(Ernest.STIMULATION_CIRCADIAN, stimuli[2][8]); 
		if (m_tracer != null)
			m_tracer.addEventElement("circadian", circadianStimulation.getValue() + "");
		
		// Adjust the current observation ====
		
		IAct enactedAct = null;		
		if (circadianStimulation.getValue() == Ernest.STIMULATION_CIRCADIAN_DAY) 
		{
			IObservation observation = m_staticSystem.adjust(visualCortex, somatoCortex, kinematicStimulation, gustatoryStimulation);	
			
			// If the intended act was null (during the first cycle), then the enacted act is null.
			if (act != null)
			{
				observation.setDynamicFeature(act);
				enactedAct = m_episodicMemory.addAct(observation.getLabel(), act.getSchema(), observation.getConfirmation(), observation.getSatisfaction(), Ernest.RELIABLE);
			}
			observation.trace(m_tracer, "current_observation");
		}
		else
		{
			// If it's the night, Ernest is dreaming, and acts are always correctly enacted.
			enactedAct = act;
		}
		
		if (act != null && m_tracer != null) m_tracer.addEventElement("primitive_enacted_schema", act.getSchema().getLabel());

		return enactedAct;
	}
	
}