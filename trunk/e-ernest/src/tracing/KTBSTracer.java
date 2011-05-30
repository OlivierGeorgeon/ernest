package tracing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.parser.RdfReader;
import org.jrdf.util.ClosableIterable;
import org.jrdf.writer.Writer;
import org.jrdf.writer.ntriples.NTriplesWriterImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ernest.ITracer;

import utils.Strings;

/**
 * This tracer logs the trace into an XML file.
 */
public class KTBSTracer implements ITracer<Element> //TODO
{

	private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	private String m_baseURL;
	private String m_traceURL;
	private BlankNode m_currentEvent;
	private String m_source;
	private int m_date;
	private NTriplesWriterImpl m_graphWriter;
	private StringWriter m_sw;
	private Graph m_graph;
	private URIReference m_traceNode;
	private URIReference m_aNode;
	private URIReference m_actionNode;
	private URIReference m_hasTraceNode;
	private URIReference m_hasBeginNode;
	private URIReference m_hasXMLValue;
	private URIReference m_hasEndNode;
	private int m_id = 0;
	
	private boolean m_eventStarted;

	private GraphElementFactory m_elef;

	private String m_traceModelURL;

	private DocumentBuilder m_builder;

	private Document m_doc;

	private Element m_xmlEventElement;

	private Transformer m_transformer;

	static final String ktbsns = "http://liris.cnrs.fr/silex/2009/ktbs#";
	static final String rdfns = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	/**
	 * Initialize the tracer.
	 * @param fileName The name of the trace file
	 */
	public KTBSTracer(String baseURL, String traceModel)
	{
		String newName = this.fetchName(baseURL);
		
		m_traceModelURL = traceModel;
		m_baseURL = baseURL;
		m_traceURL = m_baseURL + newName + "/";
		m_graphWriter = new NTriplesWriterImpl();
		m_sw = new StringWriter();
		JRDFFactory jrdfFactory = SortedMemoryJRDFFactory.getFactory();
		m_graph = jrdfFactory.getGraph();
		m_elef = m_graph.getElementFactory();
		
		/* Creation of common nodes. */
		m_traceNode = m_elef.createURIReference(URI.create(baseURL + newName + "/"));
		m_aNode = m_elef.createURIReference(URI.create(rdfns + "type"));

		m_actionNode = m_elef.createURIReference(URI.create(traceModel + "action"));
		m_hasTraceNode = m_elef.createURIReference(URI.create(ktbsns + "hasTrace"));
		m_hasBeginNode = m_elef.createURIReference(URI.create(ktbsns + "hasBegin"));
		m_hasEndNode = m_elef.createURIReference(URI.create(ktbsns + "hasEnd"));
		
		m_hasXMLValue = m_elef.createURIReference(URI.create(traceModel + "hasXMLValue"));
		
		this.createNewTrace(baseURL, newName, traceModel);
		
		m_eventStarted = false;
		

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
	}
	
	protected void createNewTrace(String baseURL, String newName, String traceModel)
	{
		try {
			m_graph.clear();

			URIReference baseURLNode = m_elef.createURIReference(URI.create(baseURL));
			URIReference traceModelNode = m_elef.createURIReference(URI.create(traceModel));

			Literal originNode = m_elef.createLiteral("0");
			
			URIReference owns = m_elef.createURIReference(URI.create(ktbsns + "owns"));
			URIReference hasModel = m_elef.createURIReference(URI.create(ktbsns + "hasModel"));
			URIReference storedTrace = m_elef.createURIReference(URI.create(ktbsns + "StoredTrace"));
			URIReference hasOrigin = m_elef.createURIReference(URI.create(ktbsns + "hasOrigin"));
			

			m_graph.add(baseURLNode, owns, m_traceNode);
			m_graph.add(m_traceNode, m_aNode, storedTrace);
			m_graph.add(m_traceNode, hasModel, traceModelNode);
			m_graph.add(m_traceNode, hasOrigin, originNode);
			
			m_sw.getBuffer().setLength(0);
			m_graphWriter.write(m_graph, m_sw);
			m_sw.write("\n\n");
			
			String newTraceData = m_sw.getBuffer().toString();
			
			System.out.println("Creating trace...");
			this.post_data(baseURL, newTraceData);

			m_graph.clear();
			
		} catch (GraphElementFactoryException e) {
			e.printStackTrace();
			System.exit(0);
		};
	}
	
	protected void post_data(String url, String data)
	{
		try
		{
			URL base = new URL(url);
			HttpURLConnection baseCon = (HttpURLConnection) base.openConnection();
			baseCon.setRequestMethod("POST");
			baseCon.setRequestProperty("Content-type", "application/x-turtle");
			baseCon.setRequestProperty("Content-length", Integer.toString(data.getBytes().length-2));
			baseCon.setDoOutput(true);
			baseCon.setDoInput(true);
			baseCon.setUseCaches (false);
	
			OutputStream os = baseCon.getOutputStream();
			os.write(data.getBytes());
			os.flush();
			os.close();
			
			int response = baseCon.getResponseCode();
			if(response >= 300 || response < 200)
			{
				System.err.println("Post failed: \"\"\"");
				InputStream es = baseCon.getErrorStream();
				for(int ch = es.read(); ch != -1; ch = es.read())
				{
					System.err.print((char)ch);
				}
				System.err.println("\"\"\"");
			}else{
				System.err.println("Done.");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.err.println("Invalid URL (" + url + ").");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't reach the server (" + url + ").");
			System.exit(0);
		};
	}
	
	protected String fetchName(String baseURL)
	{
		try {
			URL url = null;
			URLConnection con = null;
			
			url = new URL(baseURL + ".nt");
			con = url.openConnection();
			con.setRequestProperty("Accept", "text/turtle,application/x-turtle");
			con.connect();
		
			RdfReader reader = new RdfReader();
			Graph basegraph = reader.parseNTriples(con.getInputStream());
			Writer.writeNTriples(new File("/tmp/basegraph"), basegraph);
			System.out.println("Read base informations. Generating new trace name...");
			
			URIReference owns = basegraph.getElementFactory().createURIReference(URI.create(ktbsns + "owns"));
			URIReference baseURLNode = basegraph.getElementFactory().createURIReference(URI.create(baseURL));
			ClosableIterable<Triple> triples = basegraph.find(baseURLNode, owns, AnyObjectNode.ANY_OBJECT_NODE);
			
			String lastName = baseURL;
			
			for (Triple triple : triples) {
				ObjectNode nameNode = triple.getObject();
				if(Strings.compareNatural(lastName, nameNode.toString()) < 0)
				{
					lastName = nameNode.toString();
				}
			}
			
			if(lastName.equals(baseURL))
			{
				return "Ernest1";
			}

			String canonicalName = lastName.substring(
					lastName.lastIndexOf('/', lastName.length()-2)+1,
					lastName.length()-1 );

			StringBuffer index = new StringBuffer("");
			
			int i = canonicalName.length()-1;
			char c = canonicalName.charAt(i);
			while(c >= '0' && c <= '9')
			{
				index.insert(0, c);
				
				--i;
				c = canonicalName.charAt(i);
			}
			canonicalName = canonicalName.substring(0, i+1);
			
			if(index.length() == 0)
				index.append("0");
			
			int intIndex = Integer.parseInt(index.toString());
			++ intIndex; 
			
			String newName = canonicalName + Integer.toString(intIndex);
			
			System.out.println("Trace name: \"" + newName + "\"");
			return newName;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Problem while retreiving trace base informations (at URL " + baseURL + ").");
			System.exit(0);
		}
		return "Ernest1";
	}
	
	/**
	 * Write the XML document to the file trace.xml
	 * from http://java.developpez.com/faq/xml/?page=xslt#creerXmlDom
	 */
	public boolean close() 
	{
		//TODO: end of trace
		return false;
	}

	/**
	 * Create an event that can be populated using its reference.
	 * @param type The event's type.
	 * @param t The event's time stamp.
	 * @return The pointer to the event.
	 */
	public Element newEvent(String source, String type, int t)
	{
		if(m_eventStarted)
		{
			this.finishEvent();
		}

		m_doc = m_builder.newDocument();
		m_eventStarted = true;
		
		++m_id;
		
		System.err.println("NEW EVENT");
		m_xmlEventElement = m_doc.createElement("event");
		m_xmlEventElement.setAttribute("id", Integer.toString(m_id));
		m_xmlEventElement.setAttribute("source", source);
		m_xmlEventElement.setAttribute("date", Integer.toString(t));
		Element typeElement = m_doc.createElement("type");
		typeElement.setTextContent(type);
		m_xmlEventElement.appendChild(typeElement);
		m_doc.appendChild(m_xmlEventElement);
		
		m_currentEvent = m_elef.createBlankNode();
		ObjectNode dateNode = m_elef.createLiteral(Integer.toString(t), URI.create("http://www.w3.org/2001/XMLSchema#integer"));
		ObjectNode typeNode = m_elef.createLiteral(type);
		ObjectNode sourceValue = m_elef.createLiteral(source);
		ObjectNode idValNode = m_elef.createLiteral(m_id);

		m_graph.add(m_currentEvent, m_aNode, typeNode);
		m_graph.add(m_currentEvent, m_hasTraceNode, m_traceNode);
		m_graph.add(m_currentEvent, m_hasBeginNode, dateNode);
		m_graph.add(m_currentEvent, m_hasEndNode, dateNode);
		
		return m_xmlEventElement;
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
		if(m_doc == null || m_xmlEventElement == null)
			return;
		
		if(!m_eventStarted)
		{
			return;
		}

		m_eventStarted = false;
		
		//TODO: rdf and xml graph construction

		//----------------------------------------------
		Source source = new DOMSource(m_doc);
		StringWriter sw = new StringWriter();
		Result result = new StreamResult(sw);
		try {
			m_transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		ObjectNode xmlVal = m_elef.createLiteral(sw.toString());
		m_graph.add(m_currentEvent, m_hasXMLValue, xmlVal);
		
		//----------------------------------------------
		
		m_sw.getBuffer().setLength(0);
		m_graphWriter.write(m_graph, m_sw);
		//m_graphWriter.write(m_graph, System.err);
		m_sw.write("\n\n");
		
		String data = m_sw.getBuffer().toString();
		
		System.err.println(data);
		this.post_data(m_traceURL, data);
		m_graph.clear();
		
		//System.exit(0);
	}

	/**
	 * Add a new element to the current event
	 * @param name The element's name
	 * @param textContent The element's textual content
	 */
	public void addEventElement(String name, String textContent)
	{
		if (m_xmlEventElement != null)
		{
			this.addSubelement(m_xmlEventElement, name, textContent);
		}
	}
	
	/**
	 * Add a new element to the current event
	 * @param name The element's name
	 * @return a pointer to the element that can be used to add sub elements.
	 */
	public Element addEventElement(String name)
	{
		if (m_xmlEventElement != null)
		{
			return this.addSubelement(m_xmlEventElement, name);
		}else
			return null;
	}
	
	public Element addSubelement(Element element, String name)
	{
		if (element != null)
		{
			Element child = m_doc.createElement(name);
			element.appendChild(child);
			
			return child;
		}else
			return null;
	}
	
	public void addSubelement(Element element, String name, String textContent)
	{
		if (element != null)
		{
			Element child = m_doc.createElement(name);
			child.setTextContent(textContent);
			element.appendChild(child);
		}
	}

	public Element addEventElement(String name, boolean display) 
	{
		return this.addEventElement(name);
	}
}
