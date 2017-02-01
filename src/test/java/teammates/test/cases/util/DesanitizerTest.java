package teammates.test.cases.util;

import org.testng.annotations.Test;
import teammates.common.util.Desanitizer;
import teammates.common.util.Sanitizer;
import teammates.test.cases.BaseTestCase;

public class DesanitizerTest extends BaseTestCase {

    @Test
    public void testDesanitizeFromHtml() {
        String noChange = null;
        assertEquals(null, Desanitizer.desanitizeFromHtml(noChange));

        noChange = "";
        assertEquals("", Desanitizer.desanitizeFromHtml(noChange));

        String text = "<text><div> 'param' &&& \\//\\";
        String sanitizedText = Sanitizer.sanitizeForHtml(text);
        assertEquals(text, Desanitizer.desanitizeFromHtml(sanitizedText));
    }
}
