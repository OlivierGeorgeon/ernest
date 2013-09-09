package eca.spas;

import java.util.ArrayList;
import javax.vecmath.Point3f;
import tracing.ITracer;
import eca.construct.PhenomenonInstance;
import eca.ss.enaction.Enaction;

/**
 * The spatial system.
 * @author Olivier
 */
public interface Spas 
{

	/**
	 * @param tracer The tracer
	 */
	public void setTracer(ITracer tracer);
	
	/**
	 * The main routine of the Spatial System that is called on each interaction cycle.
	 * @param enaction The current enaction.
	 */
	public void track(Enaction enaction);

	/**
	 * @return The list of places in Ernest's local space memory.
	 */
	public ArrayList<Placeable> getPlaceList();
	
	/**
	 * @param position The position.
	 * @return The value to display at this position
	 */
	public int getValue(Point3f position);
	
	/**
	 * Provide a rgb code to display the local space map in the environment.
	 * @param i x coordinate.
	 * @param j y coordinate.
	 * @return The value of the bundle in this place in local space memory.
	 */
	public int getValue(int i, int j);
	
	/**
	 * @return the Phenomenon Instance which get the attention of Ernest 
	 */
	public PhenomenonInstance getFocusPhenomenon();

}
