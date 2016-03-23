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
        // tested as email
    }
    
    @Test
    public void testSanitizeTitle() {
        // tested as email
    }
    
    @Test
    public void testSanitizeTextField() {
        // tested as email
    }
    
    @Test
    public void testSanitizeForJs() {
        String unsanitized = "\\ \" ' #"; // i.e., [\ " ' #]
        String expected = "\\\\ \\&quot; \\&#39; \\#"; // i.e., [\\ \&quot; \&#39; \#]
        String sanitized = Sanitizer.sanitizeForJs(unsanitized);

        assertEquals(null, Sanitizer.sanitizeForJs(null));
        assertEquals(expected, sanitized);
    }
    
    @Test
    public void testSanitizeForHtml() {
        String unsanitized = "< > \" / ' &"
                           + "<script>alert('injected');</script>";

        String expected = "&lt; &gt; &quot; &#x2f; &#39; &amp;"
                        + "&lt;script&gt;alert(&#39;injected&#39;);&lt;&#x2f;script&gt;";

        String sanitized = Sanitizer.sanitizeForHtml(unsanitized);
        String sanitizedTwice = Sanitizer.sanitizeForHtml(sanitized);

        assertEquals(null, Sanitizer.sanitizeForHtml(null));
        assertEquals(expected, sanitized);
        assertEquals(expected, sanitizedTwice);
    }
    
    @Test
    public void testSanitizeForRichText() {
        // Not tested - using org.apache.commons.lang3.StringEscapeUtils.escapeHtml4()
    }
    
    @Test
    public void testSanitizeForCsv() {
        String unsanitized = "aaa , bb\"b, c\"\"cc";
        String expected = "\"aaa , bb\"\"b, c\"\"\"\"cc\"";
        
        String sanitized = Sanitizer.sanitizeForCsv(unsanitized);
        assertEquals(expected, sanitized);
    }
    
    @Test
    public void testSanitizeListForCsv() {
        List<String> emptyList = new ArrayList<String>();
        assertEquals(emptyList, Sanitizer.sanitizeListForCsv(emptyList));
        
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
    }
}
