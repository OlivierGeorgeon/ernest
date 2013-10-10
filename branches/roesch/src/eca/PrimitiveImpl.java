package eca;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3f;

import eca.construct.Action;
import eca.construct.ActionImpl;
import eca.construct.Area;
import eca.construct.AreaImpl;
import eca.construct.PhenomenonType;
import eca.construct.PhenomenonTypeImpl;
import eca.construct.egomem.Displacement;
import eca.ss.enaction.Act;
import eca.ss.enaction.ActImpl;

/**
 * A primitive interaction.
 * @author Olivier
 */
public class PrimitiveImpl implements Primitive {

	private static Map<String , Primitive> INTERACTIONS = new HashMap<String , Primitive>() ;

	private String label = "";
	private int value = 0;

	private Map<Displacement , Integer> displacements = new HashMap<Displacement , Integer>() ;

	/**
	 * @param label The primitive interaction's label
	 * @param value The primitive interaction's value
	 * @return The primitive interaction created or retrieved
	 */
	public static Primitive createOrGet(String label, int value){
		if (!INTERACTIONS.containsKey(label))
			INTERACTIONS.put(label, new PrimitiveImpl(label, value));			
		return INTERACTIONS.get(label);
	}
	
	/**
	 * @param preInteraction The composite interaction's pre-interaction
	 * @param postInteraction The composite interaction's post-interaction
	 * @return The primitive interaction created or retrieved
	 */
	public static Primitive createOrGetComposite(Primitive preInteraction, Primitive postInteraction){
		String label = "(" + preInteraction.getLabel() + postInteraction.getLabel() + ")";
		int value = preInteraction.getValue() + postInteraction.getValue();
		if (!INTERACTIONS.containsKey(label))
			INTERACTIONS.put(label, new PrimitiveImpl(label, value));			
		return INTERACTIONS.get(label);
	}
	
	/**
	 * @param label The primitive interaction's label
	 * @return The primitive interaction
	 */
	public static Primitive get(String label){
		return INTERACTIONS.get(label);
	}
	
	/**
	 * @return The collection of all primitive interactions
	 */
	public static Collection<Primitive> getINTERACTIONS() {
		return INTERACTIONS.values();
	}
	
	private PrimitiveImpl(String label, int value){
		this.label = label;
		this.value = value;
//		Act act = ActImpl.createOrGetPrimitiveAct(this, AreaImpl.createOrGet(new Point3f(1,0,0)));
//		Action action = ActionImpl.createOrGet("[a" + act.getLabel() + "]");
//		action.addAct(act);
//		PhenomenonType phenomenonType = PhenomenonTypeImpl.createOrGet("[p" + label +"]");
//		phenomenonType.addPrimitive(this);	
	}
	
	public String getLabel(){
		return this.label;
	}

	public int getValue(){
		return this.value;
	}
	
	/**
	 * Interactions are equal if they have the same label. 
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
			Primitive other = (Primitive)o;
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}

	public String toString()
	{
		return this.label + "(" + this.value / 10 + ")";
	}

	public void incDisplacementCounter(Displacement displacement){
		if (displacements.containsKey(displacement))
			displacements.put(displacement, displacements.get(displacement) + 1);
		else
			displacements.put(displacement, 1);
	}

	public Displacement predictDisplacement(Area area) {
		int max = 0;
		Displacement predictDisplacement = null;
		
		for (Map.Entry<Displacement, Integer> entry : displacements.entrySet())
			if (entry.getKey().getPreArea().equals(area) && entry.getValue() > max){
				predictDisplacement = entry.getKey();
				max = entry.getValue();
			}
		
		return predictDisplacement;
	}
	
	public String getDisplacementLabels(){
		String label = "";
		for (Map.Entry<Displacement, Integer> entry : displacements.entrySet())
			label += entry.getKey().getLabel() + "(" + entry.getValue() + ") ";
		
		return label;
	}

}
