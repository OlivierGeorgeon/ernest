package ernest;

/**
 * A logger supports the writing of lines into a log file. 
 * @author ogeorgeon
 */
public interface ITracer {

	public boolean writeLine(String line);
	public boolean writeHeader();
	public boolean writeFooter();
	
}
