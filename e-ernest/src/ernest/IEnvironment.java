package ernest;

import javax.media.j3d.Transform3D;

import tracing.ITracer;
import eca.ActInstance;
import eca.Primitive;

/**
 * Interface for an Environment suitable to Ernest.
 * @author mcohen
 * @author ogeorgeon
 */
public interface IEnvironment 
{
	/**
	 * Enact a primitive schema and return the enaction status.
	 * @param primitive The primitive schema that Ernest has chosen to enact.
	 * @return The effect that results from the enaction of the primitive schema in the environment.
	 */
	public ActInstance enact(Primitive primitive);
	
	/**
	 * Intialize Ernest's possibilities of interaction.
	 * @param ernest The Ernest agent.
	 */
	public void initErnest(IErnest ernest);
	
	/**
	 * Trace the environment's state.
	 * @param tracer the tracer.
	 */
	public void trace(ITracer tracer);
	
	public Transform3D getTransformation();
}
