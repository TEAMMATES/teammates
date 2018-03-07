package teammates.test.driver;

import static org.testng.AssertJUnit.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;

/**
 * Provides mechanism for HTML comparison during testing.
 * GodMode is also configured here.
 */
public final class HtmlHelper {

    private static final String INDENTATION_STEP = "  ";

    private static final String REGEX_UPPERCASE_HEXADECIMAL_CHAR_32_MULTI = "[A-F0-9]{32,}";
    private static final String REGEX_UPPERCASE_HEXADECIMAL_CHAR_32 = "[A-F0-9]{32}";

    private static final String REGEX_CONTINUE_URL = ".*?";
    private static final String REGEX_ENCRYPTED_STUDENT_EMAIL = REGEX_UPPERCASE_HEXADECIMAL_CHAR_32_MULTI;
    private static final String REGEX_ENCRYPTED_COURSE_ID = REGEX_UPPERCASE_HEXADECIMAL_CHAR_32_MULTI;
    private static final String REGEX_ENCRYPTED_REGKEY = REGEX_UPPERCASE_HEXADECIMAL_CHAR_32_MULTI;
    private static final String REGEX_ANONYMOUS_PARTICIPANT_HASH = "[0-9]{1,10}";
    private static final String REGEX_BLOB_KEY = "(encoded_gs_key:)?[a-zA-Z0-9-_]{10,}";
    private static final String REGEX_QUESTION_ID = "[a-zA-Z0-9-_]{40,}";
    private static final String REGEX_COMMENT_ID = "[0-9]{16}";
    private static final String REGEX_DISPLAY_TIME = "(0[0-9]|1[0-2]):[0-5][0-9] ([AP]M|NOON)";
    private static final String REGEX_DISPLAY_TIME_ISO_8601_UTC =
            "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]Z";
    private static final String REGEX_ADMIN_INSTITUTE_FOOTER = ".*?";
    private static final String REGEX_SESSION_TOKEN = REGEX_UPPERCASE_HEXADECIMAL_CHAR_32;
    private static final String REGEX_TIMEZONE_OFFSET = "UTC([+-]\\d{4})";

    private HtmlHelper() {
        // utility class
    }

    /**
     * Verifies that two HTML files are logically equivalent, e.g. ignores
     * differences in whitespace and attribute order. If the assertion fails,
     * <code>AssertionError</code> will be thrown and the difference can then be traced.
     * @param expected the expected string for comparison
     * @param actual the actual string for comparison
     * @param isPart if true, ignores top-level HTML tags, i.e <code>&lt;html&gt;</code>,
     *               <code>&lt;head&gt;</code>, and <code>&lt;body&gt;</code>
     */
    public static boolean assertSameHtml(String expected, String actual, boolean isPart) {
        return assertSameHtml(expected, actual, isPart, true);
    }

    /**
     * Verifies that two HTML files are logically equivalent, e.g. ignores
     * differences in whitespace and attribute order.
     * @param expected the expected string for comparison
     * @param actual the actual string for comparison
     * @param isPart if true, ignores top-level HTML tags, i.e <code>&lt;html&gt;</code>,
     *               <code>&lt;head&gt;</code>, and <code>&lt;body&gt;</code>
     */
    public static boolean areSameHtml(String expected, String actual, boolean isPart) {
        return assertSameHtml(expected, actual, isPart, false);
    }

    private static boolean assertSameHtml(String expected, String actual, boolean isPart,
                                          boolean isDifferenceToBeShown) {
        String processedActual = convertToStandardHtml(actual, isPart);

        if (areSameHtmls(expected, processedActual)) {
            return true;
        }

        // the first failure might be caused by non-standardized conversion
        String processedExpected = convertToStandardHtml(expected, isPart);

        if (areSameHtmls(processedExpected, processedActual)) {
            return true;
        }

        // if it still fails, then it is a failure after all
        if (isDifferenceToBeShown) {
            assertEquals("<expected>" + System.lineSeparator() + processedExpected + "</expected>",
                         "<actual>" + System.lineSeparator() + processedActual + "</actual>");
        }
        return false;
    }

    private static boolean areSameHtmls(String expected, String actual) {
        // accounts for the variations in line breaks
        return expected.replaceAll("[\r\n]", "").equals(actual.replaceAll("[\r\n]", ""));
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
            String initialIndentation = "";
            return getNodeContent(documentNode, initialIndentation, isPart);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Node getNodeFromString(String string) {
        return new W3CDom().fromJsoup(Jsoup.parse(string));
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
        text = text.replaceAll("[ ]*(\\r?\\n[ ]*)+[ ]*", " ");
        text = SanitizationHelper.sanitizeForHtmlTag(text);
        // line breaks in text are removed as they are ignored in HTML
        // the lines separated by line break will be joined with a single whitespace character
        return text.isEmpty() ? "" : indentation + text + System.lineSeparator();
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
                } else if (isTinymceStyleDiv(attribute)) {
                    // ignore as the style definition differs across browsers
                    return ignoreNode();
                } else if (isMotdContainerAttribute(attribute)) {
                    // replace MOTD content with placeholder
                    return generateStudentMotdPlaceholder(indentation);
                } else if (isDatepickerAttribute(attribute)) {
                    // replace datepicker with placeholder
                    return generateDatepickerPlaceholder(indentation);
                }
            }
        } else if (currentNode.getNodeName().equalsIgnoreCase("select")) {
            NamedNodeMap attributes = currentNode.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                if (isTimeZoneSelectorAttribute(attribute)) {
                    return generateTimeZoneSelectorPlaceholder(indentation);
                }
            }
        } else if (currentNode.getNodeName().equalsIgnoreCase("style")) {
            NamedNodeMap attributes = currentNode.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                if (isTinymceStyleAttribute(attribute)) {
                    // ignore as the style definition differs across browsers
                    return ignoreNode();
                }
            }
        }

        return generateNodeStringRepresentation(currentNode, indentation, isPart);
    }

    private static String ignoreNode() {
        return "";
    }

    private static String generateStudentMotdPlaceholder(String indentation) {
        return indentation + "${studentmotd.container}" + System.lineSeparator();
    }

    private static String generateTimeZoneSelectorPlaceholder(String indentation) {
        return indentation + "${timezone.options}" + System.lineSeparator();
    }

    private static String generateDatepickerPlaceholder(String indentation) {
        return indentation + "${datepicker}" + System.lineSeparator();
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
            String newIndentation = indentation + (shouldIndent(currentNodeName) ? INDENTATION_STEP : "");
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
        return !(isPart && ("html".equals(currentNodeName)
                            || "head".equals(currentNodeName)
                            || "body".equals(currentNodeName)));
    }

    private static boolean shouldIndent(String currentNodeName) {
        // Indentation is not necessary for top level elements
        return !("html".equals(currentNodeName)
                 || "head".equals(currentNodeName)
                 || "body".equals(currentNodeName));
    }

    private static boolean isTinymceStyleDiv(Node attribute) {
        if (!"style".equalsIgnoreCase(attribute.getNodeName())) {
            return false;
        }
        String value = attribute.getNodeValue();
        return value.contains("position: static") && value.contains("height: 0px") && value.contains("width: 0px")
                && value.contains("padding: 0px") && value.contains("margin: 0px");
    }

    private static boolean isTinymceStyleAttribute(Node attribute) {
        return checkForAttributeWithSpecificValue(attribute, "id", "mceDefaultStyles");
    }

    /**
     * Checks for tooltips (i.e any <code>div</code> with class <code>tooltip</code> in it).
     */
    private static boolean isTooltipAttribute(Node attribute) {
        return checkForAttributeWithSpecificValue(attribute, "class", "tooltip");
    }

    /**
     * Checks for popovers (i.e any <code>div</code> with class <code>popover</code> in it).
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

    /**
     * Checks for datepicker (i.e a <code>div</code> with id <code>ui-datepicker-div</code>).
     */
    private static boolean isDatepickerAttribute(Node attribute) {
        return checkForAttributeWithSpecificValue(attribute, "id", "ui-datepicker-div");
    }

    /**
     * Checks for timezone selectors (i.e a <code>select</code> with id <code>coursetimezone</code>).
     */
    private static boolean isTimeZoneSelectorAttribute(Node attribute) {
        return checkForAttributeWithSpecificValue(attribute, "id", "coursetimezone");
    }

    private static boolean checkForAttributeWithSpecificValue(Node attribute, String attrType, String attrValue) {
        if (attribute.getNodeName().equalsIgnoreCase(attrType)) {
            return "class".equals(attrType) ? isClassContainingValue(attrValue, attribute.getNodeValue())
                                            : attribute.getNodeValue().equals(attrValue);
        }
        return false;
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
        openingTag.append('<').append(currentNode.getNodeName().toLowerCase());

        // add the attributes of the tag (getAttributes() returns the attributes sorted alphabetically)
        NamedNodeMap attributes = currentNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            openingTag.append(" " + attribute.getNodeName().toLowerCase() + "="
                                  + "\"" + attribute.getNodeValue().replace("\"", "&quot;") + "\"");
        }

        // close the tag
        openingTag.append('>').append(System.lineSeparator());
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
        return "</" + currentNodeName + ">" + System.lineSeparator();
    }

    private static boolean isVoidElement(String elementName) {
        return "br".equals(elementName)
                || "hr".equals(elementName)
                || "img".equals(elementName)
                || "input".equals(elementName)
                || "link".equals(elementName)
                || "meta".equals(elementName);
    }

    /**
     * Injects values specified in configuration files to the appropriate placeholders.
     */
    public static String injectTestProperties(String content) {
        return content.replace("${studentmotd.url}", Config.STUDENT_MOTD_URL)
                      .replace("${support.email}", Config.SUPPORT_EMAIL)
                      .replace("${version}", TestProperties.TEAMMATES_VERSION)
                      .replace("${test.admin}", TestProperties.TEST_ADMIN_ACCOUNT)
                      .replace("${test.student1}", TestProperties.TEST_STUDENT1_ACCOUNT)
                      .replace("${test.student2}", TestProperties.TEST_STUDENT2_ACCOUNT)
                      .replace("${test.instructor}", TestProperties.TEST_INSTRUCTOR_ACCOUNT);
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
                      .replace(StringHelper.truncateLongId(TestProperties.TEST_STUDENT1_ACCOUNT),
                               TestProperties.TEST_STUDENT1_ACCOUNT)
                      .replace(StringHelper.truncateLongId(TestProperties.TEST_STUDENT2_ACCOUNT),
                               TestProperties.TEST_STUDENT2_ACCOUNT)
                      .replace(StringHelper.truncateLongId(TestProperties.TEST_INSTRUCTOR_ACCOUNT),
                               TestProperties.TEST_INSTRUCTOR_ACCOUNT)
                      .replace(StringHelper.truncateLongId(TestProperties.TEST_ADMIN_ACCOUNT),
                               TestProperties.TEST_ADMIN_ACCOUNT);
    }

    /**
     * Substitutes values that are different across various test runs with placeholders.
     * These values are identified using their known, unique formats.
     */
    private static String replaceUnpredictableValuesWithPlaceholders(String content) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy, ");
        // get session's time zone from content.
        // this method is not applicable for pages with multiple time zones like InstructorSearchPage
        sdf.setTimeZone(getTimeZone(content));
        String dateTimeNow = sdf.format(now);
        SimpleDateFormat sdfForIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'");
        String dateTimeNowInIso8601 = sdfForIso8601.format(now);
        SimpleDateFormat sdfForCoursesPage = new SimpleDateFormat("d MMM yyyy");
        String dateTimeNowInCoursesPageFormat = sdfForCoursesPage.format(now);
        return content // dev server admin absolute URLs (${teammates.url}/_ah/...)
                      .replace("\"" + TestProperties.TEAMMATES_URL + "/_ah", "\"/_ah")
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
                                  + "\\?" + Const.ParamsNames.BLOB_KEY + "=\\${blobkey}")
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
                      // anonymous student identifier on results page
                      .replaceAll(Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT + " (student|instructor|team) "
                                  + REGEX_ANONYMOUS_PARTICIPANT_HASH, Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT
                                  + " $1 \\${participant\\.hash}")
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
                      // date/time now e.g [Thu, 07 May 2015, 07:52 PM]
                      .replaceAll(dateTimeNow + REGEX_DISPLAY_TIME, "\\${datetime\\.now}")
                      .replaceAll(dateTimeNowInIso8601 + REGEX_DISPLAY_TIME_ISO_8601_UTC, "\\${datetime\\.now\\.iso8601utc}")
                      .replaceAll(dateTimeNowInCoursesPageFormat, "\\${datetime\\.now\\.courses}")
                      // admin footer, test institute section
                      .replaceAll("(?s)<div( class=\"col-md-8\"| id=\"adminInstitute\"){2}>"
                                              + REGEX_ADMIN_INSTITUTE_FOOTER + "</div>",
                                  "\\${admin\\.institute}")
                      // sessionToken in form inputs
                      .replaceAll("( type=\"hidden\"|"
                                   + " name=\"" + Const.ParamsNames.SESSION_TOKEN + "\"|"
                                   + " value=\"" + REGEX_SESSION_TOKEN + "\"){3}",
                                   " name=\"" + Const.ParamsNames.SESSION_TOKEN + "\""
                                   + " type=\"hidden\" value=\"\\${sessionToken}\"")
                      // sessionToken in URL parameters
                      .replaceAll("(\\&amp;|\\?)" + Const.ParamsNames.SESSION_TOKEN + "=" + REGEX_SESSION_TOKEN,
                                  "$1" + Const.ParamsNames.SESSION_TOKEN + "=\\${sessionToken}")
                      // top HTML tag with xmlns defined
                      // TODO check if this is necessary
                      .replace("<html xmlns=\"http://www.w3.org/1999/xhtml\">", "<html>")
                      // noscript is to be cleared
                      // TODO check if wildcarding this is better; better yet, check if not removing at all works
                      .replaceFirst("(?s)<noscript>.*</noscript>", "");
    }

    private static String replaceInjectedValuesWithPlaceholders(String content) {
        return content.replaceAll("( type=\"hidden\"| id=\"motd-url\"|"
                                      + " value=\"" + Config.STUDENT_MOTD_URL + "\"){3}",
                                  " id=\"motd-url\" type=\"hidden\" value=\"\\${studentmotd\\.url}\"")
                      .replace("V" + TestProperties.TEAMMATES_VERSION, "V${version}")
                      .replace(TestProperties.TEST_STUDENT1_ACCOUNT, "${test.student1}")
                      .replace(TestProperties.TEST_STUDENT2_ACCOUNT, "${test.student2}")
                      .replace(TestProperties.TEST_INSTRUCTOR_ACCOUNT, "${test.instructor}")
                      .replace(TestProperties.TEST_ADMIN_ACCOUNT, "${test.admin}")
                      .replace(Config.SUPPORT_EMAIL, "${support.email}");
    }

    /**
     * This method is only used for testing.
     */
    public static String injectContextDependentValuesForTest(String content) {
        Date now = new Date();
        return content.replace("<!-- test.url -->", TestProperties.TEAMMATES_URL)
                      .replace("<!-- studentmotd.url -->", Config.STUDENT_MOTD_URL)
                      .replace("<!-- support.email -->", Config.SUPPORT_EMAIL)
                      .replace("<!-- version -->", TestProperties.TEAMMATES_VERSION)
                      .replace("<!-- test.student1 -->", TestProperties.TEST_STUDENT1_ACCOUNT)
                      .replace("<!-- test.student1.truncated -->",
                               StringHelper.truncateLongId(TestProperties.TEST_STUDENT1_ACCOUNT))
                      .replace("<!-- test.student2 -->", TestProperties.TEST_STUDENT2_ACCOUNT)
                      .replace("<!-- test.student2.truncated -->",
                               StringHelper.truncateLongId(TestProperties.TEST_STUDENT2_ACCOUNT))
                      .replace("<!-- test.instructor -->", TestProperties.TEST_INSTRUCTOR_ACCOUNT)
                      .replace("<!-- test.instructor.truncated -->",
                               StringHelper.truncateLongId(TestProperties.TEST_INSTRUCTOR_ACCOUNT))
                      .replace("<!-- test.admin -->", TestProperties.TEST_ADMIN_ACCOUNT)
                      .replace("<!-- test.admin.truncated -->",
                               StringHelper.truncateLongId(TestProperties.TEST_ADMIN_ACCOUNT))
                      .replace("<!-- now.datetime -->", TimeHelper.formatTime12H(now))
                      .replace("<!-- now.datetime.sessions -->", TimeHelper.formatDateTimeForSessions(now, 0))
                      .replace("<!-- now.datetime.iso8601utc -->", TimeHelper.formatDateToIso8601Utc(now))
                      .replace("<!-- now.datetime.courses -->", TimeHelper.formatDateTimeForInstructorCoursesPage(now));
    }

    private static TimeZone getTimeZone(String content) {
        // searches for first String of pattern "UTC+xxxx" in the content.
        Pattern pattern = Pattern.compile(REGEX_TIMEZONE_OFFSET);
        Matcher matcher = pattern.matcher(content);
        // set default time zone offset.
        String timeZoneOffset = "+0000";
        if (matcher.find()) {
            timeZoneOffset = matcher.group(1);
        }
        return TimeZone.getTimeZone("GMT" + timeZoneOffset);
    }

}
