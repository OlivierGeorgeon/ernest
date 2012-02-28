package utils;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

/**
 * Miscellaneous utilities for Ernest.
 * @author Olivier
 */
public final class ErnestUtils 
{
	/**
	 * Returns the polar angle of this vector.
	 * @param vector A vector.
	 * @return The polar angle of this vector.
	 */
	public static float polarAngle(Vector3f vector) 
	{
		return (float)Math.atan2((double)vector.y, (double)vector.x);
	}
	
	/**
	 * Rotate a vector around the z axis.
	 * @param vector A vector.
	 * @param angle The angle of rotation
	 */
	public static void rotate(Vector3f vector, float angle) 
	{
		Matrix3f rot = new Matrix3f();
		rot.rotZ(angle);		
		rot.transform(vector);
	}	

	
	/**
	 * @param value rgb integer value.
	 * @return The hexadecimal code.
	 */
	public static String hexColor(int value) 
	{
		int r = (value & 0xFF0000)/65536;
		int g = (value & 0x00FF00)/256;
		int b = (value & 0x0000FF);
		
		String s = format(r) + format(g) + format(b);

		return s;
	}
	
	private static String format(int i)
	{
		if (i == 0)
			return "00";
		else if (i < 16)
			return "0" + Integer.toString(i, 16).toUpperCase();
		else
			return Integer.toString(i, 16).toUpperCase();
	}

}
