package imos;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.w3c.dom.Element;

import spas.IObservation;
import spas.IPlace;
import spas.LocalSpaceMemory;

import ernest.Ernest;
import ernest.IErnest;
import ernest.ISensorymotorSystem;
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
	
	private ISensorymotorSystem m_sensorimotorSystem;
	
	/** Regularity sensibility threshold (The weight threshold for an act to become reliable). */
	private int m_regularitySensibilityThreshold;

	/** Maximum length of a schema (For the schema to be chosen as an intention) */
	private int m_maxSchemaLength;
	
	/** A list of all the schemas ever created ... */
	private ArrayList<ISchema> m_schemas = new ArrayList<ISchema>(1000);

	/** A list of all the acts ever created. */
	private ArrayList<IAct> m_acts = new ArrayList<IAct>(2000);
	
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

	public void setSensorimotorSystem(ISensorymotorSystem sensorimotorSystem)
	{
		m_sensorimotorSystem = sensorimotorSystem;
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
	 * @param activationList The list of acts that in the sequential context that activate episodic memory.
	 * @param propositionList The list of propositions made by the spatial system.
	 * @return The selected act.
	 */
	public IAct selectAct(List<IAct> activationList, List<IProposition> propositionList)
	{
		List<IProposition> proposals = new ArrayList<IProposition>();	
		
		// Browse all the existing schemas 
		Object activations = null;
		Object inconsistences = null;
		if (m_tracer != null)
		{
			activations = m_tracer.addEventElement("activations", true);
			inconsistences = m_tracer.addEventElement("inconsistences", true);
		}
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
							m_tracer.addSubelement(activations, "activation", s + " expected_satisfaction=" + s.getIntentionAct().getSatisfaction());
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
					
					// If the intention is consistent with spatial memory 
					if (m_sensorimotorSystem.checkConsistency(proposedAct))
					{
					
						// If the intention is reliable then a proposition is constructed
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
						// If the intention is not reliable
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
					}//
					else
					{
						if (m_tracer != null)
							m_tracer.addSubelement(inconsistences, "inconsistence", proposedAct.getLabel() );
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
		
		// Add the propositions from the spatial system 
		
		for (IProposition proposition : propositionList)
		{
			int i = proposals.indexOf(proposition);
			if (i == -1)
				proposals.add(proposition);
			else
				proposals.get(i).update(proposition.getWeight(), proposition.getExpectation());
		}

		// Log the propositions
		
		//System.out.println("Propose: ");
		Object proposalElmt = null;
		if (m_tracer != null)
			proposalElmt = m_tracer.addEventElement("proposals", true);
		
		for (IProposition p : proposals)
		{
			if (m_tracer != null)
				m_tracer.addSubelement(proposalElmt, "proposal", p.toString());
			//System.out.println(p);
		}
		
		// TODO Update the expected satisfaction of each proposed schema based on the local map anticipation
		
		IAct a = selectAct(proposals);

//		// sort by weighted proposition...
//		Collections.sort(proposals);
//		
//		// count how many are tied with the  highest weighted proposition
//		int count = 0;
//		int wp = proposals.get(0).getWeight();
//		for (IProposition p : proposals)
//		{
//			if (p.getWeight() != wp)
//				break;
//			count++;
//		}
//
//		// pick one at random from the top of the proposal list
//		// count is equal to the number of proposals that are tied...
//
//		IProposition p = null;
//		if (DETERMINISTIC)
//			p = proposals.get(0); // Always take the first
//		else
//			p = proposals.get(m_rand.nextInt(count)); // Break the tie at random
//		
//		ISchema s = p.getSchema();
//		
//		//IAct a = p.getAct();
//		IAct a = m_sensorimotorSystem.anticipateInteraction(p.getSchema(), p.getExpectation(), m_acts);
		
		// Activate the selected act in Episodic memory.
		// (The act's activation is set equal to its proposition's weight)
//		a.setActivation(p.getWeight());
		
		// TODO at some point we may implement a smarter mechanism to spread the activation to sub-acts.

//		System.out.println("Select:" + a);

		return a ;
	}

	/**
	 * @return an act that permits to reach a position and an orientation relative to the agent.
	 */
	public IAct reach(Point3f position, Vector3f orientation)
	{
		IAct act = null;
		
		for (IAct a : m_acts)
		{
			Vector3f or = new Vector3f(1,0,0);
			Transform3D tf = new Transform3D(a.getTransform());
			tf.invert();
			tf.transform(or);
			
			if (a.getStartPosition().epsilonEquals(position, .1f) && or.epsilonEquals(orientation, .1f) )
				if ( act == null || a.getSatisfaction() > act.getSatisfaction()) 
					act = a;
		}
		
		if (m_tracer != null && act != null)
		{
			Object reach = m_tracer.addEventElement("reach", true);			
			m_tracer.addSubelement(reach, "position", "(" + position.x + ", " + position.y + ", " + position.z + ")" );
			m_tracer.addSubelement(reach, "orientation", "(" + orientation.x + ", " + orientation.y + ", " + orientation.z + ")" );
			m_tracer.addSubelement(reach, "act", act.toString() );
		}
			
		return act;
	}
	
	private IAct selectAct(List<IProposition> propositions)
	{
		
		//Construct a list of schemaPropositions from the list of actPropositions.
		
		List<IProposition> schemaPropositions = new ArrayList<IProposition>();	
		for (IProposition actProposition : propositions)
		{
			int w = actProposition.getWeight();
			int e = actProposition.getExpectation();
			IProposition schemaProposition = new Proposition(actProposition.getSchema(), w, e);
			int i = schemaPropositions.indexOf(schemaProposition);
			if (i == -1)
				schemaPropositions.add(schemaProposition);
			else
				schemaPropositions.get(i).update(w, e);
		}
		
		// sort by weighted proposition...
		Collections.sort(schemaPropositions);
		
		// count how many are tied with the  highest weighted proposition
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
		if (DETERMINISTIC)
			p = schemaPropositions.get(0); // Always take the first
		else
			p = schemaPropositions.get(m_rand.nextInt(count)); // Break the tie at random
		
		ISchema s = p.getSchema();
		
		IAct a = m_sensorimotorSystem.anticipateInteraction(p.getSchema(), p.getExpectation(), m_acts);
		
		a.setActivation(p.getWeight());
		
		System.out.println("Select:" + a);

		if (m_tracer != null)
			m_tracer.addEventElement("select", a.toString());

		return a;
	}
	
	/**
	 * @param activationList The list into which to add the act generated by the phenomena.
	 * @param phenomenaList The list of phenomena places that can activate acts.
	 */
	public ArrayList<IAct> getActs()
	{
		return m_acts;
	}
}
