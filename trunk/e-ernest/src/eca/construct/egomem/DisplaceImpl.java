package eca.construct.egomem;

import java.util.HashMap;
import java.util.Map;
import javax.media.j3d.Transform3D;
import utils.ErnestUtils;

/**
 * A transformation in spatial memory
 * @author Olivier
 */
public class DisplaceImpl implements Displace {

	private static Map<String , Displace> TRANSFORMATIONS = new HashMap<String , Displace>() ;

	private String label;
	private Transform3D transform3D = new Transform3D();
	
	/**
	 * Create or get an action from its label.
	 * @param label The action's label
	 * @return The created or retrieved action.
	 */
	public static Displace createOrGet(String label){
		if (!TRANSFORMATIONS.containsKey(label))
			TRANSFORMATIONS.put(label, new DisplaceImpl(label));			
		return TRANSFORMATIONS.get(label);
	}
	
	public static Displace displace(Transform3D t){
		Displace transform = IDENTITY;
		float angle = ErnestUtils.angle(t);
		if (Math.abs(angle) > .1){
			if ( angle > 0){		
				transform = SHIFT_LEFT;
				transform.setTransform3D(t);
			}
			else{ 		
				transform = SHIFT_RIGHT;
				transform.setTransform3D(t);
			}
		}

		return transform;
	}
	
	private DisplaceImpl(String label){
		this.label = label;
	}
	

	public String getLabel() {
		return label;
	}
	
	public void setTransform3D(Transform3D transform3D){
		this.transform3D.set(transform3D);
	}
	
	public Transform3D getTransform3D(){
		return this.transform3D;
	}

	/**
	 * Transformations are equal if they have the same label. 
	 */
	public boolean equals(Object o){
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			Displace other = (Displace)o;
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}

}
