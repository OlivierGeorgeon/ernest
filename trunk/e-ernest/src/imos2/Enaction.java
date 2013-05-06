package imos2;

import java.util.ArrayList;
import java.util.List;

import spas.Area;
import utils.ErnestUtils;
import ernest.Effect;
import ernest.IEffect;
import ernest.Primitive;
import ernest.ITracer;

/**
 * A structure used to handle the enaction of an interaction
 * or the simulation of the enaction of an interaction in memory.
 * @author ogeorgeon
 */
public class Enaction implements IEnaction 
{
	
	private Primitive enactedPrimitive = null;
	
	/** The intended primitive interaction */
	private Act m_intendedPrimitiveInteraction = null;
	/** The enacted primitive interaction */
	private Act m_enactedPrimitiveInteraction = null;
	/** The effect obtained from the enaction of the enacted primitive interaction */
	private IEffect m_effect = new Effect();
	/** The composite interaction being enacted */
	private Act m_topInteraction = null;
	/** The highest level composite interaction enacted thus far */
	private Act m_topEnactedInteraction = null;
	/** The highest remaining composite interaction */ 
	private Act m_topRemainingInteraction = null;
	/** The current step of this enaction*/
	private int m_step = 0;
	/** The previous learning context (the context of the stream enaction) */
	private ArrayList<Act> m_previousLearningContext   = new ArrayList<Act>();
	/** The learning context at the beginning of this enaction*/
	private ArrayList<Act> m_initialLearningContext   = new ArrayList<Act>();
	/** The learning context at the end of this enaction*/
	private ArrayList<Act> m_finalLearningContext   = new ArrayList<Act>();
	/** The activation context at the end of this enaction*/
	private ArrayList<Act> m_finalActivationContext = new ArrayList<Act>();
	/** Number of schema learned after this enaction*/
	private int m_nbSchemaLearned = 0;
	/** final status of this enaction (true correct, false incorrect) */
	private boolean m_correct = true;
	/** The area slice of the primitive enacted interaction */
	private Area area;
	
	//private ArrayList<IInteraction> m_ongoingInteractions = new ArrayList<IInteraction>();	
	//private int m_simulationStatus = 0;
	
	public void setEffect(IEffect effect) 
	{
		m_effect = effect;
	}

	public void setSlice(Area area)
	{
		this.area = area;
	}

	public Area getArea()
	{
		return this.area;
	}
	
	public IEffect getEffect() 
	{
		return m_effect;
	}

	public Primitive getEnactedPrimitive() 
	{
		return this.enactedPrimitive;
	}

	public void setEnactedPrimitive(Primitive enactedPrimitive) 
	{
		this.enactedPrimitive = enactedPrimitive;
	}

	public void setIntendedPrimitiveInteraction(Act act) 
	{
		m_intendedPrimitiveInteraction = act;
	}

	public Act getIntendedPrimitiveInteraction() 
	{
		return m_intendedPrimitiveInteraction;
	}

	public void setTopInteraction(Act act) 
	{
		m_topInteraction = act;
	}

	public Act getTopInteraction() 
	{
		return m_topInteraction;
	}
	
	public void setTopEnactedInteraction(Act act) 
	{
		m_topEnactedInteraction = act;
	}

	public Act getTopEnactedInteraction() 
	{
		return m_topEnactedInteraction;
	}
	
	public void setTopRemainingInteraction(Act act) 
	{
		m_topRemainingInteraction = act;
	}

	public Act getTopRemainingInteraction() 
	{
		return m_topRemainingInteraction;
	}
	
	public void setStep(int step)
	{
		m_step = step;
	}

	public int getStep()
	{
		return m_step;
	}

//	public void setSimulationStatus(int simulationStatus) 
//	{
//		m_simulationStatus = simulationStatus;
//	}

//	public int getSimulationStatus() 
//	{
//		return m_simulationStatus;
//	}

	public void setEnactedPrimitiveInteraction(Act act) 
	{
		m_enactedPrimitiveInteraction = act;
	}

	public Act getEnactedPrimitiveInteraction() 
	{
		return m_enactedPrimitiveInteraction;
	}
	
	public boolean isOver()
	{
		return (m_topRemainingInteraction == null);
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
	private void addContextList(List<Act> actList) 
	{
		for (Act act : actList)
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
	private void addActivationAct(Act act) 
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
	public void setFinalContext(Act enactedAct, Act performedAct, ArrayList<Act> contextList)
	{
		// The current context list becomes the base context list
		//m_baseContextList = new ArrayList<IAct>(m_contextList);
		
		// The enacted act is added first to the activation list
		addActivationAct(enactedAct); 

		// Add the performed act if different
		if (enactedAct != performedAct)
			addActivationAct(performedAct);

		// if the actually enacted act is not primitive, its intention also belongs to the context
		if (!enactedAct.isPrimitive())
			addActivationAct(enactedAct.getPostAct());	
		
		// add the streamcontext list to the context list
		addContextList(contextList);
	}
	
	public ArrayList<Act> getFinalLearningContext()
	{
		return m_finalLearningContext;
	}
	public ArrayList<Act> getFinalActivationContext()
	{
		return m_finalActivationContext;
	}
	public void setInitialLearningContext(ArrayList<Act> learningContext) 
	{
		m_initialLearningContext = learningContext;
	}

	public ArrayList<Act> getInitialLearningContext() 
	{
		return m_initialLearningContext;
	}
	public void setPreviousLearningContext(ArrayList<Act> learningContext) 
	{
		m_previousLearningContext = learningContext;
	}

	public ArrayList<Act> getPreviousLearningContext() 
	{
		return m_previousLearningContext;
	}

	public void setNbActLearned(int nbActLearned) 
	{
		m_nbSchemaLearned = nbActLearned;
	}

	public void traceTrack(ITracer tracer) 
	{
		if (tracer != null && m_intendedPrimitiveInteraction != null)
		{
			//tracer.addEventElement("primitive_enacted_act", m_enactedPrimitiveAct.getLabel());
			tracer.addEventElement("top_level", m_topInteraction.getLength() + "");
			tracer.addEventElement("satisfaction", m_enactedPrimitiveInteraction.getEnactionValue()/10 + "");
			tracer.addEventElement("primitive_enacted_schema", m_enactedPrimitiveInteraction.getLabel().substring(0, 1));

			m_effect.trace(tracer);
			
			Object e = tracer.addEventElement("track_enaction");
		
			tracer.addSubelement(e, "top_intention", m_topInteraction.getLabel());
			tracer.addSubelement(e, "top_enacted_interaction", m_topEnactedInteraction.getLabel());
			tracer.addSubelement(e, "step", m_step + "");
			tracer.addSubelement(e, "primitive_intended_interaction", m_intendedPrimitiveInteraction.getLabel());
			tracer.addSubelement(e, "primitive_enacted_interaction", this.enactedPrimitive.getLabel());
			tracer.addSubelement(e, "primitive_enacted_act", m_enactedPrimitiveInteraction.getLabel());
			tracer.addSubelement(e, "area", this.area.getLabel());
		}
	}

	public void traceCarry(ITracer tracer)
	{
		if (tracer != null)
		{
			Object e = tracer.addEventElement("carry_enaction");

			tracer.addSubelement(e, "top_intention", m_topInteraction.toString());
			tracer.addSubelement(e, "top_level", m_topInteraction.getLength() + "");
			if (m_topEnactedInteraction != null)
				tracer.addSubelement(e, "top_enacted", m_topEnactedInteraction.toString());
			tracer.addSubelement(e, "top_remaining", m_topRemainingInteraction.toString());
			tracer.addSubelement(e, "next_step", m_step + "");
			tracer.addSubelement(e, "next_primitive_intended_act", m_intendedPrimitiveInteraction.toString());
		}
	}
	
	public void traceTerminate(ITracer tracer)
	{
		if (tracer != null)
		{
			Object e = tracer.addEventElement("terminate_enaction");
			
			if (!m_correct) tracer.addSubelement(e, "incorrect");
			
			Object activation = tracer.addSubelement(e, "activation_context");
			for (Act i : m_finalActivationContext)	
			{
				System.out.println("Activation context " + i);
				tracer.addSubelement(activation, "interaction", i.getLabel());
			}
			Object learning = tracer.addSubelement(e, "learning_context");
			for (Act i : m_finalLearningContext)	
				tracer.addSubelement(learning, "interaction", i.getLabel());

			tracer.addSubelement(e, "nb_schema_learned", m_nbSchemaLearned + "");
		}
	}	
}
