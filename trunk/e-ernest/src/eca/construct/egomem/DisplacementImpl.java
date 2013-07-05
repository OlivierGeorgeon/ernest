package eca.construct.egomem;

import java.util.HashMap;
import java.util.Map;
import javax.media.j3d.Transform3D;
import utils.ErnestUtils;

/**
 * A transformation in spatial memory
 * @author Olivier
 */
public class DisplacementImpl implements Displacement {

	private static Map<String , Displacement> TRANSFORMATIONS = new HashMap<String , Displacement>() ;

	private String label;
	private Transform3D transform3D = new Transform3D();
	
	/**
	 * @param t The transformation that defines this displacement
	 * @return The displacement
	 */
	public static Displacement createOrGet(Transform3D t){
		String label = createKey(t);
		if (!TRANSFORMATIONS.containsKey(label))
			TRANSFORMATIONS.put(label, new DisplacementImpl(t));			
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

	private DisplacementImpl(String label){
		this.label = label;
	}
	
	private DisplacementImpl(Transform3D t){
		this.label = createKey(t);
		this.transform3D.set(t);
	}

	public String getLabel() {
		return label;
	}
	
//	public void setTransform3D(Transform3D transform3D){
//		this.transform3D.set(transform3D);
//	}
//	
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
			Displacement other = (Displacement)o;
			//ret = (other.getTransform3D().epsilonEquals(transform3D, .1));
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}

}
