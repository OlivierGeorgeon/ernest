package ernest;

import java.util.HashMap;
import java.util.Map;

/**
 * The categorizer that tells to which modality an interaction belongs.
 * @author Olivier
 */
public class InteractionCategorizer implements IInteractionCategorizer {

	private Map<String , IModality> modalities = new HashMap<String , IModality>() ;
	
	public Map<String , IModality> getModalities(){
		return this.modalities;
	}

	public IModality categorize(IPrimitive interaction) {
		
		// The modality of a primitive interaction is given by the first character of its label
		// TODO learn modalities without using presuppositions about the interaction's label.
		String modalityLabel = interaction.getLabel().substring(0, 1);

		if (!this.modalities.containsKey(modalityLabel)){
			IModality m = new Modality(modalityLabel);
			// The prototype is the first interaction that creates the modality.
			// TODO Improve the management of the prototype. 
			m.setPrototypeInteraction(interaction);
			this.modalities.put(modalityLabel, m);
		}
		return modalities.get(modalityLabel); 
	}
}
