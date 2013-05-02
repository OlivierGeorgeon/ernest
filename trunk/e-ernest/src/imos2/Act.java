package imos2;

import java.util.ArrayList;

import spas.Area;

import ernest.IPrimitive;

/**
 * A sensorimotor pattern of interaction of Ernest with its environment 
 * @author Olivier
 */
public class Act implements IAct 
{
	/** Default weight of primitive interactions */
	private static int PRIMITIVE_WEIGHT = 100;
	
	private String label = "";
	private boolean m_primitive = true;
	private IAct m_preInteraction = null;
	private IAct m_postInteraction = null;
	private int m_enactionValue = 0;
	private int m_enactionWeight = 0;
	private int m_length = 1;
	private IAct m_prescriber = null;
	private int m_step = 0;
	private IPrimitive interaction;
	private Area area;
	
	/** The list of alternative interactions */
	private ArrayList<IAct> m_alternateInteractions = new ArrayList<IAct>();

	/**
	 * @param label The interaction's label.
	 * @param enactionValue The value of enacting this interaction.
	 * @return The created primitive interaction.
	 */
	public static IAct createPrimitiveAct(IPrimitive interaction, Area area)
	{
		return new Act(interaction.getLabel() + area.getLabel(), true, null, null, interaction.getValue(), interaction, area);
	}
	
	/**
	 * @param preInteraction The pre-interaction.
	 * @param postInteraction The post-interaction.
	 * @return The created composite interaction.
	 */
	public static IAct createCompositeInteraction(IAct preInteraction, IAct postInteraction)
	{
		int enactionValue = preInteraction.getEnactionValue() + postInteraction.getEnactionValue();
		String label = preInteraction.getLabel() + postInteraction.getLabel();
		return new Act(label, false, preInteraction, postInteraction, enactionValue, null, null);
	}
	
	protected Act(String label, boolean primitive, IAct preInteraction, IAct postInteraction, int enactionValue, IPrimitive interaction, Area area)
	{
		this.label = label;
		m_primitive = primitive;
		m_preInteraction = preInteraction;
		m_postInteraction = postInteraction;
		m_enactionValue = enactionValue;
		if (primitive)
			m_enactionWeight = PRIMITIVE_WEIGHT;
		else
			m_length = preInteraction.getLength() + postInteraction.getLength();
		this.interaction = interaction;
		this.area = area;
	}
	
	public IPrimitive getInteraction() {
		return interaction;
	}

	public Area getArea() {
		return area;
	}

	public IAct getPreAct() 
	{
		return m_preInteraction;
	}

	public IAct getPostAct() 
	{
		return m_postInteraction;
	}

	public int getEnactionValue() 
	{
		return m_enactionValue;
	}

//	public String getMoveLabel() 
//	{
//		return m_moveLabel;
//	}

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
			IAct other = (IAct)o;
			ret = (other.getLabel().equals(getLabel()));
		}
		
		return ret;
	}

	public String getLabel() 
	{
		String l = "";
		if (m_primitive)
			l = this.label;
			//label = m_moveLabel + m_effectLabel;
		else
			l = "(" + m_preInteraction.getLabel() + m_postInteraction.getLabel() + ")";
		return l; 
	}

	public void setEnactionWeight(int enactionWeight) 
	{
		m_enactionWeight = enactionWeight;
	}

	public int getEnactionWeight() 
	{
		return m_enactionWeight;
	}

	public int getLength() 
	{
		return m_length;
	}

	public void setStep(int step) 
	{
		m_step = step;
	}

	public int getStep() 
	{
		return m_step;
	}

	public void setPrescriber(IAct prescriber) 
	{
		m_prescriber = prescriber;
	}

	public IAct getPrescriber() 
	{
		return m_prescriber;
	}
	
	/**
	 * Update the prescriber if this interaction was enacted
	 */
	public IAct updatePrescriber()
	{
		IAct prescriber = m_prescriber;
		m_prescriber = null;
		IAct nextInteraction = null;
		if (prescriber != null)
		{
			int step = prescriber.getStep();
			if (step == 0)
			{
				// The prescriber's pre-interaction was enacted
				prescriber.setStep(step + 1);
				nextInteraction = prescriber.getPostAct();
				nextInteraction.setPrescriber(prescriber);
			}
			else
			{
				// The prescriber's post-interaction was enacted
				// Update the prescriber's prescriber
				nextInteraction = prescriber.updatePrescriber();
			}
		}
		
		return nextInteraction;
	}

	public void terminate()
	{
		if (m_prescriber != null)
		{
			m_prescriber.terminate();
			m_prescriber = null;
		}
		m_step = 0;
	}

	public IAct prescribe() 
	{
		IAct prescribedInteraction = null;
		if (m_primitive)
			prescribedInteraction = this;
		else
		{
			m_step = 0;
			m_preInteraction.setPrescriber(this);
			prescribedInteraction = m_preInteraction.prescribe();
		}
		return prescribedInteraction;		
	}
	
	public String toString()
	{
		return getLabel() + "(" + m_enactionValue/10 + "," + m_enactionWeight + ")";
	}

	public boolean addAlternateInteraction(IAct alternateInteraction) 
	{
		boolean newAlternate = false;
		int i = m_alternateInteractions.indexOf(alternateInteraction);
		if (i == -1)
		{
			m_alternateInteractions.add(alternateInteraction);
			newAlternate = true;
		}
		else
			alternateInteraction = m_alternateInteractions.get(i);
		
		return newAlternate;
	}

	public ArrayList<IAct> getAlternateActs() 
	{
		return m_alternateInteractions;
	}
}
