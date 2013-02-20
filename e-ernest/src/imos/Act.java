package imos;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import ernest.Ernest;


/**
 * An act is the association of a schema with the feedback the agent receives 
 * when trying to enact that schema.  
 * The term Act and the term interaction are used indifferently.  
 * @author mcohen
 * @author ogeorgeon
 */
public class Act implements IAct
{
	/** The act's satisfaction value. Represents Ernest's satisfaction to enact the act */
	private int m_satisfaction = 0;
	
	/** The act's schema */
	private ISchema m_schema = null;
	
	/** The schema that prescribes this act during enaction */
	private ISchema m_prescriberSchema = null;
	
	/** The effect label */
	private String m_effectLabel ="";
	
	/** The act's label */
	private String m_label = "";
	
	/** Ernest's confidence in this act. Only RELIABLE acts generate higher-level learning  */
	private int m_confidence = Imos.HYPOTHETICAL;
	
	/** The act is activated for enaction  */
	private int m_activation = 0;
	
	/** The length of the act's schema  */
	private int m_length = 1;
	
	private int m_color = Ernest.PHENOMENON_EMPTY;
	private Point3f m_startPosition = new Point3f();
	
	private Transform3D m_transform = new Transform3D();
	
	/**
	 * Constructor for a succeeding act. 
	 * @param s The act's schema
	 * @return the created act
	 */
	public static IAct createCompositeSucceedingAct(ISchema s)
	{
		if (s.isPrimitive())
		{
			System.out.println("Error creating a composite act. Schema " + s + " is not a composite schema.");
			return null;
		}
		else
		{
			String label = "(" + s.getLabel() +")";
			int satisfaction = s.getContextAct().getSatisfaction() + s.getIntentionAct().getSatisfaction();
		
			return new Act(label, s, "", true, satisfaction, Imos.HYPOTHETICAL);
		}
	}
	
	/**
	 * Constructor for a failing act. 
	 * @param s The act's schema.
	 * @param satisfaction The failing satisfaciton.
	 * @return the created act.
	 */
	public static IAct createCompositeFailingAct(ISchema s, int satisfaction)
	{
		if (s.isPrimitive())
		{
			System.out.println("Error creating a composite act. Schema " + s + " is not a composite schema.");
			return null;
		}
		else
		{
			String label = "[" + s.getLabel() +"]";
			// The failing act is RELIABLE because its schema had to be reliable to be enacted and 
			// making it possible to experience its failure.
			
			return new Act(label, s, "", false, satisfaction, Imos.RELIABLE);
		}
	}
	
	/**
	 * Create an act.
	 * @param moveLabel The move's label.
	 * @param effectLabel The effect's label.
	 * @param s The act's schema. 
	 * @param satisfaction The act's satisfaction value.
	 * @return The created act.
	 */
	public static IAct createPrimitiveAct(String moveLabel, String effectLabel, ISchema s, int satisfaction)
	{
		return new Act(moveLabel + effectLabel, s, effectLabel, true, satisfaction, Imos.RELIABLE);
	}
	
	/**
	 * The abstract constructor for an act
	 * @param label The act's label
	 * @param s The act's schema if any
	 * @param effectLabel the code of the effect.
	 * @param status The act's status if any: True for success, false for failure
	 * @param type the module
	 * @param confidence The degree of confidence Ernest has in this act
	 */
	protected Act(String label, ISchema s, String effectLabel, boolean status, int satisfaction, int confidence)
	{
		m_label = label;
		m_schema = s;
		m_effectLabel = effectLabel;
		//m_status = status;
		m_satisfaction = satisfaction;
		m_confidence = confidence;
		if (s == null)
			m_length = 1;
		else
		{
			m_length = s.getLength();
			// TODO manage transformation of failing acts
			if (!s.isPrimitive())
			{
				m_transform = new Transform3D(s.getIntentionAct().getTransform());
				m_transform.mul(s.getContextAct().getTransform());
			}
		}
	}
	
	public void setSatisfaction(int s)         
	{ 
		m_satisfaction = s; 
	}
	
	public void setConfidence(int c)           
	{ 
		m_confidence = c; 
	}
	
	public void setPrescriberSchema(ISchema s) 
	{ 
		m_prescriberSchema = s; 
	}
	
	public void setActivation(int a)           
	{ 
		m_activation = a; 
	}
	
	public int getSatisfaction()           
	{ 
		return m_satisfaction; 
	}
	
	public int getConfidence()             
	{ 
		return m_confidence; 
	}
	
	public ISchema getPrescriberSchema()       
	{ 
		return m_prescriberSchema; 
	}
	
	public ISchema getSchema()                 
	{ 
		return m_schema; 
	}
	
	public int getActivation()             
	{ 
		return m_activation; 
	}
	
	public int getLength()                 
	{ 
		return m_length; 
	}

	/**
	 * @return The act's string representation
	 */
	public String getLabel()
	{
		return m_label;
	}
	
	public String toString()
	{
		String s= m_label;
		//if (m_schema != null)
		//	s = String.format("(S%s %s s=%s)", getSchema().getId() , getLabel(), getSatisfaction());  
		return s;
	}
	
	/**
	 * Acts are equal if they have the same label. 
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			IAct other = (IAct)o;
			ret = (//other.getSchema() == getSchema() &&
				   //other.getStatus() == getStatus() &&
				   other.getLabel().equals(getLabel()));
		}
		
		return ret;
	}
	
	/**
	 * The greatest act is that that has the greatest activation. 
	 */
	public int compareTo(IAct a) 
	{
		return new Integer(a.getActivation()).compareTo(m_activation);
	}

	public void setColor(int color) 
	{
		m_color = color;
	}

	public int getColor() 
	{
		return m_color;
	}

	/**
	 * The end position is the start position to which the schema's transformation is applied. 
	 */
//	public Point3f getEndPosition() 
//	{
//		Point3f position = new Point3f(getStartPosition()); 
//		getTransform().transform(position);
//		
//		return position;
//	}

	public void setPosition(Point3f position) 
	{
		m_startPosition.set(position);
	}

	/**
	 * Computes the start position of this act
	 * If primitive schema, return the predefined start position.
	 * If composite schema, return the start position of its intention
	 * to which the invert transformation of its context is applied.
	 */
	public Point3f getPosition() 
	{
		Point3f startPosition = new Point3f();
		if (m_schema.isPrimitive())
			startPosition.set(m_startPosition);
		else
		{
			startPosition.set(m_schema.getIntentionAct().getPosition());
			Transform3D tf = new Transform3D(m_schema.getContextAct().getTransform());
			tf.invert();
			tf.transform(startPosition);
		}
		return startPosition;
	}

	/**
	 * This act concerns only one place if
	 * - it is a primitive act
	 * - or its start position is superimposed with the agent
	 * - or its start position corresponds to its end position after execution. 
	 * @return true if this act concerns only one place. 
	 */
	public boolean concernOnePlace() 
	{
		boolean concernOnePlace = false;
		
		if (m_schema.isPrimitive())
			concernOnePlace = true;
		else
		{
			Point3f startPosition = new Point3f(m_schema.getContextAct().getPosition());
			if (startPosition.equals(new Point3f()))
				concernOnePlace = true;
			
			Point3f endPosition = new Point3f(startPosition);
			Transform3D tf = new Transform3D(m_transform);
			tf.invert();
			tf.transform(endPosition);
			
			if (endPosition.epsilonEquals(startPosition, .1f))
				concernOnePlace = true;
				
			
//			Vector3f destinationPosition = new Vector3f(m_schema.getIntentionAct().getEndPosition());
//			destinationPosition.sub(m_schema.getContextAct().getTranslation());
//			destinationPosition.sub(m_schema.getIntentionAct().getTranslation());
//			if (m_schema.getContextAct().getStartPosition().equals(destinationPosition))
//				concernOnePlace = true;
		}
		
		// Do not use longer schemes to construct compresences.
		if (m_schema.getLength() > 2) concernOnePlace = false;
		
		return concernOnePlace;
	}

	public void setTransform(Transform3D transform) 
	{
		m_transform = transform;
	}

	public Transform3D getTransform() 
	{
		return m_transform;
	}

	public void setEffectLabel(String effectLabel) 
	{
		m_effectLabel = effectLabel;
	}

	public String getEffectLabel() 
	{
		return m_effectLabel;
	}
}
