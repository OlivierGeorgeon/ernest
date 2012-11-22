package imos2;

/**
 * A sensorimotor pattern of interaction of Ernest with its environment 
 * @author Olivier
 */
public class Interaction implements IInteraction 
{
	private String m_moveLabel ="";
	private String m_effectLabel = "";
	private boolean m_primitive = true;
	private IInteraction m_preInteraction = null;
	private IInteraction m_postInteraction = null;
	private int m_enactionValue = 0;
	private int m_enactionWeight = 0;
	private int m_failPostValue = 0;
	private int m_failPostWeight = 0;
	
	/**
	 * @param moveLabel The move label.
	 * @param effectLabel The effect label.
	 * @param enactionValue The enaction value.
	 * @return The created primitive interaction.
	 */
	public static IInteraction createPrimitiveInteraction(String moveLabel, String effectLabel, int enactionValue)
	{
		return new Interaction(moveLabel, effectLabel, true, null, null, enactionValue);
	}
	
	/**
	 * @param preInteraction The pre-interaction.
	 * @param postInteraction The post-interaction.
	 * @return The created composite interaction.
	 */
	public static IInteraction createCompositeInteraction(IInteraction preInteraction, IInteraction postInteraction)
	{
		int enactionValue = preInteraction.getEnactionValue() + postInteraction.getEnactionValue();
		return new Interaction("", "", false, preInteraction, postInteraction, enactionValue);
	}
	
	protected Interaction(String moveLabel, String effectLabel, boolean primitive, IInteraction preInteraction, IInteraction postInteraction, int enactionValue)
	{
		m_moveLabel = moveLabel;
		m_effectLabel = effectLabel;
		m_primitive = primitive;
		m_preInteraction = preInteraction;
		m_postInteraction = postInteraction;
		m_enactionValue = enactionValue;
	}
	
	public IInteraction getPreInteraction() 
	{
		return m_preInteraction;
	}

	public IInteraction getPostInteraction() 
	{
		return m_postInteraction;
	}

	public int getEnactionValue() 
	{
		return m_enactionValue;
	}

	public void setfailPostValue(int failPostValue) 
	{
		m_failPostValue = failPostValue;
	}

	public int getfailPostValue()
	{
		return m_failPostValue;
	}

	public void setfailPostWeight(int failPostWeight) 
	{
		m_failPostWeight = failPostWeight;
	}

	public int getfailPostWeight() 
	{
		return m_failPostWeight;
	}

	public String getMoveLabel() 
	{
		return m_moveLabel;
	}

	public boolean getPrimitive() 
	{
		return m_primitive;
	}
	
	/**
	 * Interactions are equal if they have the same label. 
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			IInteraction other = (IInteraction)o;
			ret = (other.getLabel().equals(getLabel()));
		}
		
		return ret;
	}

	public String getLabel() 
	{
		String label = "";
		if (m_primitive)
			label = m_moveLabel + m_effectLabel;
		else
			label = "(" + m_preInteraction.getLabel() + m_postInteraction.getLabel() + ")";
		return label; 
	}

	public void setEnactionWeight(int enactionWeight) 
	{
		m_enactionWeight = enactionWeight;
	}

	public int getEnactionWeight() 
	{
		return m_enactionWeight;
	}
}
