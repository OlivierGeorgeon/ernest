package ernest;

import java.awt.Color;

/**
 * Ernest's attentional system.
 * Maintain lists of acts that represent Ernest's current situation
 * Control the current enaction.
 * @author ogeorgeon
 */
public interface IAttentionalSystem 
{

	/**
	 * @param tracer The tracer used to generate Ernest's activity traces
	 */
	public void setTracer(ITracer tracer);
	
	/**
	 * Get a description of Ernest's internal state.
	 * @return A representation of Ernest's internal state
	 */
	public String getInternalState();
	
	/**
	 * Ernest's central process.
	 * Choose the intentions to enact and control their enaction. 
	 * @param primitiveEnaction The actually enacted primitive act.
	 * @return The primitive schema to enact in the environment 
	 */
	public ISchema step(IAct primitiveEnaction); 
	
	/**
	 * The internal effect of the reflex mechanism of drinking when Ernest tastes water.
	 * @param landmark The landmark that is drunk.
	 */
	public void drink(ILandmark landmark);

	/**
	 * The internal effect of the reflex mechanism of eating when Ernest tastes food.
	 * @param landmark The landmark that is eaten.
	 */
	public void eat(ILandmark landmark);
	
	/**
	 * The internal effect of bumping a wall.
	 * @param landmark The landmark that is bumped.
	 */
	public void bump(ILandmark landmark);
	
	/**
	 * Tells if a color is inhibited by Ernest's attentional system.
	 * @param color The color to test.
	 * @return true if the color is inhibited.
	 */
	public boolean isInhibited(Color color);
	
	/**
	 * Tells if the landmark is inhibited by Ernest's attentional system.
	 * @param landmark The landmark to test.
	 * @return true if the landmark is inhibited.
	 */
	public boolean isInhibited(ILandmark landmark);
	
	/**
	 * Check at a landmark. 
	 * @param landmark The landmark to check
	 */
	public void check(ILandmark landmark);
	
	public boolean isThirsty();
	public void visit(ILandmark landmark);
}
