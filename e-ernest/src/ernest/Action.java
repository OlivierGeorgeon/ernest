package ernest;

/**
 * An modality is a set if interactions that are alternate to each other.
 * @author Olivier
 */
public interface Action extends Comparable
{
	public static final Action STEP = new ActionImpl(">");
	public static final Action TURN_LEFT = new ActionImpl("^");
	public static final Action TURN_RIGHT = new ActionImpl("v");

	public String getLabel();
	public int getPropositionWeight();
	public void setPropositionWeight(int propositionWeight);
	public void addPropositionWeight(int weight);

}
