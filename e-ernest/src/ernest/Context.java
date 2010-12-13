package ernest;

import java.util.ArrayList;
import java.util.List;

/**
 * A context is Ernest's internal representation of its situation at a given point in time 
 * A context stores all the data that must be passed from one step to the next
 * and from one decision cycle to the next
 * @author ogeorgeon
 */

public class Context implements IContext {

	/**
	 *  The list of acts in the scope
	 */
	private List<IAct> m_contextList = new ArrayList<IAct>();
	
	/**
	 *  The list of acts in the focus
	 */
	private List<IAct> m_focusList = new ArrayList<IAct>();

	/**
	 *  Acts of interest
	 */
	private IAct m_coreAct = null;
	private IAct m_intentionAct = null;
	private IAct m_primitiveIntention = null;
	
	/* The icon in the context */
	private IIcon m_icon = null;

	public void addContextAct(IAct act) 
	{
		if (act != null)
		{
			if (!m_contextList.contains(act))
				m_contextList.add(act);
		}
	}

	public void addContext(IContext context) 
	{
		for (IAct act : context.getContextList())
		{
			if (!m_contextList.contains(act))
				m_contextList.add(act);
		}
	}

	public void addFocusAct(IAct act) 
	{
		if (act != null)
		{
			if (!m_contextList.contains(act))
				m_contextList.add(act);
			if (!m_focusList.contains(act))
				m_focusList.add(act);
		}
	}
	public void setCoreAct(IAct act) 
	{
		if (act != null)
		{
			if (!m_contextList.contains(act))
				m_contextList.add(act);
			if (!m_focusList.contains(act))
				m_focusList.add(act);
			m_coreAct = act;
		}
	}

	public List<IAct> getContextList() 
	{
		return m_contextList;
	}

	public List<IAct> getFocusList() 
	{
		return m_focusList;
	}

	public IAct getCoreAct() 
	{
		return m_coreAct;
	}
	
	public void setIntentionAct(IAct a)
	{
		m_intentionAct = a;
	}

	public IAct getIntentionAct()
	{
		return m_intentionAct;
	}

	public IAct getPrimitiveIntention() 
	{
		return m_primitiveIntention;
	}

	public void setPrimitiveIntention(IAct a) 
	{
		m_primitiveIntention = a;	
	}

	public void setIcon(IIcon icon)
	{
		m_icon = icon;
	}
	
	public IIcon getIcon()
	{
		return m_icon;
	}
}
