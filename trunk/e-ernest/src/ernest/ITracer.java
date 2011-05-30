package ernest;


/**
 * Generates Ernest's activity traces. 
 * @author ogeorgeon
 */
public interface ITracer<EventElement> {

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
	 * Closes the current event.
	 * @param t the time stamp
	 */
	public void finishEvent();
	
	/**
	 * Add a new property to the current event
	 * @param name The property's name
	 * @param value The property's value
	 */
	public EventElement addEventElement(String name);
	
	
	/**
	 * Add a new property to the current event
	 * If display = 'no' then the subelements are not displayed by the XMLStraemTracer
	 * @param name
	 * @param display 
	 * @return
	 */
	public EventElement addEventElement(String name, boolean display);

	public void addEventElement(String name, String value);

	public EventElement addSubelement(EventElement element, String name);
	public void addSubelement(EventElement element, String name, String textContent);

	/**
	 * Create an event that can be populated using its reference.
	 * @param source The source of the event: Ernest or user
	 * @param type The event's type.
	 * @param t The event's time stamp.
	 * @return The pointer to the event.
	 */
	public EventElement newEvent(String source, String type, int t);

}
