package ernest;

/**
 * Generates Ernest's activity traces. 
 * @author ogeorgeon
 */
public interface ITracer {

	/**
	 * Cose the tracer
	 * @return true if tracer ok
	 */
	public boolean close();
	
	/**
	 * Create a new event
	 * @param t the time stamp
	 */
	public void startNewEvent(int t);
	
	/**
	 * Add a new property to the current event
	 * @param name The property's name
	 * @param value The property's value
	 */
	public void addEventProperty(String name, String value);

}
