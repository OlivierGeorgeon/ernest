package eca.construct.egomem;

/**
 * An area of the agent's surrounding space.
 * @author Olivier
 */
public interface Area 
{
	/** Predefined areas */
	public static Area A = AreaImpl.createOrGet("A");
	public static Area B = AreaImpl.createOrGet("B");
	public static Area C = AreaImpl.createOrGet("C");
	public static Area O = AreaImpl.createOrGet("O");
	public String getLabel();
}
