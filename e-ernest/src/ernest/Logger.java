package ernest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class Logger implements ILogger
{

	/**
	 * Logs trace into an xml file
	 * @author ogeorgeon 
	 */

	private  File logFile = null;
	private boolean isLogging = false;

	public static ILogger createLogger(String logFileName, boolean l) 
	{
		return new Logger(logFileName, l);
	}
	private Logger(String logFileName, boolean l)
	{ 	
		isLogging = l;
		if (isLogging)
		{
			try 
			{
				logFile = new File(logFileName);
				if (logFile.exists()) 
				{
					logFile.delete();
					logFile.createNewFile();
				}
				writeLine("<?xml version='1.0'?>\n");
				writeLine("<?xml-stylesheet href='trace.xsl' type='text/xsl'?>\n");
				writeLine("<xml>\n");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	} 
	
	/**
	 * Prints a line to the log file 
	 * @author ogeorgeon 
	 */
	public boolean writeLine(String Line) 
	{
		boolean r = false;
		if (isLogging) 
		{
			try 
			{
				FileWriter writer = new FileWriter(logFile, true);
				// writer.write(Line + "\n");
				writer.write(Line );
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
		else 
			r = true;
		return r;
	}
}
