package ernest;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

public class Effect implements IEffect 
{
	private int m_simulationStatus = 0;
	private String m_effect = "";
	private Point3d m_location = new Point3d();
	private Transform3D m_transformation = new Transform3D();
	
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

	public void setLocation(Point3d location) 
	{
		m_location = location;
	}

	public void setTransformation(Transform3D transformation) 
	{
		m_transformation = transformation;
	}

	public void setTransformation(float angle, float x) 
	{
		m_transformation.setIdentity();
		m_transformation.rotZ(angle);
		m_transformation.setTranslation(new Vector3f(x,0,0));
	}

	public String getEffect() 
	{
		return m_effect;
	}

	public Point3d getLocation() 
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

	public void trace(ITracer tracer) 
	{
		Object e = tracer.addEventElement("current_observation");
		tracer.addSubelement(e, "stimuli", m_effect + "");
	}
}
