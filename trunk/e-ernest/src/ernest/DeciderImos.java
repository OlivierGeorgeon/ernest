package ernest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import spas.ISpas;
import imos.ActProposition;
import imos.IAct;
import imos.IActProposition;
import imos.IImos;
import imos.IProposition;
import imos.ISchema;
import imos.Imos;
import imos.Proposition;

/**
 * A decider decides what interaction to try to enact next
 * when the previous decision cycle is over
 * based on the current state of sequential and spatial memory
 * @author Olivier
 */
public class DeciderImos implements IDecider 
{
	IImos m_imos;
	ISpas m_spas;
	ITracer m_tracer;
	int m_maxSchemaLength = 10;

	DeciderImos(IImos imos, ISpas spas)
	{
		m_imos = imos;
		m_spas = spas;
	}

	public void setTracer(ITracer tracer)
	{
		m_tracer = tracer;
	}
	
	public void setMaxSchemaLength(int maxSchemaLength)
	{
		m_maxSchemaLength = maxSchemaLength;
	}

	public IEnaction decide(IEnaction enaction) 
	{
		IEnaction newEnaction = new Enaction();
		
		System.out.println("New decision ================ ");

		IAct nextTopIntention = selectAct(enaction.getFinalActivationContext());
		
		newEnaction.setTopAct(nextTopIntention);
		newEnaction.setTopRemainingAct(nextTopIntention);
		newEnaction.setPreviousLearningContext(enaction.getInitialLearningContext());
		newEnaction.setInitialLearningContext(enaction.getFinalLearningContext());
		
		return newEnaction;
	}
	
	/**
	 * Select an intention act from a given activation list.
	 * The selected act receives an activation value 
	 * @param activationList The list of acts that in the sequential context that activate episodic memory.
	 * @param propositionList The list of propositions made by the spatial system.
	 * @return The selected act.
	 */
	private IAct selectAct(List<IAct> activationList)
	{
		List<IActProposition> actPropositions = new ArrayList<IActProposition>();	
		List<IProposition> schemaPropositions = new ArrayList<IProposition>();	
		
		// Browse all the existing schemas 
		Object decision = null;
		Object activations = null;
		Object inconsistences = null;
		if (m_tracer != null)
		{
			decision = m_tracer.addEventElement("decision", true);
			activations = m_tracer.addSubelement(decision, "activated_schemas");
			//inconsistences = m_tracer.addEventElement("inconsistences", true);
		}

		// Primitive acts receive a default proposition for themselves
		for(IAct a : m_imos.getActs())
		{
			if (a.getSchema().isPrimitive())
			{
				//IProposition p = new Proposition(s, 0, 0);
				IActProposition p = new ActProposition(a, 0, 0);
				if (!actPropositions.contains(p))
					actPropositions.add(p);
			}       
		}

		
		for (ISchema s : m_imos.getSchemas())
		{
			if (!s.isPrimitive())
			{
				// Activate the schemas that match the context 
				boolean activated = false;
				for (IAct contextAct : activationList)
				{
					if (s.getContextAct().equals(contextAct))
					{
						activated = true;
						if (m_tracer != null)
							m_tracer.addSubelement(activations, "schema", s + " intention " + s.getIntentionAct() + ((s.getIntentionAct().getConfidence() == Imos.RELIABLE ) ? " reliable" : " unreliable" ));
						//System.out.println("Activate " + s + " s=" + s.getIntentionAct().getSatisfaction());
					}
				}
				
				// Activated schemas propose their intention
				if (activated)
				{
					IAct proposedAct = s.getIntentionAct();
					// The weight is the proposing schema's weight multiplied by the proposed act's satisfaction
					int w = s.getWeight() * proposedAct.getSatisfaction();
					//int w = s.getWeight();
                    // The expectation is the proposing schema's weight signed with the proposed act's status  
                    int e = s.getWeight();// * (s.getIntentionAct().getStatus() ? 1 : -1);
                    //int e = 0;
					
					// If the intention is consistent with spatial memory 
					//if (checkConsistency(proposedAct))
					{
					
						// If the intention is reliable then a proposition is constructed
						if ((proposedAct.getConfidence() == Imos.RELIABLE ) &&						 
							(proposedAct.getSchema().getLength() <= m_maxSchemaLength ))
						{
							//IProposition p = new Proposition(s.getIntentionAct().getSchema(), w, e);
							IActProposition p = new ActProposition(proposedAct, w, e);
		
							int i = actPropositions.indexOf(p);
							if (i == -1)
								actPropositions.add(p);
							else
								actPropositions.get(i).update(w, e);
						}
						// If the intention is not reliable
						// if the intention's schema has not passed the threshold then  
						// the activation is propagated to the intention's schema's context
						else
						{
							// Expect the value of the intention's schema's intention
							//e = proposedAct.getSchema().getIntentionAct().getSatisfaction();
							
							if (!proposedAct.getSchema().isPrimitive())
							{
								// only if the intention's intention is positive (this is some form of positive anticipation)
								if (proposedAct.getSchema().getIntentionAct().getSatisfaction() > 0)
								{
									//IProposition p = new Proposition(proposedAct.getSchema().getContextAct().getSchema(), w, e);
									IActProposition p = new ActProposition(proposedAct.getSchema().getContextAct(), w, e);
									int i = actPropositions.indexOf(p);
									if (i == -1)
										actPropositions.add(p);
									else
										actPropositions.get(i).update(w, e);
								}
							}
						}
					}//
//					else
//					{
//						if (m_tracer != null)
//							m_tracer.addSubelement(inconsistences, "inconsistence", proposedAct.getLabel() );
//					}
				}
			}
//			// Primitive sensorymotor schemas also receive a default proposition for their succeeding act
//			if (s.isPrimitive())
//			{
//				//IProposition p = new Proposition(s, 0, 0);
//				if (s.getSucceedingAct() != null)
//				{
//					IActProposition p = new ActProposition(s.getSucceedingAct(), 0, 0);
//					if (!actPropositions.contains(p))
//						actPropositions.add(p);
//				}
//			}       
		}
		
		
		// Add the propositions from the spatial system 
		
//		for (IActProposition proposition : propositionList)
//		{
//			int i = proposals.indexOf(proposition);
//			if (i == -1)
//				proposals.add(proposition);
//			else
//				proposals.get(i).update(proposition.getWeight(), proposition.getExpectation());
//		}

		// Log the propositions
		
		//System.out.println("Propose: ");
		Object proposalElmt = null;
		if (m_tracer != null)
			proposalElmt = m_tracer.addSubelement(decision, "proposed_acts");
		
		for (IActProposition p : actPropositions)
		{
			if (m_tracer != null)
				m_tracer.addSubelement(proposalElmt, "act", p.toString());
			//System.out.println(p);
		}
		
		// TODO Update the expected satisfaction of each proposed schema based on the local map anticipation
		
		//Construct a list of schemaPropositions from the list of actPropositions.
		
		for (IActProposition actProposition : actPropositions)
		{
			//int w = actProposition.getWeight() * (actProposition.getAct().getSatisfaction() + actProposition.getExpectation());
			//int e = actProposition.getWeight();
			int w = actProposition.getWeight();
			int e = actProposition.getExpectation();
			IProposition schemaProposition = new Proposition(actProposition.getAct().getSchema(), w, e, actProposition.getAct());
			int i = schemaPropositions.indexOf(schemaProposition);
			if (i == -1)
				schemaPropositions.add(schemaProposition);
			else
				schemaPropositions.get(i).update(w, e, actProposition.getAct());
		}
		
		// sort by weighted proposition...
		Collections.sort(schemaPropositions);
		
		Object proposition = null;
		if (m_tracer != null)
			proposition = m_tracer.addSubelement(decision, "proposed_schemas");
		
		for (IProposition p : schemaPropositions)
		{
			if (m_tracer != null)
				m_tracer.addSubelement(proposition, "schema", p.toString());
			//System.out.println(p);
		}
		
		// count how many are tied with the highest weighted proposition
		int count = 0;
		int wp = schemaPropositions.get(0).getWeight();
		for (IProposition p : schemaPropositions)
		{
			if (p.getWeight() != wp)
				break;
			count++;
		}

		// pick one at random from the top of the proposal list
		// count is equal to the number of proposals that are tied...

		IProposition p = null;
		//if (DETERMINISTIC)
			p = schemaPropositions.get(0); // Always take the first
		//else
		//	p = schemaPropositions.get(m_rand.nextInt(count)); // Break the tie at random
				
		//IAct a = m_sensorimotorSystem.anticipateInteraction(p.getSchema(), p.getExpectation(), m_acts);
		IAct a = p.getAct();
		
		a.setActivation(p.getWeight());
		
		System.out.println("Select:" + a);

		if (m_tracer != null)
			m_tracer.addSubelement(decision, "select", a.toString());


		return a ;
	}

	public void carry(IEnaction enaction)
	{
		enaction.setIntendedPrimitiveAct(spreadActivation(enaction.getTopRemainingAct()));
		enaction.setStep(enaction.getStep() + 1);
		enaction.traceCarry(m_tracer);
	}

	/**
	 * Recursively prescribe an act's subacts and subschemas.
	 * (Set the subacts' activation equal to the prescribing act's activation, not used)
	 * @param The prescriber act.
	 * @return The prescribed act.
	 */
	private IAct spreadActivation(IAct a)
	{
		IAct primitiveAct = null;
		ISchema subschema = a.getSchema();
		subschema.setPrescriberAct(a);
		
		if (subschema.isPrimitive())
			primitiveAct = a;
		else
		{
			subschema.setPointer(0);
			IAct subact = subschema.getContextAct();
			subact.setPrescriberSchema(subschema);
			//subact.setActivation(a.getActivation());
			primitiveAct = spreadActivation(subact);
		}
		
		return primitiveAct;
	}	

	private IAct selectAct2(List<IActProposition> propositions)
	{
		
		//Construct a list of schemaPropositions from the list of actPropositions.
		
		List<IProposition> schemaPropositions = new ArrayList<IProposition>();	
		for (IActProposition actProposition : propositions)
		{
			int w = actProposition.getWeight() * (actProposition.getAct().getSatisfaction() + actProposition.getExpectation());
			int e = actProposition.getWeight();
			IProposition schemaProposition = new Proposition(actProposition.getAct().getSchema(), w, e, actProposition.getAct());
			int i = schemaPropositions.indexOf(schemaProposition);
			if (i == -1)
				schemaPropositions.add(schemaProposition);
			else
				schemaPropositions.get(i).update(w, e, actProposition.getAct());
		}
		
		// sort by weighted proposition...
		Collections.sort(schemaPropositions);
		
		Object proposalElmt = null;
		if (m_tracer != null)
			proposalElmt = m_tracer.addEventElement("schema_propositions", true);
		
		for (IProposition p : schemaPropositions)
		{
			if (m_tracer != null)
				m_tracer.addSubelement(proposalElmt, "schema", p.toString());
			//System.out.println(p);
		}
		
		// count how many are tied with the highest weighted proposition
		int count = 0;
		int wp = schemaPropositions.get(0).getWeight();
		for (IProposition p : schemaPropositions)
		{
			if (p.getWeight() != wp)
				break;
			count++;
		}

		// pick one at random from the top of the proposal list
		// count is equal to the number of proposals that are tied...

		IProposition p = null;
		//if (DETERMINISTIC)
			p = schemaPropositions.get(0); // Always take the first
		//else
		//	p = schemaPropositions.get(m_rand.nextInt(count)); // Break the tie at random
				
		//IAct a = m_sensorimotorSystem.anticipateInteraction(p.getSchema(), p.getExpectation(), m_acts);
		IAct a = p.getAct();
		
		a.setActivation(p.getWeight());
		
		System.out.println("Select:" + a);

		if (m_tracer != null)
			m_tracer.addEventElement("select", a.toString());

		return a;
	}	
}
