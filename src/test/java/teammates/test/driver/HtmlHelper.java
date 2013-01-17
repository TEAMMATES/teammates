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
		
		Node page1 = getNodeFromString(html1);
		Node page2 = getNodeFromString(html2);
		eliminateEmptyTextNodes(page1);
		eliminateEmptyTextNodes(page2);
		
		StringBuilder expectedHTML= new StringBuilder();
		StringBuilder actualHTML = new StringBuilder();
		boolean isLogicalMatch = compare(page1, page2, "  ", expectedHTML, actualHTML);
		if(!isLogicalMatch){
			//If they are not a logical match, we force a literal comparison
			//   just so that JUnit gives us a side-by-side comparison.
			assertEquals("Error: DOM Checker failed. There are difference(s) in the webpage.", expectedHTML.toString(), actualHTML.toString());		
		}
		return isLogicalMatch;
	}
		
	private static String preProcessHtml(String htmlString){
		htmlString = htmlString.replaceAll("&nbsp;", "");
		htmlString = htmlString.replaceFirst("<html xmlns=\"http://www.w3.org/1999/xhtml\">", "<html>");	
		htmlString = htmlString.replaceAll("height=\"([0-9]+)\"", "height=\"$1px\"");
		htmlString = htmlString.replaceAll("width=\"([0-9]+)\"", "width=\"$1px\"");
		htmlString = htmlString.replaceAll("(?s)<noscript>.*</noscript>", "");
		htmlString = htmlString.replaceAll("<script type=\"text/javascript\" src=\"http://www.google-analytics.com/ga.js\"></script>", "<script async=\"\"  type=\"text/javascript\" src=\"http://www.google-analytics.com/ga.js\"></script>");
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
	
	public static boolean compare(Node actual, Node expected, String indentation, StringBuilder expectedOutput, StringBuilder actualOutput){
		//Building the HTML string
		if(expected.getNodeType() == Node.TEXT_NODE){
			expectedOutput.append(indentation + expected.getNodeValue() + "\n");
		}
		if (expected.getNodeType() != Node.TEXT_NODE){
			expectedOutput.append(indentation + "<" + expected.getNodeName() + ">   ");
		}
		if(actual.getNodeType() == Node.TEXT_NODE){
			actualOutput.append(indentation + actual.getNodeValue() + "\n");
		}
		if (actual.getNodeType() != Node.TEXT_NODE){
			actualOutput.append(indentation + "<" + actual.getNodeName() + ">   ");
		}
		
		
		Node debugNode = actual.cloneNode(false);
		if (expected.getNodeType() == Node.ELEMENT_NODE){
			if(actual.getNodeType() != Node.ELEMENT_NODE){
				actualOutput.append("Element: " + printHtmlFromNode(debugNode));
				actualOutput.append("Error: Supposed to have element node but given " + actual.getNodeName() + "\n");
				return false;
			}
			if(!actual.getNodeName().equals(expected.getNodeName())){
				actualOutput.append("Element: " + printHtmlFromNode(debugNode));
				actualOutput.append("Error: Supposed to have " + expected.getNodeName() + " tag but given " + actual.getNodeName() + " tag instead\n");
				return false;
			}
			
			NamedNodeMap actualAttributeList = actual.getAttributes();
			NamedNodeMap expectedAttributeList = expected.getAttributes();
			boolean dhtmltooltipIgnore = false;
			
			//Building HTML string
			for (int i = 0; i < expectedAttributeList.getLength(); i++){
				Node expectedAttribute = expectedAttributeList.item(i);
				expectedOutput.append(expectedAttribute.getNodeName() + "=\"" + expectedAttribute.getNodeValue() + "\" ");
			}
			expectedOutput.append("\n");
			for (int i = 0; i < actualAttributeList.getLength(); i++){
				Node actualAttribute = actualAttributeList.item(i);
				actualOutput.append(actualAttribute.getNodeName() + "=\"" + actualAttribute.getNodeValue() + "\" ");
			}
			actualOutput.append("\n");
			
			
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
					actualOutput.append("Element: " + printHtmlFromNode(debugNode));
					actualOutput.append("Error: Unable to find attribute: " + expectedAttribute.getNodeName() + "\n");
					return false;
				}
				if (actualAttribute.getNodeValue().trim().equals("{*}")){
					//Regex should pass
				} else if (!actualAttribute.getNodeValue().equals(expectedAttribute.getNodeValue())){
					actualOutput.append("Element: " + printHtmlFromNode(debugNode));
					actualOutput.append("Error: attribute " + expectedAttribute.getNodeName() + " has value \"" + actualAttribute.getNodeValue() + "\" instead of \"" + expectedAttribute.getNodeValue() + "\"\n");
					return false;
				}								
			}
			if(actualAttributeList.getLength() > 0){
				actualOutput.append("Element: " + printHtmlFromNode(debugNode));
				actualOutput.append("Error: there are extra attributes in the element tag ");
				for (int i = 0; i < actualAttributeList.getLength(); i++){
					Node actualAttribute = actualAttributeList.item(i);
					actualOutput.append("[" + actualAttribute.getNodeName() + ": " + actualAttribute.getNodeValue() + "] ");
				}
				return false;
			}
				
			
		} else if (expected.getNodeType() == Node.TEXT_NODE){
			if(actual.getNodeType() != Node.TEXT_NODE){
				actualOutput.append("Element: " + printHtmlFromNode(debugNode));
				actualOutput.append(indentation + "Error: Supposed to have text node but given " + actual.getNodeName() + "\n");
				return false;
			} else if (actual.getNodeValue().trim().equals("{*}")){
				//Regex should pass
			} else if(!actual.getNodeValue().trim().equals(expected.getNodeValue().trim())){
				actualOutput.append("Element: " + printHtmlFromNode(debugNode));
				actualOutput.append(indentation + "Error: Supposed to have value \"" + expected.getNodeValue() + "\" but given \"" + actual.getNodeValue() + "\" instead\n");
				return false;
			}	
		}
				
		if(expected.hasChildNodes() || expected.hasChildNodes()){
			NodeList actualChildNodes = actual.getChildNodes();
			NodeList expectedChildNodes = expected.getChildNodes();
			if (actualChildNodes.getLength() != expectedChildNodes.getLength()){
				actualOutput.append(indentation + "Error: Parse tree structure is different\n");
				actualOutput.append(indentation + "Actual webpage - Parent Element: " + printHtmlFromNode(debugNode));
				actualOutput.append(indentation + "Actual webpage - child Elements: ");
				for (int i = 0; i < actualChildNodes.getLength(); i++){
					actualOutput.append(actualChildNodes.item(i).getNodeName() + " ");
				}
				actualOutput.append("\n");
				actualOutput.append(indentation + "Expected webpage - Parent Element: " + printHtmlFromNode(expected));
				actualOutput.append(indentation + "Expected webpage - child Elements: ");
				for (int i = 0; i < expectedChildNodes.getLength(); i++){
					actualOutput.append(expectedChildNodes.item(i).getNodeName() + " ");
				}
				actualOutput.append("\n");
				return false;
			} else {
				for (int i = 0; i < actualChildNodes.getLength(); i++){
					if(!compare(actualChildNodes.item(i), expectedChildNodes.item(i), indentation + "   ", expectedOutput, actualOutput)){
						return false;
					}
				}
			}
		}
		
		//Building HTML string
		if (expected.getNodeType() != Node.TEXT_NODE){
			expectedOutput.append(indentation + "</" + expected.getNodeName() + ">\n");
		}
		if (actual.getNodeType() != Node.TEXT_NODE){
			actualOutput.append(indentation + "</" + actual.getNodeName() + ">\n");
		}
	
		return true;
	}

}
