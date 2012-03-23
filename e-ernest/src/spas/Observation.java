package spas;

import imos.IAct;

import javax.vecmath.Vector3f;

import utils.ErnestUtils;

import ernest.Ernest;
import ernest.ITracer;

/**
 * An observation holds the significant consequences that the enacted interaction had on the spatial system.
 * It is the structure that supports the interaction between the spatial system (spas) 
 * and the intrinsic motivation system (imos).
 * @author Olivier
 */
public class Observation implements IObservation 
{

	private float m_span = 0;

	private Vector3f m_position = new Vector3f();
	
	private Vector3f m_speed = new Vector3f();
	
	/** The attractiveness of Ernest's interest. */
	private int m_attractiveness = 0;

	/** The kinematic stimulation. */
	private int m_kinematicValue;
	
	/** The gustatory stimulation. */
	private int m_gustatoryValue;
	
	/** The focus bundle. */
	private IBundle m_bundle; 
	
	/** The initial feedback obtained when the act starts. */
	private String m_instantaneousFeedback = "";
	
	/** The resulting stimuli of the enacted act. */
	private String m_stimuli;
	
	private int m_type;
	
	private int m_updateCount;
	
	private boolean m_newFocus = false;
	
	private Vector3f m_translation = new Vector3f();
	private float m_rotation = 0;
	
	private int m_satisfaction;
	private String m_visualFeedback;
	private int[] m_tactileStimuli = new int[9];
	private int[] m_visualStimuli = new int[Ernest.RESOLUTION_RETINA];
	
	private IPlace m_focusPlace;
	
	private IAct m_primitiveAct = null;
	
	private IAct m_affordanceAct = null;
	
	public void setAttractiveness(int attractiveness) 
	{
		m_attractiveness = attractiveness;
	}

	public int getAttractiveness() 
	{
		return m_attractiveness;
	}

	public void setKinematicValue(int value)
	{
		m_kinematicValue = value;
	}

	public int getKinematicValue()
	{
		return m_kinematicValue;
	}

	public void setGustatoryValue(int Value)
	{
		m_gustatoryValue = Value;
	}
	
	public int getGustatoryValue()
	{
		return m_gustatoryValue;
	}

	public void setPosition(Vector3f position) 
	{
		m_position.set(position);
	}

	public Vector3f getPosition() 
	{
		return m_position;
	}

	public float getDirection() 
	{
		return (float)Math.atan2((double)m_position.y, (double)m_position.x);
	}

	public float getDistance() 
	{
		return m_position.length();
	}

	public void setSpan(float span) 
	{
		m_span = span;
	}

	public float getSpan() 
	{
		return m_span;
	}

	public void setBundle(IBundle bundle) 
	{
		m_bundle = bundle;
	}

	public IBundle getBundle() 
	{
		return m_bundle;
	}

	public void setSpeed(Vector3f speed) 
	{
		m_speed = speed;
	}

	public Vector3f getSpeed()
	{
		return m_speed;
	}

	public void setInstantaneousFeedback(String instantaneousFeedback) 
	{
		m_instantaneousFeedback = instantaneousFeedback;
	}

	public void setStimuli(String stimuli) 
	{
		m_stimuli = stimuli;
	}

	public String getInstantaneousFeedback() 
	{
		return m_instantaneousFeedback;
	}

	public String getStimuli() 
	{
		return m_stimuli;
	}

	public void setType(int type) 
	{
		m_type = type;
	}

	public int getType() 
	{
		return m_type;
	}

	public void setUpdateCount(int updateCount)
	{
		m_updateCount = updateCount;
	}

	public int getUpdateCount() 
	{
		return m_updateCount;
	}

	public void setNewFocus(boolean newFocus) 
	{
		m_newFocus = newFocus;
	}

	public boolean getNewFocus() 
	{
		return m_newFocus;
	}

	public void setTranslation(Vector3f translation) 
	{
		m_translation.set(translation);
	}

	public Vector3f getTranslation() 
	{
		return m_translation;
	}

	public void setRotation(float rotation) 
	{
		m_rotation = rotation;
	}

	public float getRotation() 
	{
		return m_rotation;
	}

	public void trace(ITracer tracer) 
	{
		Object e = tracer.addEventElement("current_observation");
		tracer.addSubelement(e, "direction", getDirection() + "");
		tracer.addSubelement(e, "distance", getDistance() + "");
		tracer.addSubelement(e, "span", getSpan() + "");
		tracer.addSubelement(e, "attractiveness", getAttractiveness() + "");
		tracer.addSubelement(e, "relative_speed_x", getSpeed().x + "");
		tracer.addSubelement(e, "relative_speed_y", getSpeed().y + "");
		tracer.addSubelement(e, "stimuli", m_stimuli);
		tracer.addSubelement(e, "dynamic_feature", m_visualFeedback);
		tracer.addSubelement(e, "satisfaction", getSatisfaction() + "");
		tracer.addSubelement(e, "kinematic", ErnestUtils.hexColor(getKinematicValue()));
		tracer.addSubelement(e, "gustatory", ErnestUtils.hexColor(getGustatoryValue()));
		tracer.addSubelement(e, "type", getType() + "");
		tracer.addSubelement(e, "update_count", getUpdateCount() + "");
		if (getNewFocus()) tracer.addSubelement(e, "new_focus");
		if (getAffordanceAct() != null)
			tracer.addSubelement(e, "affordance_act", getAffordanceAct().getLabel());
		
		Object focusElmt = tracer.addEventElement("focus");
		tracer.addSubelement(focusElmt, "salience", ErnestUtils.hexColor(getBundle().getValue()));
		getBundle().trace(tracer, "focus_bundle");

		// Vision
		Object retinaElmt = tracer.addEventElement("retina");
		for (int i = Ernest.RESOLUTION_RETINA - 1; i >= 0 ; i--)
			tracer.addSubelement(retinaElmt, "pixel_0_" + i, ErnestUtils.hexColor(m_visualStimuli[i]));

		// Tactile
		Object s = tracer.addEventElement("tactile");
		tracer.addSubelement(s, "here", ErnestUtils.hexColor(m_tactileStimuli[8]));
		tracer.addSubelement(s, "rear", ErnestUtils.hexColor(m_tactileStimuli[7]));
		tracer.addSubelement(s, "touch_6", ErnestUtils.hexColor(m_tactileStimuli[6]));
		tracer.addSubelement(s, "touch_5", ErnestUtils.hexColor(m_tactileStimuli[5]));
		tracer.addSubelement(s, "touch_4", ErnestUtils.hexColor(m_tactileStimuli[4]));
		tracer.addSubelement(s, "touch_3", ErnestUtils.hexColor(m_tactileStimuli[3]));
		tracer.addSubelement(s, "touch_2", ErnestUtils.hexColor(m_tactileStimuli[2]));
		tracer.addSubelement(s, "touch_1", ErnestUtils.hexColor(m_tactileStimuli[1]));
		tracer.addSubelement(s, "touch_0", ErnestUtils.hexColor(m_tactileStimuli[0]));
		
	}

	public void setSatisfaction(int satisfaction) 
	{
		m_satisfaction = satisfaction;
	}

	public int getSatisfaction() 
	{
		return m_satisfaction;
	}

	public void setVisualFeedback(String visualStimuli) 
	{
		m_visualFeedback = visualStimuli;
	}

	public String getVisualFeedback() 
	{
		return m_visualFeedback;
	}

	public void setTactileStimuli(int index, int value) 
	{
		m_tactileStimuli[index] = value;
	}

	public int[] getTactileStimuli() 
	{
		return m_tactileStimuli;
	}

	public void setVisualStimuli(int index, int value) 
	{
		m_visualStimuli[index] = value;
	}

	public int[] getVisualStimuli() 
	{
		return m_visualStimuli;
	}

	public void setFocusPlace(IPlace focusPlace) 
	{
		m_focusPlace = focusPlace;
	}

	public IPlace getFocusPlace() 
	{
		return m_focusPlace;
	}

	public void setPrimitiveAct(IAct primitiveAct) 
	{
		m_primitiveAct = primitiveAct;
	}

	public IAct getPrimitiveAct() 
	{
		return m_primitiveAct;
	}

	public void setAffordanceAct(IAct affordanceAct) 
	{
		m_affordanceAct = affordanceAct;
	}

	public IAct getAffordanceAct() 
	{
		return m_affordanceAct;
	}
}
