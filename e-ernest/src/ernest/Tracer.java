package ernest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.w3c.dom.Element;

/**
 * This tracer logs the trace into a text file.
 * @author ogeorgeon 
 */
public class Tracer implements ITracer
{

	private  File logFile = null;

	/**
	 * Initialize the tracer.
	 * @param fileName The name of the file where to log the trace.
	 */
	public Tracer(String fileName)
	{ 	
		try 
		{
			logFile = new File(fileName);
			if (logFile.exists()) 
			{
				logFile.delete();
				logFile.createNewFile();
			}
		}
		catch (IOException e) {
			System.out.println("Error creating the file " + fileName);
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

	public boolean close() 
	{
		return true;
	}

	public void startNewEvent(int t) 
	{
		addEventElement("cycle", " " + t); 
	}

	public Element addEventElement(String name, String value) 
	{
		if (name.equals("cycle") || name.equals("enacted_act") || name.equals("interrupted")) 
		{
			try 
			{
				FileWriter writer = new FileWriter(logFile, true);
				writer.write(value );
				writer.close();
			} 
			catch (FileNotFoundException e) 
			{
				System.out.println("Error logging the trace.");
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				System.out.println("Error logging the trace.");
				e.printStackTrace();
			}
		}	
		return null;
	}

	public Element addSubelement(Element element, String name, String textContent) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Element newEvent(String source, String type, int t) 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
