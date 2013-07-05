package eca.construct.egomem;

import javax.media.j3d.Transform3D;

/**
 * A transformation in spatial memory
 * @author Olivier
 */
public interface Displacement {

	/**
	 * @return The transformation's label.
	 */
	public String getLabel();
	
	/**
	 * @return The 3D transformation
	 */
	public Transform3D getTransform3D();

}
