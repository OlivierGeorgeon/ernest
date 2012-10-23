package ernest;

import utils.ErnestUtils;
import imos.IAct;

/**
 * A structure used to manage the enaction of a scheme in the real world
 * or the simulation of the enaction of a scheme in spatial memory.
 * @author ogeorgeon
 */
public class Enaction implements IEnaction 
{
	private IEffect m_effect = new Effect();
	private IAct m_intendedPrimitiveAct = null;
	private IAct m_enactedPrimitiveAct = null;
	private IAct m_topAct = null;
	private IAct m_affordanceAct = null;
	private IAct m_topRemainingAct = null;
	private int m_simulationStatus = 0;
	private int m_step = 0;
	
	public void setEffect(IEffect effect) 
	{
		m_effect = effect;
	}

	public IEffect getEffect() 
	{
		return m_effect;
	}

	public void setIntendedPrimitiveAct(IAct act) 
	{
		m_intendedPrimitiveAct = act;
	}

	public IAct getIntendedPrimitiveAct() 
	{
		return m_intendedPrimitiveAct;
	}

	public void setTopAct(IAct act) 
	{
		m_topAct = act;
	}

	public IAct getTopAct() 
	{
		return m_topAct;
	}
	
	public void setTopEnactedAct(IAct act) 
	{
		m_affordanceAct = act;
	}

	public IAct getTopEnactedAct() 
	{
		return m_affordanceAct;
	}
	
	public void setTopRemainingAct(IAct act) 
	{
		m_topRemainingAct = act;
	}

	public IAct getTopRemainingAct() 
	{
		return m_topRemainingAct;
	}
	
	public void setStep(int step)
	{
		m_step = step;
	}

	public int getStep()
	{
		return m_step;
	}

	public void setSimulationStatus(int simulationStatus) 
	{
		m_simulationStatus = simulationStatus;
	}

	public int getSimulationStatus() 
	{
		return m_simulationStatus;
	}

	public void setEnactedPrimitiveAct(IAct act) 
	{
		m_enactedPrimitiveAct = act;
	}

	public IAct getEnactedPrimitiveAct() 
	{
		return m_enactedPrimitiveAct;
	}
	
	public void trace(ITracer tracer) 
	{
		m_effect.trace(tracer);
		
		if (tracer != null) 
		{
			if (m_topAct != null)
			{
				tracer.addEventElement("top_intention", m_topAct.getLabel());
				tracer.addEventElement("top_level", m_topAct.getLength() + "");
			}
			tracer.addEventElement("new_intention", m_step == 0 ? "true" : "false");
			if (m_intendedPrimitiveAct != null)
				tracer.addEventElement("primitive_intended_act", m_intendedPrimitiveAct.getLabel());
			if (m_enactedPrimitiveAct !=  null)
			{
				tracer.addEventElement("primitive_enacted_act", m_enactedPrimitiveAct.getLabel());
				//tracer.addEventElement("primitive_enacted_color", ErnestUtils.hexColor(m_enactedPrimitiveAct.getColor()));
				tracer.addEventElement("primitive_enacted_schema", m_enactedPrimitiveAct.getSchema().getLabel());
				tracer.addEventElement("satisfaction", m_enactedPrimitiveAct.getSatisfaction()/10 + "");
			}
		}
	}
}
