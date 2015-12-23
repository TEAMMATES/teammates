package teammates.test.driver;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FileHelper;

public class HtmlHelper {
    
    private static final String INDENTATION_STEP = "   ";

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
        String processedExpected = standardizeLineBreaks(expected);
        String processedActual = convertToStandardHtml(actual, isPart);

        if (areSameHtmls(processedExpected, processedActual)) {
            return true;
        }
        
        // the first failure might be caused by non-standardized conversion
        processedExpected = convertToStandardHtml(expected, isPart);

        if (areSameHtmls(processedExpected, processedActual)) {
            return true;
        } else {
            // if it still fails, then it is a failure after all
            if (isDifferenceToBeShown) {
                assertEquals("<expected>\n" + processedExpected + "</expected>",
                             "<actual>\n" + processedActual + "</actual>");
            }
            return false;
        }
    }
    
    private static boolean areSameHtmls(String expected, String actual) {
        return AssertHelper.isContainsRegex(expected, actual);
    }
    
    /**
     * {@link FileHelper#readFile} uses the system's line separator as line break,
     * while {@link #convertToStandardHtml} uses LF <code>\n</code> character.
     * Standardize by replacing each line separator with LF character.
     */
    private static String standardizeLineBreaks(String expected) {
        return expected.replace(Const.EOL, "\n");
    }

    /**
     * Transform the HTML text to follow a standard format. 
     * Element attributes are reordered in alphabetical order.
     * Spacing and line breaks are standardized too.
     * @param rawHtml the raw HTML string to be converted
     * @param isPart if true, ignores top-level HTML tags, i.e <code>&lt;html&gt;</code>,
     *               <code>&lt;head&gt;</code>, and <code>&lt;body&gt;</code>
     * @return converted HTML string
     */
    public static String convertToStandardHtml(String rawHtml, boolean isPart) {
        try {
            Node documentNode = getNodeFromString(rawHtml);
            String initialIndentation = INDENTATION_STEP; // TODO start from zero indentation
            return getNodeContent(documentNode, initialIndentation, isPart);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Node getNodeFromString(String string) throws SAXException, IOException {
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(string)));
        return parser.getDocument();
    }

    private static String convertToStandardHtmlRecursively(Node currentNode, String indentation,
                                                           boolean isPart) {
        switch (currentNode.getNodeType()) {
            case Node.TEXT_NODE:
                return generateNodeTextContent(currentNode, indentation);
            case Node.DOCUMENT_TYPE_NODE:
            case Node.COMMENT_NODE:
                // ignore the doctype definition and all HTML comments
                return ignoreNode();
            default: // in HTML this can only be Node.ELEMENT_NODE
                return convertElementNode(currentNode, indentation, isPart);
        }
    }
    
    private static String generateNodeTextContent(Node currentNode, String indentation) {
        String text = currentNode.getNodeValue().trim();
        return text.isEmpty() ? "" : indentation + text + "\n";
    }

    private static String convertElementNode(Node currentNode, String indentation, boolean isPart) {
        if (currentNode.getNodeName().equalsIgnoreCase("div")) {
            NamedNodeMap attributes = currentNode.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                if (isTooltipAttribute(attribute)
                     || isPopoverAttribute(attribute)
                     || Config.STUDENT_MOTD_URL.isEmpty() && isMotdWrapperAttribute(attribute)) {
                    // ignore all tooltips and popovers, also ignore studentMotd if the URL is empty
                    return ignoreNode();
                } else if (isMotdContainerAttribute(attribute)) {
                    // replace MOTD content with placeholder
                    return generateStudentMotdPlaceholder(indentation);
                }
            }
        }
        
        return generateNodeStringRepresentation(currentNode, indentation, isPart);
    }
    
    private static String ignoreNode() {
        return "";
    }
    
    private static String generateStudentMotdPlaceholder(String indentation) {
        return indentation + "${studentmotd.container}\n";
    }
    
    private static String generateNodeStringRepresentation(Node currentNode, String indentation, boolean isPart) {
        StringBuilder currentHtmlText = new StringBuilder();
        String currentNodeName = currentNode.getNodeName().toLowerCase();
        boolean shouldIncludeOpeningAndClosingTags = shouldIncludeOpeningAndClosingTags(isPart, currentNodeName);

        if (shouldIncludeOpeningAndClosingTags) {
            String nodeOpeningTag = indentation + getNodeOpeningTag(currentNode);
            currentHtmlText.append(nodeOpeningTag);
        }
        
        if (!isVoidElement(currentNodeName)) {
            String newIndentation = indentation + (shouldIncludeOpeningAndClosingTags ? INDENTATION_STEP : "");
            String nodeContent = getNodeContent(currentNode, newIndentation, isPart);
            currentHtmlText.append(nodeContent);

            if (shouldIncludeOpeningAndClosingTags) {
                String nodeClosingTag = indentation + getNodeClosingTag(currentNodeName);
                currentHtmlText.append(nodeClosingTag);
            }
        }
        
        return currentHtmlText.toString();
    }

    /**
     * If <code>isPart</code> (i.e only partial HTML checking is done),
     * do not generate opening/closing tags for top level elements
     * (i.e <code>html</code>, <code>head</code>, <code>body</code>).
     */
    private static boolean shouldIncludeOpeningAndClosingTags(boolean isPart, String currentNodeName) {
        return !(isPart && (currentNodeName.equals("html")
                            || currentNodeName.equals("head")
                            || currentNodeName.equals("body")));
    }

    /**
     * Checks for tooltips (i.e any <code>div</code> with class <code>tooltip</code> in it)
     */
    private static boolean isTooltipAttribute(Node attribute) {
        return checkForAttributeWithSpecificValue(attribute, "class", "tooltip");
    }
    
    /**
     * Checks for popovers (i.e any <code>div</code> with class <code>popover</code> in it)
     */
    private static boolean isPopoverAttribute(Node attribute) {
        return checkForAttributeWithSpecificValue(attribute, "class", "popover");
    }
    
    /**
     * Checks for Message of the Day (MOTD) wrapper (i.e a <code>div</code> with id
     * <code>student-motd-wrapper</code>).
     */
    private static boolean isMotdWrapperAttribute(Node attribute) {
        return checkForAttributeWithSpecificValue(attribute, "id", "student-motd-wrapper");
    }
    
    /**
     * Checks for Message of the Day (MOTD) container (i.e a <code>div</code> with id
     * <code>student-motd-container</code>).
     */
    private static boolean isMotdContainerAttribute(Node attribute) {
        return checkForAttributeWithSpecificValue(attribute, "id", "student-motd-container");
    }
    
    private static boolean checkForAttributeWithSpecificValue(Node attribute, String attrType, String attrValue) {
        if (attribute.getNodeName().equalsIgnoreCase(attrType)) {
            return attrType.equals("class") ? isClassContainingValue(attrValue, attribute.getNodeValue())
                                            : attribute.getNodeValue().equals(attrValue);
        } else {
            return false;
        }
    }
    
    private static boolean isClassContainingValue(String expected, String actual) {
        return actual.equals(expected)
                || actual.startsWith(expected + " ")
                || actual.endsWith(" " + expected)
                || actual.contains(" " + expected + " ");
    }

    private static String getNodeOpeningTag(Node currentNode) {
        StringBuilder openingTag = new StringBuilder();
        // add the start of opening tag
        openingTag.append("<" + currentNode.getNodeName().toLowerCase());
        
        // add the attributes of the tag (getAttributes() returns the attributes sorted alphabetically)
        NamedNodeMap attributes = currentNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            openingTag.append(" " + attribute.getNodeName().toLowerCase() + "="
                                  + "\"" + attribute.getNodeValue().replace("\"", "&quot;") + "\"");
        }
        
        // close the tag
        openingTag.append(getEndOfOpeningTag(currentNode) + "\n");
        return openingTag.toString();
    }
    
    private static String getNodeContent(Node currentNode, String indentation, boolean isPart) {
        StringBuilder nodeContent = new StringBuilder();
        NodeList childNodes = currentNode.getChildNodes();
        // recursively add contents of the child nodes
        for (int i = 0; i < childNodes.getLength(); i++) {
            String childNode = convertToStandardHtmlRecursively(childNodes.item(i), indentation, isPart);
            nodeContent.append(childNode);
        }
        return nodeContent.toString();
    }
    
    private static String getNodeClosingTag(String currentNodeName) {
        return "</" + currentNodeName + ">\n";
    }

    // TODO remove this method and use > for all cases, as defined in our style guide
    private static String getEndOfOpeningTag(Node node) {
        String tagName = node.getNodeName().toLowerCase();
        if(isVoidElement(tagName)){
            return "/>";
        }else {
            return ">";
        }
    }
    
    private static boolean isVoidElement(String elementName){
        return elementName.equals("br")
                || elementName.equals("hr")
                || elementName.equals("img")
                || elementName.equals("input")
                || elementName.equals("link")
                || elementName.equals("meta");
    }

}
