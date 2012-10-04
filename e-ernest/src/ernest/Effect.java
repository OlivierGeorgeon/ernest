package ernest;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import utils.ErnestUtils;

public class Effect implements IEffect 
{
	private int m_simulationStatus = 0;
	private String m_effect = "";
	private Point3f m_location = new Point3f();
	private Transform3D m_transformation = new Transform3D();
	private int m_color = 0;
	
	/**
	 * @param effect
	 */
//	public Effect(char effect)
//	{
//		m_effect = effect;
//	}
	
	public void setEffect(String effect) 
	{
		m_effect = effect;
	}

	public void setLocation(Point3f location) 
	{
		m_location.set(location);
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
	
	public void setColor(int color)
	{
		m_color = color;
	}

	public String getEffect() 
	{
		return m_effect;
	}

	public Point3f getLocation() 
	{
		return m_location;
	}

	public Transform3D getTransformation() 
	{
		return m_transformation;
	}

	public void setSimulationStatus(int simulationStatus) 
	{
		m_simulationStatus = simulationStatus;
	}

	public int getSimulationStatus() 
	{
		return m_simulationStatus;
	}

	public int getColor()
	{
		return m_color;
	}
	
	public void trace(ITracer tracer) 
	{
		tracer.addEventElement("touch_color", ErnestUtils.hexColor(m_color));
		Object e = tracer.addEventElement("current_observation");
		tracer.addSubelement(e, "stimuli", m_effect + "");
	}
}
