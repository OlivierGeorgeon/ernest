package eca;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eca.construct.Action;
import eca.construct.ActionImpl;
import eca.construct.Phenomenon;
import eca.construct.PhenomenonImpl;
import eca.construct.egomem.AreaImpl;
import eca.enaction.ActImpl;

/**
 * A primitive interaction.
 * @author Olivier
 */
public class PrimitiveImpl implements Primitive {

	private static Map<String , Primitive> INTERACTIONS = new HashMap<String , Primitive>() ;

	private String label = "";
	private int value = 0;
	private Action action = ActionImpl.createNew();
	private Phenomenon phenomenon = PhenomenonImpl.createNew();

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
		ActImpl.createOrGetPrimitiveAct(this, AreaImpl.createOrGet("B"));
		this.action.addPrimitive(this);	
		this.phenomenon.addPrimitive(this);
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

	public Phenomenon getPhenomenon() {
		return this.phenomenon;
	}

	public void setAspect(Phenomenon phenomenon) {
		this.phenomenon = phenomenon;
		phenomenon.addPrimitive(this);
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
		action.addPrimitive(this);
	}
	
}
