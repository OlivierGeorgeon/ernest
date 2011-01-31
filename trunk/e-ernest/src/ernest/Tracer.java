package ernest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * This tracer logs the trace into a text file.
 * This text file can contain xml if xml tags are included in the logged lines and if the header and footer are used.   
 * @author ogeorgeon 
 */
public class Tracer implements ITracer
{

	private  File logFile = null;

	/**
	 * Initialize the tracer.
	 * @param logFileName The name of the file where to log the trace.
	 */
	public Tracer(String logFileName)
	{ 	
		try 
		{
			logFile = new File(logFileName);
			if (logFile.exists()) 
			{
				logFile.delete();
				logFile.createNewFile();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
	/**
	 * Prints a line to the log file. 
	 * @param line The line to print in the log file.
	 * @return True if success, false if failure.
	 */
	public boolean writeLine(String line) 
	{
		boolean r = false;
		if (logFile != null)
		{
			try 
			{
				FileWriter writer = new FileWriter(logFile, true);
				writer.write(line );
				writer.close();
				r = true;
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return r;
	}

	/**
	 * Print the the log file's header
	 * @return True if success, false if failure.
	 */
	public boolean writeHeader() 
	{
		boolean r = false;
		if (logFile != null)
		{
			try 
			{
				FileWriter writer = new FileWriter(logFile, true);
				writeLine("<?xml version='1.0'?>\n");
				writeLine("<?xml-stylesheet href='trace.xsl' type='text/xsl'?>\n");
				writer.close();
				r = true;
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return r;
	}

	/**
	 * Print the the log file's footer
	 * @return True if success, false if failure.
	 */
	public boolean writeFooter() 
	{
		boolean r = false;
		if (logFile != null)
		{
			try 
			{
				FileWriter writer = new FileWriter(logFile, true);
				writeLine("<xml>\n");
				writer.close();
				r = true;
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return r;
	}
}
