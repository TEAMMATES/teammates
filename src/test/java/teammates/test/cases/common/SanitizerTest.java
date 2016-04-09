package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.util.Sanitizer;
import teammates.test.cases.BaseTestCase;

public class SanitizerTest extends BaseTestCase {
    
    @Test
    public void testSanitizeGoogleId() {
        assertEquals("big-small.20_12", Sanitizer.sanitizeGoogleId(" big-small.20_12 @Gmail.COM \t\n"));
        assertEquals("user@hotmail.com", Sanitizer.sanitizeGoogleId(" user@hotmail.com \t\n"));
    }
    
    @Test
    public void testSanitizeEmail() {
        String emailWithWhiteSpaces = "\tnormal@email.com \t\n";
        String normalEmail = "normal@email.com";

        assertEquals(null, Sanitizer.sanitizeEmail(null));
        assertEquals(normalEmail, Sanitizer.sanitizeEmail(normalEmail));
        assertEquals(normalEmail, Sanitizer.sanitizeEmail(emailWithWhiteSpaces));
    }
    
    @Test
    public void testSanitizeName() {

        String nameWithWhiteSpaces = "\t alice   bob \t\n";
        String normalName = "alice bob";

        assertEquals(null, Sanitizer.sanitizeName(null));
        assertEquals(normalName, Sanitizer.sanitizeName(normalName));
        assertEquals(normalName, Sanitizer.sanitizeName(nameWithWhiteSpaces));
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
        assertEquals(null, Sanitizer.sanitizeForJs(null));
    }

    private void sanitizeJs_receivesUnsanitized_returnsSanitized() {
        String unsanitized = "\\ \" ' #"; // i.e., [\ " ' #]
        String expected = "\\\\ \\&quot; \\&#39; \\#"; // i.e., [\\ \&quot; \&#39; \#]
        String sanitized = Sanitizer.sanitizeForJs(unsanitized);
        assertEquals(expected, sanitized);
    }
    @Test
    public void testSanitizeForHtml() {
        sanitizeHtml_receivesNull_returnsNull();
        sanitizeHtml_receivesCodeInjection_returnsSanitized();
        sanitizeHtml_receivesSanitized_returnsUnchanged();
    }
    
    @Test
    public void testSanitizeForHtmlTag() {
        sanitizeHtmlTag_receivesNull_returnsNull();
        sanitizeHtmlTag_receivesHtml_returnsSanitized();
    }

    private void sanitizeHtmlTag_receivesHtml_returnsSanitized() {
        String unsanitized = "<div><td>&lt;</td></div>";
         String expected = "&lt;div&gt;&lt;td&gt;&lt;&lt;/td&gt;&lt;/div&gt;";
         String sanitized = Sanitizer.sanitizeForHtmlTag(unsanitized);
         assertEquals(expected, sanitized);
    }

    private void sanitizeHtmlTag_receivesNull_returnsNull() {
        String nullString = null;
        assertEquals(null, Sanitizer.sanitizeForHtmlTag(nullString));
    }

    private void sanitizeHtml_receivesNull_returnsNull() {
        String nullString = null;
        assertEquals(null, Sanitizer.sanitizeForHtml(nullString));
    };

    private void sanitizeHtml_receivesCodeInjection_returnsSanitized() {
        String unsanitized = "< > \" / ' &"
                           + "<script>alert('injected');</script>";
        String expected = "&lt; &gt; &quot; &#x2f; &#39; &amp;"
                        + "&lt;script&gt;alert(&#39;injected&#39;);&lt;&#x2f;script&gt;";
        String sanitized = Sanitizer.sanitizeForHtml(unsanitized);
        assertEquals(expected, sanitized);
    };

    private void sanitizeHtml_receivesSanitized_returnsUnchanged() {
        String sanitized = "&lt; &gt; &quot; &#x2f; &#39; &amp;"
                         + "&lt;script&gt;alert(&#39;injected&#39;);&lt;&#x2f;script&gt;";
        assertEquals(sanitized, Sanitizer.sanitizeForHtml(sanitized));
    };
    
    @Test
    public void testSanitizeForRichText() {
        // Not tested - using org.apache.commons.lang3.StringEscapeUtils.escapeHtml4()
    }
    
    @Test
    public void testSanitizeForCsv() {
        sanitizeCsv_receivesUnsanitized_returnsSanitized();
    }

    private void sanitizeCsv_receivesUnsanitized_returnsSanitized() {
        String unsanitized = "aaa , bb\"b, c\"\"cc";
        String expected = "\"aaa , bb\"\"b, c\"\"\"\"cc\"";
        String sanitized = Sanitizer.sanitizeForCsv(unsanitized);
        assertEquals(expected, sanitized);
    }
    
    @Test
    public void testSanitizeListForCsv() {
        sanitizeCsvList_receivesEmptyList_returnsEmptyList();
        sanitizeCsvList_receivesUnsanitized_returnsSanitized();
    }

    private void sanitizeCsvList_receivesEmptyList_returnsEmptyList() {
        List<String> emptyList = new ArrayList<String>();
        assertEquals(emptyList, Sanitizer.sanitizeListForCsv(emptyList));
    }

    private void sanitizeCsvList_receivesUnsanitized_returnsSanitized() {
        List<String> unsanitized = new ArrayList<String>();
        unsanitized.add("aaa , bb\"b, c\"\"cc");
        unsanitized.add("aaa , bb\"b, c\"\"cc");

        List<String> expected = new ArrayList<String>();
        expected.add("\"aaa , bb\"\"b, c\"\"\"\"cc\"");
        expected.add("\"aaa , bb\"\"b, c\"\"\"\"cc\"");

        assertEquals(expected, Sanitizer.sanitizeListForCsv(unsanitized));
    }
    
    @Test
    public void testClearStringForXPath() {
        String text = "";
        String expected = "''";
        assertEquals(expected, Sanitizer.convertStringForXPath(text));
        
        text = "Will o' The Wisp";
        expected = "concat('Will o',\"'\",' The Wisp','')";
        assertEquals(expected, Sanitizer.convertStringForXPath(text));
        
        text = "'''''Will o''''' The''''' Wisp";
        expected = "concat(\"'''''\",'Will o',\"'''''\",' The',\"'''''\",' Wisp','')";
        assertEquals(expected, Sanitizer.convertStringForXPath(text));
        
        text = "Team 1</td></div>'\"";
        expected = "concat('Team 1</td></div>',\"'\",'\"','')";
        assertEquals(expected, Sanitizer.convertStringForXPath(text));
        
    }
}
