package spas;

import javax.vecmath.Point3f;

import utils.ErnestUtils;

/**
 * Slices the surrounding space into left(A)/front(B)/right(C) areas.
 * @author Olivier
 */
public class Slicer implements ISlicer {

	public String slice(Point3f point) 
	{
		if (ErnestUtils.polarAngle(point) > .1f)
			return "A"; // Area A: left
		else if (ErnestUtils.polarAngle(point) >= -.1f)
			return "B"; // Area B: front
		else 
			return "C"; // Area C: right
	}
	
}
