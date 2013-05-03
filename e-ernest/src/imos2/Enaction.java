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
	private IAct m_intendedPrimitiveInteraction = null;
	/** The enacted primitive interaction */
	private IAct m_enactedPrimitiveInteraction = null;
	/** The effect obtained from the enaction of the enacted primitive interaction */
	private IEffect m_effect = new Effect();
	/** The composite interaction being enacted */
	private IAct m_topInteraction = null;
	/** The highest level composite interaction enacted thus far */
	private IAct m_topEnactedInteraction = null;
	/** The highest remaining composite interaction */ 
	private IAct m_topRemainingInteraction = null;
	/** The current step of this enaction*/
	private int m_step = 0;
	/** The previous learning context (the context of the stream enaction) */
	private ArrayList<IAct> m_previousLearningContext   = new ArrayList<IAct>();
	/** The learning context at the beginning of this enaction*/
	private ArrayList<IAct> m_initialLearningContext   = new ArrayList<IAct>();
	/** The learning context at the end of this enaction*/
	private ArrayList<IAct> m_finalLearningContext   = new ArrayList<IAct>();
	/** The activation context at the end of this enaction*/
	private ArrayList<IAct> m_finalActivationContext = new ArrayList<IAct>();
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

	public void setIntendedPrimitiveInteraction(IAct act) 
	{
		m_intendedPrimitiveInteraction = act;
	}

	public IAct getIntendedPrimitiveInteraction() 
	{
		return m_intendedPrimitiveInteraction;
	}

	public void setTopInteraction(IAct act) 
	{
		m_topInteraction = act;
	}

	public IAct getTopInteraction() 
	{
		return m_topInteraction;
	}
	
	public void setTopEnactedInteraction(IAct act) 
	{
		m_topEnactedInteraction = act;
	}

	public IAct getTopEnactedInteraction() 
	{
		return m_topEnactedInteraction;
	}
	
	public void setTopRemainingInteraction(IAct act) 
	{
		m_topRemainingInteraction = act;
	}

	public IAct getTopRemainingInteraction() 
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

	public void setEnactedPrimitiveInteraction(IAct act) 
	{
		m_enactedPrimitiveInteraction = act;
	}

	public IAct getEnactedPrimitiveInteraction() 
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
	private void addContextList(List<IAct> actList) 
	{
		for (IAct act : actList)
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
	private void addActivationAct(IAct act) 
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
	public void setFinalContext(IAct enactedAct, IAct performedAct, ArrayList<IAct> contextList)
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
			addActivationAct(enactedAct.getPostAct());	
		
		// add the streamcontext list to the context list
		addContextList(contextList);
	}
	
	public ArrayList<IAct> getFinalLearningContext()
	{
		return m_finalLearningContext;
	}
	public ArrayList<IAct> getFinalActivationContext()
	{
		return m_finalActivationContext;
	}
	public void setInitialLearningContext(ArrayList<IAct> learningContext) 
	{
		m_initialLearningContext = learningContext;
	}

	public ArrayList<IAct> getInitialLearningContext() 
	{
		return m_initialLearningContext;
	}
	public void setPreviousLearningContext(ArrayList<IAct> learningContext) 
	{
		m_previousLearningContext = learningContext;
	}

	public ArrayList<IAct> getPreviousLearningContext() 
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
			for (IAct i : m_finalActivationContext)	
			{
				System.out.println("Activation context " + i);
				tracer.addSubelement(activation, "interaction", i.getLabel());
			}
			Object learning = tracer.addSubelement(e, "learning_context");
			for (IAct i : m_finalLearningContext)	
				tracer.addSubelement(learning, "interaction", i.getLabel());

			tracer.addSubelement(e, "nb_schema_learned", m_nbSchemaLearned + "");
		}
	}	
}
