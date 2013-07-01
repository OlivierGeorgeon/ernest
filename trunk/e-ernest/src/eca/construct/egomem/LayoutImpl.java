package eca.construct.egomem;

import java.util.HashMap;
import java.util.Map;

import eca.construct.Observation;
import eca.construct.ObservationImpl;
import eca.construct.Phenomenon;
import eca.construct.SimuImpl;

public class LayoutImpl implements Layout {
	
	private static Map<String , Layout> LAYOUTS = new HashMap<String , Layout>() ;

	private String label;
	private Map<Area , Phenomenon> phenomenons = new HashMap<Area , Phenomenon>() ;

	/**
	 * @param aspectA The aspect in area A
	 * @param aspectB The aspect in area B
	 * @param aspectC The aspect in area C
	 * @return The aspect
	 */
	public static Layout createOrGet(Phenomenon aspectA, Phenomenon aspectB, Phenomenon aspectC){
		String key = createPrimitiveKey(aspectA, aspectB, aspectC);
		if (!LAYOUTS.containsKey(key))
			LAYOUTS.put(key, new LayoutImpl(aspectA, aspectB, aspectC));			
		return LAYOUTS.get(key);
	}
	
	private static String createPrimitiveKey(Phenomenon aspectA, Phenomenon aspectB, Phenomenon aspectC) {
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
		if (transformation.equals(SimuImpl.SHIFT_LEFT)){
			//transformedLayout = createOrGet(layout.getAspect(SimuImpl.B),layout.getAspect(SimuImpl.C), SimuImpl.EMPTY);
			if (!layout.getPhenomenon(SimuImpl.B).equals(SimuImpl.EMPTY) )
				transformedLayout = createOrGet(layout.getPhenomenon(SimuImpl.B),SimuImpl.EMPTY, SimuImpl.EMPTY);
			else if (!layout.getPhenomenon(SimuImpl.C).equals(SimuImpl.EMPTY) )
				transformedLayout = createOrGet(layout.getPhenomenon(SimuImpl.C),SimuImpl.EMPTY, SimuImpl.EMPTY);
			else 
				transformedLayout = createOrGet(SimuImpl.EMPTY,SimuImpl.EMPTY, SimuImpl.EMPTY);
		}
		if (transformation.equals(SimuImpl.SHIFT_RIGHT)){
			//transformedLayout = createOrGet(SimuImpl.EMPTY, layout.getAspect(SimuImpl.A),layout.getAspect(SimuImpl.B));
			if (!layout.getPhenomenon(SimuImpl.A).equals(SimuImpl.EMPTY) )
				transformedLayout = createOrGet(SimuImpl.EMPTY, SimuImpl.EMPTY, layout.getPhenomenon(SimuImpl.A));
			else if (!layout.getPhenomenon(SimuImpl.B).equals(SimuImpl.EMPTY) )
				transformedLayout = createOrGet(SimuImpl.EMPTY, SimuImpl.EMPTY, layout.getPhenomenon(SimuImpl.B));
			else 
				transformedLayout = createOrGet(SimuImpl.EMPTY,SimuImpl.EMPTY, SimuImpl.EMPTY);
		}
		return transformedLayout;
	}
	
	private LayoutImpl(Phenomenon aspectA, Phenomenon aspectB, Phenomenon aspectC){
		this.label = createPrimitiveKey(aspectA, aspectB, aspectC);
		phenomenons.put(SimuImpl.A, aspectA);
		phenomenons.put(SimuImpl.B, aspectB);
		phenomenons.put(SimuImpl.C, aspectC);	
		phenomenons.put(SimuImpl.O, SimuImpl.EMPTY);
	}

	public Phenomenon getPhenomenon(Area area) {
		return phenomenons.get(area);
	}

	public boolean isEmpty(Area area) {
		return this.phenomenons.get(area).equals(SimuImpl.EMPTY);
	}

	public String getLabel() {
		return this.label;
	}
	
	public Observation observe(){
		Area area = SimuImpl.O;
		for (Area a : AreaImpl.getAREAS())
			if (!this.phenomenons.get(a).equals(SimuImpl.EMPTY))
				area = a;
		Phenomenon phenomenon = getPhenomenon(area);
		return ObservationImpl.createOrGet(phenomenon, area);
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
