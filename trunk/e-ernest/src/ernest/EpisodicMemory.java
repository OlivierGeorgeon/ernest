package ernest;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.w3c.dom.Element;

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
	
	/** A list of all the schemas ever created ... */
	private List<ISchema> m_schemas = new ArrayList<ISchema>(1000);

	/** A list of all the acts ever created. */
	public List<IAct> m_acts = new ArrayList<IAct>(2000);
	
	/** A list of all the landmarks ever identified. */
	public List<ILandmark> m_landmarks = new ArrayList<ILandmark>(20);
	
	/** Random generator used to break a tie when selecting a schema... */
	private static Random m_rand = new Random(); 

	/** Counter of learned schemas for tracing */
	private int m_learnCount = 0;
	
	/**
	 * @param tracer The tracer
	 */
	public void setTracer(ITracer tracer)
	{
		m_tracer = tracer;
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
				newSchema.incWeight();
				//System.out.println("learned " + newSchema.getLabel());
				
					// Created acts are part of the context 
					// if their context and intention have passed the regularity
					// if they are based on reliable noèmes
				if ((contextAct.getConfidence() == Ernest.RELIABLE) &&
  				   (intentionAct.getConfidence() == Ernest.RELIABLE))
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
	public IAct selectAct(List<IAct> activationList)
	{

		List<IProposition> proposals = new ArrayList<IProposition>();	
		
		// Browse all the existing schemas 
		Element activations = m_tracer.addEventElement("activations", "");
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
						m_tracer.addSubelement(activations, "activation", s + " s=" + s.getIntentionAct().getSatisfaction());
						System.out.println("Activate " + s + " s=" + s.getIntentionAct().getSatisfaction());
					}
				}
				
				// Activated schemas propose their intention
				if (activated)
				{
					// The weight is the proposing schema's weight multiplied by the proposed act's satisfaction
					int w = s.getWeight() * s.getIntentionAct().getSatisfaction();
					// The expectation is the proposing schema's weight signed with the proposed act's status  
					int e = s.getWeight() * (s.getIntentionAct().getStatus() ? 1 : -1);
					
					// If the intention is reliable
					if ((s.getIntentionAct().getConfidence() == Ernest.RELIABLE ) &&						 
						(s.getIntentionAct().getSchema().getLength() <= Ernest.SCHEMA_MAX_LENGTH ))
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
						if (!s.getIntentionAct().getSchema().isPrimitive())
						{
							// only if the intention's intention is positive (this is some form of positive anticipation)
							if (s.getIntentionAct().getSchema().getIntentionAct().getSatisfaction() > 0)
							{
								IProposition p = new Proposition(s.getIntentionAct().getSchema().getContextAct().getSchema(), w, e);
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

		System.out.println("Propose: ");
		Element proposalElmt = m_tracer.addEventElement("proposals", "");
		for (IProposition p : proposals)
		{
			m_tracer.addSubelement(proposalElmt, "proposal", p.toString());
			System.out.println(p);
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

		IProposition p = proposals.get(m_rand.nextInt(count));
		
		ISchema s = p.getSchema();
		
		IAct a = (p.getExpectation() >= 0 ? s.getSucceedingAct() : s.getFailingAct());
		
		// Activate the selected act in Episodic memory.
		// (The act's activation is set equal to its proposition's weight)
		a.setActivation(p.getWeight());
		
		// TODO at some point we may implement a smarter mechanism to spread the activation to sub-acts.

		System.out.println("Select:" + a);

		return a ;
	}

	/**
	 * Add a landmark to episodic memory if it does not already exist
	 * @param red Component of the landmark's color
	 * @param green Component of the landmark's color
	 * @param blue Component of the landmark's color
	 * @return the new landmark if created or the already existing landmark
	 */
	public ILandmark addLandmark(int red, int green, int blue)
	{
		ILandmark l = new Landmark(red,green,blue);
		
		int i = m_landmarks.indexOf(l);
		if (i == -1)
			// The landmark does not exist
			m_landmarks.add(l);
		else 
			// The landmark already exists: return a pointer to it.
			l =  m_landmarks.get(i);
		return l;
	}
	
	/**
	 * Add a landmark to episodic memory if it does not already exist
	 * @param color The landmark's color
	 * @return the new landmark if created or the already existing landmark
	 */
	public ILandmark addLandmark(Color color)
	{
		return addLandmark(color.getRed() ,color.getGreen() ,color.getBlue());
	}
	
	public ILandmark getLandmark(Color color)
	{
		ILandmark l = new Landmark(color.getRed(), color.getGreen(), color.getBlue());
		if (m_landmarks.contains(l))
			return l;
		else
			return null;
	}
	
	public void UpdateDistanceToWater(int clock)
	{
		for (ILandmark l : m_landmarks)
			l.setDistanceToWater(clock);
	}

	public void UpdateDistanceToFood(int clock)
	{
		for (ILandmark l : m_landmarks)
			l.setDistanceToFood(clock);
	}
}
