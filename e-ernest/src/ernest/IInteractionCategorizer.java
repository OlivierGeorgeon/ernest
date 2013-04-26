package ernest;

import java.util.Map;

/**
 * The categorizer that tells to which modality an interaction belongs.
 * @author Olivier
 */
public interface IInteractionCategorizer {
	
	/**
	 * @return The list of modalities.
	 */
	public Map<String , IModality> getModalities();

	/**
	 * Gives the modality to which an interaction belongs.
	 * @param interaction The interaction
	 * @return The modality
	 */
	public IModality categorize(IPrimitive interaction);

}
