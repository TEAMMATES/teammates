package teammates.testing.lib;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class TestHtmlHelper {
	
	@Test
	public void testComparison() throws SAXException, IOException, TransformerException{
		String expected = "<html></html>";
		String actual = expected;
		assertEquals(true,HtmlHelper.isSame(expected, actual));
		
		actual = "<html> </html>";
		assertEquals(true,HtmlHelper.isSame(expected, actual));
		
		actual = "<html> x </html>";
		assertEquals(false,HtmlHelper.isSame(expected, actual));
		
		actual = "<html><body></body></html>";
		assertEquals(false,HtmlHelper.isSame(expected, actual));
		
		expected = "<html><body></body></html>";
		assertEquals(true,HtmlHelper.isSame(expected, actual));
		
		expected = "   <html>   <body>   </body>   </html>  ";
		assertEquals(true,HtmlHelper.isSame(expected, actual));
		
	}
	
	@Test 
	public void testCleanUpHtml() throws TransformerException, SAXException, IOException{
		String original = "<HTML><HEAD></HEAD><BODY id=\"5\">abc</BODY></HTML>";
		String expected = "<HTML>\n<HEAD>\n\n</HEAD>\n<BODY id=\"5\">abc</BODY>\n</HTML>";
		assertEquals(expected,HtmlHelper.cleanupHtml(original));
	}

}
