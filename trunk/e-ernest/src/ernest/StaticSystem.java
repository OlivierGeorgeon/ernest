package ernest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Ernest's static system contains all the landmarks ever created.
 * It offers methods to record and refresh new landmarks.
 * @author ogeorgeon
 */
public class StaticSystem 
{

	/** The Tracer. */
	private ITracer m_tracer = null; //new Tracer("trace.txt");

	/** Ernest's internal clock  */
	private int m_clock;

	/** A list of all the landmarks ever identified. */
	public List<ILandmark> m_landmarks = new ArrayList<ILandmark>(20);
	
	/** The increment in the water or food tank gained from drinking or eating a square  */
	private int LEVEL_INCREMENT    = 90;

	/** The duration during which checked landmarks remain inhibited  */
	private int PERSISTENCE = 50;// 40;
	
	/** The water tank level that raises thirst when empty (homeostatic sodium dilution)    */
	private int m_waterLevel = 0;

	/** The food tank level that raises hunger when empty (homeostatic glucose level)  */
	private int m_glucoseLevel = LEVEL_INCREMENT;
	
	private int m_distanceToTarget = Ernest.INFINITE;
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
	 * Also generates the trace of the motivational information
	 */
	public void tick()
	{
		m_tracer.addEventElement("clock", m_clock + "");
		m_tracer.addEventElement("is_thristy", new Boolean(isThirsty()).toString());
		m_tracer.addEventElement("is_hungry", new Boolean(isHungry()).toString());
		m_tracer.addEventElement("time_to_target", m_distanceToTarget + "");
		
		m_clock++;
		System.out.println("Distance to target " + m_distanceToTarget );
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
			l.setDistanceToWater(clock);
	}

	/**
	 * Update all landmarks' distance to food based on the current time.
	 * @param clock Ernest's current time.
	 */
	public void updateDistanceToFood(int clock)
	{
		for (ILandmark l : m_landmarks)
			l.setDistanceToFood(clock);
	}
	public boolean isInhibited(ILandmark landmark)
	{
		boolean inhibited = true;
		
		if (!landmark.getColor().equals(Ernest.WALL_COLOR))
		{
			// If the landmark has been forgotten then it is deshinibited
			if (isThirsty())
			{
				if ((m_clock - landmark.getLastTimeChecked()) > PERSISTENCE  &&
						landmark.getDistanceToFood() != 0 )
					inhibited = false;
			
			// If the landmark matches Ernest's current state then it is disinhibited
			// if the landmark's distance to target is closer than Ernest's current distance then it is disinhibited
			// TODO: only disinhibit the closest landmark to target if there is more than one in the vicinity. 
				if (landmark.getDistanceToWater() < m_distanceToTarget) 
					inhibited = false;
			}
			// back to hive
			if (isHungry())
			{
				if ((m_clock - landmark.getLastTimeChecked()) > PERSISTENCE   &&
						landmark.getDistanceToWater() != 0 )
					inhibited = false;
			
				if (landmark.getDistanceToFood() < m_distanceToTarget) 
					inhibited = false;		
			}
		}	
		return inhibited;
	}

	/**
	 * The internal effect of the reflex mechanism of drinking when Ernest tastes water.
	 * @param landmark The landmark that is drunk.
	 */
	public void drink(ILandmark landmark)
	{
		m_tracer.addEventElement("drink", landmark.getHexColor());

		//landmark.setDrinkable(); // not necessary because drinkable landmarks are disinhibited when thristy due to their null distance to water.
		landmark.setVisited();
		check(landmark);
		updateDistanceToWater(m_clock);
		landmark.setDrinkable(); 

		// Get hungry after drinking
		m_waterLevel = LEVEL_INCREMENT;
		m_glucoseLevel = 0;
		check(landmark); // check again now that Ernest is hungry
	}
	
	/**
	 * The internal effect of the reflex mechanism of eating when Ernest tastes food.
	 * @param landmark The landmark that is eaten.
	 */
	public void eat(ILandmark landmark)
	{
		m_tracer.addEventElement("eat", landmark.getHexColor());
		//landmark.setEdible(); // not necessary because edible landmarks are disinhibited when hungry due to their null distance to food.
		landmark.setVisited();
		check(landmark);
		updateDistanceToFood(m_clock);
		landmark.setEdible(); // set time to food to null even when Ernest is not hungry
		m_lastTimeInHive = m_clock;
		
		// Get thirsty after eating
		m_glucoseLevel = LEVEL_INCREMENT;
		m_waterLevel = 0;
		check(landmark); // check again now that Ernest is thirsty
	}
	
	public void visit(ILandmark landmark)
	{
		m_tracer.addEventElement("visit", landmark.getHexColor());
		landmark.setVisited();
		check(landmark);
	}
	
	/**
	 * The internal effect of bumping a wall.
	 * @param landmark The landmark that is bumped.
	 */
	public void bump(ILandmark landmark)
	{
		m_tracer.addEventElement("bump_landmark", landmark.getHexColor());
		landmark.setVisited();
		check(landmark);
		//landmark.setLastTimeChecked(m_clock);
	}
	
	/**
	 * Check at a landmark. 
	 * @param landmark The landmark to check
	 */
	public void check(ILandmark landmark)
	{
		if (!landmark.getColor().equals(Ernest.WALL_COLOR))
		{
			m_tracer.addEventElement("check_landmark", landmark.getHexColor());
	
			if (landmark.isVisited())
				landmark.setLastTimeChecked(m_clock);
			if (isThirsty())
			{
				landmark.setLastTimeThirsty(m_clock);
				landmark.updateTimeFromHive(m_clock - m_lastTimeInHive);
				if (landmark.getDistanceToWater() > 0) // (not yet arrived to final target)
					m_distanceToTarget = landmark.getDistanceToWater();
			}
			if (isHungry())
			{
				landmark.setLastTimeHungry(m_clock);
				if (landmark.getDistanceToFood() > 0) // (not yet arrived to final target)
				m_distanceToTarget = landmark.getDistanceToFood();
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
	

}
