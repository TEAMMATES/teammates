package teammates.test.driver;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
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
		html1 = preProcessHtml(html1);
		html2 = preProcessHtml(html2);

		Node page1 = getNodeFromString(html1);
		Node page2 = getNodeFromString(html2);
		eliminateEmptyTextNodes(page1);
		eliminateEmptyTextNodes(page2);
		
		StringBuilder output = new StringBuilder();
		if(compare(page1, page2, "  ", output)){
			assertEquals(output.toString(), output.toString());
		} else {
			assertEquals(output.toString(), output.toString() + "ERROR");
		}
		
	}
	
	public static String parseHtml(String htmlString) throws TransformerException, SAXException, IOException{
		htmlString = preProcessHtml(htmlString);
		htmlString = cleanupHtml(htmlString);
		htmlString = postProcessHtml(htmlString);
		
		return htmlString;
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
	
	private static String preProcessHtml(String htmlString){
		htmlString = htmlString.replaceAll("&nbsp;", "");
		//Required for chrome selenium testing
		htmlString = htmlString.replaceFirst("<html>", "<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		
		//Required for IE selenium testing
		if (htmlString.indexOf("<!DOCTYPE") < 0){
			htmlString = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
					+"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
					+ htmlString
					+ "</html>";
		}
	
		return htmlString;
	}
	
	private static String postProcessHtml(String htmlString){
		//Required for IE selenium testing
		htmlString = htmlString.replaceAll("<DIV id=\"statusMessage\" style=\"display: none;\">&nbsp;</DIV>", "<DIV id=\"statusMessage\" style=\"display: none;\"/>");
		
		return htmlString;
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
	
	
	private static void eliminateEmptyTextNodes(Node n){
		NodeList childNodes = n.getChildNodes();
		List<Node> toRemove = new ArrayList<Node>();
		for (int i = childNodes.getLength() - 1; i >= 0; i --){
			Node current = childNodes.item(i);
			if (current.getNodeType() == Node.TEXT_NODE && current.getNodeValue().trim().isEmpty()){
				toRemove.add(current);
			}
			else {
				eliminateEmptyTextNodes(current);
			}
		}
		
		for (int i = 0; i < toRemove.size(); i++){
			n.removeChild(toRemove.get(i));
		}
	}
	
	
	public static boolean compare(Node webpage, Node testpage, String indentation, StringBuilder output){
		if (testpage.getNodeType() != Node.TEXT_NODE){
			output.append(indentation + "<" + testpage.getNodeName() + ">   ");
		}
		if (testpage.getNodeType() == Node.ELEMENT_NODE){
			if(webpage.getNodeType() != Node.ELEMENT_NODE){
				output.append("Error: Supposed to have element node but given " + webpage.getNodeName() + "\n");
				return false;
			}
			if(!webpage.getNodeName().equals(testpage.getNodeName())){
				output.append("Error: Supposed to have " + testpage.getNodeName() + " tag but given " + webpage.getNodeName() + " tag instead\n");
				return false;
			}
			
			NamedNodeMap webpageAttributeList = webpage.getAttributes();
			NamedNodeMap testpageAttributeList = testpage.getAttributes();
			for (int i = 0; i < testpageAttributeList.getLength(); i++){
				Node attribute = testpageAttributeList.item(i);
				Node retrieved;
				try{
					retrieved = webpageAttributeList.removeNamedItem(attribute.getNodeName());
				}
				catch (DOMException e){
					output.append("Error: Unable to find attribute: " + attribute.getNodeName() + "\n");
					return false;
				}
				if (!retrieved.getNodeValue().equals(attribute.getNodeValue())){
					output.append("Error: attribute " + attribute.getNodeName() + " has value \"" + retrieved.getNodeValue() + "\" instead of \"" + attribute.getNodeValue() + "\"\n");
					return false;
				}								
			}
			if(webpageAttributeList.getLength() > 0){
				output.append("Error: there are extra attributes in the element tag ");
				for (int i = 0; i < webpageAttributeList.getLength(); i++){
					Node attribute = webpageAttributeList.item(i);
					output.append("[" + attribute.getNodeName() + ": " + attribute.getNodeValue() + "] ");
				}
				return false;
			}
			else{
				for (int i = 0; i < testpageAttributeList.getLength(); i++){
					Node attribute = testpageAttributeList.item(i);
					output.append("[" + attribute.getNodeName() + ": " + attribute.getNodeValue() + "] ");
				}
			}
			output.append("\n");	
			
		} else if (testpage.getNodeType() == Node.TEXT_NODE){
			if(webpage.getNodeType() != Node.TEXT_NODE){
				output.append(indentation + "Error: Supposed to have text node but given " + webpage.getNodeName() + "\n");
				return false;
			}
			if(!webpage.getNodeValue().trim().equals(testpage.getNodeValue().trim())){
				output.append(indentation + "Error: Supposed to have value \"" + testpage.getNodeValue() + "\" but given \"" + webpage.getNodeValue() + "\" instead\n");
				return false;
			}	
		}
				
		if(testpage.hasChildNodes() || testpage.hasChildNodes()){
			NodeList webpageChildNodes = webpage.getChildNodes();
			NodeList testpageChildNodes = testpage.getChildNodes();
			if (webpageChildNodes.getLength() != testpageChildNodes.getLength()){
				output.append(indentation + "Error: Parse tree structure is different\n");
				output.append(indentation + "Webpage - current tree level: ");
				for (int i = 0; i < webpageChildNodes.getLength(); i++){
					output.append(webpageChildNodes.item(i).getNodeName() + " ");
				}
				output.append("\n");
				output.append(indentation + "Testpage - current tree level: ");
				for (int i = 0; i < testpageChildNodes.getLength(); i++){
					output.append(testpageChildNodes.item(i).getNodeName() + " ");
				}
				output.append("\n");
				return false;
			} else {
				for (int i = 0; i < webpageChildNodes.getLength(); i++){
					if(!compare(webpageChildNodes.item(i), testpageChildNodes.item(i), indentation + "   ", output)){
						return false;
					}
				}
			}
		}
		
		if (testpage.getNodeType() != Node.TEXT_NODE){
			output.append(indentation + "</" + testpage.getNodeName() + ">\n");
		}		
		return true;
	}

}
