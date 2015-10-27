package teammates.test.driver;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlHelper {

    /**
     * Verifies that two HTML files are logically equivalent, e.g. ignores
     * differences in whitespace and attribute order. If the assertion fails,
     * <code>AssertionError</code> will be thrown and the difference can then be traced.
     * @param expectedString the expected string for comparison
     * @param actualString the actual string for comparison
     * @param isPart if true, ignores top-level HTML tags, i.e <code>&lt;html&gt;</code>,
     *               <code>&lt;head&gt;</code>, and <code>&lt;body&gt;</code>
     */
    public static boolean assertSameHtml(String expected, String actual, boolean isPart) {
        return assertSameHtml(expected, actual, isPart, true);
    }
    
    /**
     * Verifies that two HTML files are logically equivalent, e.g. ignores
     * differences in whitespace and attribute order.
     * @param expectedString the expected string for comparison
     * @param actualString the actual string for comparison
     * @param isPart if true, ignores top-level HTML tags, i.e <code>&lt;html&gt;</code>,
     *               <code>&lt;head&gt;</code>, and <code>&lt;body&gt;</code>
     */
    public static boolean areSameHtml(String expected, String actual, boolean isPart) {
        return assertSameHtml(expected, actual, isPart, false);
    }
    
    private static boolean assertSameHtml(String expected, String actual, boolean isPart,
                                          boolean isDifferenceToBeShown) {
        String processedExpected = convertToStandardHtml(expected, isPart);
        String processedActual = convertToStandardHtml(actual, isPart);

        if (!AssertHelper.isContainsRegex(processedExpected, processedActual)) {
            if (isDifferenceToBeShown) {
                assertEquals("<expected>\n" + processedExpected + "</expected>",
                             "<actual>\n" + processedActual + "</actual>");
            }
            return false;
        }
        return true;
    }

    /**
     * Transform the HTML text to follow a standard format. 
     * Element attributes are reordered in alphabetical order.
     * Spacing and line breaks are standardized too.
     */
    public static String convertToStandardHtml(String rawHtml, boolean isHtmlPartPassedIn) {
        String preProcessedHtml = preProcessHtml(rawHtml);
        
        return convertRawHtmlString(preProcessedHtml, isHtmlPartPassedIn);
    }
    
    private static String convertRawHtmlString(String preProcessedHtml, boolean isHtmlPartPassedIn) {
        try {
            Node currentNode = getNodeFromString(preProcessedHtml);
            StringBuilder currentHtml = new StringBuilder();
            String initialIndentation = "   ";
            convertToStandardHtmlRecursively(currentNode, initialIndentation, currentHtml, isHtmlPartPassedIn);
            return currentHtml.toString()
                    .replace("%20", " ")
                    .replace("%27", "'");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Modifies the HTML to deal with wild cards (e.g., "{*}") and some other
     * inconsistencies in the HTML code produced by the DOM parser and the Browsers.
     */
    private static String preProcessHtml(String htmlString){
        htmlString = htmlString.replaceFirst("<html xmlns=\"http://www.w3.org/1999/xhtml\">", "<html>");    
        htmlString = htmlString.replaceAll("(?s)<noscript>.*</noscript>", "");
        htmlString = htmlString.replaceAll("src=\"https://ssl.google-analytics.com/ga.js\"", "async=\"\" src=\"https://ssl.google-analytics.com/ga.js\"");
        return htmlString;
    }

    private static Node getNodeFromString(String string) throws SAXException, IOException {
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(string)));
        return parser.getDocument();
    }

    public static void convertToStandardHtmlRecursively(Node currentNode, String indentation,
        StringBuilder currentHtmlText, boolean isHtmlPartPassedIn){
        
        if(currentNode.getNodeType() == Node.TEXT_NODE){
            String text = currentNode.getNodeValue();
            if(!text.trim().isEmpty()){
                currentHtmlText.append(indentation + text.trim() + "\n");
            }
            return;
        } else if(isToolTip(currentNode)){
            String tooltip = currentNode.getTextContent();
            if(!tooltip.trim().isEmpty()){
                //ignore tool tip
            }
            return;
        } else if (isMotdComponent(currentNode)) {
            return;
        }

        //Add the start of opening tag
        String currentNodeName = currentNode.getNodeName().toLowerCase();
        boolean shouldIncludeCurrentNode = shouldIncludeCurrentNode(isHtmlPartPassedIn, currentNode);

        if (shouldIncludeCurrentNode) {
            currentHtmlText.append(indentation + "<" + currentNodeName);
            
            //Add the attributes of the tag
            NamedNodeMap actualAttributeList = currentNode.getAttributes();
            if(actualAttributeList!=null){
                
                List<Node> nodesList = getAttributesAsNodeList(actualAttributeList);
                sortAttributes(nodesList);
                
                for (int i = 0; i < actualAttributeList.getLength(); i++){
                    Node actualAttribute = actualAttributeList.item(i);
                    currentHtmlText.append(" "+ actualAttribute.getNodeName().toLowerCase() + "=\"" + actualAttribute.getNodeValue().replace("\"", "'") + "\"");
                }
                //close the tag
                currentHtmlText.append(getEndOfOpeningTag(currentNode)+"\n");
            }
        }
        
        // Recursively add contents of the child nodes 
        NodeList actualChildNodes = currentNode.getChildNodes();
        int numberOfChildNodes = actualChildNodes.getLength();
        for (int i = 0; i < numberOfChildNodes; i++){
            if (shouldIncludeCurrentNode) {
                convertToStandardHtmlRecursively(actualChildNodes.item(i), indentation + "   ", currentHtmlText, isHtmlPartPassedIn);
            } else {
                convertToStandardHtmlRecursively(actualChildNodes.item(i), indentation, currentHtmlText, isHtmlPartPassedIn);
            }
        }
        
        if (shouldIncludeCurrentNode) {
            if (currentNode.getNodeType() != Node.TEXT_NODE){
                currentHtmlText.append(indentation + getEndTag(currentNode));
            }
        }
    
    }

    private static boolean shouldIncludeCurrentNode(boolean isHtmlPartPassedIn, Node currentNode) {
        if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
            return false;
        } else {
            String currentNodeName = currentNode.getNodeName().toLowerCase();
            return !(isHtmlPartPassedIn && (currentNodeName.equals("html")
                                            || currentNodeName.equals("head")
                                            || currentNodeName.equals("body")));
        }
    }

    private static boolean isToolTip(Node currentNode) {
        
        if(!currentNode.getNodeName().equalsIgnoreCase("div")){
            return false;
        }
        
        NamedNodeMap attributes = currentNode.getAttributes();
        
        if(attributes == null){ 
            return false;
        }
            
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if(attribute.getNodeName().equalsIgnoreCase("class")
                    && attribute.getNodeValue().contains("tooltip")){
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean isMotdComponent(Node currentNode) {      
        if (currentNode.getNodeName().equalsIgnoreCase("script")) {
            NamedNodeMap attributes = currentNode.getAttributes();
            
            if (attributes != null) { 
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attribute = attributes.item(i);
                    
                    // script to include studentMotd.js
                    if (attribute.getNodeName().equalsIgnoreCase("src")
                          && attribute.getNodeValue().contains("studentMotd.js")) {
                        return true;
                    }
                }
            }
                
            // script with variable motdUrl
            return currentNode.getTextContent().contains("motdUrl");
            
        } else if (currentNode.getNodeName().equalsIgnoreCase("div")) {
            NamedNodeMap attributes = currentNode.getAttributes();
            
            if (attributes == null) { 
                return false;
            }
                
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                
                // Motd container
                if (attribute.getNodeName().equalsIgnoreCase("id")
                      && attribute.getNodeValue().contains("student-motd-container")) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private static List<Node> getAttributesAsNodeList(NamedNodeMap actualAttributeList) {
        List<Node> nodesList= new ArrayList<Node>();
        for (int i = 0; i < actualAttributeList.getLength(); i++){
            nodesList.add(actualAttributeList.item(i));
        }
        return nodesList;
    }

    private static String getEndOfOpeningTag(Node node) {
        String tagName = node.getNodeName().toLowerCase();
        if(isVoidElement(tagName)){
            return "/>";
        }else {
            return ">";
        }
    }
    
    private static String getEndTag(Node node) {
        String tagName = node.getNodeName().toLowerCase();
        if(isVoidElement(tagName)){
            return "";
        }else {
            return "</"+tagName+">\n";
        }
    }
    
    private static boolean isVoidElement(String elementName){
        return elementName.equals("area")
                || elementName.equals("base")
                || elementName.equals("br")
                || elementName.equals("col")
                || elementName.equals("command")
                || elementName.equals("embed")
                || elementName.equals("hr")
                || elementName.equals("img")
                || elementName.equals("input")
                || elementName.equals("keygen")
                || elementName.equals("link")
                || elementName.equals("meta")
                || elementName.equals("param")
                || elementName.equals("source")
                || elementName.equals("track")
                || elementName.equals("wbr");
    }

    private static void sortAttributes(List<Node> attributeList) {
        Collections.sort(attributeList, new Comparator<Node>() {
            public int compare(Node n1, Node n2) {
                return n1.getNodeName().compareTo(n2.getNodeName());
            }
        });
        
    }

}
