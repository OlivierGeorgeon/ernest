package eca.construct.egomem;

import java.util.HashMap;
import java.util.Map;
import javax.media.j3d.Transform3D;

import eca.construct.Area;
import utils.ErnestUtils;

/**
 * A transformation in spatial memory
 * @author Olivier
 */
public class DisplacementImpl implements Displacement {

	private static Map<String , Displacement> TRANSFORMATIONS = new HashMap<String , Displacement>() ;

	private String label;
	private Transform3D transform3D = new Transform3D();
	private Area preArea = null;
	private Area postArea = null;
	
	/**
	 * @param preArea The area before displacement
	 * @param postArea The area after displacement
	 * @return The displacement
	 */
	public static Displacement createOrGet(Area preArea, Area postArea){
		String label = createKey(preArea, postArea);
		if (!TRANSFORMATIONS.containsKey(label))
			TRANSFORMATIONS.put(label, new DisplacementImpl(preArea, postArea));			
		return TRANSFORMATIONS.get(label);
	}
	
	private static String createKey(Area preArea, Area postArea){
		return preArea.getLabel() + postArea.getLabel();
	}
	
	/**
	 * @param t The transformation that defines this displacement
	 * @return The displacement
	 */
//	public static Displacement createOrGet(Transform3D t){
//		String label = createKey(t);
//		if (!TRANSFORMATIONS.containsKey(label))
//			TRANSFORMATIONS.put(label, new DisplacementImpl(t));			
//		return TRANSFORMATIONS.get(label);
//	}
	
	/**
	 * @param t The transformation that defines this displacement
	 * @return The displacement
	 */
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

	private DisplacementImpl(Area preArea, Area postArea){
		this.preArea = preArea;
		this.postArea = postArea;
		this.label = createKey(preArea, postArea);
	}
	
	private DisplacementImpl(Transform3D t){
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
			Displacement other = (Displacement)o;
			//ret = (other.getTransform3D().epsilonEquals(transform3D, .1));
			ret = (other.getLabel().equals(this.label));
		}
		
		return ret;
	}

	public Area getPreArea() {
		return preArea;
	}

	public Area getPostArea() {
		return postArea;
	}

}
