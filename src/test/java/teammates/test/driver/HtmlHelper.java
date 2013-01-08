package teammates.test.driver;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;


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
	public static boolean assertSameHtml(String html1, String html2)
			throws SAXException, IOException, TransformerException {
		
		html1 = preProcessHtml(html1);
		html2 = preProcessHtml(html2);
		System.out.println(html2);
		
		Node page1 = getNodeFromString(html1);
		Node page2 = getNodeFromString(html2);
		eliminateEmptyTextNodes(page1);
		eliminateEmptyTextNodes(page2);
		
		StringBuilder annotatedHtml= new StringBuilder();
		annotatedHtml.append("\n\n");
		boolean isLogicalMatch = compare(page1, page2, "  ", annotatedHtml);
		annotatedHtml.append("\n\n");
		if(!isLogicalMatch){
			//If they are not a logical match, we force a literal comparison
			//   just so that JUnit gives us a side-by-side comparison.
			System.out.println(annotatedHtml.toString());
			
			//remove the noscript tag first
			html1 = html1.replaceAll("(?s)<noscript>.*</noscript>", "");
			html2 = html2.replaceAll("(?s)<noscript>.*</noscript>", "");
			assertEquals(annotatedHtml.toString(), html1, html2);		
		}
		return isLogicalMatch;
	}
		
	private static String preProcessHtml(String htmlString){
		htmlString = htmlString.replaceAll("&nbsp;", "");
		htmlString = htmlString.replaceFirst("<html xmlns=\"http://www.w3.org/1999/xhtml\">", "<html>");	
		htmlString = htmlString.replaceAll("height=\"([0-9]+)\"", "height=\"$1px\"");
		htmlString = htmlString.replaceAll("width=\"([0-9]+)\"", "width=\"$1px\"");
		if (!htmlString.contains("<!DOCTYPE")){
			htmlString = "<!DOCTYPE html>\n" + htmlString;
		}
		return htmlString;
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

	public static String printHtmlFromNode(Node n){
		NamedNodeMap attributeList = n.getAttributes();
		
		String s = new String();
		s += n.getNodeName() + " ";
		
		for (int i = 0; i <attributeList.getLength(); i++ ){
			Node attribute = attributeList.item(i);
			s += attribute.getNodeName() + "=\"" + attribute.getNodeValue() + "\" ";
		}
		s += "\n";
		return s;
	}
	
	public static boolean compare(Node actual, Node expected, String indentation, StringBuilder output){
		Node debugNode = actual.cloneNode(false);
		if (expected.getNodeType() == Node.ELEMENT_NODE){
			if(actual.getNodeType() != Node.ELEMENT_NODE){
				output.append("Element: " + printHtmlFromNode(debugNode));
				output.append("Error: Supposed to have element node but given " + actual.getNodeName() + "\n");
				return false;
			}
			if(!actual.getNodeName().equals(expected.getNodeName())){
				output.append("Element: " + printHtmlFromNode(debugNode));
				output.append("Error: Supposed to have " + expected.getNodeName() + " tag but given " + actual.getNodeName() + " tag instead\n");
				return false;
			}
			
			NamedNodeMap actualAttributeList = actual.getAttributes();
			NamedNodeMap expectedAttributeList = expected.getAttributes();
			boolean dhtmltooltipIgnore = false;
			for (int i = 0; i < expectedAttributeList.getLength(); i++){
				Node expectedAttribute = expectedAttributeList.item(i);
				Node actualAttribute = null;				
				try{
					actualAttribute = actualAttributeList.removeNamedItem(expectedAttribute.getNodeName());
					if(actualAttribute.getNodeName().equals("id") && actualAttribute.getNodeValue().equals("dhtmltooltip")){						
						dhtmltooltipIgnore = true;
					}
				}
				catch (DOMException e){
					//skip if the style for the dhtmltooltip is set
					if(dhtmltooltipIgnore && expectedAttribute.getNodeName().equals("style")){						
						return true;
					}
					output.append("Element: " + printHtmlFromNode(debugNode));
					output.append("Error: Unable to find attribute: " + expectedAttribute.getNodeName() + "\n");
					return false;
				}
				if (actualAttribute.getNodeValue().trim().equals("{*}")){
					//Regex should pass
				} else if (!actualAttribute.getNodeValue().equals(expectedAttribute.getNodeValue())){
					output.append("Element: " + printHtmlFromNode(debugNode));
					output.append("Error: attribute " + expectedAttribute.getNodeName() + " has value \"" + actualAttribute.getNodeValue() + "\" instead of \"" + expectedAttribute.getNodeValue() + "\"\n");
					return false;
				}								
			}
			if(actualAttributeList.getLength() > 0){
				output.append("Element: " + printHtmlFromNode(debugNode));
				output.append("Error: there are extra attributes in the element tag ");
				for (int i = 0; i < actualAttributeList.getLength(); i++){
					Node actualAttribute = actualAttributeList.item(i);
					output.append("[" + actualAttribute.getNodeName() + ": " + actualAttribute.getNodeValue() + "] ");
				}
				return false;
			}
			
		} else if (expected.getNodeType() == Node.TEXT_NODE){
			if(actual.getNodeType() != Node.TEXT_NODE){
				output.append("Element: " + printHtmlFromNode(debugNode));
				output.append(indentation + "Error: Supposed to have text node but given " + actual.getNodeName() + "\n");
				return false;
			} else if (actual.getNodeValue().trim().equals("{*}")){
				//Regex should pass
			} else if(!actual.getNodeValue().trim().equals(expected.getNodeValue().trim())){
				output.append("Element: " + printHtmlFromNode(debugNode));
				output.append(indentation + "Error: Supposed to have value \"" + expected.getNodeValue() + "\" but given \"" + actual.getNodeValue() + "\" instead\n");
				return false;
			}	
		}
				
		if(expected.hasChildNodes() || expected.hasChildNodes()){
			NodeList actualChildNodes = actual.getChildNodes();
			NodeList expectedChildNodes = expected.getChildNodes();
			if (actualChildNodes.getLength() != expectedChildNodes.getLength()){
				output.append(indentation + "Error: Parse tree structure is different\n");
				output.append(indentation + "Actual webpage - Parent Element: " + printHtmlFromNode(debugNode));
				output.append(indentation + "Actual webpage - child Elements: ");
				for (int i = 0; i < actualChildNodes.getLength(); i++){
					output.append(actualChildNodes.item(i).getNodeName() + " ");
				}
				output.append("\n");
				output.append(indentation + "Expected webpage - Parent Element: " + printHtmlFromNode(expected));
				output.append(indentation + "Expected webpage - child Elements: ");
				for (int i = 0; i < expectedChildNodes.getLength(); i++){
					output.append(expectedChildNodes.item(i).getNodeName() + " ");
				}
				output.append("\n");
				return false;
			} else {
				for (int i = 0; i < actualChildNodes.getLength(); i++){
					if(!compare(actualChildNodes.item(i), expectedChildNodes.item(i), indentation + "   ", output)){
						return false;
					}
				}
			}
		}
		
	
		return true;
	}

}
