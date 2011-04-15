package ernest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

import tracing.ITracer;

/**
 * Ernest's static system contains all the landmarks ever created.
 * It offers methods to record and refresh new landmarks.
 * @author ogeorgeon
 */
public class StaticSystem 
{

	/** The Tracer. */
	private ITracer m_tracer = null; 

	/** Ernest's internal clock  */
	private int m_clock;

	/** A list of all the stimulations ever identified. */
	public List<IStimulation> m_stimulations = new ArrayList<IStimulation>(20);
	
	/** A list of all the bundles ever identified. */
	public List<IBundle> m_bundles = new ArrayList<IBundle>(20);
	
	/**
	 * @param tracer The tracer
	 */
	public void setTracer(ITracer tracer)
	{
		m_tracer = tracer;
	}

	/**
	 * Tick Ernest's clock.
	 * Used to simulate a decay in Static memory
	 */
	public void tick()
	{
		m_tracer.addEventElement("clock", m_clock + "");
		
		m_clock++;
	}
	
	/**
	 * @return Ernest's current time
	 */
	public int getClock()
	{
		return m_clock;
	}
	
	/**
	 * Add a stimulation to static memory if it does not already exist
	 * @param red Component of the landmark's color
	 * @param green Component of the landmark's color
	 * @param blue Component of the landmark's color
	 * @param distance The distance of the stimulation (or intensity)
	 * @return the new stimulation if created or the already existing landmark
	 */
	public IStimulation addStimulation(int red, int green, int blue, int distance)
	{
		IStimulation l = new Stimulation(red,green,blue,distance);
		
		int i = m_stimulations.indexOf(l);
		if (i == -1)
			// The landmark does not exist
			m_stimulations.add(l);
		else 
		{
			// The landmark already exists: return a pointer to it.
			l =  m_stimulations.get(i);
			l.setDistance(distance);
		}
		return l;
	}
	
	/**
	 * Add a bundle to static memory if it does not already exist
	 * @param stimulation1 First stimulation
	 * @param stimulation2 Second stimulation
	 * @param stimulation3 Third stimulation
	 * @param motivation The bundle's motivational value
	 * @return the new bundle if created or the already existing landmark
	 */
	public IBundle addBundle(IStimulation stimulation1, IStimulation stimulation2, IStimulation stimulation3, int motivation)
	{
		IBundle bundle = new Bundle(stimulation1,stimulation2,stimulation3);
		
		int i = m_bundles.indexOf(bundle);
		if (i == -1)
		{
			m_bundles.add(bundle);
			Object b = m_tracer.addEventElement("bundle");
			m_tracer.addSubelement(b, "color", bundle.getVisualStimulation().getHexColor());
			m_tracer.addSubelement(b, "tactile", bundle.getTactileStimulation().getValue() + "");
			m_tracer.addSubelement(b, "gustatory", bundle.getGustatoryStimulation().getValue() + "");
		}
		else 
			// The landmark already exists: return a pointer to it.
			bundle =  m_bundles.get(i);
		
		bundle.setLastTimeBundled(m_clock);

		return bundle;
	}
	
	/**
	 * Add a stimulation to episodic memory if it does not already exist
	 * @param type The stimulation's type
	 * @param value The stimulation's value
	 * @return the new landmark if created or the already existing landmark
	 */
	public IStimulation addStimulation(int type, int value)
	{
		IStimulation l = new Stimulation(type, value);
		
		int i = m_stimulations.indexOf(l);
		if (i == -1)
			// The landmark does not exist
			m_stimulations.add(l);
		else 
			// The landmark already exists: return a pointer to it.
			l =  m_stimulations.get(i);
		return l;
	}
	
	/**
	 * Find the most motivating observation in the colliculus
	 * TODO use the tactile cortex too
	 * @param visualCortex The set of visual stimulations in the visual cortex
	 * @param tactileCortex The set of tactile stimulations in the tactile cortex
	 * @return The most motivating observation with its motivation value and its direction (*10).
	 */
	public IObservation salientObservation(IStimulation[] visualCortex, IStimulation[][] tactileCortex)
	{
		IObservation salientObservation = new Observation();
		int distance = Ernest.INFINITE;
		int[] colliculus = new int[Ernest.RESOLUTION_COLLICULUS];
		int[][] tactileMotivation = new int[3][3];
		String color = "";

		// Find the max attractiveness in the visual cortex
		
		int maxAttractiveness = 0;
		for (int i = 0 ; i < Ernest.RESOLUTION_RETINA; i++)
		{
			int attractiveness = visualAttractiveness(visualCortex[i]) - visualCortex[i].getDistance();
			colliculus[i] = attractiveness;
			if (attractiveness > maxAttractiveness)
			{
				maxAttractiveness = attractiveness;
				distance = visualCortex[i].getDistance();
				color = visualCortex[i].getHexColor();
			}
		}
		
		// The tactile motivation
		for (int i = 0 ; i < 3; i++)
			for (int j = 0 ; j < 3; j++)	
				tactileMotivation[i][j] = tactileAttractiveness(tactileCortex[i][j]);

		
		// The direction is the average direction of the max attractiveness in the colliculus
		
		if (maxAttractiveness  > 0)
		{
			int sumDirection = 0;
			int nbDirection = 0;
			for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0; i--)
				if (colliculus[i] >= maxAttractiveness)
				{
					sumDirection += i * 10;
					nbDirection++;
				}
			salientObservation.setHexColor(color);
			salientObservation.setDistance(distance);
			salientObservation.setAttractiveness(maxAttractiveness);
			salientObservation.setDirection((int) (sumDirection / nbDirection + .5));
		}		
		
		return salientObservation;
	}
		
	/**
	 * TODO manage different bundles with the same stimulation and more than one visual stimulation
	 * @param stimulation The stimulation
	 * @return The motivation value generated by this stimulation.
	 * (Either the bundle's motivation or the base motivation is this stimulation evokes no bundle.
	 */
	private int visualAttractiveness(IStimulation stimulation)
	{
		if (stimulation.getColor().equals(Ernest.COLOR_WALL))
			return 0;
		
		for (IBundle bundle : m_bundles)
			if (bundle.getVisualStimulation().equals(stimulation))
				return bundle.getAttractiveness(m_clock);

		return Ernest.BASE_MOTIVATION;
	}
	/**
	 * TODO manage different bundles with the same stimulation and more than one visual stimulation
	 * @param stimulation The stimulation
	 * @return The motivation value generated by this stimulation.
	 * (Either the bundle's motivation or the base motivation is this stimulation evokes no bundle.
	 */
	private int tactileAttractiveness(IStimulation stimulation)
	{
		for (IBundle bundle : m_bundles)
			if (bundle.getTactileStimulation().equals(stimulation))
				return bundle.getAttractiveness(m_clock);

		return Ernest.BASE_MOTIVATION;
	}
}
	
