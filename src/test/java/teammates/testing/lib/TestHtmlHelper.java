package teammates.testing.lib;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class TestHtmlHelper {
	
	@Test
	public void testComparison() throws SAXException, IOException{
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

}
