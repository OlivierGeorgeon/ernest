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
	private ITracer m_tracer = null; 

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
	 * Also traces the data related to homeostasis motivation
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
	
	/**
	 * Tells if a landmark is inhibited or not
	 * @param landmark The landmark to check 
	 * @return true if inhibited, false if not.
	 */
	public boolean isInhibited(ILandmark landmark)
	{
		boolean inhibited = true;
		
		if (!landmark.getColor().equals(Ernest.WALL_COLOR))
		{
			// If the landmark has been forgotten then it is uninhibited
			if (isThirsty())
			{
				// Forgotten landmarks are uninhibited (except the hive that can't be forgotten)
				if ((m_clock - landmark.getLastTimeChecked()) > PERSISTENCE  &&
						landmark.getDistanceToFood() != 0 )
					inhibited = false;
			
				// if the landmark's distance to target is closer than Ernest's current distance then it is uninhibited
				if (landmark.getDistanceToWater() < m_distanceToTarget) 
					inhibited = false;
			}
			// back to hive
			if (isHungry())
			{
				// Forgotten landmarks are uninhibited (except the flowers that can't be forgotten)
				if ((m_clock - landmark.getLastTimeChecked()) > PERSISTENCE   &&
						landmark.getDistanceToWater() != 0 )
					inhibited = false;
				// landmarks closer to target are uninhibited
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

		landmark.setLastTimeChecked(m_clock);
		check(landmark);
		updateDistanceToWater(m_clock);

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
		landmark.setLastTimeChecked(m_clock);
		check(landmark);
		updateDistanceToFood(m_clock);
		m_lastTimeInHive = m_clock;
		
		// Get thirsty after eating
		m_glucoseLevel = LEVEL_INCREMENT;
		m_waterLevel = 0;
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
		m_tracer.addEventElement("bump_landmark", landmark.getHexColor());
		landmark.setLastTimeChecked(m_clock);
		check(landmark);
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
	
			// Only check the landmark if it already has been visited 
			if (landmark.getLastTimeChecked() > 0)
				landmark.setLastTimeChecked(m_clock);
			
			// Estimate distance to water (even if the landmark is not checked)
			if (isThirsty())
			{
				landmark.setLastTimeThirsty(m_clock);
				landmark.updateTimeFromHive(m_clock - m_lastTimeInHive);
				if (landmark.getDistanceToWater() > 0) // (not yet arrived to final target)
					m_distanceToTarget = landmark.getDistanceToWater();
			}
			
			// Estimate distance to food (even if the landmark is not checked)
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

	/**
	 * Compute a focus observation from the colliculus and from the previous focus observation.
	 * If several landmarks are tied, returns the most inward.
	 * @param colliculus The colliculus
	 * @return The desired direction or -1 if no desirable landmark
	 */
	public IObservation focusObservation(IObservation previousObservation, IObservation[][] colliculus)
	{
		IObservation currentObservation = new Observation();
		ILandmark previousLandmark = previousObservation.getLandmark();
		int distanceToTarget = Ernest.INFINITE + 1; // need to consider unvisited landmarks
		String dynamicFeature = "";
		int satisfaction = 0;
		
		if (isThirsty())
			distanceToTarget = previousLandmark.getDistanceToWater();
		if (isHungry())
			distanceToTarget = previousLandmark.getDistanceToFood();
		
		// Look for a more interesting observation than the previous observation (starting from the colliculus's center) 
				
		for (int i = 0 ; i < colliculus.length / 2 ; i++)
		//for (int i = 0 ; i < 3 ; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				for (int k = -1; k <= 1; k = k + 2)
				{
					int r = k * i +  colliculus.length / 2; // get retinotopic coordinate from colliculus coordinate
					ILandmark l = colliculus[r][j].getLandmark();
					if ( isThirsty() && !isInhibited(l) && (l.getDistanceToWater() < distanceToTarget || l.equals(previousLandmark)))
					{
						distanceToTarget = l.getDistanceToWater();
						currentObservation.setLandmark(l);
						currentObservation.setDistance(distanceToTarget);
						currentObservation.setDirection(r);
					}
					if ( isHungry() && !isInhibited(l) && (l.getDistanceToFood() < distanceToTarget || l.equals(previousLandmark)))
					{
						distanceToTarget = l.getDistanceToFood();
						currentObservation.setLandmark(l);
						currentObservation.setDistance(distanceToTarget);
						currentObservation.setDirection(r);
					}
				}
			}
		}
		
		if (previousLandmark == null)
		{
			// There was no previous observation
			if (currentObservation.getLandmark() != null)
			{
				// A landmark of interest has appeared
				if (currentObservation.getDirection() < colliculus.length / 4 )
					dynamicFeature = ".+";
				else if (currentObservation.getDirection() > colliculus.length * 3 / 4 )
					dynamicFeature = "+.";
				else 
					dynamicFeature = "+";
				satisfaction = 100;
			}
		}
		else
		{
			// There was a previous observation
			if (previousLandmark.equals(currentObservation.getLandmark()))
			{
				// Track the same landmark
				if (Math.abs(currentObservation.getDirection() - previousObservation.getDirection()) < colliculus.length / 4
						&& (currentObservation.getDistance() < previousObservation.getDistance()))
				{
					// Closer in the same direction
					dynamicFeature = "+";
					satisfaction = 100;
				}
				else if (currentObservation.getDirection() < colliculus.length / 2 )
				{
					// To the right
					if ( previousObservation.getDistance() > currentObservation.getDistance()
						||	Math.abs(colliculus.length / 2 - previousObservation.getDirection()) > colliculus.length / 2 - currentObservation.getDirection())
					{
						dynamicFeature = ".+";
						satisfaction = 100;
					}
					else 
					{
						dynamicFeature = ".-";
						satisfaction = -100;
					}
				}
				else if (currentObservation.getDirection() >= colliculus.length / 2 )
				{
					// To the left
					if ( previousObservation.getDistance() > currentObservation.getDistance()
						||	Math.abs(previousObservation.getDirection() - colliculus.length / 2) > currentObservation.getDirection() - colliculus.length / 2)
					{
						dynamicFeature = "+.";
						satisfaction = 100;
					}
					else 
					{
						dynamicFeature = ".-";
						satisfaction = -100;
					}
				}
				else
				{
					dynamicFeature = "-";
					satisfaction = -100;
				}
				dynamicFeature = "+";
				satisfaction = 100;
			}
			else if (currentObservation.getLandmark() == null)
			{
				// All landmarks of interest have disappeared 
				if (previousObservation.getDirection() < colliculus.length / 4 )
					dynamicFeature = ".-";
				else if (currentObservation.getDirection() > colliculus.length * 3 / 4 )
					dynamicFeature = "-.";
				else 
					dynamicFeature = "-";
				satisfaction = -100;
			}
			else
			{
				// A landmark of greater interest has appeared
				if (currentObservation.getDirection() < colliculus.length / 4 )
					dynamicFeature = ".+";
				else if (currentObservation.getDirection() > colliculus.length * 3 / 4 )
					dynamicFeature = "+.";
				else 
					dynamicFeature = "+";
				satisfaction = 100;
			}
		}
		currentObservation.setDynamicFeature(dynamicFeature);
		currentObservation.setSatisfaction(satisfaction);
		
		return currentObservation;
	}
	
}
