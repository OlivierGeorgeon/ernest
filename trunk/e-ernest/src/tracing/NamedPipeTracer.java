package tracing;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jrdf.graph.ObjectNode;
import org.w3c.dom.*;

import ernest.ITracer;


import java.util.Calendar;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

/**
 * This tracer logs the trace thru a named pipe.
 */
public class NamedPipeTracer implements ITracer<Element>
{

	private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	private Document m_document;
	private Element m_sequence;
	private Element m_currentEvent;
	private String m_fileName;
	private int m_id = 0;

	private boolean m_eventStarted;

	private StringWriter m_sw;

	private DocumentBuilder m_builder;

	private Transformer m_transformer;

	private FileOutputStream m_fos;

	private Element m_root;

	private int m_last_t;
	
	/**
	 * Initialize the tracer.
	 * @param fileName The name of the trace file
	 */
	public NamedPipeTracer(String fileName)
	{
		m_fileName = fileName;
		try {
			m_fos = new FileOutputStream(new File(fileName), true);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
			System.exit(0);
		}
		
		m_eventStarted = false;
		
		// date
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String date =  sdf.format(cal.getTime());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		m_builder = null;
		try {
			m_builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			System.err.println("Error creating the DOM builder.");
			e1.printStackTrace();
		}
		
		TransformerFactory tfactory = TransformerFactory.newInstance();
		try {
			m_transformer = tfactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		m_transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		m_transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		m_transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		
		m_document = m_builder.newDocument();
	} 
	
	/**
	 * Write the XML document to the file trace.xml
	 * from http://java.developpez.com/faq/xml/?page=xslt#creerXmlDom
	 */
	public boolean close() 
	{
		//TODO: notify eot
		return true;
	}

	/**
	 * Create an event that can be populated using its reference.
	 * @param type The event's type.
	 * @param t The event's time stamp.
	 * @return The pointer to the event.
	 */
	public Element newEvent(String source, String type, int t)
	{
		if(!m_eventStarted)
		{
			m_root = m_document.createElement("slice");
			m_root.setAttribute("date", Integer.toString(t));
			m_document.appendChild(m_root);
		}else if(t != m_last_t)
		{
			this.finishEvent();
			m_root = m_document.createElement("slice");
			m_root.setAttribute("date", Integer.toString(t));
			m_document.appendChild(m_root);
		}
		m_last_t = t;
		m_eventStarted = true;
		
		m_id++;
		m_currentEvent = m_document.createElement("event");
		m_currentEvent.setAttribute("id", Integer.toString(m_id));
		m_currentEvent.setAttribute("source", source);
		m_currentEvent.setAttribute("date", Integer.toString(t));
		Element typeElement = m_document.createElement("type");
		typeElement.setTextContent(type);
		m_currentEvent.appendChild(typeElement);
		m_root.appendChild(m_currentEvent);

		return m_currentEvent;
	}
	
	/**
	 * Create a new event that can be populated with elements later.
	 * @param t the time stamp
	 */
	public void startNewEvent(int t)
	{
		this.newEvent("Ernest", "action", t);
	}
	
	public void finishEvent()
	{
		if(!m_eventStarted)
		{
			return;
		}
		
		m_eventStarted = false;
		
		Source source = new DOMSource(m_document);
		StringWriter sw = new StringWriter();
		Result result = new StreamResult(sw);
		try {
			m_transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		byte[] message = sw.toString().getBytes();
		int len = message.length;
		
		try {
			m_fos.write(Integer.toString(len).getBytes());
			m_fos.write("\n".getBytes());
			m_fos.write(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		m_document.removeChild(m_root);
	}

	/**
	 * Add a new element to the current event
	 * @param name The element's name
	 * @param textContent The element's textual content
	 * @return a pointer to the element that can be used to add sub elements.
	 */
	public Element addEventElement(String name)
	{
		return this.addEventElementImpl(name, "");
	}
	
	public void addEventElement(String name, String textContent)
	{
		this.addEventElementImpl(name, textContent);
	}
	
	private Element addEventElementImpl(String name, String textContent)
	{
		if (m_currentEvent != null)
		{
			Element element = m_document.createElement(name);
			element.setTextContent(textContent);
			m_currentEvent.appendChild(element);
			return element;
		}
		else 
			return null;
	}

	public Element addSubelement(Element element, String name)
	{
		return this.addSubelementImpl(element, name, "");
	}
	
	public void addSubelement(Element element, String name, String textContent)
	{
		this.addSubelementImpl(element, name, textContent);
	}
	
	private Element addSubelementImpl(Element element, String name, String textContent)
	{
		if (element != null)
		{
			Element subElement = m_document.createElement(name);
			subElement.setTextContent(textContent);
			element.appendChild(subElement);
			return subElement;
		}
		else return null;
	}

	public Element addEventElement(String name, boolean display) 
	{
		return this.addEventElement(name);
	}
}
