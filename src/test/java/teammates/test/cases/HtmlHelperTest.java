package teammates.test.cases;

import static org.junit.Assert.assertEquals;
import static teammates.common.Common.EOL;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import teammates.test.driver.HtmlHelper;

public class HtmlHelperTest {
	
	@Test
	public void testComparison() throws SAXException, IOException, TransformerException{
		String expected = "<html></html>";
		String actual = expected;
		HtmlHelper.assertSameHtml(expected, actual, false);
		
		actual = "<html> </html>";
		HtmlHelper.assertSameHtml(expected, actual, false);
		
		expected = "<HTML><HEAD><SCRIPT language=\"JavaScript\" src=\"a.js\" ></SCRIPT></HEAD><BODY id=\"5\"><P>abc</P><DIV id=\"frameBottom\"><DIV></DIV></DIV></BODY></HTML>";
		actual = expected.replace("<HEAD>", "    <HEAD>    \t"+EOL);
		HtmlHelper.assertSameHtml(expected, actual, false);
		
		//change attribute order
		actual = expected.replace("language=\"JavaScript\" src=\"a.js\"", "  src=\"a.js\"   language=\"JavaScript\"  ");
		HtmlHelper.assertSameHtml(expected, actual, false);
		
		actual = expected.replace("<P>", "<P>\n\n"+EOL+EOL);
		HtmlHelper.assertSameHtml(expected, actual, false);
		
		actual = expected.replace("<DIV></DIV></DIV>", EOL+EOL+"\n<DIV>\n\n</DIV></DIV>\n\n"+EOL);
		HtmlHelper.assertSameHtml(expected, actual, false);

	}
	

}
