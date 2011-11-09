package spas;

import javax.vecmath.Vector3f;

/**
 * A place is a location in the local space that holds a placeable item (stimulation, salience, or bundle).
 * @author Olivier
 */
public interface IPlace {
	
	/**
	 * @return The location's bundle.
	 */
	public IBundle getBundle();
	
	/**
	 * Allocate a bundle to this place.
	 * @param bundle The bundle at this place.
	 */
	public void setBundle(IBundle bundle);

	/**
	 * @return The location's position.
	 */
	public Vector3f getPosition();
	
	/**
	 * Rotate this location with regard to the agent's center.
	 * @param angle The rotation angle.
	 */
	public void rotate(float angle);

	/**
	 * Translate this location along the agent's axis.
	 * @param distance The translation distance.
	 */
	public void translate(float distance);

	/**
	 * Test if this place is at this position.
	 * @param position The position to test
	 * @return true if this place is in the same cell as thi position.
	 */
	public boolean isInCell(Vector3f position);

}
