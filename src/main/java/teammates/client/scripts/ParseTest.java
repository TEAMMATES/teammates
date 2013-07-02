package teammates.client.scripts;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import teammates.common.util.Config;
import teammates.common.util.FileHelper;

public class ParseTest {
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
	
	
	public static boolean compare(Node webpage, Node testpage, String indentation){
		
		System.out.print(indentation + testpage.getNodeName() + "   ");
		if (testpage.getNodeType() == Node.ELEMENT_NODE){
			if(webpage.getNodeType() != Node.ELEMENT_NODE){
				System.out.println(indentation + "Error: Supposed to have element node but given " + webpage.getNodeName());
				return false;
			}
			if(!webpage.getNodeName().equals(testpage.getNodeName())){
				System.out.println(indentation + "Error: Supposed to have " + testpage.getNodeName() + " tag but given " + webpage.getNodeName() + " tag instead");
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
					System.out.println("Error: Unable to find attribute: " + attribute.getNodeName());
					return false;
				}
				if (!retrieved.getNodeValue().equals(attribute.getNodeValue())){
					System.out.println("Error: attribute " + attribute.getNodeName() + " has value " + retrieved.getNodeValue() + " instead of " + attribute.getNodeValue());
					return false;
				}								
			}
			if(webpageAttributeList.getLength() > 0){
				System.out.print("Error: there are extra attributes in the element tag ");
				for (int i = 0; i < webpageAttributeList.getLength(); i++){
					Node attribute = webpageAttributeList.item(i);
					System.out.print("[" + attribute.getNodeName() + ": " + attribute.getNodeValue() + "] ");
				}
				return false;
			}
			else{
				for (int i = 0; i < testpageAttributeList.getLength(); i++){
					Node attribute = testpageAttributeList.item(i);
					System.out.print("[" + attribute.getNodeName() + ": " + attribute.getNodeValue() + "] ");
				}
			}
			System.out.println();	
			
		} else if (testpage.getNodeType() == Node.TEXT_NODE){
			if(webpage.getNodeType() != Node.TEXT_NODE){
				System.out.println(indentation + "Error: Supposed to have text node but given " + webpage.getNodeName());
				return false;
			}
			if(!webpage.getNodeValue().trim().equals(testpage.getNodeValue().trim())){
				System.out.println(indentation + "Error: Supposed to have \"" + testpage.getNodeValue() + "\" but given \"" + webpage.getNodeValue() + "\" instead");
				return false;
			}
			System.out.println();	
		}
				
		if(testpage.hasChildNodes() || testpage.hasChildNodes()){
			NodeList webpageChildNodes = webpage.getChildNodes();
			NodeList testpageChildNodes = testpage.getChildNodes();
			if (webpageChildNodes.getLength() != testpageChildNodes.getLength()){
				System.out.println(indentation + "Error: Parse tree structure is different");
				System.out.print("Webpage: ");
				for (int i = 0; i < webpageChildNodes.getLength(); i++){
					System.out.print(webpageChildNodes.item(i).getNodeName() + " ");
				}
				System.out.println();
				System.out.print("Testpage: ");
				for (int i = 0; i < testpageChildNodes.getLength(); i++){
					System.out.print(webpageChildNodes.item(i).getNodeName() + " ");
				}
				System.out.println();
				return false;
			} else {
				for (int i = 0; i < webpageChildNodes.getLength(); i++){
					if(!compare(webpageChildNodes.item(i), testpageChildNodes.item(i), indentation + "   ")){
						return false;
					}
				}
			}
		}
		
		System.out.println(indentation + "/" + testpage.getNodeName());
		return true;
	}
	
	public static void main(String args[]) throws SAXException, IOException{
		String webpage, testpage;
		
		webpage = FileHelper.readFile(Config.TEST_PAGES_FOLDER+"/test1.html");
		testpage = FileHelper.readFile(Config.TEST_PAGES_FOLDER+"/test2.html");
				
		DOMParser parser = new DOMParser();
		
		parser.parse(new InputSource(new StringReader(webpage)));
		Node webpage_node = parser.getDocument();
		eliminateEmptyTextNodes(webpage_node);
		
		parser.parse(new InputSource(new StringReader(testpage)));
		Node testpage_node = parser.getDocument();
		eliminateEmptyTextNodes(testpage_node);
		
		if (compare(webpage_node, testpage_node, "")){
			System.out.println("success");
		}
		else{
			System.out.println("failure");
		}

	}
}
