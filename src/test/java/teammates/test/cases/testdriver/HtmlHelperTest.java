package teammates.test.cases.testdriver;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
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
		
		expected = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER +"/sampleExpected.html");
		actual = FileHelper.readFile(TestProperties.TEST_PAGES_FOLDER +"/sampleActual.html");
		HtmlHelper.assertSameHtml(actual, expected);

	}
	
	@Test
	public void testConvertToStandardHtml() throws Exception{
		
		//Tool tip in actual. Should not be ignored.
		String actual = "<html><head></head><body><div id=\"dhtmltooltip\">tool tip <br> 2nd line </div></body></html>";
		String expected = "<html><head></head><body></body></html>";
		assertEquals(HtmlHelper.convertToStandardHtml(expected),HtmlHelper.convertToStandardHtml(actual));
		
		//'<div>' without attributes (not a tool tip). Should not be ignored.
		actual = "<html><head></head><body><div></div></body></html>";
		expected = "<html><head></head><body></body></html>";
		assertFalse(HtmlHelper.convertToStandardHtml(expected).equals(HtmlHelper.convertToStandardHtml(actual)));
				
		//Using a '<div>' that is not a tool tip. Should not be ignored.
		actual = "<html><head></head><body><div id=\"otherId\"></div></body></html>";
		expected = "<html><head></head><body></body></html>";
		assertFalse(HtmlHelper.convertToStandardHtml(expected).equals(HtmlHelper.convertToStandardHtml(actual)));
		
		//Different tool tips. Will be ignored (the logic does not detect this).
		actual = "<html><head></head><body><div id=\"dhtmltooltip\">tool tip <br> 2nd line </div></body></html>";
		expected = "<html><head></head><body><div id=\"dhtmltooltip\"></div></body></html>";
		assertEquals(HtmlHelper.convertToStandardHtml(expected),HtmlHelper.convertToStandardHtml(actual));
	}
	

}
