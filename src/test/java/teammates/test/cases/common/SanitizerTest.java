package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

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
        
    }
    
    @Test
    public void testSanitizeForHtml() {
        
    }
    
    @Test
    public void testSanitizeForRichText() {
        
    }
    
    @Test
    public void testSanitizeForCsv() {
        
    }
    
    @Test
    public void testSanitizeListForCsv() {
        
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
