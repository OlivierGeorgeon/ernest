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

	/** The observations */
	private IObservation m_currentObservation = new Observation();
	private IObservation m_previousObservation;
	
	private IStimulation m_visualStandStimulation;
	private IStimulation m_visualFrontStimulation;
	private IStimulation m_tactileFrontStimulation;
	private IStimulation m_gustatoryFrontStimulation;
	
	/**
	 * Determine the enacted act.
	 * Made from the binary feedback status plus the features provided by the distal sensory system. 
	 * @param schema The selected schema. 
	 * @param status The binary feedback
	 */
	public IAct enactedAct(ISchema schema, boolean status)
	{
		// The schema is null during the first cycle, then the enacted act is null.
		if (schema == null) return null;
		
		m_tracer.addEventElement("primitive_enacted_schema", schema.getLabel());
		m_tracer.addEventElement("primitive_feedback", new Boolean(status).toString());

		// Computes the act's label from the features returned by the sensory system and from the status.
		
		String label = schema.getLabel() + m_currentObservation.getDynamicFeature();
		
		if (status)
			label = "(" + label + ")";
		else 
			label = "[" + label + "]";
		
		// Bump into a landmark TODO make sure we declare the good landmark bumped
		if (label.equals("[>]"))
			m_staticSystem.addBundle(m_visualFrontStimulation, m_tactileFrontStimulation, m_gustatoryFrontStimulation, 0);
		
		// Compute the act's satisfaction 
		
		int satisfaction = m_currentObservation.getSatisfaction() + schema.resultingAct(status).getSatisfaction();  
		
		m_tracer.addEventElement("primitive_satisfaction", satisfaction + "");
		
		// Create the act in episodic memory if it does not exist.
		
		IAct enactedAct = m_episodicMemory.addAct(label, schema, status, satisfaction, Ernest.RELIABLE);
		
		return enactedAct;
	}
	
	/**
	 * Generate sensory stimulations from the sensed matrix received from the environment.
	 * @param matrix The matrix sensed in the environment. 
	 */
	public void senseMatrix(int[][] matrix) 
	{
		// Vision =====
		
		IStimulation[] visualCortex = new Stimulation[Ernest.RESOLUTION_RETINA];
		for (int i = 0; i < Ernest.RESOLUTION_RETINA; i++)
			visualCortex[i] = m_staticSystem.addStimulation(matrix[i][1], matrix[i][2], matrix[i][3], matrix[i][0]);

		m_visualStandStimulation = m_staticSystem.addStimulation(matrix[0][5], matrix[0][6], matrix[0][7], matrix[0][4]);
		m_visualFrontStimulation = visualCortex[Ernest.RESOLUTION_RETINA / 2];
		
		Object retinaElmt = m_tracer.addEventElement("visual");
		for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0 ; i--)
		{
			m_tracer.addSubelement(retinaElmt, "pixel_" + i, visualCortex[i].getHexColor());
			m_tracer.addSubelement(retinaElmt, "distance_" + i, visualCortex[i].getDistance() + "");
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
		m_tactileFrontStimulation = somatoCortex[1][0];

		// Taste =====
		
		IStimulation taste = m_staticSystem.addStimulation(Ernest.STIMULATION_GUSTATORY, matrix[0][8]); 
		m_tracer.addEventElement("gustatory", taste.getValue() + "");
		m_gustatoryFrontStimulation = m_staticSystem.addStimulation(Ernest.STIMULATION_GUSTATORY, Ernest.STIMULATION_TASTE_NOTHING);

		// Bundle the stimulations together ===
		
		int attractiveness = 0;
		if (taste.getValue() == Ernest.STIMULATION_TASTE_FISH)
			attractiveness = Ernest.TOP_MOTIVATION;
		else if (somatoCortex[1][1].getValue() == Ernest.STIMULATION_TOUCH_ALGA)
			attractiveness = Ernest.BASE_MOTIVATION;
		
		m_staticSystem.addBundle(m_visualStandStimulation, somatoCortex[1][1], taste, attractiveness);
		
		// Updates the current observation
		
		m_previousObservation  = m_currentObservation;		
		m_currentObservation = m_staticSystem.salientObservation(visualCortex, somatoCortex);
		m_currentObservation.setDynamicFeature(m_previousObservation);
		m_currentObservation.trace(m_tracer, "current_observation");
				
	}	
}