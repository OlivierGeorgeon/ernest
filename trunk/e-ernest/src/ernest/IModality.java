package ernest;

/**
 * An modality is a set if interactions that are alternate to each other.
 * @author Olivier
 */
public interface IModality extends Comparable
{
	public String getLabel();
	public void setPrototypeInteraction(IPrimitive interaction);
	public IPrimitive getPrototypeInteraction();
	public int getPropositionWeight();
	public void setPropositionWeight(int propositionWeight);
	public void addPropositionWeight(int weight);

}
