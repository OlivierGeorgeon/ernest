package ernest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

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

	/** A list of all the landmarks ever identified. */
	public List<ILandmark> m_landmarks = new ArrayList<ILandmark>(20);
	
	/** The increment in the water or food tank gained from drinking or eating a square  */
	private int LEVEL_INCREMENT    = 90;

	/** The water tank level that raises thirst when empty (homeostatic sodium dilution)    */
	private int m_waterLevel = 0;

	/** The food tank level that raises hunger when empty (homeostatic glucose level)  */
	private int m_glucoseLevel = LEVEL_INCREMENT;
	
	// private int m_distanceToTarget = Ernest.INFINITE;
	
	private int m_lastTimeInHive = 0; // Ok if Ernest does not pass by the hive at start because it is close anyway.
	
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
	 * Also traces the data related to homeostasis motivation
	 */
	public void tick()
	{
		m_tracer.addEventElement("clock", m_clock + "");
		m_tracer.addEventElement("is_thristy", new Boolean(isThirsty()).toString());
		m_tracer.addEventElement("is_hungry", new Boolean(isHungry()).toString());
		
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
	 * Add a landmark to episodic memory if it does not already exist
	 * @param red Component of the landmark's color
	 * @param green Component of the landmark's color
	 * @param blue Component of the landmark's color
	 * @return the new landmark if created or the already existing landmark
	 */
	public ILandmark addLandmark(int red, int green, int blue)
	{
		ILandmark l = new Landmark(red,green,blue);
		
		int i = m_landmarks.indexOf(l);
		if (i == -1)
			// The landmark does not exist
			m_landmarks.add(l);
		else 
			// The landmark already exists: return a pointer to it.
			l =  m_landmarks.get(i);
		return l;
	}
	
	/**
	 * Add a landmark to episodic memory if it does not already exist
	 * @param color The landmark's color
	 * @return the new landmark if created or the already existing landmark
	 */
	public ILandmark addLandmark(Color color)
	{
		return addLandmark(color.getRed() ,color.getGreen() ,color.getBlue());
	}
	
	/**
	 * Search for a landmark associated with the specified color.
	 * @param color The specified color.
	 * @return the landmark if it exists or null if not.
	 */
	public ILandmark getLandmark(Color color)
	{
		ILandmark l = new Landmark(color.getRed(), color.getGreen(), color.getBlue());

		int i = m_landmarks.indexOf(l);
		if (i == -1)
			// The landmark does not exist
			return null;
		else 
			// The landmark already exists: return a pointer to it.
			return  m_landmarks.get(i);
	}
	
	/**
	 * Update all landmarks' distance to water based on the current time.
	 * @param clock Ernest's current time.
	 */
	public void updateDistanceToWater(int clock)
	{
		for (ILandmark l : m_landmarks)
		{
			l.setDistanceToWater(clock);
			l.setLastTimeChecked(- Ernest.INFINITE); // needed to reconsider recently visited landmarks on the way back
		}
	}

	/**
	 * Update all landmarks' distance to food based on the current time.
	 * @param clock Ernest's current time.
	 */
	public void updateDistanceToFood(int clock)
	{
		for (ILandmark l : m_landmarks)
		{
			l.setDistanceToFood(clock);
			l.setLastTimeChecked(- Ernest.INFINITE); // needed to reconsider recently visited landmarks on the way back
		}
	}
	
	/**
	 * The internal effect of the reflex mechanism of drinking when Ernest tastes water.
	 * @param landmark The landmark that is drunk.
	 */
	public void drink(ILandmark landmark)
	{
		if (isThirsty())
			m_tracer.addEventElement("drink", landmark.getHexColor());
		landmark.setLastTimeChecked(m_clock);
		check(landmark);
		updateDistanceToWater(m_clock);
		m_waterLevel = LEVEL_INCREMENT;
		m_glucoseLevel = 0;
		landmark.setLastTimeChecked(m_clock);
		check(landmark); // check again now that Ernest is hungry
	}
	
	/**
	 * The internal effect of the reflex mechanism of eating when Ernest tastes food.
	 * @param landmark The landmark that is eaten.
	 */
	public void eat(ILandmark landmark)
	{
		if (isHungry())
			m_tracer.addEventElement("eat", landmark.getHexColor());
		landmark.setLastTimeChecked(m_clock);
		check(landmark);
		updateDistanceToFood(m_clock);
		m_lastTimeInHive = m_clock;
		m_glucoseLevel = LEVEL_INCREMENT;
		m_waterLevel = 0;
		landmark.setLastTimeChecked(m_clock);
		check(landmark); // check again now that Ernest is thirsty
	}
	
	/**
	 * Visit a landmark 
	 * Mark this landmark as visited so it can be inhibited later.
	 * @param landmark The visited landmark.
	 */
	public void visit(ILandmark landmark)
	{
		m_tracer.addEventElement("visit", landmark.getHexColor());
		landmark.setLastTimeChecked(m_clock);
		check(landmark);
	}
	
	/**
	 * The internal effect of bumping a wall.
	 * @param landmark The landmark that is bumped.
	 */
	public void bump(ILandmark landmark)
	{
		if (landmark != null)
		{
			m_tracer.addEventElement("bump_landmark", landmark.getHexColor());
			landmark.setLastTimeChecked(m_clock);
			check(landmark);
		}
	}
	
	/**
	 * Check-in at a landmark. 
	 * The landmark is tagged with the current time (lastTimeChecked)
	 * The tag is associated with Ernest's motivation (lastTimeThirsty and lastTimeHungry)
	 * Ernest's estimated time to target is updated based on this landmark (distanceToFood or distanceToWater).
	 * @param landmark The landmark to check
	 */
	public void check(ILandmark landmark)
	{
		if (!landmark.getColor().equals(Ernest.WALL_COLOR))
		{
	
			// Only check the landmark if it has already been visited 
			if (landmark.getLastTimeChecked() > 0)
			{
				Element e = m_tracer.addEventElement("check", "");
				m_tracer.addSubelement(e, "color", landmark.getHexColor());
				landmark.setLastTimeChecked(m_clock);
			
				// Estimate distance to water 
				if (isThirsty())
				{
					landmark.setLastTimeThirsty(m_clock);
					landmark.updateTimeFromHive(m_clock - m_lastTimeInHive);
					m_tracer.addSubelement(e, "time_from_hive", (m_clock - m_lastTimeInHive) + "");
				}
				
				// Estimate distance to food 
				if (isHungry())
				{
					landmark.setLastTimeHungry(m_clock);
				}
			}
		}
	}
	
	/**
	 * @return true if Ernest is thirsty
	 */
	public boolean isThirsty()
	{
		return m_waterLevel <= 0;
	}
	
	/**
	 * @return true is Ernest is hungry
	 */
	public boolean isHungry()
	{
		return m_glucoseLevel <= 0;
	}

	/**
	 * Find the most motivating observation in the colliculus
	 * @param colliculus The colliculus
	 * @return The most motivating observation with its motivation value and its direction (*10).
	 */
	public IObservation salientObservation(IObservation[][] colliculus)
	{
		IObservation salientObservation = new Observation();
		ILandmark currentLandmark = null;
		int distance = Ernest.INFINITE;

		// Find the max motivation in the colliculus
		
		int maxMotivation = 0;
		for (int i = 0 ; i < Ernest.RESOLUTION_RETINA; i++)
			for (int j = 0; j <= 1; j++)
			{
				int interest = colliculus[i][j].getLandmark().currentMotivation(isThirsty(), m_clock) - colliculus[i][j].getDistance();
				colliculus[i][j].setMotivation(interest);
				if (interest > maxMotivation)
				{
					maxMotivation = interest;
					currentLandmark = colliculus[i][j].getLandmark();
					distance = colliculus[i][j].getDistance();
				}
			}
		
		// The direction is the average direction of the max motivation
		
		if (maxMotivation  > 0)
		{
			int sumDirection = 0;
			int nbDirection = 0;
			for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0; i--)
				for (int j = 0; j <= 1; j++)
					if (colliculus[i][j].getMotivation() >= maxMotivation)
					{
						sumDirection += i * 10;
						nbDirection++;
					}
			salientObservation.setLandmark(currentLandmark);
			salientObservation.setDistance(distance);
			salientObservation.setMotivation(maxMotivation);
			salientObservation.setDirection((int) (sumDirection / nbDirection + .5));
		}		
		
		return salientObservation;
	}
	
}
	
