package teammates.test.cases.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.util.SanitizationHelper;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link SanitizationHelper}.
 */
public class SanitizationHelperTest extends BaseTestCase {

    @Test
    public void testSanitizeGoogleId() {
        assertEquals("big-small.20_12", SanitizationHelper.sanitizeGoogleId(" big-small.20_12 @Gmail.COM \t\n"));
        assertEquals("user@hotmail.com", SanitizationHelper.sanitizeGoogleId(" user@hotmail.com \t\n"));
    }

    @Test
    public void testSanitizeEmail() {
        String emailWithWhiteSpaces = "\tnormal@email.com \t\n";
        String normalEmail = "normal@email.com";

        assertNull(SanitizationHelper.sanitizeEmail(null));
        assertEquals(normalEmail, SanitizationHelper.sanitizeEmail(normalEmail));
        assertEquals(normalEmail, SanitizationHelper.sanitizeEmail(emailWithWhiteSpaces));
    }

    @Test
    public void testSanitizeName() {

        String nameWithWhiteSpaces = "\t alice   bob \t\n";
        String normalName = "alice bob";

        assertNull(SanitizationHelper.sanitizeName(null));
        assertEquals(normalName, SanitizationHelper.sanitizeName(normalName));
        assertEquals(normalName, SanitizationHelper.sanitizeName(nameWithWhiteSpaces));
    }

    @Test
    public void testSanitizeTitle() {
        // tested as name
    }

    @Test
    public void testSanitizeTextField() {
        // tested as email
    }

    @Test
    public void testSanitizeForJs() {
        sanitizeJs_receivesNull_returnsNull();
        sanitizeJs_receivesUnsanitized_returnsSanitized();
    }

    private void sanitizeJs_receivesNull_returnsNull() {
        assertNull(SanitizationHelper.sanitizeForJs(null));
    }

    private void sanitizeJs_receivesUnsanitized_returnsSanitized() {
        String unsanitized = "\\ \" ' #"; // i.e., [\ " ' #]
        String expected = "\\\\ \\&quot; \\&#39; \\#"; // i.e., [\\ \&quot; \&#39; \#]
        String sanitized = SanitizationHelper.sanitizeForJs(unsanitized);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testSanitizeForHtml() {
        sanitizeHtml_receivesNull_returnsNull();
        sanitizeHtml_receivesCodeInjection_returnsSanitized();
        sanitizeHtml_receivesSanitized_returnsUnchanged();
    }

    private void sanitizeHtml_receivesNull_returnsNull() {
        assertNull(SanitizationHelper.sanitizeForHtml((String) null));
    }

    private void sanitizeHtml_receivesCodeInjection_returnsSanitized() {
        String unsanitized = "< > \" / ' &"
                             + "<script>alert('injected');</script>";
        String expected = "&lt; &gt; &quot; &#x2f; &#39; &amp;"
                          + "&lt;script&gt;alert(&#39;injected&#39;);&lt;&#x2f;script&gt;";
        String sanitized = SanitizationHelper.sanitizeForHtml(unsanitized);
        assertEquals(expected, sanitized);
    }

    private void sanitizeHtml_receivesSanitized_returnsUnchanged() {
        String sanitized = "&lt; &gt; &quot; &#x2f; &#39; &amp;"
                           + "&lt;script&gt;alert(&#39;injected&#39;);&lt;&#x2f;script&gt;";
        assertEquals(sanitized, SanitizationHelper.sanitizeForHtml(sanitized));
    }

    @Test
    public void testSanitizeForHtmlList() {
        List<String> unsanitizedHtml = new ArrayList<>(Arrays.asList(
                "apple <", "banana ' dogs ", "rollercoasters & tycoons", "", null)
        );

        List<String> sanitizedHtml = new ArrayList<>(Arrays.asList(
                "apple &lt;", "banana &#39; dogs ", "rollercoasters &amp; tycoons", "", null)
        );

        assertEquals(sanitizedHtml, SanitizationHelper.sanitizeForHtml(unsanitizedHtml));
        assertEquals(new ArrayList<>(), SanitizationHelper.sanitizeForHtml(new ArrayList<>()));
        assertNull(SanitizationHelper.sanitizeForHtml((List<String>) null));
    }

    @Test
    public void testDesanitizeFromHtml() {
        desanitizeFromHtml_receivesNull_returnsNull();
        desanitizeFromHtml_recievesEmpty_returnsEmpty();
        desanitizeFromHtml_receivesSanitized_returnsDesanitized();
    }

    private void desanitizeFromHtml_receivesNull_returnsNull() {
        assertNull(SanitizationHelper.desanitizeFromHtml((String) null));
    }

    private void desanitizeFromHtml_recievesEmpty_returnsEmpty() {
        String emptyString = "";
        assertEquals(emptyString, SanitizationHelper.desanitizeFromHtml(emptyString));
    }

    private void desanitizeFromHtml_receivesSanitized_returnsDesanitized() {
        String text = "<text><div> 'param' &&& \\//\\ \" <The quick brown fox jumps over the lazy dog.>";
        String sanitizedText = SanitizationHelper.sanitizeForHtml(text);
        assertEquals(text, SanitizationHelper.desanitizeFromHtml(sanitizedText));
    }

    @Test
    public void testDesanitizeFromHtmlSet() {
        Set<String> sanitizedHtml = new HashSet<>(Arrays.asList(
                "apple &lt;", "banana &#39; dogs ", "rollercoasters &amp; tycoons", "", null)
        );
        Set<String> desanitizedHtml = new HashSet<>(Arrays.asList(
                "apple <", "banana ' dogs ", "rollercoasters & tycoons", "", null)
        );

        assertEquals(desanitizedHtml, SanitizationHelper.desanitizeFromHtml(sanitizedHtml));
        assertEquals(new HashSet<>(), SanitizationHelper.desanitizeFromHtml(new HashSet<>()));
        assertNull(SanitizationHelper.desanitizeFromHtml((Set<String>) null));
    }

    @Test
    public void testSanitizeForHtmlTag() {
        sanitizeHtmlTag_receivesNull_returnsNull();
        sanitizeHtmlTag_receivesHtml_returnsSanitized();
    }

    private void sanitizeHtmlTag_receivesHtml_returnsSanitized() {
        String unsanitized = "<div><td>&lt;</td></div>";
        String expected = "&lt;div&gt;&lt;td&gt;&lt;&lt;/td&gt;&lt;/div&gt;";
        String sanitized = SanitizationHelper.sanitizeForHtmlTag(unsanitized);
        assertEquals(expected, sanitized);
    }

    private void sanitizeHtmlTag_receivesNull_returnsNull() {
        assertNull(SanitizationHelper.sanitizeForHtmlTag(null));
    }

    @Test
    public void testSanitizeForRichText() {
        assertNull(SanitizationHelper.sanitizeForRichText((Text) null));
        assertNull(SanitizationHelper.sanitizeForRichText((String) null));
        assertEquals("", SanitizationHelper.sanitizeForRichText(""));
        assertEquals("<p>wihtout changes</p>", SanitizationHelper.sanitizeForRichText("<p>wihtout changes</p>"));
        assertEquals("<p>spaces test</p>", SanitizationHelper.sanitizeForRichText("<p >spaces test</p >"));
        String actualRichText = "<body onload=\"alert('onload');\">"
                                + "<a href=\"https://teammatesv4.appspot.com\" onclick=\"alert('fail');\"></a>"
                                + "<script>alert('fail');</script>"
                                + "<h1></h1><h2></h2><h3></h3><h4></h4><h5></h5><h6></h6>"
                                + "<hr />"
                                + "<img src=\"https://teammatesv4.appspot.com/images/overview.png\" />"
                                + "<p style=\"text-align:center\"><strong>Content</strong></p>"
                                + "<div onmouseover=\"alert('onmouseover');\"></div>"
                                + "<iframe></iframe>"
                                + "<input></input>"
                                + "<span style=\"color:#339966\">Content</span>";
        String expectedRichText = "<a href=\"https://teammatesv4.appspot.com\"></a>"
                                  + "<h1></h1><h2></h2><h3></h3><h4></h4><h5></h5><h6></h6>"
                                  + "<hr />"
                                  + "<img src=\"https://teammatesv4.appspot.com/images/overview.png\" />"
                                  + "<p style=\"text-align:center\"><strong>Content</strong></p>"
                                  + "<div></div>"
                                  + "<span style=\"color:#339966\">Content</span>";
        String sanitized = SanitizationHelper.sanitizeForRichText(actualRichText);
        assertEquals(expectedRichText, sanitized);

        Text actualRichTextObj = new Text(actualRichText);
        Text sanitizedTextObj = SanitizationHelper.sanitizeForRichText(actualRichTextObj);
        assertEquals(expectedRichText, sanitizedTextObj.getValue());

        actualRichText = "<table cellspacing = \"5\" onmouseover=\"alert('onmouseover');\">"
                + "<thead><tr><td>No.</td><td colspan = \"2\">People</td></tr></thead>"
                + "<caption> Table with caption</caption>"
                + "<tbody><tr><td>1</td><td>Amy</td><td><strong>Smith</strong></td></tr>"
                + "<tr><td>2</td><td>Bob</td><td><strong>Tan</strong></td></tr>"
                + "</tbody></table>"
                + "<p>Chemical formula: C<sub>6</sub>H<sub>12</sub>O<sub>6</sub></p>"
                + "</td></option></div> invalid closing tags"
                + "f(x) = x<sup>2</sup>"
                + "<code>System.out.println(\"Hello World\");</code>";
        expectedRichText = "<table cellspacing=\"5\">"
                + "<thead><tr><td>No.</td><td colspan=\"2\">People</td></tr></thead>"
                + "<caption> Table with caption</caption>"
                + "<tbody><tr><td>1</td><td>Amy</td><td><strong>Smith</strong></td></tr>"
                + "<tr><td>2</td><td>Bob</td><td><strong>Tan</strong></td></tr>"
                + "</tbody></table>"
                + "<p>Chemical formula: C<sub>6</sub>H<sub>12</sub>O<sub>6</sub></p>"
                + " invalid closing tags"
                + "f(x) &#61; x<sup>2</sup>"
                + "<code>System.out.println(&#34;Hello World&#34;);</code>";
        sanitized = SanitizationHelper.sanitizeForRichText(actualRichText);
        assertEquals(expectedRichText, sanitized);
    }

    @Test
    public void testSanitizeForNextUrl() {
        sanitizeForNextUrl_receivesNull_returnsNull();
        sanitizeForNextUrl_receivesUrl_returnsSanitizedUrl();
    }

    private void sanitizeForNextUrl_receivesNull_returnsNull() {
        assertNull(SanitizationHelper.sanitizeForNextUrl(null));
    }

    private void sanitizeForNextUrl_receivesUrl_returnsSanitizedUrl() {
        String url = "/page/studentCourseJoinAuthenticated?key=FF6266"
                     + "&next=/page/studentHomePage%23/encodedHashHere%2B/encodedPlusHere";
        String expected = "/page/studentCourseJoinAuthenticated?key=FF6266$"
                          + "{amp}next=/page/studentHomePage${hash}/encodedHashHere${plus}/encodedPlusHere";
        assertEquals(expected, SanitizationHelper.sanitizeForNextUrl(url));
    }

    @Test
    public void testDesanitizeFromNextUrl() {
        desanitizeFromNextUrl_receivesNull_returnsNull();
        desanitizeFromNextUrl_receivesSanitized_returnsDesanitized();
    }

    private void desanitizeFromNextUrl_receivesNull_returnsNull() {
        assertNull(SanitizationHelper.desanitizeFromNextUrl(null));
    }

    private void desanitizeFromNextUrl_receivesSanitized_returnsDesanitized() {
        String expected = "/page/studentCourseJoinAuthenticated?key=FF6266"
                          + "&next=/page/studentHomePage%23/encodedHashHere%2B/encodedPlusHere";
        String sanitizedUrl = SanitizationHelper.sanitizeForNextUrl(expected);
        assertEquals(expected, SanitizationHelper.desanitizeFromNextUrl(sanitizedUrl));

        sanitizedUrl = "/page/studentCourseDetailsPage?user=USERNAME"
                       + "${amp}courseid=CS2103-Aug2016${hash}/encodedHashHere${plus}/encodedPlusHere"
                       + " /plusHere";
        expected = "/page/studentCourseDetailsPage?user=USERNAME"
                   + "&courseid=CS2103-Aug2016%23/encodedHashHere%2B/encodedPlusHere"
                   + "+/plusHere";
        assertEquals(expected, SanitizationHelper.desanitizeFromNextUrl(sanitizedUrl));
    }

    @Test
    public void testSanitizeForCsv() {
        sanitizeCsv_receivesUnsanitized_returnsSanitized();
    }

    private void sanitizeCsv_receivesUnsanitized_returnsSanitized() {
        String unsanitized = "aaa , bb\"b, c\"\"cc";
        String expected = "\"aaa , bb\"\"b, c\"\"\"\"cc\"";
        String sanitized = SanitizationHelper.sanitizeForCsv(unsanitized);
        assertEquals(expected, sanitized);
    }

    @Test
    public void testSanitizeListForCsv() {
        sanitizeCsvList_receivesEmptyList_returnsEmptyList();
        sanitizeCsvList_receivesUnsanitized_returnsSanitized();
    }

    private void sanitizeCsvList_receivesEmptyList_returnsEmptyList() {
        assertEquals(new ArrayList<>(), SanitizationHelper.sanitizeListForCsv(new ArrayList<>()));
    }

    private void sanitizeCsvList_receivesUnsanitized_returnsSanitized() {
        List<String> unsanitized = new ArrayList<>();
        unsanitized.add("aaa , bb\"b, c\"\"cc");
        unsanitized.add("aaa , bb\"b, c\"\"cc");

        List<String> expected = new ArrayList<>();
        expected.add("\"aaa , bb\"\"b, c\"\"\"\"cc\"");
        expected.add("\"aaa , bb\"\"b, c\"\"\"\"cc\"");

        assertEquals(expected, SanitizationHelper.sanitizeListForCsv(unsanitized));
    }

    @Test
    public void testSanitizeStringForXPath() {
        String text = "";
        String expected = "''";
        assertEquals(expected, SanitizationHelper.sanitizeStringForXPath(text));

        text = "Will o' The Wisp";
        expected = "concat('Will o',\"'\",' The Wisp','')";
        assertEquals(expected, SanitizationHelper.sanitizeStringForXPath(text));

        text = "'''''Will o''''' The''''' Wisp";
        expected = "concat(\"'''''\",'Will o',\"'''''\",' The',\"'''''\",' Wisp','')";
        assertEquals(expected, SanitizationHelper.sanitizeStringForXPath(text));

        text = "Team 1</td></div>'\"";
        expected = "concat('Team 1</td></div>',\"'\",'\"','')";
        assertEquals(expected, SanitizationHelper.sanitizeStringForXPath(text));

    }

    @Test
    public void testIsSanitizedHtml() {
        assertFalse("should return false if string is null",
                SanitizationHelper.isSanitizedHtml(null));
        assertFalse("should return false if empty string",
                SanitizationHelper.isSanitizedHtml(""));

        assertFalse("should return false for string with no special characters",
                SanitizationHelper.isSanitizedHtml("This is an normal string."));

        String sanitized = SanitizationHelper.sanitizeForHtml("<script>alert('hi');</script>");
        assertTrue("should return true if string is sanitized",
                SanitizationHelper.isSanitizedHtml(sanitized));

        String unsanitized = "<not sanitized>" + sanitized;
        assertFalse("should return false if string contains unsanitized characters",
                SanitizationHelper.isSanitizedHtml(unsanitized));
    }

    @Test
    public void testDesanitizeIfSanitized() {
        assertNull("should return null if given null", SanitizationHelper.desanitizeIfHtmlSanitized(null));

        String unsanitized = "This is a normal string...";
        assertEquals("should return same unsanitized string if given unsanitized with normal characters",
                unsanitized, SanitizationHelper.desanitizeIfHtmlSanitized(unsanitized));

        unsanitized = "<script>alert('hi');</script>";
        assertEquals("should return same unsanitized string if given unsanitized with special characters",
                unsanitized, SanitizationHelper.desanitizeIfHtmlSanitized(unsanitized));

        String sanitized = SanitizationHelper.sanitizeForHtml(unsanitized);
        assertEquals("should desanitize string if given sanitized",
                unsanitized, SanitizationHelper.desanitizeIfHtmlSanitized(sanitized));

        unsanitized = "\"not sanitized\"" + sanitized;
        assertEquals("should return same unsanitized string if given unsanitized string containing sanitized sequences",
                unsanitized, SanitizationHelper.desanitizeIfHtmlSanitized(unsanitized));
    }

    @Test
    public void testSanitizeForLogMessage() {
        assertNull("should return null if given null", SanitizationHelper.sanitizeForLogMessage(null));

        String unsanitized = "<span class=\"text-danger\"> A <span class=\"bold\">typical</span> log  message <br>"
                + " It contains some <script>dangerous</script> elements </span>";
        String correctSanitized =
                "<span class=\"text-danger\"> A <span class=\"bold\">typical</span> log  message <br>"
                + " It contains some &lt;script&gt;dangerous&lt;&#x2f;script&gt; elements </span>";
        assertEquals("Should escape HTML special characters"
                + "other than in <span class=\"text-danger\">, <span class=\"bold\"> and <br>",
                correctSanitized, SanitizationHelper.sanitizeForLogMessage(unsanitized));

        unsanitized = "Hmm. <span class=\"text-info\"> How about this? </span> and <span> this</span>";
        correctSanitized =
                "Hmm. &lt;span class=&quot;text-info&quot;&gt; How about this? </span> and <span> this</span>";
        assertEquals("Should escape if span has a class other than 'bold' or 'text-danger'",
                correctSanitized, SanitizationHelper.sanitizeForLogMessage(unsanitized));

        unsanitized = "Single <span class='bold'> quotation mark? </span>";
        correctSanitized = "Single &lt;span class=&#39;bold&#39;&gt; quotation mark? </span>";
        assertEquals("Should escape if attribute is specified using single quotation marks",
                correctSanitized, SanitizationHelper.sanitizeForLogMessage(unsanitized));
    }
}
