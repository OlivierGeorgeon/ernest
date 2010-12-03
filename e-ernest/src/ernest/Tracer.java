package ernest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class Tracer implements ITracer
{

	/**
	 * This tracer logs the trace into an xml file
	 * @author ogeorgeon 
	 */

	private  File logFile = null;
	private boolean isLogging = false;

	public Tracer(String logFileName, boolean l)
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

	/**
	 * Print the the log file's header
	 * @author ogeorgeon 
	 */
	public boolean writeHeader() 
	{
		boolean r = false;
		if (isLogging) 
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
		else 
			r = true;
		return r;
	}

	/**
	 * Print the the log file's footer
	 * @author ogeorgeon 
	 */
	public boolean writeFooter() 
	{
		boolean r = false;
		if (isLogging) 
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
		else 
			r = true;
		return r;
	}
}
