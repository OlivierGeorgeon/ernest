package eca.ss.enaction;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Transform3D;

import tracing.ITracer;

import eca.construct.Action;
import eca.construct.PhenomenonInstance;
import eca.construct.egomem.Displacement;
import eca.spas.egomem.Place;
import eca.ss.Appearance;
import ernest.IEffect;

/**
 * A structure used to manage the enaction of a scheme in the real world
 * or the simulation of the enaction of a scheme in spatial memory.
 * @author ogeorgeon
 */
public interface Enaction 
{
	/**
	 * Track the current enaction based on Ernest's input.
	 * @param input Ernest's input.
	 */
	public void track(IEffect input);
	
	/**
	 * Track the current enaction based on the list of places received from the environment
	 * @param places The list of places received from the environment
	 * @param transform The transformation sensed in the environment.
	 */
	public void track(List<Place> places, Transform3D transform);
	
	/**
	 * @param displacement The displacement sensed during this primitive enaction.
	 */
	public void setDisplacement(Displacement displacement);
	
	/**
	 * @return The displacement sensed during this primitive enaction.
	 */
	public Displacement getDisplacement();
	
	/**
	 * @param appearance The Appearance sensed during this primitive enaction.
	 */
	public void setPhenomenonInstance(PhenomenonInstance phenomenonInstance);
	
	/**
	 * @return The Appearance sensed during this primitive enaction.
	 */
	public PhenomenonInstance getPhenomenonInstance();
	
	/**
	 * @param act The last primitive intended interaction
	 */
	public void setIntendedPrimitiveAct(Act act);
	
	/**
	 * @return The last primitive intended interaction
	 */
	public Act getIntendedPrimitiveAct();	

	/**
	 * @param act The last primitive enacted interaction
	 */
	public void setEnactedPrimitiveAct(Act act);
	
	/**
	 * @return The last primitive enacted interaction
	 */
	public Act getEnactedPrimitiveAct();	

	/**
	 * @param act The composite interaction to be enacted
	 */
	public void setTopIntendedAct(Act act);
	
	/**
	 * @return The composite interaction to be enacted
	 */
	public Act getTopAct();	

	/**
	 * @param act The highest-level composite interaction enacted thus far.
	 */
	public void setTopEnactedAct(Act act);
	
	/**
	 * @return The highest-level composite interaction enacted thus far.
	 */
	public Act getTopEnactedAct();	

	/**
	 * @param act The remaining highest-level composite interaction to enact.
	 */
	public void setTopRemainingAct(Act act);
	
	/**
	 * @return The remaining highest-level composite interaction to enact.
	 */
	public Act getTopRemainingAct();	

	/**
	 * @param step The rank of the primitive act in the current enaction 
	 */
	public void setStep(int step);

	/**
	 * @return The rank of the primitive act in the current enaction 
	 */
	public int getStep();
	
	/**
	 * @param successful false if the top intention was not correctly enacted
	 */
	public void setSuccessful(boolean successful);
	
	/**
	 * @return True if this enaction is terminated.
	 */
	public boolean isOver();
	
	/**
	 * @param enactedAct The enacted act.
	 * @param performedAct The performed act.
	 * @param contextList The context List.
	 */
	public void setFinalContext(Act enactedAct, Act performedAct, ArrayList<Act> contextList);
	
	/**
	 * @return
	 */
	public ArrayList<Act> getFinalLearningContext();
	
	/**
	 * @return
	 */
	public ArrayList<Act> getFinalActivationContext();
	
	/**
	 * @param learningContext
	 */
	public void setInitialLearningContext(ArrayList<Act> learningContext);
	
	/**
	 * @return
	 */
	public ArrayList<Act>  getInitialLearningContext();
	
	/**
	 * @param learningContext
	 */
	public void setPreviousLearningContext(ArrayList<Act> learningContext);
	
	/**
	 * @return
	 */
	public ArrayList<Act>  getPreviousLearningContext();
	
	/**
	 * @param nbActLearned
	 */
	public void setNbActLearned(int nbActLearned);
	

	/**
	 * Trace the tracking of the enaction (just after a primitive interaction being enacted)
	 * @param tracer The tracer
	 */
	public void traceTrack(ITracer tracer);
	
	/**
	 * Trace the carrying out of an enaction (just before the next intended primitive interaction)
	 * @param tracer The tracer
	 */
	public void traceCarry(ITracer tracer);

	/**
	 * Trace the termination of an enaction
	 * @param tracer The tracer
	 */
	public void traceTerminate(ITracer tracer);
	
	/**
	 * @return The list of places enacted during the last interaction cycle.
	 */
	public List<Place> getEnactedPlaces();
	
	public void setIntendedAction(Action action);
	public Action getIntendedAction();
}
