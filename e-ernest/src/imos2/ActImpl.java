package imos2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spas.Area;

import ernest.Action;
import ernest.Primitive;

/**
 * A sensorimotor pattern of interaction of Ernest with its environment 
 * @author Olivier
 */
public class ActImpl implements Act 
{

	/** The list of all acts */
	private static Map<String , Act> ACTS = new HashMap<String , Act>() ;

	/** Default weight of primitive interactions */
	private static int PRIMITIVE_WEIGHT = 100;
	
	private String label = "";
	private boolean m_primitive = true;
	private Act m_preInteraction = null;
	private Act m_postInteraction = null;
	private int m_enactionValue = 0;
	private int m_enactionWeight = 0;
	private int m_length = 1;
	private Act m_prescriber = null;
	private int m_step = 0;
	private Primitive interaction;
	private Area area;
	
	/** The list of alternative interactions */
	private ArrayList<Act> m_alternateInteractions = new ArrayList<Act>();

	/**
	 * @param label The interaction's label.
	 * @param enactionValue The value of enacting this interaction.
	 * @return The created primitive interaction.
	 */
	public static Act createPrimitiveAct(Primitive interaction, Area area)
	{
		return new ActImpl(interaction.getLabel() + area.getLabel(), true, null, null, interaction.getValue(), interaction, area);
	}
	
	/**
	 * @param preInteraction The pre-interaction.
	 * @param postInteraction The post-interaction.
	 * @return The created composite interaction.
	 */
	public static Act createCompositeInteraction(Act preInteraction, Act postInteraction)
	{
		int enactionValue = preInteraction.getEnactionValue() + postInteraction.getEnactionValue();
		String label = preInteraction.getLabel() + postInteraction.getLabel();
		return new ActImpl(label, false, preInteraction, postInteraction, enactionValue, null, null);
	}
	
	protected ActImpl(String label, boolean primitive, Act preInteraction, Act postInteraction, int enactionValue, Primitive interaction, Area area)
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
	
	public Primitive getInteraction() {
		return interaction;
	}

	public Area getArea() {
		return area;
	}

	public Act getPreAct() 
	{
		return m_preInteraction;
	}

	public Act getPostAct() 
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
			Act other = (Act)o;
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

	public void setPrescriber(Act prescriber) 
	{
		m_prescriber = prescriber;
	}

	public Act getPrescriber() 
	{
		return m_prescriber;
	}
	
	/**
	 * Update the prescriber if this interaction was enacted
	 */
	public Act updatePrescriber()
	{
		Act prescriber = m_prescriber;
		m_prescriber = null;
		Act nextInteraction = null;
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

	public Act prescribe() 
	{
		Act prescribedInteraction = null;
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

	public boolean addAlternateInteraction(Act alternateInteraction) 
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

	public ArrayList<Act> getAlternateActs() 
	{
		return m_alternateInteractions;
	}
}
