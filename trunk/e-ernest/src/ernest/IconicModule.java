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
	
	/** The iconic buffer. */
	private IAct m_icon;
	
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
	 */
	public void processMatrix(int[][] matrix) 
	{
		IIcon icon =  new Icon(matrix);

		int i = m_icons.indexOf(icon);
		if (i == -1)
			// The act does not exist
			m_icons.add(icon);
		else 
			// The schema already exists: return a pointer to it.
			icon =  m_icons.get(i);
		
		m_icon = icon;
	}

	/**
	 * Update the iconic elements of the context
	 * TODO Determines when the percieved icon should not be sent to the central system.
	 * @return the current icon in the iconic module.
	 */
	public IContext updateContext(IContext context) 
	{
		context.removeIcons();
		if (m_icon != null ) 
			context.addActivationAct(m_icon);
		return context; 
	}
	
}
