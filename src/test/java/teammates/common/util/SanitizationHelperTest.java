package teammates.common.util;

import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link SanitizationHelper}.
 */
public class SanitizationHelperTest extends BaseTestCase {

    @Test
    public void testSanitizeGoogleId() {
        Assertions.assertEquals("big-small.20_12 @Gmail.COM", SanitizationHelper.sanitizeGoogleId(" big-small.20_12 @Gmail.COM \t\n"));
        Assertions.assertEquals("user@hotmail.com", SanitizationHelper.sanitizeGoogleId(" user@hotmail.com \t\n"));
    }

    @Test
    public void testSanitizeEmail() {
        String emailWithWhiteSpaces = "\tnormal@email.com \t\n";
        String emailWithMixedCase = "\tNormal@Email.COM \t\n";
        String normalEmail = "normal@email.com";

        Assertions.assertNull(SanitizationHelper.sanitizeEmail(null));
        Assertions.assertEquals(normalEmail, SanitizationHelper.sanitizeEmail(normalEmail));
        Assertions.assertEquals(normalEmail, SanitizationHelper.sanitizeEmail(emailWithWhiteSpaces));
        Assertions.assertEquals(normalEmail, SanitizationHelper.sanitizeEmail(emailWithMixedCase));
    }

    @Test
    public void testAreEmailsEqual() {
        Assertions.assertTrue(SanitizationHelper.areEmailsEqual(" Test@Email.COM ", "test@email.com"));
        Assertions.assertTrue(SanitizationHelper.areEmailsEqual(null, null));
        Assertions.assertFalse(SanitizationHelper.areEmailsEqual("test1@email.com", "test2@email.com"));
        Assertions.assertFalse(SanitizationHelper.areEmailsEqual("test@email.com", null));
    }

    @Test
    public void testSanitizeName() {

        String nameWithWhiteSpaces = "\t alice   bob \t\n";
        String normalName = "alice bob";

        Assertions.assertNull(SanitizationHelper.sanitizeName(null));
        Assertions.assertEquals(normalName, SanitizationHelper.sanitizeName(normalName));
        Assertions.assertEquals(normalName, SanitizationHelper.sanitizeName(nameWithWhiteSpaces));
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
    public void testSanitizeForHtml() {
        sanitizeHtml_receivesNull_returnsNull();
        sanitizeHtml_receivesCodeInjection_returnsSanitized();
        sanitizeHtml_receivesSanitized_returnsUnchanged();
    }

    private void sanitizeHtml_receivesNull_returnsNull() {
        Assertions.assertNull(SanitizationHelper.sanitizeForHtml((String) null));
    }

    private void sanitizeHtml_receivesCodeInjection_returnsSanitized() {
        String unsanitized = "< > \" / ' &"
                             + "<script>alert('injected');</script>";
        String expected = "&lt; &gt; &quot; &#x2f; &#39; &amp;"
                          + "&lt;script&gt;alert(&#39;injected&#39;);&lt;&#x2f;script&gt;";
        String sanitized = SanitizationHelper.sanitizeForHtml(unsanitized);
        Assertions.assertEquals(expected, sanitized);
    }

    private void sanitizeHtml_receivesSanitized_returnsUnchanged() {
        String sanitized = "&lt; &gt; &quot; &#x2f; &#39; &amp;"
                           + "&lt;script&gt;alert(&#39;injected&#39;);&lt;&#x2f;script&gt;";
        Assertions.assertEquals(sanitized, SanitizationHelper.sanitizeForHtml(sanitized));
    }

    @Test
    public void testSanitizeForRichText() {
        Assertions.assertNull(SanitizationHelper.sanitizeForRichText((String) null));
        Assertions.assertEquals("", SanitizationHelper.sanitizeForRichText(""));
        Assertions.assertEquals("<p>wihtout changes</p>", SanitizationHelper.sanitizeForRichText("<p>wihtout changes</p>"));
        Assertions.assertEquals("<p>spaces test</p>", SanitizationHelper.sanitizeForRichText("<p >spaces test</p >"));
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
        Assertions.assertEquals(expectedRichText, sanitized);

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
        Assertions.assertEquals(expectedRichText, sanitized);
    }

}
