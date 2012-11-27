package imos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ernest.ITracer;
import spas.ISpas;

/**
 * This is the regular decider for Ernest 7 that does not use spatial memory.
 * @author Olivier
 */
public class Decider implements IDecider 
{
	IImos m_imos;
	ISpas m_spas;
	ITracer m_tracer;
	int m_maxSchemaLength = 10;

	Decider(IImos imos, ISpas spas)
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

		ArrayList<IActProposition> actPropositions = proposeActs(enaction.getFinalActivationContext());
		IAct nextTopIntention = selectAct(actPropositions);
		//IAct nextTopIntention = selectAct(enaction.getFinalActivationContext());
		
		newEnaction.setTopAct(nextTopIntention);
		newEnaction.setTopRemainingAct(nextTopIntention);
		newEnaction.setPreviousLearningContext(enaction.getInitialLearningContext());
		newEnaction.setInitialLearningContext(enaction.getFinalLearningContext());
		
		return newEnaction;
	}
	
	/**
	 * Construct a list of proposed acts based on the current context
	 * @param activationList The list of acts that form the current context.
	 * @return The list of proposed acts.
	 */
	protected ArrayList<IActProposition> proposeActs(ArrayList<IAct> activationList)
	{
		ArrayList<IActProposition> actPropositions = new ArrayList<IActProposition>();	
		
		// Prepare the tracer.
		Object propositions = null;
		if (m_tracer != null)
			propositions = m_tracer.addEventElement("proposed_acts", true);

		// Primitive acts receive a default proposition for themselves
		for(IAct a : m_imos.getActs())
		{
			if (a.getSchema().isPrimitive())
			{
				IActProposition p = new ActProposition(a, 0, 0);
				if (!actPropositions.contains(p))
					actPropositions.add(p);
			}       
		}

		// Browse all the existing schemas 
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
							m_tracer.addSubelement(propositions, "schema", s + " intention " + s.getIntentionAct() + ((s.getIntentionAct().getConfidence() == Imos.RELIABLE ) ? " reliable" : " unreliable" ));
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
				}
			}
		}
		
		// Log the propositions
		
		//System.out.println("Propose: ");
		Object proposalElmt = null;
		if (m_tracer != null)
		{
			proposalElmt = m_tracer.addSubelement(propositions, "proposed_acts");
		
			for (IActProposition p : actPropositions)
				m_tracer.addSubelement(proposalElmt, "act", p.toString());
		}
		return actPropositions;
	}
	
	/**
	 * Select an act from the list of proposed acts
	 * @param actPropositions The list of act propostion.
	 * @return The selected act.
	 */
	protected IAct selectAct(ArrayList<IActProposition> actPropositions)
	{
		//Construct a list of schemaPropositions from the list of actPropositions.
		ArrayList<IProposition> schemaPropositions = new ArrayList<IProposition>();	
		
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
		
		// sort the schema propositions by weight.
		Collections.sort(schemaPropositions);
		
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

		IProposition selectedProposition = null;
		//if (DETERMINISTIC)
			selectedProposition = schemaPropositions.get(0); // Always take the first
		//else
		//	p = schemaPropositions.get(m_rand.nextInt(count)); // Break the tie at random
				
		IAct a = selectedProposition.getAct();
		
		a.setActivation(selectedProposition.getWeight());
		
		System.out.println("Select:" + a);

		// Trace the schema propositions
		Object decision = null;
		Object proposition = null;
		if (m_tracer != null)
		{
			decision = m_tracer.addEventElement("decision", true);
			proposition = m_tracer.addSubelement(decision, "proposed_schemas");
		
			for (IProposition p : schemaPropositions)
					m_tracer.addSubelement(proposition, "schema", p.toString());
			
			m_tracer.addSubelement(decision, "select", a.toString());
		}
		
		return a;
	}

	public void carry(IEnaction enaction)
	{
		IAct intendedPrimitiveAct = spreadActivation(enaction.getTopRemainingAct());
		enaction.setIntendedPrimitiveAct(intendedPrimitiveAct);
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
}
