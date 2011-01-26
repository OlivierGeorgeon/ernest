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
	 * The context to learn new schemas with the first learning mechanism.
	 */
	private List<IAct> m_contextList = new ArrayList<IAct>();
	
	/**
	 * The context to learn new schemas with the second learning mechanism.
	 */
	private List<IAct> m_baseContextList = new ArrayList<IAct>();
	
	/**
	 * The list of acts that can activate a new intention. 
	 */
	private List<IAct> m_activationList = new ArrayList<IAct>();

	/** 
	 * The decided intention
	 */
	private IAct m_intentionAct = null;

	/**
	 * The primitive intended act in the current automatic loop.
	 */
	private IAct m_primitiveIntention = null;

	/**
	 * The primitive actually enacted act
	 */
	private IAct m_primitiveEnaction = null;

	public void setPrimitiveEnaction(IAct n) 
	{
		m_primitiveEnaction =n; 
	}

	public IAct getPrimitiveEnaction()       
	{ 
		return m_primitiveEnaction; 
	}
	

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
	 * Add a list of acts to the context list (scope). 
	 * This list is used to learn new schemas in the next decision cycle.
	 * @param actList The list of acts to append in the context list.
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
	 * Get the activation list. 
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
	 * @return The intention act decided during the last decision cycle.
	 */
	public IAct getIntentionAct()
	{
		return m_intentionAct;
	}

	/**
	 * @return The primitive intention act in the current automatic loop.
	 */
	public IAct getPrimitiveIntention() 
	{
		return m_primitiveIntention;
	}

	/**
	 * @param act the primitive intention act. 
	 */
	public void setPrimitiveIntention(IAct act) 
	{
		m_primitiveIntention = act;	
	}

	public void shiftDecisionCycle(IAct enactedAct, IAct performedAct, List<IAct> contextList)
	{
		// The current context list becomes the base context list
		m_baseContextList = new ArrayList<IAct>(m_contextList);
		
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
	
}
