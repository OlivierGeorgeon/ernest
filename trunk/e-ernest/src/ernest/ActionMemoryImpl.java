package ernest;

import java.util.HashMap;
import java.util.Map;

/**
 * The categorizer that tells to which action an interaction corresponds.
 * @author Olivier
 */
public class ActionMemoryImpl implements ActionMemory {

	private Map<String , Action> actions = new HashMap<String , Action>() ;
	
	public ActionMemoryImpl(){
		actions.put(">", Action.STEP);
		actions.put("^", Action.TURN_LEFT);
		actions.put("v", Action.TURN_RIGHT);
	}
	
	public Map<String , Action> getActions(){
		return this.actions;
	}

	public Action categorize(IPrimitive interaction) {
		
		// The modality of a primitive interaction is given by the first character of its label
		// TODO learn modalities without using assumption about the interaction's label.
		String actionLabel = interaction.getLabel().substring(0, 1);

		if (!this.actions.containsKey(actionLabel)){
			Action m = new ActionImpl(actionLabel);
			this.actions.put(actionLabel, m);
		}
		return actions.get(actionLabel); 
	}
}
