package eca.construct.egomem;

import javax.media.j3d.Transform3D;

/**
 * A transformation in spatial memory
 * @author Olivier
 */
public interface Transformation {

	/**
	 * @return The transformation's label.
	 */
	public String getLabel();
	
	public void setTransform3D(Transform3D transform3D);
	
	public Transform3D getTransform3D();

}
