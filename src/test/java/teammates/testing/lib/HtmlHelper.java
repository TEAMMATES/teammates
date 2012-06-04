package teammates.testing.lib;

//import javax.swing.text.html.HTMLDocument;

import java.io.IOException;
import java.io.StringReader;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlHelper {

	public static void print(Node node, String indent) {
		// System.out.println(indent+node.getClass().getName());
		// System.out.println(indent+node.getTextContent());
		if (!((node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue()
				.trim().isEmpty()))) {
			System.out.println(indent + node.getNodeValue());
		}
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				System.out.println(attributes.item(i));
			}
		}
		Node child = node.getFirstChild();
		while (child != null) {
			print(child, indent + " ");
			child = child.getNextSibling();
		}
	}

	public static boolean isSame(String html1, String html2)
			throws SAXException, IOException {
		Node node1 = getNodeFromString(html1);
		Node node2 = getNodeFromString(html2);
		return isSame(node1, node2);
	}

	private static boolean isSame(Node node1, Node node2) {
		NodeList childnodes1 = node1.getChildNodes();
		NodeList childnodes2 = node2.getChildNodes();
		childnodes1 = removeWhiteSpaceChildren(childnodes1);
		int childCount1 = childnodes1.getLength();
		int childCount2 = childnodes2.getLength();
		if (childCount1 != childCount2) {
			System.out.println("Mismatch of child count :"+childCount1 +" != "+ childCount2);
			return false;
		} else {
			for (int i = 0; i < childCount1; i++) {
				Node childOf1 = childnodes1.item(i);
				Node childOf2 = childnodes2.item(i);
				System.out.println("comparing ["+childOf1.getNodeName()+"] to ["+childOf1.getNodeName()+"]");
				if (!isSame(childOf1, childOf2)) {
					return false;
				}
			}
		}
		return true;
	}

	private static NodeList removeWhiteSpaceChildren(NodeList nodes) {
		
		return null;
	}

	private static Node getNodeFromString(String string) throws SAXException,
			IOException {
		DOMParser parser = new DOMParser();
		parser.parse(new InputSource(new StringReader(string)));
		return parser.getDocument();
	}

}
