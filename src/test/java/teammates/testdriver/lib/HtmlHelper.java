package teammates.testdriver.lib;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is used to compare two html files to see if they are logically 
 * equivalent e.g., ignore differences in whitespace and attribute order.
 * @author dcsdcr
 *
 */
public class HtmlHelper {

	/**
	 * Assert whether two HTML strings are the same in the DOM representation.
	 * This ignores the order of attributes, and ignores unnecessary whitespaces as well.
	 * @param html1
	 * @param html2
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	public static void assertSameHtml(String html1, String html2)
			throws SAXException, IOException, TransformerException {
		html1 = cleanupHtml(html1);
		html2 = cleanupHtml(html2);
		assertEquals(html1,html2);
	}

	/**
	 * Cleanup an HTML string to remove unnecessary whitespaces.
	 * This converts all tag names into uppercase.
	 * @param htmlString
	 * @return
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static String cleanupHtml(String htmlString) throws TransformerException, SAXException, IOException{
		Node node = getNodeFromString(htmlString);
		removeWhiteSpace(node);
		return nodeToString(node);
	}

	private static void removeWhiteSpace(Node node) {
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node child = nodes.item(i);
			
			if (child.getNodeType()==Node.TEXT_NODE) {
				if(child.getNodeValue().trim().isEmpty()){
					node.removeChild(child);
					i--;
				} else {
					child.setNodeValue(child.getNodeValue().trim());
				}
			}else{
				removeWhiteSpace(child);
			}
		}
	}
	
	
	private static String nodeToString(Node node) throws TransformerException{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "html");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		//from http://stackoverflow.com/questions/2571866/java-xml-output-proper-indenting-for-child-items
		transformer.setOutputProperty(
				   "{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(node);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		String formatted =  writer.toString();
		//TODO: find a better way to omit this attribute
		formatted = formatted.replaceAll("\\s*xmlns=\"http://www.w3.org/1999/xhtml\"", "");
		formatted = formatted.replaceAll("\\s*<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">","");
		return formatted;
	}
	
	private static Node getNodeFromString(String string) throws SAXException,
			IOException {
		DOMParser parser = new DOMParser();
		parser.parse(new InputSource(new StringReader(string)));
		return parser.getDocument();
	}

}
