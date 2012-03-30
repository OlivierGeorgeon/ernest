package imos;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.w3c.dom.Element;

import spas.IObservation;
import spas.IPlace;
import spas.PersistenceMemory;

import ernest.Ernest;
import ernest.IErnest;
import ernest.ITracer;


/**
 * Ernest's episodic memory contains all the schemas and acts ever created.
 * It offers methods to record new schemas and acts.
 * Episodic memory can be queried with the method selectAct() that returns the next intention to enact. 
 * @author ogeorgeon
 */
public class EpisodicMemory 
{
	/** The tracer */
	private ITracer m_tracer;
	
	/** Regularity sensibility threshold (The weight threshold for an act to become reliable). */
	private int m_regularitySensibilityThreshold;

	/** Maximum length of a schema (For the schema to be chosen as an intention) */
	private int m_maxSchemaLength;
	
	/** A list of all the schemas ever created ... */
	private List<ISchema> m_schemas = new ArrayList<ISchema>(1000);

	/** A list of all the acts ever created. */
	private List<IAct> m_acts = new ArrayList<IAct>(2000);
	
	/** If true then the IMOS does not use random */
	public static boolean DETERMINISTIC = true; 

	/** Random generator used to break a tie when selecting a schema... */
	private static Random m_rand = new Random(); 
	
	/** Counter of learned schemas for tracing */
	private int m_learnCount = 0;
	
	public EpisodicMemory(int regularitySensibilityThreshold, int maxShemaLength)
	{
		m_regularitySensibilityThreshold = regularitySensibilityThreshold;
		m_maxSchemaLength = maxShemaLength;
	}
	
	/**
	 * @param tracer The tracer
	 */
	public void setTracer(ITracer tracer)
	{
		m_tracer = tracer;
	}

	/**
	 * @param threshold The regularity sensibility threshold
	 */
	public void setRegularitySensibilityThreshold(int threshold)
	{
		m_regularitySensibilityThreshold = threshold;
	}
	
	/**
	 * @param length The maximum length of a schema
	 */
	public void setMaxSchemaLength(int length)
	{
		m_maxSchemaLength = length;
	}
	
	/**
	 * @return the number of schema learned since the last reset
	 */
	public int getLearnCount() { return m_learnCount; };
	
	/**
	 * Reset the count of schema learned
	 */
	public void resetLearnCount() { m_learnCount = 0; };

	/**
	 * Reset the episodic memory.
	 * TODO Maybe should not clear primitive schemas and acts.
	 */
	public void clear() 
	{
		m_schemas.clear();
		m_acts.clear();
	}

	/**
	 * Add an act to episodic memory if it does not already exist
	 * @param label The act's label
	 * @param schema The act's schema
	 * @param status The act's status
	 * @param satisfaction The act's satisfaction
	 * @param confidence The act's confidence
	 * @return the new act if created or the already existing acts
	 */
	public IAct addAct(String label, ISchema schema, boolean status, int satisfaction, int confidence)
	{
		IAct a = Act.createAct(label, schema, status, satisfaction, confidence);
		
		int i = m_acts.indexOf(a);
		if (i == -1)
			// The act does not exist
			m_acts.add(a);
		else 
			// The act already exists: return a pointer to it.
			a =  m_acts.get(i);
		return a;
	}
	
	/**
	 * Add a primitive schema 
	 * @param label The schema's string identifier.
	 * @return The created primitive schema.
	 */
	public ISchema addPrimitiveSchema(String label) 
	{
		ISchema s =  Schema.createPrimitiveSchema(m_schemas.size() + 1, label);
		int i = m_schemas.indexOf(s);
		if (i == -1)
			m_schemas.add(s);
		else
			// The schema already exists: return a pointer to it.
			s =  m_schemas.get(i);
    	return s;
	}

	/**
	 * Add a composite schema and its succeeding act that represent a composite possibility 
	 * of interaction between Ernest and its environment. 
	 * @param contextAct The context Act.
	 * @param intentionAct The intention Act.
	 * @return The schema made of the two specified acts, whether it has been created or it already existed. 
	 */
    public ISchema addCompositeInteraction(IAct contextAct, IAct intentionAct)
    {
    	ISchema s = Schema.createCompositeSchema(m_schemas.size() + 1, contextAct, intentionAct);
    	
		int i = m_schemas.indexOf(s);
		if (i == -1)
		{
			// The schema does not exist: create its succeeding act and add it to Ernest's memory
			IAct a = Act.createCompositeSucceedingAct(s); 
	    	s.setSucceedingAct(a);
			m_schemas.add(s);
			m_acts.add(a);
			m_learnCount++;
		}
		else
			// The schema already exists: return a pointer to it.
			s =  m_schemas.get(i);
    	return s;
    }

	/**
	 * Add or update a failing possibility of interaction between Ernest and its environment.
	 * Add or update the schema's failing act to Ernest's memory. 
	 * If the failing act does not exist then create it. 
	 * If the failing act exists then update its satisfaction.
	 * @param schema The schema that failed.
	 * @param satisfaction The satisfaction obtained during the failure.
	 * @return The failing act.
	 */
    public IAct addFailingInteraction(ISchema schema, int satisfaction)
    {
    	IAct failingAct = schema.getFailingAct();
    	
		if (!schema.isPrimitive())
		{
			if (failingAct == null)
			{
				failingAct = Act.createCompositeFailingAct(schema, satisfaction);
				schema.setFailingAct(failingAct);
				m_acts.add(failingAct);
			}
			else
				// If the failing act already exists then 
				//  its satisfaction is averaged with the previous value
				failingAct.setSatisfaction((failingAct.getSatisfaction() + satisfaction)/2);
		}
		
		return failingAct;
    }

	/**
	 * Learn from an enacted intention after a given context.
	 * Returns the list of learned acts that are based on reliable subacts. The first act of the list is the stream act.
	 * @param contextList The list of acts that constitute the context in which the learning occurs.
	 * @param intentionAct The intention.
	 * @return A list of the acts created from the learning. The first act of the list is the stream act if the first act of the contextList was the performed act.
	 */
	public List<IAct> record(List<IAct> contextList, IAct intentionAct)
	{
		List<IAct> newContextList= new ArrayList<IAct>(20);;
		
		if (intentionAct != null)
		{
			// For each act in the context ...
			for (IAct contextAct : contextList)
			{
				// Build a new schema with the context act and the intention act 
				ISchema newSchema = addCompositeInteraction(contextAct, intentionAct);
				newSchema.incWeight(m_regularitySensibilityThreshold);
				//System.out.println("learned " + newSchema.getLabel());
				
					// Created acts are part of the context 
					// if their context and intention have passed the regularity
					// if they are based on reliable no�mes
				if ((contextAct.getConfidence() == Imos.RELIABLE) &&
  				   (intentionAct.getConfidence() == Imos.RELIABLE))
				{
					newContextList.add(newSchema.getSucceedingAct());
					// System.out.println("Reliable schema " + newSchema);
				}
			}
		}
		return newContextList; 
	}

	/**
	 * Select an intention act from a given activation list.
	 * The selected act receives an activation value 
	 * @param activationList The list of acts that activate episodic memory.
	 * @return The selected act.
	 */
	public IAct selectAct(List<IAct> activationList, List<IPlace> phenomenaList)
	{

		List<IProposition> proposals = new ArrayList<IProposition>();	
		
		// Browse all the existing schemas 
		Object activations = null;
		if (m_tracer != null)
			activations = m_tracer.addEventElement("activations", false);
		for (ISchema s : m_schemas)
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
							m_tracer.addSubelement(activations, "activation", s + " s=" + s.getIntentionAct().getSatisfaction());
						//System.out.println("Activate " + s + " s=" + s.getIntentionAct().getSatisfaction());
					}
				}
				
				// Activated schemas propose their intention
				if (activated)
				{
					IAct proposedAct = s.getIntentionAct();
					// The weight is the proposing schema's weight multiplied by the proposed act's satisfaction
					int w = s.getWeight() * proposedAct.getSatisfaction();
                    // The expectation is the proposing schema's weight signed with the proposed act's status  
                    int e = s.getWeight() * (s.getIntentionAct().getStatus() ? 1 : -1);
					
					// If primitive act then simulate it in the local map
					if (proposedAct.getSchema().isPrimitive())
					{
						//IObservation anticipation = m_simulationSystem.anticipate(s.getIntentionAct());
						//IObservation anticipation = m_ernest.getStaticSystem().anticipate(s.getIntentionAct());
						// If disagreement 
						//if (anticipation.getStatus() != s.getIntentionAct().getStatus())
						//if (!anticipation.getStatus())
						//w = 0;//(w +  s.getWeight() * s.getIntentionAct().getSchema().resultingAct(anticipation.getStatus()).getSatisfaction())/2;
						//e = s.getWeight() * (anticipation.getStatus() ? 1 : -1);
					}
					
					// If the intention is reliable
					if ((proposedAct.getConfidence() == Imos.RELIABLE ) &&						 
						(proposedAct.getSchema().getLength() <= m_maxSchemaLength ))
					{
						IProposition p = new Proposition(s.getIntentionAct().getSchema(), w, e);
	
						int i = proposals.indexOf(p);
						if (i == -1)
							proposals.add(p);
						else
							proposals.get(i).update(w, e);
					}
					// if the intention's schema has not passed the threshold then  
					// the activation is propagated to the intention's schema's context
					else
					{
						if (!proposedAct.getSchema().isPrimitive())
						{
							// only if the intention's intention is positive (this is some form of positive anticipation)
							if (proposedAct.getSchema().getIntentionAct().getSatisfaction() > 0)
							{
								IProposition p = new Proposition(proposedAct.getSchema().getContextAct().getSchema(), w, e);
								int i = proposals.indexOf(p);
								if (i == -1)
									proposals.add(p);
								else
									proposals.get(i).update(w, e);
							}
						}
					}
				}
			}

			// Primitive sensorymotor schemas also receive a default proposition for themselves
			if (s.isPrimitive())
			{
				IProposition p = new Proposition(s, 0, 0);
				if (!proposals.contains(p))
					proposals.add(p);
			}
			
		}

		// Acts that match a phenomenon propose their schemas if they are primitive
		for (IAct a : m_acts)
		{
			if (a.getSchema().isPrimitive())
			{
				for (IPlace place : phenomenaList)
				{
					if (a.getPhenomenon() == place.getValue() && a.getPosition().epsilonEquals(place.getPosition(), .1f))
					{
						IProposition p = new Proposition(a.getSchema(), a.getSatisfaction(), 1);
						int i = proposals.indexOf(p);
						if (i == -1)
							proposals.add(p);
						else
							proposals.get(i).update(a.getSatisfaction(), 1);
					}
				}
			}
		}

		System.out.println("Propose: ");
		Object proposalElmt = null;
		if (m_tracer != null)
			proposalElmt = m_tracer.addEventElement("proposals", false);
		
//		for (IProposition p : proposals)
//		{
//			if (m_tracer != null)
//				m_tracer.addSubelement(proposalElmt, "proposal", p.toString());
//			//System.out.println(p);
//		}
		
		// TODO Update the expected satisfaction of each proposed schema based on the local map anticipation

		for (IProposition p : proposals)
		{
			System.out.println(p);
			//IObservation anticipation = m_ernest.getStaticSystem().anticipate(p.getAct());
			// Adjust the proposition's weight based on the anticipation in the local map 
		}
		
		
		// sort by weighted proposition...
		Collections.sort(proposals);
		
		// count how many are tied with the  highest weighted proposition
		int count = 0;
		int wp = proposals.get(0).getWeight();
		for (IProposition p : proposals)
		{
			if (p.getWeight() != wp)
				break;
			count++;
		}

		// pick one at random from the top of the proposal list
		// count is equal to the number of proposals that are tied...

		IProposition p = null;
		if (DETERMINISTIC)
			p = proposals.get(0); // Always take the first
		else
			p = proposals.get(m_rand.nextInt(count)); // Break the tie at random
		
		ISchema s = p.getSchema();
		
		//IAct a = p.getAct();
		IAct a = anticipateInteraction(p.getSchema(), p.getExpectation());
		
		// Activate the selected act in Episodic memory.
		// (The act's activation is set equal to its proposition's weight)
		a.setActivation(p.getWeight());
		
		// TODO at some point we may implement a smarter mechanism to spread the activation to sub-acts.

		System.out.println("Select:" + a);

		return a ;
	}

	/**
	 * Tells the interaction that is likely to result from the enaction of this schema.
	 * If the schema has no succeeding or failing act defined, 
	 * then pick a random interaction attached to this schema.
	 * TODO Simulate the action to get a better anticipation.
	 * @param s The schema. 
	 * @return The anticipated resulting interaction.
	 */
	private IAct anticipateInteraction(ISchema s, int e)
	{
		IAct anticipateInteraction = null;
		boolean status = (e >= 0);
		anticipateInteraction = (status ? s.getSucceedingAct() : s.getFailingAct());
		
		// if the schema has no succeeding or failing act, then pick an act randomly
		if (anticipateInteraction==null)
		{
			for (IAct a : m_acts)
			{
				//if (a.getSchema().equals(s) && (a.getStatus() == true))
				if (a.getSchema().equals(s) )
					anticipateInteraction = a;
			}
		}
		return anticipateInteraction;
	}


}
