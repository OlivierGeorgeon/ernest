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

	/** The previous observations */
	private IObservation m_previousObservation;
	
	public IAct enactedAct(IAct act, int[][] matrix) 
	{
		IStimulation visualFrontStimulation;
		IStimulation visualStandStimulation;
		IStimulation tactileFrontStimulation;
		IStimulation gustatoryFrontStimulation;
		IStimulation kinematicStimulation;
		
		// Vision =====
		
		IStimulation[] visualCortex = new Stimulation[Ernest.RESOLUTION_RETINA];
		for (int i = 0; i < Ernest.RESOLUTION_RETINA; i++)
			visualCortex[i] = m_staticSystem.addStimulation(matrix[i][1], matrix[i][2], matrix[i][3], matrix[i][0]);

		visualStandStimulation = m_staticSystem.addStimulation(matrix[0][5], matrix[0][6], matrix[0][7], matrix[0][4]);
		visualFrontStimulation = visualCortex[Ernest.RESOLUTION_RETINA / 2];
		
		Object retinaElmt = m_tracer.addEventElement("visual");
		for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0 ; i--)
		{
			m_tracer.addSubelement(retinaElmt, "pixel_" + i, visualCortex[i].getHexColor());
			//m_tracer.addSubelement(retinaElmt, "distance_" + i, visualCortex[i].getDistance() + "");
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
		tactileFrontStimulation = somatoCortex[1][0];
		kinematicStimulation = m_staticSystem.addStimulation(Ernest.STIMULATION_KINEMATIC, matrix[1][8]);
		m_tracer.addEventElement("kinematic", kinematicStimulation.getValue() + "");

		// Taste =====
		
		IStimulation taste = m_staticSystem.addStimulation(Ernest.STIMULATION_GUSTATORY, matrix[0][8]); 
		m_tracer.addEventElement("gustatory", taste.getValue() + "");
		gustatoryFrontStimulation = m_staticSystem.addStimulation(Ernest.STIMULATION_GUSTATORY, Ernest.STIMULATION_TASTE_NOTHING);

		// Circadian ====
		
		IStimulation circadian = m_staticSystem.addStimulation(Ernest.STIMULATION_CIRCADIAN, matrix[2][8]); 
		m_tracer.addEventElement("circadian", circadian.getValue() + "");
		
		
		// Bundle the stimulations together ===
		
		int attractiveness = 0;
		if (taste.getValue() == Ernest.STIMULATION_TASTE_FISH)
			attractiveness = Ernest.TOP_MOTIVATION;
		else if (somatoCortex[1][1].getValue() == Ernest.STIMULATION_TOUCH_ALGA)
			attractiveness = Ernest.BASE_MOTIVATION;
		
		m_staticSystem.addBundle(visualStandStimulation, somatoCortex[1][1], taste, attractiveness);
		
		// Updates the current observation
		
		m_previousObservation  = m_currentObservation;		
		m_currentObservation = m_staticSystem.observe(visualCortex, somatoCortex, kinematicStimulation, taste);
		m_currentObservation.setDynamicFeature2(act, m_previousObservation);
		m_currentObservation.trace(m_tracer, "current_observation");
				
		// If the intended act was null (during the first cycle), then the enacted act is null.
		if (act == null) return null;

		m_tracer.addEventElement("primitive_enacted_schema", act.getSchema().getLabel());

		// Bump
		
		if (m_currentObservation.getLabel().equals("[>]"))
			m_staticSystem.addBundle(visualFrontStimulation, tactileFrontStimulation, gustatoryFrontStimulation, 0);
				
		// Create the act in episodic memory if it does not exist.

		IAct enactedAct;
		if (circadian.getValue() == 1) 
			// If it's the night, Ernest is dreaming and acts are always correctly enacted.
			enactedAct = act;
		else
			enactedAct = m_episodicMemory.addAct(m_currentObservation.getLabel(), act.getSchema(), (m_currentObservation.getKinematic()==1), m_currentObservation.getSatisfaction(), Ernest.RELIABLE);
		
		return enactedAct;
	}
}