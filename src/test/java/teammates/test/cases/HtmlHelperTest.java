package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;
import static teammates.common.Common.EOL;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import teammates.test.driver.HtmlHelper;

public class HtmlHelperTest {
	
	@Test
	public void testComparison() throws SAXException, IOException, TransformerException{
		String expected = "<html></html>";
		String actual = expected;
		HtmlHelper.assertSameHtml(actual, expected);
		
		actual = "<html> </html>";
		HtmlHelper.assertSameHtml(actual, expected);
		
		expected = "<HTML><HEAD><SCRIPT language=\"JavaScript\" src=\"a.js\" ></SCRIPT></HEAD><BODY id=\"5\"><P>abc</P><DIV id=\"frameBottom\"><DIV></DIV></DIV></BODY></HTML>";
		actual = expected.replace("<HEAD>", "    <HEAD>    \t"+EOL);
		HtmlHelper.assertSameHtml(actual, expected);
		
		//change attribute order
		actual = expected.replace("language=\"JavaScript\" src=\"a.js\"", "  src=\"a.js\"   language=\"JavaScript\"  ");
		HtmlHelper.assertSameHtml(actual, expected);
		
		actual = expected.replace("<P>", "<P>\n\n"+EOL+EOL);
		HtmlHelper.assertSameHtml(actual, expected);
		
		actual = expected.replace("<DIV></DIV></DIV>", EOL+EOL+"\n<DIV>\n\n</DIV></DIV>\n\n"+EOL);
		HtmlHelper.assertSameHtml(actual, expected);

	}
	

}
