package teammates.test.cases.testdriver;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static teammates.common.util.Const.EOL;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import teammates.common.util.FileHelper;
import teammates.test.driver.HtmlHelper;
import teammates.test.driver.TestProperties;

public class HtmlHelperTest {
    
    @Test
    public void testComparison() throws SAXException, IOException, TransformerException{
        String expected = "<html></html>";
        String actual = expected;
        HtmlHelper.assertSameHtml(expected, actual, false, true);
        
        actual = "<html> </html>";
        HtmlHelper.assertSameHtml(expected, actual, false, true);
        
        expected = "<HTML><HEAD><SCRIPT language=\"JavaScript\" src=\"a.js\" ></SCRIPT></HEAD><BODY id=\"5\"><P>abc</P><DIV id=\"frameBottom\"><DIV></DIV></DIV></BODY></HTML>";
        actual = expected.replace("<HEAD>", "    <HEAD>    \t"+EOL);
        HtmlHelper.assertSameHtml(expected, actual, false, true);
        
        //change attribute order
        actual = expected.replace("language=\"JavaScript\" src=\"a.js\"", "  src=\"a.js\"   language=\"JavaScript\"  ");
        HtmlHelper.assertSameHtml(expected, actual, false, true);
        
        actual = expected.replace("<P>", "<P>\n\n"+EOL+EOL);
        HtmlHelper.assertSameHtml(expected, actual, false, true);
        
        actual = expected.replace("<DIV></DIV></DIV>", EOL+EOL+"\n<DIV>\n\n</DIV></DIV>\n\n"+EOL);
        HtmlHelper.assertSameHtml(expected, actual, false, true);
        
        expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER +"/sampleExpected.html");
        actual = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER +"/sampleActual.html");
        HtmlHelper.assertSameHtml(expected, actual, false, true);

    }
    
    @Test
    public void testConvertToStandardHtml() throws Exception{
        
        //Tool tip in actual. Should not be ignored.
        String actual = "<html><head></head><body><div class=\"tooltip\">tool tip <br> 2nd line </div></body></html>";
        String expected = "<html><head></head><body></body></html>";
        assertTrue(HtmlHelper.assertSameHtml(expected, actual, false, false));
        
        //'<div>' without attributes (not a tool tip). Should not be ignored.
        actual = "<html><head></head><body><div></div></body></html>";
        expected = "<html><head></head><body></body></html>";
        assertFalse(HtmlHelper.assertSameHtml(expected, actual, false, false));
                
        //Using a '<div>' that is not a tool tip. Should not be ignored.
        actual = "<html><head></head><body><div id=\"otherId\"></div></body></html>";
        expected = "<html><head></head><body></body></html>";
        assertFalse(HtmlHelper.assertSameHtml(expected, actual, false, false));
        
        //Different tool tips. Will be ignored (the logic does not detect this).
        actual = "<html><head></head><body><div class=\"tooltip\">tool tip <br> 2nd line </div></body></html>";
        expected = "<html><head></head><body><div class=\"tooltip\"></div></body></html>";
        assertTrue(HtmlHelper.assertSameHtml(expected, actual, false, false));
        
        //Test against areSameHtmlPart
        actual = "<html><head><script></script></head><body><div></div></body></html>";
        expected = "<html><head></head><body><script></script><div></div></body></html>";
        assertFalse(HtmlHelper.assertSameHtml(expected, actual, false, false));
        
        //Test against areSameHtmlPart
        expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER +"/sampleExpected.html");
        actual = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER +"/sampleActualPart.html");
        assertFalse(HtmlHelper.assertSameHtml(expected, actual, false, false));
    }
    
    @Test
    public void testConvertToStandardHtmlPart() throws Exception {
        
        //Tool tip in actual. Should not be ignored.
        String actual = "<html><head></head><body><div class=\"tooltip\">tool tip <br> 2nd line </div></body></html>";
        String expected = "<html><head></head><body></body></html>";
        assertTrue(HtmlHelper.assertSameHtml(expected, actual, true, false));
        
        //one part does not contain html tag. Should be the same
        actual = "<html><head></head><body><div></div></body></html>";
        expected = "<html><div></div></html>";
        assertTrue(HtmlHelper.assertSameHtml(expected, actual, true, false));
        
        //one part does not contain head tag. Should be the same
        actual = "<html><head></head><body><div></div></body></html>";
        expected = "<html><body><div></div></body></html>";
        assertTrue(HtmlHelper.assertSameHtml(expected, actual, true, false));
        
        //one part does not contain body tag. Should be the same
        actual = "<html><head></head><body><div></div></body></html>";
        expected = "<html><head></head><div></div></html>";
        assertTrue(HtmlHelper.assertSameHtml(expected, actual, true, false));
        
        //Different tool tips. Will be ignored (the logic does not detect this)
        actual = "<html><head></head><body><div class=\"tooltip\">tool tip <br> 2nd line </div></body></html>";
        expected = "<html><head></head><body><div class=\"tooltip\"></div></body></html>";
        assertTrue(HtmlHelper.assertSameHtml(expected, actual, true, false));
        
        //Different tool tips and three tags(html, head, body) ignored. Should be the same
        actual = "<html><head></head><body><div class=\"tooltip\">tool tip <br> 2nd line </div></body></html>";
        expected = "<div class=\"tooltip\"></div>";
        assertTrue(HtmlHelper.assertSameHtml(expected, actual, true, false));
        
        //Contents inside div are different . Should be different
        actual = "<html><head></head><body><div>content<br> 2nd line </div></body></html>";
        expected = "<html><head></head><body><div></div></body></html>";
        assertFalse(HtmlHelper.assertSameHtml(expected, actual, true, false));
        
        //Test against areSameHtml
        actual = "<html><head><script></script></head><body><div></div></body></html>";
        expected = "<html><head></head><body><script></script><div></div></body></html>";
        assertTrue(HtmlHelper.assertSameHtml(expected, actual, true, false));
        
        //Same html structure
        expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER +"/sampleExpected.html");
        actual = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER +"/sampleActual.html");
        HtmlHelper.assertSameHtml(expected, actual, true, true);
        
        //Same after ignoring html & head & body tag
        expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER +"/sampleExpected.html");
        actual = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER +"/sampleActualPart.html");
        HtmlHelper.assertSameHtml(expected, actual, true, true);
        
        //other cases are tested in testComparison
    }
}
