package ernest;


import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import eca.Act;

import utils.ErnestUtils;

public class Effect implements IEffect 
{
	private String m_label = "";
	private Point3f m_location = new Point3f();
	private Transform3D m_transformation = new Transform3D();
	private int m_color = 0xFFFFFF;
	private float m_angle =0;
	private String enactedInteractionLabel;
	
	public void setLabel(String label) 
	{
		m_label = label;
	}

	public String getLabel() 
	{
		return m_label;
	}

	public void setEnactedInteractionLabel(String label) 
	{
		this.enactedInteractionLabel = label;
	}

	public String getEnactedInteractionLabel() 
	{
		return this.enactedInteractionLabel;
	}

	public void setLocation(Point3f location) 
	{
		m_location.set(location);
	}

	public Point3f getLocation() 
	{
		return m_location;
	}

//	public void setTransformation(Transform3D transformation) 
//	{
//		m_transformation.set(transformation);
//	}

	public void setTransformation(float angle, float x) 
	{
		m_transformation.setIdentity();
		m_angle = angle;
		m_transformation.rotZ(angle);
		m_transformation.setTranslation(new Vector3f(x,0,0));
	}
	
	public Transform3D getTransformation() 
	{
		return m_transformation;
	}

	public void setColor(int color)
	{
		m_color = color;
	}

	public int getColor()
	{
		return m_color;
	}
	
	public void trace(ITracer tracer) 
	{
		if (tracer != null)
		{
			Object e = tracer.addEventElement("effect");
			tracer.addSubelement(e, "label", m_label + "");
			tracer.addSubelement(e, "color", ErnestUtils.hexColor(m_color));
			tracer.addSubelement(e, "position_x", ErnestUtils.format(m_location.x,0));
			tracer.addSubelement(e, "position_y", ErnestUtils.format(m_location.y,0));
			Matrix3f rot = new Matrix3f();
			Vector3f trans = new Vector3f();
			m_transformation.get(rot, trans);
			tracer.addSubelement(e, "translation_x", ErnestUtils.format(trans.x,0));
			tracer.addSubelement(e, "translation_y", ErnestUtils.format(trans.y,0));
			//tracer.addSubelement(e, "rotation", m_angle +"");
			tracer.addSubelement(e, "rotation", ErnestUtils.format((float) - Math.atan2(rot.m01,rot.m00),2));
		}
	}
}
