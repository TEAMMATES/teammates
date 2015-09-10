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

import teammates.common.util.Config;
import teammates.test.pageobjects.AppPage;

public class HtmlHelper {

    /**
     * Verifies that two HTML files are logically 
     * equivalent e.g., ignores differences in whitespace and attribute order.
     */
    //TODO: for the following 4 methods, change the order of parameters passed in
    //      should be expectedString, acutalString
    public static void assertSameHtml(String actualString, String expectedString){       
        String processedExpectedHtml = convertToStandardHtml(expectedString, false);
        String processedActualHtml = convertToStandardHtml(actualString, false);
        
        if(!AssertHelper.isContainsRegex(processedExpectedHtml, processedActualHtml)){
            processedActualHtml = AppPage.processPageSourceForFailureCase(processedActualHtml);
            processedExpectedHtml = AppPage.processPageSourceForFailureCase(processedExpectedHtml);
            assertEquals("<expected>\n"+processedExpectedHtml+"</expected>", "<actual>\n"+processedActualHtml+"</actual>");
        }
    }
    
    public static void assertSameHtmlPart(String actualString, String expectedString) {
        String processedExpectedHtmlPart = convertToStandardHtml(expectedString, true);
        String processedActualHtmlPart = convertToStandardHtml(actualString, true);
        
        if(!AssertHelper.isContainsRegex(processedExpectedHtmlPart, processedActualHtmlPart)){
            processedActualHtmlPart = AppPage.processPageSourceForFailureCase(processedActualHtmlPart);
            processedExpectedHtmlPart = AppPage.processPageSourceForFailureCase(processedExpectedHtmlPart);
            assertEquals("<expected>\n"+processedExpectedHtmlPart+"</expected>", "<actual>\n"+processedActualHtmlPart+"</actual>");
        }
    }

    /**
     * Verifies that two HTML files are logically 
     * equivalent e.g., ignores differences in whitespace and attribute order.
     */
    public static boolean areSameHtml(String actualString, String expectedString){
        String processedExpectedHtml = convertToStandardHtml(expectedString, false);
        String processedActualHtml = convertToStandardHtml(actualString, false);
        
        return AssertHelper.isContainsRegex(processedExpectedHtml, processedActualHtml);
    }
    
    /**
     * Verifies that two HTML parts are logically 
     * equivalent e.g., ignores differences in whitespace and attribute order.
     */
    public static boolean areSameHtmlPart(String actualString, String expectedString){
        String processedExpectedHtml = convertToStandardHtml(expectedString, true);
        String processedActualHtml = convertToStandardHtml(actualString, true);
        
        return AssertHelper.isContainsRegex(processedExpectedHtml, processedActualHtml);
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
            String initialIndentation = "";
            convertToStandardHtmlRecursively(currentNode, initialIndentation, currentHtml, isHtmlPartPassedIn);
            return currentHtml.toString()
                    .replace("%20", " ")
                    .replace("%27", "'")
                    .replace("<#document", "")
                    .replace("   <html   </html>", "")
                    .replace("</#document>", ""); //remove two unnecessary tags added by DOM parser.
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Modifies the HTML to deal with wild cards (e.g., "{*}") and some other
     * inconsistencies in the HTML code produced by the DOM parser and the Browsers.
     */
    private static String preProcessHtml(String htmlString){
        htmlString = replaceInRawHtmlString(htmlString);

        if (!htmlString.contains("<!DOCTYPE")){
            htmlString = "<!DOCTYPE html>\n" + htmlString;
        }
        return htmlString;
    }

    private static String replaceInRawHtmlString(String htmlString) {
        htmlString = htmlString.replace("${version}", TestProperties.inst().TEAMMATES_VERSION);
        htmlString = htmlString.replace("${test.student1}", TestProperties.inst().TEST_STUDENT1_ACCOUNT);
        htmlString = htmlString.replace("${test.student2}", TestProperties.inst().TEST_STUDENT2_ACCOUNT);
        htmlString = htmlString.replace("${test.instructor}", TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT);
        htmlString = htmlString.replace("${test.unreg}", TestProperties.inst().TEST_UNREG_ACCOUNT);
        htmlString = htmlString.replace("${test.admin}", TestProperties.inst().TEST_ADMIN_ACCOUNT);
        htmlString = htmlString.replace("${support.email}", Config.SUPPORT_EMAIL);
        htmlString = htmlString.replace("${app.url}", Config.APP_URL);
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
        boolean shouldIncludeCurrentNode = shouldIncludeCurrentNode(isHtmlPartPassedIn, currentNodeName);

        if (shouldIncludeCurrentNode) {
            currentHtmlText.append(indentation + "<" + currentNodeName);
            
            //Add the attributes of the tag
            NamedNodeMap actualAttributeList = currentNode.getAttributes();
            if(actualAttributeList!=null){
                
                List<Node> nodesList = getAttributesAsNodeList(actualAttributeList);
                sortAttributes(nodesList);
                
                for (int i = 0; i < actualAttributeList.getLength(); i++){
                    Node actualAttribute = actualAttributeList.item(i);
                    currentHtmlText.append(" "+ actualAttribute.getNodeName().toLowerCase() + "=\"" + actualAttribute.getNodeValue() + "\"");
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

    private static boolean shouldIncludeCurrentNode(boolean isHtmlPartPassedIn, String currentNodeName) {
        boolean shouldIncludeCurrentNode = !(isHtmlPartPassedIn && (currentNodeName.equals("html")
                                                                         || currentNodeName.equals("head")
                                                                         || currentNodeName.equals("body")
                                                                         || currentNodeName.equals("#comment")));
        return shouldIncludeCurrentNode;
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
