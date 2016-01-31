package teammates.test.driver;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FileHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;

public class HtmlHelper {
    
    private static final String INDENTATION_STEP = "   ";
    
    private static final String REGEX_CONTINUE_URL = ".*?";
    private static final String REGEX_ENCRYPTED_STUDENT_EMAIL = "[A-F0-9]{32,}";
    private static final String REGEX_ENCRYPTED_COURSE_ID = "[A-F0-9]{32,}";
    private static final String REGEX_ENCRYPTED_REGKEY = "[a-zA-Z0-9-_]{10,}";
    private static final String REGEX_BLOB_KEY = "(encoded_gs_key:)?[a-zA-Z0-9-_]{10,}";
    private static final String REGEX_QUESTION_ID = "[a-zA-Z0-9-_]{40,}";
    private static final String REGEX_COMMENT_ID = "[0-9]{16}";
    private static final String REGEX_DISPLAY_TIME = "(0[0-9]|1[0-2]):[0-5][0-9] [AP]M( UTC)?";
    private static final String REGEX_ADMIN_INSTITUTE_FOOTER = ".*?";
    
    private static final TestProperties TP = TestProperties.inst();

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
    private static String convertToStandardHtml(String rawHtml, boolean isPart) {
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
        openingTag.append(">\n");
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

    private static boolean isVoidElement(String elementName){
        return elementName.equals("br")
                || elementName.equals("hr")
                || elementName.equals("img")
                || elementName.equals("input")
                || elementName.equals("link")
                || elementName.equals("meta");
    }
    
    /**
     * Injects values specified in configuration files to the appropriate placeholders.
     */
    public static String injectTestProperties(String content) {
        return content.replace("${studentmotd.url}", Config.STUDENT_MOTD_URL)
                      .replace("${version}", TP.TEAMMATES_VERSION)
                      .replace("${test.admin}", TP.TEST_ADMIN_ACCOUNT)
                      .replace("${test.student1}", TP.TEST_STUDENT1_ACCOUNT)
                      .replace("${test.student2}", TP.TEST_STUDENT2_ACCOUNT)
                      .replace("${test.instructor}", TP.TEST_INSTRUCTOR_ACCOUNT);
    }
    
    /**
     * Processes the string from web page source for HTML comparison.
     */
    public static String processPageSourceForHtmlComparison(String content) {
        return replaceUnpredictableValuesWithPlaceholders(
                      suppressVariationsInInjectedValues(content));
    }
    
    /**
     * Processes the string from web page source for regeneration of expected HTML.<br>
     * Pre-condition: {@code content} has previously been processed with the
     * {@link #processPageSourceForHtmlComparison} function.
     */
    public static String processPageSourceForExpectedHtmlRegeneration(String content, boolean isPart) {
        return convertToStandardHtml(replaceInjectedValuesWithPlaceholders(content), isPart);
    }
    
    private static String suppressVariationsInInjectedValues(String content) {
        return content // replace truncated long accounts with their original counterparts
                      .replace(StringHelper.truncateLongId(TP.TEST_STUDENT1_ACCOUNT), TP.TEST_STUDENT1_ACCOUNT)
                      .replace(StringHelper.truncateLongId(TP.TEST_STUDENT2_ACCOUNT), TP.TEST_STUDENT2_ACCOUNT)
                      .replace(StringHelper.truncateLongId(TP.TEST_INSTRUCTOR_ACCOUNT), TP.TEST_INSTRUCTOR_ACCOUNT)
                      .replace(StringHelper.truncateLongId(TP.TEST_ADMIN_ACCOUNT), TP.TEST_ADMIN_ACCOUNT);
    }
    
    /**
     * Substitutes values that are different across various test runs with placeholders.
     * These values are identified using their known, unique formats.
     */
    private static String replaceUnpredictableValuesWithPlaceholders(String content) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy, ");
        String dateTimeNow = sdf.format(now);
        return content // dev server admin absolute URLs (${teammates.url}/_ah/...)
                      .replace("\"" + TP.TEAMMATES_URL + "/_ah", "\"/_ah")
                      // logout URL generated by Google
                      .replaceAll("_ah/logout\\?continue=" + REGEX_CONTINUE_URL + "\"",
                                  "_ah/logout?continue=\\${continue\\.url}\"")
                      // student profile picture link
                      .replaceAll(Const.ActionURIs.STUDENT_PROFILE_PICTURE
                                  + "\\?" + Const.ParamsNames.STUDENT_EMAIL + "=" + REGEX_ENCRYPTED_STUDENT_EMAIL
                                  + "\\&amp;" + Const.ParamsNames.COURSE_ID + "=" + REGEX_ENCRYPTED_COURSE_ID, 
                                  Const.ActionURIs.STUDENT_PROFILE_PICTURE
                                  + "\\?" + Const.ParamsNames.STUDENT_EMAIL + "=\\${student\\.email\\.enc}"
                                  + "\\&amp;" + Const.ParamsNames.COURSE_ID + "=\\${course\\.id\\.enc}")
                      // blob-key in student profile page
                      .replaceAll(Const.ActionURIs.STUDENT_PROFILE_PICTURE
                                  + "\\?" + Const.ParamsNames.BLOB_KEY + "=" + REGEX_BLOB_KEY,
                                  Const.ActionURIs.STUDENT_PROFILE_PICTURE
                                  + "\\?" + Const.ParamsNames.BLOB_KEY+ "=\\${blobkey}")
                      .replaceAll("( type=\"hidden\"|"
                                  + " name=\"" + Const.ParamsNames.BLOB_KEY + "\"|"
                                  + " id=\"blobKey\"|"
                                  + " value=\"" + REGEX_BLOB_KEY + "\"){4}",
                                  " id=\"blobKey\" name=\"" + Const.ParamsNames.BLOB_KEY + "\""
                                  + " type=\"hidden\" value=\"\\${blobkey}\"")
                      // regkey in URLs
                      .replaceAll(Const.ParamsNames.REGKEY + "=" + REGEX_ENCRYPTED_REGKEY,
                                  Const.ParamsNames.REGKEY + "=\\${regkey\\.enc}")
                      // regkey in unreg student page
                      .replaceAll("( type=\"hidden\"|"
                                  + " name=\"" + Const.ParamsNames.REGKEY + "\"|"
                                  + " value=\"" + REGEX_ENCRYPTED_REGKEY + "\"){3}",
                                  " name=\"" + Const.ParamsNames.REGKEY + "\""
                                  + " type=\"hidden\" value=\"\\${regkey\\.enc}\"")
                      // questionid as value
                      .replaceAll("value=\"" + REGEX_QUESTION_ID + "\"", "value=\"\\${question\\.id}\"")
                      // questionid as part of responseid
                      .replaceAll("\"" + REGEX_QUESTION_ID + "%", "\"\\${question\\.id}%")
                      // commentid in quotes, used as values
                      .replaceAll("\"" + REGEX_COMMENT_ID + "\"", "\"\\${comment\\.id}\"")
                      // commentid in URLs
                      .replaceAll("#" + REGEX_COMMENT_ID, "#\\${comment\\.id}")
                      // commentid as part of div ids
                      .replaceAll("responseCommentRow-" + REGEX_COMMENT_ID, "responseCommentRow-\\${comment\\.id}")
                      .replaceAll("commentBar-" + REGEX_COMMENT_ID, "commentBar-\\${comment\\.id}")
                      .replaceAll("plainCommentText-" + REGEX_COMMENT_ID, "plainCommentText-\\${comment\\.id}")
                      // today's date
                      .replace(TimeHelper.formatDate(now), "${today}")
                      // date/time now e.g [Thu, 07 May 2015, 07:52 PM] or [Thu, 07 May 2015, 07:52 PM UTC]
                      .replaceAll(dateTimeNow + REGEX_DISPLAY_TIME, "\\${datetime\\.now}")
                      // jQuery files
                      .replace("/js/lib/jquery.min.js", "${lib.path}/jquery.min.js")
                      .replace("https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js", "${lib.path}/jquery.min.js")
                      // jQuery-ui files
                      .replace("/js/lib/jquery-ui.min.js", "${lib.path}/jquery-ui.min.js")
                      .replace("https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js", "${lib.path}/jquery-ui.min.js")
                      // admin footer, test institute section
                      .replaceAll("(?s)<div( class=\"col-md-8\"| id=\"adminInstitute\"){2}>"
                                              + REGEX_ADMIN_INSTITUTE_FOOTER + "</div>",
                                  "\\${admin\\.institute}")
                      // top HTML tag with xmlns defined
                      // TODO check if this is necessary
                      .replace("<html xmlns=\"http://www.w3.org/1999/xhtml\">", "<html>")
                      // noscript is to be cleared
                      // TODO check if wildcarding this is better; better yet, check if not removing at all works
                      .replaceFirst("(?s)<noscript>.*</noscript>", "");
    }
    
    private static String replaceInjectedValuesWithPlaceholders(String content) {
        return content.replace("window.location.origin + '/" + Config.STUDENT_MOTD_URL + "';",
                               "window.location.origin + '/${studentmotd.url}';")
                      .replace("V" + TP.TEAMMATES_VERSION, "V${version}")
                      .replace(TP.TEST_STUDENT1_ACCOUNT, "${test.student1}")
                      .replace(TP.TEST_STUDENT2_ACCOUNT, "${test.student2}")
                      .replace(TP.TEST_INSTRUCTOR_ACCOUNT, "${test.instructor}")
                      .replace(TP.TEST_ADMIN_ACCOUNT, "${test.admin}");
    }
    
    /**
     * This method is only used for testing.
     */
    public static String injectContextDependentValuesForTest(String content) {
        Date now = new Date();
        return content.replace("<!-- test.url -->", TP.TEAMMATES_URL)
                      .replace("<!-- studentmotd.url -->", Config.STUDENT_MOTD_URL)
                      .replace("<!-- version -->", TP.TEAMMATES_VERSION)
                      .replace("<!-- test.student1 -->", TP.TEST_STUDENT1_ACCOUNT)
                      .replace("<!-- test.student1.truncated -->",
                               StringHelper.truncateLongId(TP.TEST_STUDENT1_ACCOUNT))
                      .replace("<!-- test.student2 -->", TP.TEST_STUDENT2_ACCOUNT)
                      .replace("<!-- test.student2.truncated -->",
                               StringHelper.truncateLongId(TP.TEST_STUDENT2_ACCOUNT))
                      .replace("<!-- test.instructor -->", TP.TEST_INSTRUCTOR_ACCOUNT)
                      .replace("<!-- test.instructor.truncated -->",
                               StringHelper.truncateLongId(TP.TEST_INSTRUCTOR_ACCOUNT))
                      .replace("<!-- test.admin -->", TP.TEST_ADMIN_ACCOUNT)
                      .replace("<!-- test.admin.truncated -->",
                               StringHelper.truncateLongId(TP.TEST_ADMIN_ACCOUNT))
                      .replace("<!-- now.date -->", TimeHelper.formatDate(now))
                      .replace("<!-- now.datetime -->", TimeHelper.formatTime12H(now))
                      .replace("<!-- now.datetime.comments -->", TimeHelper.formatDateTimeForComments(now));
    }

}
