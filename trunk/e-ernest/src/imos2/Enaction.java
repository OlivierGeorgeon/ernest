package imos2;

import java.util.ArrayList;
import java.util.List;

import ernest.Effect;
import ernest.IEffect;
import ernest.ITracer;

/**
 * A structure used to manage the enaction of a scheme in the real world
 * or the simulation of the enaction of a scheme in spatial memory.
 * @author ogeorgeon
 */
public class Enaction implements IEnaction 
{
	private IEffect m_effect = new Effect();
	private IInteraction m_intendedPrimitiveAct = null;
	private IInteraction m_enactedPrimitiveAct = null;
	private IInteraction m_topAct = null;
	private IInteraction m_topEnactedAct = null;
	private IInteraction m_topRemainingAct = null;
	private int m_simulationStatus = 0;
	private int m_step = 0;
	private ArrayList<IInteraction> m_previousLearningContext   = new ArrayList<IInteraction>();
	private ArrayList<IInteraction> m_initialLearningContext   = new ArrayList<IInteraction>();
	private ArrayList<IInteraction> m_finalLearningContext   = new ArrayList<IInteraction>();
	private ArrayList<IInteraction> m_finalActivationContext = new ArrayList<IInteraction>();
	private int m_nbSchemaLearned = 0;
	private boolean m_correct = true;
	
	private ArrayList<IInteraction> m_ongoingInteractions = new ArrayList<IInteraction>();
	
	public void setEffect(IEffect effect) 
	{
		m_effect = effect;
	}

	public IEffect getEffect() 
	{
		return m_effect;
	}

	public void setIntendedPrimitiveInteraction(IInteraction act) 
	{
		m_intendedPrimitiveAct = act;
	}

	public IInteraction getIntendedPrimitiveInteraction() 
	{
		return m_intendedPrimitiveAct;
	}

	public void setTopInteraction(IInteraction act) 
	{
		m_topAct = act;
	}

	public IInteraction getTopInteraction() 
	{
		return m_topAct;
	}
	
	public void setTopEnactedInteraction(IInteraction act) 
	{
		m_topEnactedAct = act;
	}

	public IInteraction getTopEnactedInteraction() 
	{
		return m_topEnactedAct;
	}
	
	public void setTopRemainingInteraction(IInteraction act) 
	{
		m_topRemainingAct = act;
	}

	public IInteraction getTopRemainingInteraction() 
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

	public void setEnactedPrimitiveInteraction(IInteraction act) 
	{
		m_enactedPrimitiveAct = act;
	}

	public IInteraction getEnactedPrimitiveInteraction() 
	{
		return m_enactedPrimitiveAct;
	}
	
	public boolean isOver()
	{
		return (m_topRemainingAct == null);
	}
	
	public void setCorrect(boolean correct) 
	{
		m_correct = correct;
	}
	/**
	 * Add a list of acts to the context list (scope). 
	 * This list is used to learn new schemas in the next decision cycle.
	 * @param actList The list of acts to append in the context list.
	 */
	private void addContextList(List<IInteraction> actList) 
	{
		for (IInteraction act : actList)
		{
			if (!m_finalLearningContext.contains(act))
				m_finalLearningContext.add(act);
		}
	}

	/**
	 * Add an act to the list of acts in the context and in the focus list. 
	 * The focus list is used for learning new schemas in the next decision cycle.
	 * @param act The act that will be added to the context list and to the focus list.
	 */
	private void addActivationAct(IInteraction act) 
	{
		if (act != null)
		{
			if (!m_finalLearningContext.contains(act))
				m_finalLearningContext.add(act);
			if (!m_finalActivationContext.contains(act))
				m_finalActivationContext.add(act);
		}
	}

	/**
	 * Shift the context when a decision cycle terminates and the next begins.
	 * The context list is passed to the base context list.
	 * The activation list is reinitialized from the enacted act and the performed act.
	 * The context list is reinitialized from the activation list and the additional list provided as a parameter. 
	 * @param enactedAct The act that was actually enacted during the terminating decision cycle.
	 * @param performedAct The act that was performed during the terminating decision cycle.
	 * @param contextList The additional acts to add to the new context list
	 */
	public void setFinalContext(IInteraction enactedAct, IInteraction performedAct, ArrayList<IInteraction> contextList)
	{
		// The current context list becomes the base context list
		//m_baseContextList = new ArrayList<IAct>(m_contextList);
		
		// The enacted act is added first to the activation list
		addActivationAct(enactedAct); 

		// Add the performed act if different
		if (enactedAct != performedAct)
			addActivationAct(performedAct);

		// if the actually enacted act is not primitive, its intention also belongs to the context
		if (!enactedAct.getPrimitive())
			addActivationAct(enactedAct.getPostInteraction());	
		
		// add the streamcontext list to the context list
		addContextList(contextList);
	}
	
	public ArrayList<IInteraction> getFinalLearningContext()
	{
		return m_finalLearningContext;
	}
	public ArrayList<IInteraction> getFinalActivationContext()
	{
		return m_finalActivationContext;
	}
	public void setInitialLearningContext(ArrayList<IInteraction> learningContext) 
	{
		m_initialLearningContext = learningContext;
	}

	public ArrayList<IInteraction> getInitialLearningContext() 
	{
		return m_initialLearningContext;
	}
	public void setPreviousLearningContext(ArrayList<IInteraction> learningContext) 
	{
		m_previousLearningContext = learningContext;
	}

	public ArrayList<IInteraction> getPreviousLearningContext() 
	{
		return m_previousLearningContext;
	}

	public void setNbActLearned(int nbActLearned) 
	{
		m_nbSchemaLearned = nbActLearned;
	}

	public void traceTrack(ITracer tracer) 
	{
		if (tracer != null && m_intendedPrimitiveAct != null)
		{
			//tracer.addEventElement("primitive_enacted_act", m_enactedPrimitiveAct.getLabel());
			tracer.addEventElement("top_level", m_topAct.getLength() + "");
			tracer.addEventElement("satisfaction", m_enactedPrimitiveAct.getEnactionValue()/10 + "");
			tracer.addEventElement("primitive_enacted_schema", m_enactedPrimitiveAct.getLabel());

			m_effect.trace(tracer);
			
			Object e = tracer.addEventElement("track_enaction");
		
			tracer.addSubelement(e, "top_intention", m_topAct.getLabel());
			//tracer.addSubelement(e, "top_level", m_topAct.getSchema().getLength() + "");
			tracer.addSubelement(e, "step", m_step + "");
			tracer.addSubelement(e, "primitive_intended_act", m_intendedPrimitiveAct.getLabel());
			tracer.addSubelement(e, "primitive_enacted_act", m_enactedPrimitiveAct.getLabel());
			//tracer.addSubelement(e, "satisfaction", m_enactedPrimitiveAct.getSatisfaction()/10 + "");
		}
	}

	public void traceCarry(ITracer tracer)
	{
		if (tracer != null)
		{
			Object e = tracer.addEventElement("carry_enaction");

			tracer.addSubelement(e, "top_intention", m_topAct.getLabel());
			tracer.addSubelement(e, "top_level", m_topAct.getLength() + "");
			if (m_topEnactedAct != null)
				tracer.addSubelement(e, "top_enacted", m_topEnactedAct.getLabel());
			tracer.addSubelement(e, "top_remaining", m_topRemainingAct.getLabel());
			tracer.addSubelement(e, "next_step", m_step + "");
			tracer.addSubelement(e, "next_primitive_intended_act", m_intendedPrimitiveAct.getLabel());
		}
	}
	
	public void traceTerminate(ITracer tracer)
	{
		if (tracer != null)
		{
			Object e = tracer.addEventElement("terminate_enaction");
			
			if (!m_correct) tracer.addSubelement(e, "incorrect");
			
			Object activation = tracer.addSubelement(e, "activation_context");
			for (IInteraction a : m_finalActivationContext)	
				tracer.addSubelement(activation, "act", a.getLabel());

			Object learning = tracer.addSubelement(e, "learning_context");
			for (IInteraction a : m_finalLearningContext)	
				tracer.addSubelement(learning, "act", a.getLabel());

			tracer.addSubelement(e, "nb_schema_learned", m_nbSchemaLearned + "");
		}
	}
	
	public void trace(ITracer tracer) 
	{
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
				tracer.addEventElement("primitive_enacted_schema", m_enactedPrimitiveAct.getLabel());
				tracer.addEventElement("satisfaction", m_enactedPrimitiveAct.getEnactionValue()/10 + "");
			}
		}
	}

	public void addOngoingInteraction(IInteraction interaction) 
	{
		m_ongoingInteractions.add(interaction);
	}

	public ArrayList<IInteraction> getOngoingInteractions() 
	{
		return m_ongoingInteractions;
	}

}
