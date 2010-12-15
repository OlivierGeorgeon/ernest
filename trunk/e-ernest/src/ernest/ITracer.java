package ernest;

/**
 * The generator of Ernest's activity traces. 
 * @author ogeorgeon
 */
public interface ITracer {

	public boolean writeLine(String line);
	public boolean writeHeader();
	public boolean writeFooter();
	
}
