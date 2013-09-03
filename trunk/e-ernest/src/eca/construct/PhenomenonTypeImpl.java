package eca.construct;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tracing.ITracer;
import eca.Primitive;

/**
 * A PhenomenonType is intended to represent a type of phenomenon that can be observed in the external world.
 * A PhenomenonType conflates primitive interactions based on the fact that their spatial location consistently overlaps.
 * A PhenomenonType may also be called a Bundle.
 * @author Olivier
 */
public class PhenomenonTypeImpl implements PhenomenonType {
	
	private static Map<String , PhenomenonType> PHENOMENA = new HashMap<String , PhenomenonType>() ;
	private static int index = 0;
	
	private String label;
	private Aspect aspect = Aspect.MOVE;
	private List<Primitive> primitives = new ArrayList<Primitive>();

	/**
	 * @return The list of all types of phenomenon known by the agent thus far
	 */
	public static Collection<PhenomenonType> getPhenomenonTypes() {
		return PHENOMENA.values();
	}
	
	/**
	 * @param label The aspect's label
	 * @return The aspect
	 */
	public static PhenomenonType createOrGet(String label){
		if (!PHENOMENA.containsKey(label))
			PHENOMENA.put(label, new PhenomenonTypeImpl(label));			
		return PHENOMENA.get(label);
	}
	
	/**
	 * Creates a new aspect using an incremental label
	 * @return The new created aspect
	 */
	public static PhenomenonType createNew(){
		index++;
		PHENOMENA.put(index + "", new PhenomenonTypeImpl(index +""));			
		return PHENOMENA.get(index + "");
	}
	
	/**
	 * @param primitive The primitive that evoke this phenomenon.
	 * @return The evoked phenimenon.
	 */
	public static PhenomenonType evoke(Primitive primitive){
		PhenomenonType phenomenonType = null;
		for (PhenomenonType p : getPhenomenonTypes()){
			if (p.contains(primitive))
				phenomenonType = p;
		}		
		return phenomenonType;
	}
	
	/**
	 * Merge the new PhenomenonType into the previous PhenomenonType.
	 * The interactions attached to the new PhenomenonType are added to the previous PhenomenonType 
	 * The new PhenomenonType is removed from the list of PHENOMENA
	 * @param newPhenomenonType The first action from which to merge (removed). 
	 * @param previousPhenomenonType The second action to which to merge (kept).
	 */
//	public static void merge(PhenomenonType newPhenomenonType, PhenomenonType previousPhenomenonType){
//		if (!newPhenomenonType.equals(previousPhenomenonType)){
//			for (Primitive act : newPhenomenonType.getPrimitives())
//				act.setPhenomenonType(previousPhenomenonType);
//			PHENOMENA.remove(newPhenomenonType.getLabel());
//		}
//	}
	
	/**
	 * Merge the interactions of the phenomenonType of the enacted interaction
	 * into the new PhenomenonType.
	 * The interactions attached to the enacted act's phenomenonType are transferred 
	 * to the new phenomenonType
	 * @param primitive The primitive to merge. 
	 * @param newPhenomenonType The second phenomenonType to which to merge (kept).
	 */
	public static void merge(Primitive primitive, PhenomenonType newPhenomenonType){
		if (!newPhenomenonType.contains(primitive)){
			PhenomenonType oldPhenomenonType = null;
			for (PhenomenonType phenomenonType : getPhenomenonTypes()){
				if (phenomenonType.contains(primitive))
					oldPhenomenonType = phenomenonType;
			}
			// TODO more sophisticated merge of phenomenonType.
			if (oldPhenomenonType != null){
				for (Primitive p : oldPhenomenonType.getPrimitives())
					newPhenomenonType.addPrimitive(p);
				PHENOMENA.remove(oldPhenomenonType.getLabel());
			}
			newPhenomenonType.addPrimitive(primitive);
		}
	}
	
	private PhenomenonTypeImpl(String label){
		this.label = label;
	}

	public String getLabel(){
		return this.label;
	}
	
	public void addPrimitive(Primitive act){
		if (!this.primitives.contains(act))
				this.primitives.add(act);
	}
	
	public List<Primitive> getPrimitives(){
		return this.primitives;
	}
	
	/**
	 * Phenomenon types are equal if they have the same label. 
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
			PhenomenonType other = (PhenomenonType)o;
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}

	public String toString(){
		String s = "";
		for (Primitive i : primitives)
			s += " " + i.getLabel();
		return s;
	}
	
	public boolean contains(Primitive primitive){
		return this.primitives.contains(primitive);
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}

	public Aspect getAspect() {
		return this.aspect;
	}
	
	public void trace(ITracer tracer, Object e) {
		
		Object p = tracer.addSubelement(e, "phenomenon_type");		
		tracer.addSubelement(p, "label", this.label);
		tracer.addSubelement(p, "aspect", this.aspect.toString());
		tracer.addSubelement(p, "primitives", this.toString());
	}

}
