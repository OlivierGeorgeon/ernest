package ernest;

import java.util.ArrayList;
import java.util.List;

/**
 * Ernest's internal representation of its situation at a given point in time. 
 * A context stores all the data that must be passed from one step to the next
 * and from one decision cycle to the next.
 * @author ogeorgeon
 */

public class Context implements IContext {

	/**
	 * The list of acts in the scope. 
	 * These are used for activating schemas during the next decision cycle
	 */
	private List<IAct> m_contextList = new ArrayList<IAct>();
	
	/**
	 * The list of acts in the focus. 
	 * These are used for learning new schemas in the next decision cycle
	 */
	private List<IAct> m_activationList = new ArrayList<IAct>();

	/** Acts of interest */
	private IAct m_coreAct = null;
	private IAct m_intentionAct = null;
	private IAct m_primitiveIntention = null;
	private IAct m_sensedIcon = null;
	private IAct m_animationNoeme = null;
	
	public void setAnimationNoeme(IAct n) { m_animationNoeme = n;}
	public IAct getAnimationNoeme()       { return m_animationNoeme;}
	

	/**
	 * Add an act to the list of acts in the context (scope). 
	 * This list is used to learn new schemas in the next decision cycle.
	 * @param act The act to add.
	 */
	public void addContextAct(IAct act) 
	{
		if (act != null)
		{
			if (!m_contextList.contains(act))
				m_contextList.add(act);
		}
	}

	/**
	 * Add the acts in another context to the list of acts in this context (scope). 
	 * This list is used to learn new schemas in the next decision cycle.
	 * @param context The context to append in this context.
	 */
	public void addContext(IContext context) 
	{
		for (IAct act : context.getContextList())
		{
			if (!m_contextList.contains(act))
				m_contextList.add(act);
		}
	}

	/**
	 * Add an act to the list of acts in the context and in the focus list. 
	 * The focus list is used for learning new schemas in the next decision cycle.
	 * @param act The act that will be added to the context list and to the focus list.
	 */
	public void addActivationAct(IAct act) 
	{
		if (act != null)
		{
			if (!m_contextList.contains(act))
				m_contextList.add(act);
			if (!m_activationList.contains(act))
				m_activationList.add(act);
		}
	}

	/**
	 * Defines the context's core act. 
	 * The core act is also added to the context list and to the focus list.
	 * The core act is the central act in the context. The core act will be used to form the stream act.
	 * @param act The act to become the core act.
	 */
	public void setCoreAct(IAct act) 
	{
		if (act != null)
		{
			if (!m_contextList.contains(act))
				m_contextList.add(act);
			if (!m_activationList.contains(act))
				m_activationList.add(act);
			m_coreAct = act;
		}
	}

	/**
	 * Get the context list. 
	 * This list is used to activate schemas during the next decision cycle.
	 * @return The list of acts in the context.
	 */
	public List<IAct> getContextList() 
	{
		return m_contextList;
	}

	/**
	 * Get the focus list. 
	 * This list is used to learn new schemas in the next decision cycle.
	 * @return The list of acts in the focus list.
	 */
	public List<IAct> getActivationList() 
	{
		return m_activationList;
	}

	/**
	 * Get the core act. 
	 * @return The core act.
	 */
	public IAct getCoreAct() 
	{
		return m_coreAct;
	}
	
	/**
	 * Set the intention act. 
	 * @param a The intention act decided during the decision cycle.
	 */
	public void setIntentionAct(IAct a)
	{
		m_intentionAct = a;
	}

	/**
	 * Get the intention act. 
	 * @return The intention act decided during the last decision cycle.
	 */
	public IAct getIntentionAct()
	{
		return m_intentionAct;
	}

	/**
	 * Get the primitive intention act. 
	 * @return The primitive intention act in the current automatic loop.
	 */
	public IAct getPrimitiveIntention() 
	{
		return m_primitiveIntention;
	}

	/**
	 * Set the primitive intention act. 
	 */
	public void setPrimitiveIntention(IAct a) 
	{
		m_primitiveIntention = a;	
	}

	/**
	 * Set the sensed icon in the context
	 * If the icon is not null, add it to the activation list and to the full context list. 
	 * if the icon is not null
	 * @param icon The icon to add.
	 */
	public void addSensedIcon(IAct icon)
	{
		m_sensedIcon = icon;
		addActivationAct(icon);
	}
	
	/**
	 * @return The sensed icon
	 */
	public IAct getSensedIcon()
	{
		return m_sensedIcon;
	}

	public void removeIcons()
	{
		
		m_sensedIcon = null;
		for (int i = m_activationList.size() - 1 ; i>= 0; i--)
		{
			if (m_activationList.get(i).getModule() == Ernest.ICONIC)
				m_activationList.remove(i);
		}
		for (int i = m_contextList.size() - 1 ; i>= 0; i--)
		{
			if (m_contextList.get(i).getModule() == Ernest.ICONIC)
				m_contextList.remove(i);
		}

	}
}
