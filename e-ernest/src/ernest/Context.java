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
	 * The list of context acts. 
	 * Used by the first learning mechanism to learn schemas from the enacted act
	 */
	private List<IAct> m_contextList = new ArrayList<IAct>();
	
	/**
	 * The list of acts in the base context. 
	 * Used by the second learning mechanism to learn schemas from the stream act.
	 */
	private List<IAct> m_baseContextList = new ArrayList<IAct>();
	
	/**
	 * The list of acts that can activate a new intention. 
	 * These are used by the intention selection mechanism.
	 */
	private List<IAct> m_activationList = new ArrayList<IAct>();

	/** Acts of interest */
	private IAct m_intentionAct = null;
	private IAct m_primitiveIntention = null;
	private IAct m_primitiveEnaction = null;

	private IAct m_homeostaticNoeme = null;
	private IAct m_sensedIcon = null;
	private IAct m_animationNoeme = null;
	
	public void setPrimitiveEnaction(IAct n) {m_primitiveEnaction =n; }
	public void setHomeostaticNoeme(IAct n)  {m_homeostaticNoeme =n; }
	public void setAnimationNoeme(IAct n)    { m_animationNoeme = n;}

	public IAct getPrimitiveEnaction()       { return m_primitiveEnaction; }
	public IAct getHomeostaticNoeme()        { return m_homeostaticNoeme; }
	public IAct getAnimationNoeme()          { return m_animationNoeme;}
	

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
	 * Add the acts in another context to the list of acts in this context (scope). 
	 * This list is used to learn new schemas in the next decision cycle.
	 * @param context The context to append in this context.
	 */
	public void addContextList(List<IAct> actList) 
	{
		for (IAct act : actList)
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

	public List<IAct> getBaseContextList()
	{
		return m_baseContextList;
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
	
	public void shiftDecisionCycle(IAct enactedAct, IAct performedAct, List<IAct> contextList)
	{
		// The current context list becomes the base context list
		m_baseContextList = m_contextList;
		
		m_contextList.clear();
		m_activationList.clear();
		
		// The enacted act is added first to the activation list
		addActivationAct(enactedAct); 

		// Add the performed act if different
		if (enactedAct != performedAct)
			addActivationAct(performedAct);

		// if the actually enacted act is not primitive, its intention also belongs to the context
		if (!enactedAct.getSchema().isPrimitive())
			addActivationAct(enactedAct.getSchema().getIntentionAct());	
		
		// add the streamcontext list to the context list
		addContextList(contextList);
	}
	
	public void shiftStep(IAct[] pixelMatrix)
	{
		removeIcons();
		addActivationAct(pixelMatrix[0]);
		addActivationAct(pixelMatrix[1]);
	}
}
