package utils;

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
	 * @param value rgb integer value.
	 * @return The hexadecimal code.
	 */
	public static String hexColor(int value) 
	{
		int r = value/65536;
		int g = (value - r * 65536)/256;
		int b = value - r * 65536 - g * 256;
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
