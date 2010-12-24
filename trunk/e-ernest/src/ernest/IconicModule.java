package ernest;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement Ernest's skills to process icons.
 * @author ogeorgeon
 */
public class IconicModule
{

	/** The label of the noème that represents being on a blue square . */
	public static final String BLUE_SQUARE_LABEL = "[0,0]";

	/** A list of all the icons in the iconic module. */
	private List<IIcon> m_icons = new ArrayList<IIcon>(100);
	
	/** The icon currently sensed by Ernest. */
	IIcon m_sensorIcon = null;

	
	/**
	 * Reset the sensory module by clearing all of its long-term memory.
	 */
	public void clear() 
	{
		m_icons.clear();
	}

	/**
	 * Create a primitive iconic noème if it does not yet exist. 
	 * @param label A string label defined by the modeler for trace and debug.
	 * @param matrix The sensor matrix. 
	 * @return The iconic noème that was created or retrieved
	 */
	public IAct addInteraction(String label, int[][] matrix) 
	{
		IIcon icon =  new Icon(label, matrix);

		int i = m_icons.indexOf(icon);
		if (i == -1)
			// The act does not exist
			m_icons.add(icon);
		else 
			// The schema already exists: return a pointer to it.
			icon =  m_icons.get(i);
		return icon;
	}

	/**
	 * Create a primitive iconic noème if it does not yet exist. 
	 * Set this icon in the visual system current state 
	 * @param matrix The sensor matrix. 
	 * @return The iconic noème that was created or retrieved
	 */
	public IAct addInteraction(int[][] matrix) 
	{
		IIcon icon =  new Icon(matrix);

		int i = m_icons.indexOf(icon);
		if (i == -1)
			// The act does not exist
			m_icons.add(icon);
		else 
			// The schema already exists: return a pointer to it.
			icon =  m_icons.get(i);
		
		m_sensorIcon = icon;
		return icon;
	}

	/**
	 * Proposed iconic noème to push into Ernest's situation awareness.
	 * @return The iconic noème to push to the central context.
	 */
	public IAct step() 
	{
		if ((m_sensorIcon != null) && (m_sensorIcon.toString().equals(BLUE_SQUARE_LABEL) ))
			return m_sensorIcon;
		else
			return null;
	}

}
