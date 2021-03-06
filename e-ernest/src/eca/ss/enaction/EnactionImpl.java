package eca.ss.enaction;

import java.util.ArrayList;
import java.util.List;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import tracing.ITracer;
import eca.ActInstance;
import eca.ActInstanceImpl;
import eca.Primitive;
import eca.PrimitiveImpl;
import eca.construct.Action;
import eca.construct.Appearance;
import eca.construct.AppearanceImpl;
import eca.construct.Displacement;
import eca.construct.DisplacementImpl;
import eca.construct.egomem.Area;
import eca.construct.egomem.AreaImpl;
import eca.construct.egomem.PhenomenonInstance;
import eca.construct.experiment.Experiment;
import ernest.Effect;

/**
 * A structure used to handle the enaction of an interaction
 * or the simulation of the enaction of an interaction in memory.
 * @author ogeorgeon
 */
public class EnactionImpl implements Enaction 
{
	private Action intendedAction;
	
	/** The intended primitive interaction */
	private Act m_intendedPrimitiveAct = null;
	
	/** The enacted primitive interaction */
	private Act m_enactedPrimitiveAct = null;
	
	/** The composite interaction being enacted */
	private Act m_topAct = null;

	/** The highest level composite interaction enacted thus far */
	private Act m_topEnactedAct = null;

	/** The highest remaining composite interaction */ 
	private Act m_topRemainingAct = null;

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
	
	private Transform3D transformation = new Transform3D();
	
	//private Area initialArea = AreaImpl.createOrGet(new Point3f());
	
	private List<Appearance> appearances = new ArrayList<Appearance>(0);
	private Area area = null;
	private Experiment experiment = null;

	private Displacement displacement = null;
	
	private List<ActInstance> actInstances = new ArrayList<ActInstance>();
	
	private ActInstance salientActInstance;
	
	public void setIntendedPrimitiveAct(Act act) 
	{
		m_intendedPrimitiveAct = act;
	}

	public Act getIntendedPrimitiveAct() 
	{
		return m_intendedPrimitiveAct;
	}

	public void setTopIntendedAct(Act act) 
	{
		m_topAct = act;
	}

	public Act getTopAct() 
	{
		return m_topAct;
	}
	
	public void setTopEnactedAct(Act act) 
	{
		m_topEnactedAct = act;
	}

	public Act getTopEnactedAct() 
	{
		return m_topEnactedAct;
	}
	
	public void setTopRemainingAct(Act act) 
	{
		m_topRemainingAct = act;
	}

	public Act getTopRemainingAct() 
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

	public void setEnactedPrimitiveAct(Act act) 
	{
		m_enactedPrimitiveAct = act;
	}

	public Act getEnactedPrimitiveAct() 
	{
		return m_enactedPrimitiveAct;
	}
	
	public boolean isOver()
	{
		return (m_topRemainingAct == null);
	}
	
	public void setSuccessful(boolean correct) 
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
		if (tracer != null && m_intendedPrimitiveAct != null)
		{
			tracer.addEventElement("top_level", m_topAct.getLength() + "");
			tracer.addEventElement("satisfaction", m_enactedPrimitiveAct.getEnactionValue()/10 + "");
			tracer.addEventElement("primitive_enacted_schema", m_enactedPrimitiveAct.getLabel().substring(0, 1));
			
			Object e = tracer.addEventElement("track_enaction");		
			tracer.addSubelement(e, "top_intention", m_topAct.getLabel());
			tracer.addSubelement(e, "top_enacted_interaction", m_topEnactedAct.getLabel());
			tracer.addSubelement(e, "step", m_step + "");
			tracer.addSubelement(e, "primitive_intended_interaction", m_intendedPrimitiveAct.getLabel());
			tracer.addSubelement(e, "primitive_enacted_interaction", m_enactedPrimitiveAct.getPrimitive().getLabel());
			tracer.addSubelement(e, "primitive_enacted_act", m_enactedPrimitiveAct.getLabel());
			tracer.addSubelement(e, "area", m_enactedPrimitiveAct.getArea().getLabel());
			tracer.addSubelement(e, "intended_action", this.intendedAction.getLabel());
			tracer.addSubelement(e, "aspect", this.salientActInstance.getAspect().toString());
			tracer.addSubelement(e, "displacement", m_enactedPrimitiveAct.getPrimitive().getDisplacement().getLabel());
			if (!this.appearances.isEmpty())
				for (Appearance appearance : this.appearances)
					appearance.trace(tracer, e);
		}
	}

	public void traceCarry(ITracer tracer)
	{
		if (tracer != null)
		{
			Object e = tracer.addEventElement("carry_enaction");

			tracer.addSubelement(e, "top_intention", m_topAct.toString());
			tracer.addSubelement(e, "top_level", m_topAct.getLength() + "");
			if (m_topEnactedAct != null)
				tracer.addSubelement(e, "top_enacted", m_topEnactedAct.toString());
			tracer.addSubelement(e, "top_remaining", m_topRemainingAct.toString());
			tracer.addSubelement(e, "next_step", m_step + "");
			tracer.addSubelement(e, "next_primitive_intended_act", m_intendedPrimitiveAct.toString());
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

			if (!this.appearances.isEmpty())
				for (Appearance appearance : this.appearances)
					tracer.addSubelement(e, "observation", appearance.getLabel());
					//appearance.trace(tracer, e);

			//tracer.addSubelement(e, "nb_schema_learned", m_nbSchemaLearned + "");	
		}
	}

//	public void setDisplacement(Displacement displacement) {
//		this.displacement = displacement;
//	}
//
//	public Displacement getDisplacement() {
//		return this.displacement;
//	}

	public void track(Effect input) {
		
		Transform3D t = input.getTransformation();
		Primitive p = PrimitiveImpl.get(">_");
		Point3f l = new Point3f();
		
		if (this.m_intendedPrimitiveAct != null){
			// If we are not on startup
			// Compute the enacted primitive act from the primitive interaction and the area.
			p = PrimitiveImpl.get(input.getEnactedInteractionLabel());
			l.set(input.getLocation());
		}

		ActInstance enactedPlace = new ActInstanceImpl(p, l);
		List<ActInstance> actInstances =  new ArrayList<ActInstance>(1);
		actInstances.add(enactedPlace);
		track(actInstances, t, null);
	}	
	
	public void track(List<ActInstance> actInstances, Transform3D transform, PhenomenonInstance focusPhenomenonInstance){
		
		this.actInstances = actInstances;
		this.displacement = DisplacementImpl.createOrGet(transform);
		this.transformation.set(transform);
		
		this.salientActInstance  = null;
		
		if (actInstances.size() > 0){
			this.salientActInstance  = actInstances.get(0);
			
			if (focusPhenomenonInstance != null){
				Point3f anticipatedPhenomenonInstancePosition = new Point3f(focusPhenomenonInstance.getPosition());
				transform.transform(anticipatedPhenomenonInstancePosition);
				for (ActInstance actInstance : actInstances)
					if (actInstance.getPosition().epsilonEquals(anticipatedPhenomenonInstancePosition, .1f))
						this.salientActInstance = actInstance;
			}
			for (ActInstance actInstance : actInstances)
				if (actInstance.getModality() == ActInstance.MODALITY_CONSUME)
					this.salientActInstance = actInstance;
		
			this.m_enactedPrimitiveAct =this.salientActInstance.getAct();
			this.area = this.salientActInstance.getArea();
			this.m_enactedPrimitiveAct.setArea(this.area);
			this.m_enactedPrimitiveAct.setColor(this.salientActInstance.getDisplayCode());
			//this.m_enactedPrimitiveAct.getPrimitive().incDisplacementCounter(displacement);
		}
	}
	
	public List<ActInstance> getEnactedPlaces(){
		return this.actInstances;
	}

	public Action getIntendedAction() {
		return intendedAction;
	}

	public void setIntendedAction(Action intendedAction) {
		this.intendedAction = intendedAction;
	}

//	public Area getInitialArea() {
//		return initialArea;
//	}
//
//	public void setInitialArea(Area area) {
//		this.initialArea = area;
//	}

	public List<Appearance> getAppearances() {
		return this.appearances;
	}

	public void setAppearances(List<Appearance> appearances) {
		this.appearances = appearances;
	}

	public Transform3D getTransform3D() {
		return this.transformation;
	}

	public ActInstance getSalientActInstance() {
		return this.salientActInstance;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment anticipatedAppearance) {
		this.experiment = anticipatedAppearance;
	}

	public Displacement getDisplacement() {
		return displacement;
	}


}
