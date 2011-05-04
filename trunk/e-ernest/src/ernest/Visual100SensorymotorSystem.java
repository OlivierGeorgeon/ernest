package ernest;

import java.awt.Color;

import org.w3c.dom.Element;

/**
 * Implement Ernest 10.0's sensorymotor system.
 * Ernest 10.0 has a visual resolution of 2x12 pixels and a kinesthetic resolution of 3x3 pixels.
 * @author ogeorgeon
 */
public class Visual100SensorymotorSystem  extends BinarySensorymotorSystem
{

	/** The current observations */
	private IObservation m_currentObservation = new Observation();

	public IAct enactedAct(IAct act, int[][] matrix) 
	{
		IStimulation kinematicStimulation;
		
		// Vision =====
		
		IStimulation[] visualCortex = new Stimulation[Ernest.RESOLUTION_RETINA];
		for (int i = 0; i < Ernest.RESOLUTION_RETINA; i++)
			visualCortex[i] = m_staticSystem.addStimulation(matrix[i][1], matrix[i][2], matrix[i][3], matrix[i][0]);

		Object retinaElmt = m_tracer.addEventElement("retina");
		for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0 ; i--)
		{
			m_tracer.addSubelement(retinaElmt, "pixel_0_" + i, visualCortex[i].getHexColor());
		}
		
		// Touch =====
		
		Object s = m_tracer.addEventElement("tactile");
		IStimulation [][] somatoCortex = new IStimulation[3][3];
		for (int j = 0; j < 3; j++)
			for (int i = 0; i < 3; i++)
			{
				somatoCortex[i][j] = m_staticSystem.addStimulation(Ernest.STIMULATION_TACTILE, matrix[i][9 + j]);
				m_tracer.addSubelement(s, "cell_" + i + "_" + j, somatoCortex[i][j].getValue() + "");
			}
		
		kinematicStimulation = m_staticSystem.addStimulation(Ernest.STIMULATION_KINEMATIC, matrix[1][8]);

		// Taste =====
		
		IStimulation gustatoryStimulation = m_staticSystem.addStimulation(Ernest.STIMULATION_GUSTATORY, matrix[0][8]); 

		// Circadian ====
		
		IStimulation circadianStimulation = m_staticSystem.addStimulation(Ernest.STIMULATION_CIRCADIAN, matrix[2][8]); 
		m_tracer.addEventElement("circadian", circadianStimulation.getValue() + "");
		
		// Updates the current observation
		
		IAct enactedAct = null;
		
		if (circadianStimulation.getValue() == Ernest.STIMULATION_CIRCADIAN_DAY) 
		{
			IObservation previousObservation = m_currentObservation;
			if (act != null)
				m_currentObservation = m_staticSystem.anticipate(m_currentObservation, act.getSchema());

			m_staticSystem.adjust(m_currentObservation, visualCortex, somatoCortex, kinematicStimulation, gustatoryStimulation);	
			// If the intended act was null (during the first cycle), then the enacted act is null.
			if (act != null)
			{
				m_currentObservation.setDynamicFeature(act);
				enactedAct = m_episodicMemory.addAct(m_currentObservation.getLabel(), act.getSchema(), m_currentObservation.getConfirmation(), m_currentObservation.getSatisfaction(), Ernest.RELIABLE);
			}
		}
		else
		{
			// If it's the night, Ernest is dreaming, and acts are always correctly enacted.
			m_currentObservation = m_staticSystem.anticipate(m_currentObservation, act.getSchema());
			enactedAct = act;
		}
		
		m_currentObservation.trace(m_tracer, "current_observation");
		if (act != null) m_tracer.addEventElement("primitive_enacted_schema", act.getSchema().getLabel());

		return enactedAct;
	}
	
	public IObservation getObservation()
	{
		return m_currentObservation;
	}
	
}