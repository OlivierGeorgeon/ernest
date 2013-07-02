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
	
//	public static Displace displace(Transform3D t){
//		Displace transform = IDENTITY;
//		float angle = ErnestUtils.angle(t);
//		if (Math.abs(angle) > .1){
//			if ( angle > 0){		
//				transform = SHIFT_LEFT;
//				transform.setTransform3D(t);
//			}
//			else{ 		
//				transform = SHIFT_RIGHT;
//				transform.setTransform3D(t);
//			}
//		}
//
//		return transform;
//	}
	
	public static Displace createOrGet(Transform3D t){
		String label = createKey(t);
		if (!TRANSFORMATIONS.containsKey(label))
			TRANSFORMATIONS.put(label, new DisplaceImpl(t));			
		return TRANSFORMATIONS.get(label);
	}
	
	private static String createKey(Transform3D t) {
		String key = "";
		float angle = ErnestUtils.angle(t);
		if (Math.abs(angle) > .1){
			if ( angle > 0)	key = "^";
			else			key ="v";
		}
		else{
			if (ErnestUtils.translationX(t) > .5) key =".";
			else key = "<";
		}
		return key;
	}
	private DisplaceImpl(String label){
		this.label = label;
	}
	
	private DisplaceImpl(Transform3D t){
		this.label = createKey(t);
		this.transform3D.set(t);
	}

	public String getLabel() {
		return label;
	}
	
	public void setTransform3D(Transform3D transform3D){
		this.transform3D.set(transform3D);
	}
	
	public Transform3D getTransform3D(){
		Transform3D t = this.transform3D;
		if (this.label.equals("<")) t = new Transform3D();
		return t;
	}

	/**
	 * Displacements are equal if they have the same label. 
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
			//ret = (other.getTransform3D().epsilonEquals(transform3D, .1));
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}

}
