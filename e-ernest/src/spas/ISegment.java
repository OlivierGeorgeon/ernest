package spas;

import javax.vecmath.Vector3f;

/**
 * A segment provided by the visual system implemented in VacuumSG.
 */
public interface ISegment 
{
	/**
	 * @return The segment's median position relatively to Ernest.
	 */
	public Vector3f getPosition();
	
	/**
	 * @return The segment's median speed relatively to Ernest.
	 */
	public Vector3f getSpeed();
	
	/**
	 * @return The RGB color code of this segment.
	 */
	public int getValue();
	
	/**
	 * @return The position of the first point of this segment relatively to Ernest.
	 */
	public Vector3f getFirstPosition();
	
	/**
	 * @return The position of the second point of this segment relatively to Ernest.
	 */
	public Vector3f getSecondPosition();
	
	/**
	 * @return The span of this segment from Ernest's viewpoint.
	 */
	public float getSpan();
	
	public float getWidth();
	
	public Vector3f getFirstPositionAllocentric(); 

	public Vector3f getSecondPositionAllocentric(); 
	
	public float getAbsoluteOrientation();

}
