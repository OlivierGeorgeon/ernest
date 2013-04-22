package imos2;

import java.util.ArrayList;

/**
 * A sensorimotor pattern of interaction of Ernest with its environment 
 * @author Olivier
 */
public class Interaction implements IInteraction 
{
	/** Default weight of primitive interactions */
	private static int PRIMITIVE_WEIGHT = 100;
	
	private String label = "";
	//private String m_moveLabel ="";
	//private String m_effectLabel = "";
	private boolean m_primitive = true;
	private IInteraction m_preInteraction = null;
	private IInteraction m_postInteraction = null;
	private int m_enactionValue = 0;
	private int m_enactionWeight = 0;
	private int m_length = 1;
	private IInteraction m_prescriber = null;
	private int m_step = 0;
	
	private IAct preAct;
	private IAct postAct;
	
	/** The list of alternative interactions */
	private ArrayList<IInteraction> m_alternateInteractions = new ArrayList<IInteraction>();

	/**
	 * @param moveLabel The move label.
	 * @param effectLabel The effect label.
	 * @param enactionValue The value of enacting this interaction.
	 * @return The created primitive interaction.
	 */
	public static IInteraction createPrimitiveInteraction(String label, int enactionValue)
	{
		return new Interaction(label, true, null, null, enactionValue);
	}
	
	/**
	 * @param preInteraction The pre-interaction.
	 * @param postInteraction The post-interaction.
	 * @return The created composite interaction.
	 */
	public static IInteraction createCompositeInteraction(IInteraction preInteraction, IInteraction postInteraction)
	{
		int enactionValue = preInteraction.getEnactionValue() + postInteraction.getEnactionValue();
		String label = preInteraction.getLabel() + postInteraction.getLabel();
		return new Interaction(label, false, preInteraction, postInteraction, enactionValue);
	}
	
	protected Interaction(String label, boolean primitive, IInteraction preInteraction, IInteraction postInteraction, int enactionValue)
	{
		this.label = label;
		//m_moveLabel = moveLabel;
		//m_effectLabel = effectLabel;
		m_primitive = primitive;
		m_preInteraction = preInteraction;
		m_postInteraction = postInteraction;
		m_enactionValue = enactionValue;
		if (primitive)
			m_enactionWeight = PRIMITIVE_WEIGHT;
		else
			m_length = preInteraction.getLength() + postInteraction.getLength();
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
			IInteraction other = (IInteraction)o;
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

	public void setPrescriber(IInteraction prescriber) 
	{
		m_prescriber = prescriber;
	}

	public IInteraction getPrescriber() 
	{
		return m_prescriber;
	}
	
	/**
	 * Update the prescriber if this interaction was enacted
	 */
	public IInteraction updatePrescriber()
	{
		IInteraction prescriber = m_prescriber;
		m_prescriber = null;
		IInteraction nextInteraction = null;
		if (prescriber != null)
		{
			int step = prescriber.getStep();
			if (step == 0)
			{
				// The prescriber's pre-interaction was enacted
				prescriber.setStep(step + 1);
				nextInteraction = prescriber.getPostInteraction();
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

	public IInteraction prescribe() 
	{
		IInteraction prescribedInteraction = null;
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

	public boolean addAlternateInteraction(IInteraction alternateInteraction) 
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

	public ArrayList<IInteraction> getAlternateInteractions() 
	{
		return m_alternateInteractions;
	}

	public void setPreAct(IAct act) 
	{
		this.preAct = act;
	}

	public void setPostAct(IAct act) 
	{
		this.postAct = act;
	}

	public IAct getPreAct() 
	{
		return this.preAct;
	}

	public IAct getPostAct() 
	{
		return this.postAct;
	}
	
}
