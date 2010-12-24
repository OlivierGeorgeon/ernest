package ernest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implement Ernest's homeostatic proclivities
 * @author Olivier
 */
public class HomeostaticModule 
{
	/** Used to break a tie when selecting a schema... */
	private static Random m_rand = new Random(); 

	/**
	 * Activate a homeostatic noème in a specific context.
	 * @param context The context that generates the proposals.
	 * @param schemas The list of schemas capable of activating noèmes.
	 * @return The activated homeostatic noème.
	 */
	public IAct activateNoeme(IContext context, List<ISchema> schemas)
	{

		List<IProposition> proposals = new ArrayList<IProposition>();	
		
		// Browse all the schemas 
		for (ISchema s : schemas)
		{
			if (!s.isPrimitive())
			{
				// Activate the schemas that match the context 
				boolean activated = false;
				for (IAct contextAct : context.getFocusList())
				{
					if (s.getContextAct().equals(contextAct))
					{
						activated = true;
						// System.out.println("Activate " + s);
					}
				}
				
				// Activated schemas propose their intention
				if (activated)
				{
					// The weight is the proposing schema's weight multiplied by the proposed act's satisfaction
					int w = s.getWeight() * s.getIntentionAct().getSatisfaction();
					
					// If the intention is a homeostatic noème. 
					if (s.getIntentionAct().getModule() == Ernest.HOMEOSTATIC)
					{
						IProposition p = new Proposition(s.getIntentionAct().getSchema(), w, 1);
	
						int i = proposals.indexOf(p);
						if (i == -1)
							proposals.add(p);
						else
							proposals.get(i).update(w, 1);
					}
				}
			}
		}

		// System.out.println("Propose homeostatic: ");
		// for (IProposition p : proposals)
		//	System.out.println(p);
		
		// If the proposal list is not empty
		
		if (proposals.size() > 0)
		{
			// sort by weighted proposition...
			Collections.sort(proposals);
			
			// count how many are tied with the  highest weighted proposition
			int count = 0;
			int wp = proposals.get(0).getWeight();
			for (IProposition p : proposals)
			{
				if (p.getWeight() != wp)
					break;
				count++;
			}
	
			// pick one at random from the top of the proposal list
			// count is equal to the number of proposals that are tied...
	
			IProposition p = proposals.get(m_rand.nextInt(count));
			
			ISchema s = p.getSchema();
			
			IAct a = (p.getExpectation() >= 0 ? s.getSucceedingAct() : s.getFailingAct());
			
			System.out.println("Select:" + a);
			return a ;
		}
		else
			return null;
		
	}

	/**
	 * Homeostatic process.
	 * @return The homeostatic noème to enact if any.
	 */
	public IAct step(IContext context) 
	{
			return null;
	}

}
