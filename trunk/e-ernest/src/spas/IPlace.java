package spas;

import javax.vecmath.Vector3f;

/**
 * A location is a place in the local space that has been associated with a bundle.
 * @author Olivier
 */
public interface IPlace {
	
	/**
	 * @return The location's bundle.
	 */
	public IBundle getBundle();
	
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

	public boolean isInCell(Vector3f position);

}
