package spas;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;

import utils.ErnestUtils;

/**
 * Categorize the surrounding space into areas left(A)/front(B)/right(C).
 * @author Olivier
 */
public class PositionCategorizer implements IPositionCategorizer {
	
	private Map<String , IArea> areas = new HashMap<String , IArea>() ;
	
	public IArea categorize(Point3f point) 
	{
		if (ErnestUtils.polarAngle(point) > .1f)
		{
			// Area A: left
			if (!this.areas.containsKey("A"))
				this.areas.put("A", new Area("A"));
			return areas.get("A"); 
		}
		else if (ErnestUtils.polarAngle(point) >= -.1f)
		{
			// Area B: front
			if (!this.areas.containsKey("B"))
				this.areas.put("B", new Area("B"));
			return areas.get("B"); 
		}
		else
		{
			// Area C: right
			if (!this.areas.containsKey("C"))
				this.areas.put("C", new Area("C"));
			return areas.get("C"); 
		}
	}
	
}
