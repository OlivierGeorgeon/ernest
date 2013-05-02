package ernest;

import java.util.Map;

/**
 * The categorizer that tells to which modality an interaction belongs.
 * @author Olivier
 */
public interface ActionMemory {
	
	/**
	 * @return The list of modalities.
	 */
	public Map<String , Action> getActions();

	/**
	 * Gives the modality to which an interaction belongs.
	 * @param interaction The interaction
	 * @return The modality
	 */
	public Action categorize(IPrimitive interaction);

}
