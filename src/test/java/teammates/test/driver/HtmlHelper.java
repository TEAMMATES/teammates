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
	public static boolean assertSameHtml(String actualString, String expectedString)
			throws SAXException, IOException, TransformerException {
		
		actualString = preProcessHtml(actualString);
		expectedString = preProcessHtml(expectedString);
		
		Node actualPage = getNodeFromString(actualString);
		Node expectedPage = getNodeFromString(expectedString);
		eliminateEmptyTextNodes(actualPage);
		eliminateEmptyTextNodes(expectedPage);
		
		StringBuilder expectedHTML= new StringBuilder();
		StringBuilder actualHTML = new StringBuilder();
		boolean isLogicalMatch = compare(actualPage, expectedPage, "", actualHTML, expectedHTML);
		if(!isLogicalMatch){
			//If they are not a logical match, we force a literal comparison
			//   just so that JUnit gives us a side-by-side comparison.
			assertEquals("The two HTML pages are not logically equivalent. Aborting comparison at the first difference encountered.", expectedHTML.toString(), actualHTML.toString());		
		}
		return isLogicalMatch;
	}
	
	/**
	 * Modifies the html string to accommodate the differences and inconsistencies between different browsers
	 * @param htmlString The string to process
	 * @return The processed HTML string
	 */
	private static String preProcessHtml(String htmlString){
		htmlString = htmlString.replaceAll("&nbsp;", "");
		htmlString = htmlString.replaceFirst("<html xmlns=\"http://www.w3.org/1999/xhtml\">", "<html>");	
		htmlString = htmlString.replaceAll("height=\"([0-9]+)\"", "height=\"$1px\"");
		htmlString = htmlString.replaceAll("width=\"([0-9]+)\"", "width=\"$1px\"");
		htmlString = htmlString.replaceAll("(?s)<noscript>.*</noscript>", "");
		htmlString = htmlString.replaceAll("src=\"https://ssl.google-analytics.com/ga.js\"", "async=\"\" src=\"https://ssl.google-analytics.com/ga.js\"");

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
	
	/**
	 * recursively remove all empty text nodes within the Node n 
	 * @param n the Node
	 */
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

	/**
	 * Parse Node n into a readable String
	 * @param n
	 * @return
	 */
	public static String printHtmlFromNode(Node n){
		NamedNodeMap attributeList = n.getAttributes();
		
		String s = new String();
		s += n.getNodeName() + " "; 
		
		if (attributeList != null){
			for (int i = 0; i <attributeList.getLength(); i++ ){
				Node attribute = attributeList.item(i);
				s += attribute.getNodeName() + "=\"" + attribute.getNodeValue() + "\" ";
			}
		} else {
			s += n.getNodeValue().trim();
		}
		s += "\n";
		return s;
	}
	
	public static boolean compare(Node actual, Node expected, String indentation, StringBuilder actualOutput, StringBuilder expectedOutput){
		//Building the HTML string for display
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
			//Verify node type is correct
			if(actual.getNodeType() != Node.ELEMENT_NODE){
				actualOutput.append("----------------------------------------\n");
				actualOutput.append("Error in Element: " + printHtmlFromNode(debugNode));
				actualOutput.append("Supposed to have element node but given " + actual.getNodeName() + "\n");
				return false;
			}
			//Verify element tag is correct
			if(!actual.getNodeName().equals(expected.getNodeName())){
				actualOutput.append("----------------------------------------\n");
				actualOutput.append("Error in Element: " + printHtmlFromNode(debugNode));
				actualOutput.append("Supposed to have " + expected.getNodeName() + " tag but given " + actual.getNodeName() + " tag instead\n");
				return false;
			}
			
			NamedNodeMap actualAttributeList = actual.getAttributes();
			NamedNodeMap expectedAttributeList = expected.getAttributes();
			boolean dhtmltooltipIgnore = false;
			
			//Building HTML string for display
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
			
			//Verify attributes in element tag is correct (checks number, name, value of attributes)
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
					actualOutput.append("----------------------------------------\n");
					actualOutput.append("Error in Element: " + printHtmlFromNode(debugNode));
					actualOutput.append("Unable to find attribute: " + expectedAttribute.getNodeName() + "\n");
					return false;
				}
				if (expectedAttribute.getNodeValue().trim().equals("{*}")){
					//Regex should pass
				} else if (!actualAttribute.getNodeValue().equals(expectedAttribute.getNodeValue())){
					actualOutput.append("----------------------------------------\n");
					actualOutput.append("Error in Element: " + printHtmlFromNode(debugNode));
					actualOutput.append("Attribute " + expectedAttribute.getNodeName() + " has value \"" + actualAttribute.getNodeValue() + "\" instead of \"" + expectedAttribute.getNodeValue() + "\"\n");
					return false;
				}								
			}
			if(actualAttributeList.getLength() > 0){				
				if(dhtmltooltipIgnore && actualAttributeList.getLength() == 1 && actualAttributeList.item(0).getNodeName().equals("style")){						
					//skip if the style for the dhtmltooltip is set
				} else {
					actualOutput.append("----------------------------------------\n");
					actualOutput.append("Error in Element: " + printHtmlFromNode(debugNode));
					actualOutput.append("There are extra attributes in the element tag ");
					for (int i = 0; i < actualAttributeList.getLength(); i++){
						Node actualAttribute = actualAttributeList.item(i);
						actualOutput.append("[" + actualAttribute.getNodeName() + ": " + actualAttribute.getNodeValue() + "] ");
					}
					return false;
				}
			}
				
			
		} else if (expected.getNodeType() == Node.TEXT_NODE){
			//Verify node type is correct
			if(actual.getNodeType() != Node.TEXT_NODE){
				actualOutput.append("----------------------------------------\n");
				actualOutput.append("Error in Element: " + printHtmlFromNode(debugNode));
				actualOutput.append("Supposed to have text node but given " + actual.getNodeName() + "\n");
				return false;
			} else if (expected.getNodeValue().trim().equals("{*}")){
				//Regex should pass
			} else if(!actual.getNodeValue().trim().equals(expected.getNodeValue().trim())){
				//Verify text node value is correct
				actualOutput.append("----------------------------------------\n");
				actualOutput.append("Error in Element: " + printHtmlFromNode(debugNode));
				actualOutput.append("Supposed to have value \"" + expected.getNodeValue().trim() + "\" but given \"" + actual.getNodeValue().trim() + "\" instead\n");
				return false;
			}	
		}
				
		//Verify tree structure is correct
		if(expected.hasChildNodes() || expected.hasChildNodes()){
			NodeList actualChildNodes = actual.getChildNodes();
			NodeList expectedChildNodes = expected.getChildNodes();
			if (actualChildNodes.getLength() != expectedChildNodes.getLength()){
				actualOutput.append("----------------------------------------\n");
				actualOutput.append("Error: Parse tree structure is different\n");
				actualOutput.append("Actual webpage - Parent Element: " + printHtmlFromNode(debugNode));
				actualOutput.append("Actual webpage - child Elements: \n");
				for (int i = 0; i < actualChildNodes.getLength(); i++){
					actualOutput.append("    " + actualChildNodes.item(i).getNodeName() + " ");
					NamedNodeMap actualAttributeList = actualChildNodes.item(i).getAttributes();
					//We may be looking at a text node, which does not have attributes, so we have to check for null
					if (actualAttributeList != null) {		
						for (int j = 0; j < actualAttributeList.getLength(); j++){
							actualOutput.append(actualAttributeList.item(j).getNodeName() + "=\"" + actualAttributeList.item(j).getNodeValue() + "\" ");
						}
					} else {
						actualOutput.append(actualChildNodes.item(i).getNodeValue().trim());
					}
					actualOutput.append("\n");
				}
				actualOutput.append("\n");
				actualOutput.append("Expected webpage - Parent Element: " + printHtmlFromNode(expected));
				actualOutput.append("Expected webpage - child Elements: \n");
				for (int i = 0; i < expectedChildNodes.getLength(); i++){
					actualOutput.append("    " + expectedChildNodes.item(i).getNodeName() + " ");
					NamedNodeMap expectedAttributeList = expectedChildNodes.item(i).getAttributes();
					//We may be looking at a text node, which does not have attributes, so we have to check for null
					if (expectedAttributeList != null){
						for (int j = 0; j < expectedAttributeList.getLength(); j++){
							actualOutput.append(expectedAttributeList.item(j).getNodeName() + "=\"" + expectedAttributeList.item(j).getNodeValue() + "\" ");
						}
					} else {
						 actualOutput.append(expectedChildNodes.item(i).getNodeValue().trim());
					}
					actualOutput.append("\n");
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
		
		//Building HTML string for display
		if (expected.getNodeType() != Node.TEXT_NODE){
			expectedOutput.append(indentation + "</" + expected.getNodeName() + ">\n");
		}
		if (actual.getNodeType() != Node.TEXT_NODE){
			actualOutput.append(indentation + "</" + actual.getNodeName() + ">\n");
		}
	
		return true;
	}

}
