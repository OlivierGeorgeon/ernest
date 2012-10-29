package ernest;

//import imos.ActProposition;
import imos.IAct;
import imos.IActProposition;
//import imos.IProposition;
//import imos.ISchema;
import imos.Imos;
//import imos.Proposition;

//import java.awt.Color;
import java.util.ArrayList;

import javax.media.j3d.Transform3D;
//import javax.swing.JFrame;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

//import spas.IBundle;
//import spas.IPlace;
import spas.ISpatialMemory;
import spas.LocalSpaceMemory;
//import spas.Place;
//import spas.Spas;
//import utils.ErnestUtils;
import spas.Place;

/**
 * Implement Ernest 12.0's sensorimotor system.
 * The binary sensorimotor system plus local space memory tracking.
 * @author ogeorgeon
 */
public class Ernest12SensorimotorSystem extends BinarySensorymotorSystem 
{
	/** The observation */
    
    private int m_satisfaction = 0;
    
	//private JFrame m_frame;

    private String sensePixel(int previousPixel, int currentPixel) 
    {
            String feature = " ";
            int satisfaction = 0;
            
            // arrived
            if (previousPixel > currentPixel && currentPixel == 0)
            {
                    feature = "x";
                    satisfaction = 10;
            }
            
            // closer
            else if (previousPixel < Ernest.INFINITE && currentPixel < previousPixel)
            {
                    feature = "+";
                    satisfaction = 10;
            }

            // appear
            else if (previousPixel == Ernest.INFINITE && currentPixel < Ernest.INFINITE)
            {
                    feature = "*";
                    satisfaction = 15;
            }
            
            // disappear
            else if (previousPixel < Ernest.INFINITE && currentPixel == Ernest.INFINITE)
            {
                    feature = "o";
                    satisfaction = -15;
            }

            System.out.println("Sensed " + "prev=" + previousPixel + "cur=" + currentPixel + " feature " + feature);
            
            m_satisfaction += satisfaction;

            return feature;
    }
    
	/**
	 * The agent's self model is hard coded in the interactions.
	 * TODO The phenomenon code, position, and spatial transformation should be learned rather than hard coded.
	 */
	public IAct addInteraction(String schemaLabel, String stimuliLabel, int satisfaction)
	{
		// Create the act in imos
		
		IAct act = m_imos.addInteraction(schemaLabel, stimuliLabel, satisfaction);
		
		// Add the spatial properties ========
		
		if (schemaLabel.equals(">"))
		{
			Transform3D tf = new Transform3D();
			if (stimuliLabel.indexOf("f") >= 0)
			{
				act.setColor(Ernest.PHENOMENON_WALL);
				//act.setColor(0xFF0000);
				act.setPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			}
			else if (stimuliLabel.indexOf("b") >= 0)
			{
				//act.setColor(0xFF0000);
				act.setColor(Ernest.PHENOMENON_BRICK);
				act.setPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			}
			else if (stimuliLabel.indexOf("a") >= 0)
			{
				act.setColor(Ernest.PHENOMENON_ALGA);
				//act.setColor(0x73E600);
				act.setPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				tf.setTranslation(new Vector3f(-1,0,0));
			}
			else if (stimuliLabel.equals("++t"))
			{
				act.setColor(Ernest.PHENOMENON_FISH);
				act.setPosition(new Point3f(4,0,0));
				tf.setTranslation(new Vector3f(-1,0,0));
			}
			else if (stimuliLabel.equals(" +t"))
			{
				act.setColor(Ernest.PHENOMENON_FISH);
				act.setPosition(new Point3f(3,-3,0));
				tf.setTranslation(new Vector3f(-1,0,0));
			}
			else if (stimuliLabel.equals("+ t"))
			{
				act.setColor(Ernest.PHENOMENON_FISH);
				act.setPosition(new Point3f(3,3,0));
				tf.setTranslation(new Vector3f(-1,0,0));
			}
			else 
			{
				act.setColor(Ernest.PHENOMENON_EMPTY);
				act.setPosition(LocalSpaceMemory.DIRECTION_AHEAD);
				tf.setTranslation(new Vector3f(-1,0,0));
			}
			act.setTransform(tf);
		}

		if (schemaLabel.equals("<"))
		{
			if (stimuliLabel.indexOf("t") >= 0 || stimuliLabel.equals("  "))
			{
				act.setColor(Ernest.PHENOMENON_EMPTY);
				act.setPosition(LocalSpaceMemory.DIRECTION_BEHIND);
				Transform3D tf = new Transform3D();
				tf.rotZ(0);
				tf.setTranslation(new Vector3f(1,0,0));
				act.setTransform(tf);
			}
			else
			{
				act.setColor(Ernest.PHENOMENON_WALL);
				act.setPosition(LocalSpaceMemory.DIRECTION_BEHIND);
				Transform3D tf = new Transform3D();
				tf.rotZ(0);
				tf.setTranslation(new Vector3f(0,0,0));
				act.setTransform(tf);
			}
		}

		if (act.getLabel().equals("^* f"))
		{
			act.setColor(Ernest.PHENOMENON_FISH);
			act.setPosition(new Point3f(3,3,0));
			Transform3D tf = new Transform3D();
			tf.rotZ(- Math.PI / 2);
			act.setTransform(tf);
		}
		else if (schemaLabel.equals("^"))
		{
			act.setColor(Ernest.PHENOMENON_EMPTY);
			act.setPosition(LocalSpaceMemory.DIRECTION_HERE);
			Transform3D tf = new Transform3D();
			tf.rotZ(- Math.PI / 2);
			act.setTransform(tf);
		}
		
		if (act.getLabel().equals("v *f"))
		{
			act.setColor(Ernest.PHENOMENON_FISH);
			act.setPosition(new Point3f(3,-3,0));
			Transform3D tf = new Transform3D();
			tf.rotZ(Math.PI / 2);
			act.setTransform(tf);
		}
		else if (schemaLabel.equals("v"))
		{
			act.setColor(Ernest.PHENOMENON_EMPTY);
			act.setPosition(LocalSpaceMemory.DIRECTION_HERE);
			Transform3D tf = new Transform3D();
			tf.rotZ(Math.PI / 2);
			act.setTransform(tf);
		}
		
		if (schemaLabel.equals("/") )
		{
			act.setPosition(LocalSpaceMemory.DIRECTION_LEFT);
			if (stimuliLabel.indexOf("f") >= 0 || stimuliLabel.equals("  "))
				act.setColor(Ernest.PHENOMENON_EMPTY);
			else if (stimuliLabel.indexOf("a") >= 0 )
				act.setColor(Ernest.PHENOMENON_ALGA);
			else
				act.setColor(Ernest.PHENOMENON_WALL);
		}
		
		if (schemaLabel.equals("-"))
		{
			act.setPosition(LocalSpaceMemory.DIRECTION_AHEAD);
			if (stimuliLabel.indexOf("f") >= 0 || stimuliLabel.equals("  "))
				act.setColor(Ernest.PHENOMENON_EMPTY);
			else if (stimuliLabel.indexOf("a") >= 0 )
				act.setColor(Ernest.PHENOMENON_ALGA);
			else if (stimuliLabel.indexOf("b") >= 0 )
				act.setColor(Ernest.PHENOMENON_BRICK);
			else
				act.setColor(Ernest.PHENOMENON_WALL);
		}
		
		if (schemaLabel.equals("\\"))
		{
			act.setPosition(LocalSpaceMemory.DIRECTION_RIGHT);
			if (stimuliLabel.indexOf("f") >= 0 || stimuliLabel.equals("  "))
				act.setColor(Ernest.PHENOMENON_EMPTY);
			else if (stimuliLabel.indexOf("a") >= 0 )
				act.setColor(Ernest.PHENOMENON_ALGA);
			else
				act.setColor(Ernest.PHENOMENON_WALL);
		}

		return act;
	}

}
