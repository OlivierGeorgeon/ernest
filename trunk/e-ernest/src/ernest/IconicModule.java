package ernest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implement Ernest's skills to process icons.
 * @author ogeorgeon
 */
public class IconicModule
{

	/** Used to break a tie when evoking an icon... */
	private static Random m_rand = new Random(); 
 
	/** A list of all the icons in the iconic module. */
	private List<IIcon> m_icons = new ArrayList<IIcon>(100);
	
	/** The icon currently sensed. */
	private IAct m_sensedIcon;
	
	/**
	 * Create an icon if it does not yet exist. 
	 * @param label A string label defined by the modeler for trace and debug.
	 * @param matrix The sensor matrix. 
	 * @return The iconic noème that was created or retrieved
	 */
	public IAct addInteraction(String label, int[][] matrix) 
	{
		IIcon icon =  new Icon(label, matrix);

		int i = m_icons.indexOf(icon);
		if (i == -1)
			// The icon does not exist
			m_icons.add(icon);
		else 
			// The icon already exists: return a pointer to it.
			icon =  m_icons.get(i);
		return icon;
	}

	/**
	 * Convert the matrix provided by the environment into an icon.
	 * Update the currently sensed icon. 
	 * @param matrix The matrix sensed in the environment. 
	 */
	public void senseMatrix(int[][] matrix) 
	{
		IIcon icon =  new Icon(matrix);

		int i = m_icons.indexOf(icon);
		if (i == -1)
			// The icon does not exist
			m_icons.add(icon);
		else 
			// The icon already exists: return a pointer to it.
			icon =  m_icons.get(i);
		
		m_sensedIcon = icon;
	}

	/**
	 * @return The icon currently sensed
	 */
	public IAct getSensedIcon() 
	{
		return m_sensedIcon;
	}
	
	/**
	 * Update the context with the currently sensed icon.
	 * TODO Determines when the sensed icon should not be sent to the central system.
	 * @param context The current context to update
	 * @return the updated context.
	 */
	public IContext updateContext(IContext context) 
	{
		context.removeIcons();
		if (m_sensedIcon != null ) 
			context.addSensedIcon(m_sensedIcon);
		return context; 
	}
	
	/**
	 * Check whether the sensed icon was that expected or not
	 * @param context The current context to update
	 * @param schemas The list of schemas that can invoke an icon from the input context.
	 * @return True if expected, false if not expected.
	 */
	public boolean checkIcon(IContext context, List<ISchema> schemas)
	{
		// Evoke an icon from the current context
		IAct EvokedIcon = evokeIcon(context.getAnimationNoeme(), schemas);
		if (m_sensedIcon.equals(EvokedIcon))
		{
			System.out.println("Expected Icon " + EvokedIcon );
			return true;
		}
		else
		{
			System.out.println("Unexpected Icon! Evoked " + EvokedIcon +" but sensed " + m_sensedIcon);
			return false;
		}
	}
	
	/**
	 * Evoke an icon in a specific context.
	 * @param contextNoeme The context that generates the proposals.
	 * @param schemas The list of schemas capable of evoking the icon.
	 * @return The evoked icon.
	 */
	public IAct evokeIcon(IAct contextNoeme, List<ISchema> schemas)
	{

		List<IActivation> activations = new ArrayList<IActivation>();	
		
		// Browse all the schemas in the provided list
		for (ISchema s : schemas)
		{
			if (!s.isPrimitive())
			{
				// Activate the schemas that match the context 
				boolean activated = false;
				//for (IAct contextAct : contextList)
				//{
					if ((s.getModule() == Ernest.ICONIC) && (s.getContextAct().equals(contextNoeme)))
					{
						activated = true;
						// System.out.println("Activate " + s);
					}
				//}
				
				// Activated schemas propose their intention
				if (activated)
				{
					// The weight is the proposing schema's weight 
					int w = s.getWeight();
					
					// If the intention is a iconic noème. 
					if (s.getIntentionAct().getModule() == Ernest.ICONIC)
					{
						IActivation a = new Activation(s.getIntentionAct(), w);
	
						int i = activations.indexOf(a);
						if (i == -1)
							activations.add(a);
						else
							activations.get(i).update(w);
					}
				}
			}
		}

		// System.out.println("Activate icons: ");
		// for (IProposition p : proposals)
		//	System.out.println(p);
		
		// If the proposal list is not empty
		
		if (activations.size() > 0)
		{
			// sort by weighted proposition...
			Collections.sort(activations);
			
			// count how many are tied with the  highest weighted proposition
			int count = 0;
			int wp = activations.get(0).getWeight();
			for (IActivation a : activations)
			{
				if (a.getWeight() != wp)
					break;
				count++;
			}
	
			// pick one at random from the top of the proposal list
			// count is equal to the number of proposals that are tied...
	
			IActivation a = activations.get(m_rand.nextInt(count));
			System.out.println("Activate: " + a);
			
			return a.getNoeme() ;
		}
		else
			return null;
		
	}
}
