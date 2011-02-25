package ernest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import java.util.Calendar;
import java.io.File;
import java.text.SimpleDateFormat;

/**
 * This tracer logs the trace into an XML file.
 * @author Olivier
 */
public class XMLTracer implements ITracer 
{

	private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	private Document m_document;
	private Element m_sequence;
	private Element m_currentEvent;
	private String m_fileName;
	
	/**
	 * Initialize the tracer.
	 * @param fileName The name of the trace file
	 */
	public XMLTracer(String fileName)
	{ 	
		m_fileName = fileName;
		
		// Create a new DOM
		DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructeur = null;
		try {
			constructeur = fabrique.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			System.out.println("Error creating the DOM");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		m_document = constructeur.newDocument();
		
		// DOM's properties
		m_document.setXmlVersion("1.0");
		m_document.setXmlStandalone(true);
		
		// date
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String date =  sdf.format(cal.getTime());

		// Prepare the tree
		m_sequence = m_document.createElement("sequence");
		m_sequence.setAttribute("version","Ernest 9.0");
		m_sequence.setAttribute("date",date);
		m_document.appendChild(m_sequence);
		
	} 
	
	/**
	 * Write the XML document to the file trace.xml
	 * from http://java.developpez.com/faq/xml/?page=xslt#creerXmlDom
	 */
	public boolean close() 
	{
		boolean status = true;
		try {
			// Create the DOM source
			Source source = new DOMSource(m_document);
			
			// Create the output file
			File file = new File(m_fileName);
			Result resultat = new StreamResult(m_fileName);
			
			// Create the transformer
			TransformerFactory fabrique = TransformerFactory.newInstance();
			Transformer transformer = fabrique.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			
			// Transformation
			transformer.transform(source, resultat);
		}catch(Exception e){
			System.out.println("Error creating the file trace.xml");
			status = false;
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * Create a new event
	 * @param t the time stamp
	 */
	public void startNewEvent(int t)
	{
		m_currentEvent = m_document.createElement("event");
		m_sequence.appendChild(m_currentEvent);
		
		Element date = m_document.createElement("date");
		date.setTextContent(t + "");

		m_currentEvent.appendChild(date);
	}

	/**
	 * Add a new element to the current event
	 * @param name The element's name
	 * @param textContent The element's textual content
	 * @return a pointer to the element that can be used to add sub elements.
	 */
	public Element addEventElement(String name, String textContent)
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
	
	public Element addSubelement(Element element, String name, String textContent)
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
}
