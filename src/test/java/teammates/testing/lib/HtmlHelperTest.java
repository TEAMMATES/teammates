package teammates.testing.lib;

import static org.junit.Assert.*;
import static teammates.api.Common.EOL;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

import teammates.api.Common;

public class HtmlHelperTest {
	
	@Test
	public void testComparison() throws SAXException, IOException, TransformerException{
		String expected = "<html></html>";
		String actual = expected;
		HtmlHelper.assertSameHtml(expected, actual);
		
		actual = "<html> </html>";
		HtmlHelper.assertSameHtml(expected, actual);
		
		expected = "<HTML><HEAD><SCRIPT language=\"JavaScript\" src=\"a.js\" ></SCRIPT></HEAD><BODY id=\"5\"><P>abc</P><DIV id=\"frameBottom\"><DIV></DIV></DIV></BODY></HTML>";
		actual = expected.replace("<HEAD>", "    <HEAD>    \t"+EOL);
		HtmlHelper.assertSameHtml(expected, actual);
		
		//change attribute order
		actual = expected.replace("language=\"JavaScript\" src=\"a.js\"", "  src=\"a.js\"   language=\"JavaScript\"  ");
		HtmlHelper.assertSameHtml(expected, actual);
		
		actual = expected.replace("<P>", "<P>\n\n"+EOL+EOL);
		HtmlHelper.assertSameHtml(expected, actual);
		
		actual = expected.replace("<DIV></DIV></DIV>", EOL+EOL+"\n<DIV>\n\n</DIV></DIV>\n\n"+EOL);
		HtmlHelper.assertSameHtml(expected, actual);
		
		String file = Common.TEST_PAGES_FOLDER+"coordListCourseByIDNew.html";
		String inputStr = Common.getFileContents(file).replace("{version}",Common.VERSION);
		System.out.println(HtmlHelper.cleanupHtml(inputStr));
	}
	
	@Test 
	public void testCleanUpHtml() throws TransformerException, SAXException, IOException{
		String original = "<HTML><HEAD><SCRIPT language=\"JavaScript\" src=\"a.js\" ></SCRIPT></HEAD><BODY id=\"5\"><P>abc</P></BODY></HTML>";
		String expected = "<HTML>"+EOL
				+"<HEAD>"+EOL
				+EOL
				+"<SCRIPT language=\"JavaScript\" src=\"a.js\"></SCRIPT>"+EOL
				+"</HEAD>"+EOL
				+"<BODY id=\"5\">"+EOL
				+"<P>abc</P>"+EOL
				+"</BODY>"+EOL
				+"</HTML>"+EOL;
		assertEquals(expected,HtmlHelper.cleanupHtml(original));
		
		original = original.replace("abc", "   abc  "+EOL);
		assertEquals(expected,HtmlHelper.cleanupHtml(original));

		original = original.replace("<HTML>", EOL+"   <HTML>  \t  "+EOL);
		assertEquals(expected,HtmlHelper.cleanupHtml(original));
		
	}

}
