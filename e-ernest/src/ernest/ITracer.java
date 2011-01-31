package ernest;

/**
 * Generates Ernest's activity traces. 
 * @author ogeorgeon
 */
public interface ITracer {

	/**
	 * @param line The line to trace
	 * @return true if tracer ok
	 */
	public boolean writeLine(String line);
	
	/**
	 * Initialize the tracer
	 * @return true if tracer ok
	 */
	public boolean writeHeader();
	
	/**
	 * Cose the tracer
	 * @return true if tracer ok
	 */
	public boolean writeFooter();
	
}
