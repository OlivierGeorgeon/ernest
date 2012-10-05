package ernest;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import utils.ErnestUtils;

public class Effect implements IEffect 
{
	private String m_label = "";
	private Point3f m_location = new Point3f();
	private Transform3D m_transformation = new Transform3D();
	private int m_color = 0xFFFFFF;
	
	public void setLabel(String label) 
	{
		m_label = label;
	}

	public String getLabel() 
	{
		return m_label;
	}

	public void setLocation(Point3f location) 
	{
		m_location.set(location);
	}

	public Point3f getLocation() 
	{
		return m_location;
	}

	public void setTransformation(Transform3D transformation) 
	{
		m_transformation.set(transformation);
	}

	public void setTransformation(float angle, float x) 
	{
		m_transformation.setIdentity();
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
			tracer.addEventElement("primitive_enacted_color", ErnestUtils.hexColor(m_color));
			Object e = tracer.addEventElement("current_observation");
			tracer.addSubelement(e, "stimuli", m_label + "");
		}
	}
}
