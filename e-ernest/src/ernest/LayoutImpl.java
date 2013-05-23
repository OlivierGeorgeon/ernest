package ernest;

import java.util.HashMap;
import java.util.Map;
import spas.Area;
import spas.AreaImpl;
import spas.SimuImpl;
import spas.Transformation;

public class LayoutImpl implements Layout {
	
	private static Map<String , Layout> LAYOUTS = new HashMap<String , Layout>() ;

	private String label;
	private Map<Area , Aspect> aspects = new HashMap<Area , Aspect>() ;

	/**
	 * @param aspectA The aspect in area A
	 * @param aspectB The aspect in area B
	 * @param aspectC The aspect in area C
	 * @return The aspect
	 */
	public static Layout createOrGet(Aspect aspectA, Aspect aspectB, Aspect aspectC){
		String key = createPrimitiveKey(aspectA, aspectB, aspectC);
		if (!LAYOUTS.containsKey(key))
			LAYOUTS.put(key, new LayoutImpl(aspectA, aspectB, aspectC));			
		return LAYOUTS.get(key);
	}
	
	private static String createPrimitiveKey(Aspect aspectA, Aspect aspectB, Aspect aspectC) {
		String key = aspectA.getLabel() + "-" + aspectB.getLabel() + "-" + aspectC.getLabel();
		return key;
	}
	
	/**
	 * @param layout The origin layout
	 * @param transformation The transformation
	 * @return The transformed layout
	 */
	public static Layout transform(Layout layout, Transformation transformation){
		Layout transformedLayout = layout;
		if (transformation.equals(SimuImpl.SHIFT_LEFT))
			transformedLayout = createOrGet(layout.getAspect(SimuImpl.B),layout.getAspect(SimuImpl.C), SimuImpl.NONE);
		if (transformation.equals(SimuImpl.SHIFT_RIGHT))
			transformedLayout = createOrGet(SimuImpl.NONE, layout.getAspect(SimuImpl.A),layout.getAspect(SimuImpl.B));
		return transformedLayout;
	}
	
	private LayoutImpl(Aspect aspectA, Aspect aspectB, Aspect aspectC){
		this.label = createPrimitiveKey(aspectA, aspectB, aspectC);
		aspects.put(SimuImpl.A, aspectA);
		aspects.put(SimuImpl.B, aspectB);
		aspects.put(SimuImpl.C, aspectC);	
		aspects.put(SimuImpl.O, SimuImpl.NONE);
	}

	public Aspect getAspect(Area area) {
		return aspects.get(area);
	}

	public boolean isEmpty(Area area) {
		return this.aspects.get(area).equals(SimuImpl.NONE);
	}

	public String getLabel() {
		return this.label;
	}
	
	public Observation observe(){
		Area area = SimuImpl.O;
		for (Area a : AreaImpl.getAREAS())
			if (!this.aspects.get(a).equals(SimuImpl.NONE))
				area = a;
		Aspect aspect = getAspect(area);
		return ObservationImpl.createOrGet(aspect, area);
	}
	
	/**
	 * Layouts are equal if they have the same label. 
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			Layout other = (Layout)o;
			ret = (other.getLabel().equals(this.label));
		}
		return ret;
	}

	public boolean isEmpty() {
		return isEmpty(SimuImpl.A) && isEmpty(SimuImpl.B) && isEmpty(SimuImpl.C);
	}

}
