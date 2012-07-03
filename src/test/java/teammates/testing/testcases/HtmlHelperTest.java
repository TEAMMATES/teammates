package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static teammates.api.Common.EOL;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import teammates.testing.lib.HtmlHelper;

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

	}
	
	@Ignore
	@Test
	/**
	 * TODO: Still fails
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void testCleanUpHtml() throws TransformerException, SAXException, IOException{
		String original = "<html>\r\n" +
				"  <head>\r\n" + 
				"	<link href=\"/favicon.png\" rel=\"shortcut icon\" />\r\n" + 
				"	<meta content=\"IE=8\" http-equiv=\"X-UA-Compatible\" />\r\n" + 
				"	<title>Teammates - Coordinator</title>\r\n" + 
				"	<link type=\"text/css\" href=\"/stylesheets/main.css\" rel=\"stylesheet\" />\r\n" + 
				"	<link type=\"text/css\" href=\"/stylesheets/evaluation.css\" rel=\"stylesheet\" />\r\n" + 
				"	\r\n" + 
				"	<script src=\"/js/jquery-1.6.2.min.js\" language=\"JavaScript\"></script>\r\n" + 
				"	<script src=\"/js/tooltip.js\" language=\"JavaScript\"></script>\r\n" + 
				"	<script src=\"/js/date.js\" language=\"JavaScript\"></script>\r\n" + 
				"	<script src=\"/js/CalendarPopup.js\" language=\"JavaScript\"></script>\r\n" + 
				"	<script src=\"/js/AnchorPosition.js\" language=\"JavaScript\"></script>\r\n" + 
				"	<script src=\"/js/helper.js\" language=\"JavaScript\"></script>\r\n" + 
				"	<script src=\"/js/constants.js\" language=\"JavaScript\"></script>\r\n" + 
				"	<script src=\"/js/common.js\" language=\"JavaScript\"></script>\r\n" + 
				"	\r\n" + 
				"	<script src=\"/js/coordCoursePageNew.js\" language=\"JavaScript\"></script>\r\n" + 
				"	<script src=\"/js/coordinator.js\" language=\"JavaScript\"></script>\r\n" + 
				"\r\n" + 
				"  </head>\r\n" +
				"</html>";
		String expected = "<HTML>\r\n" +
				"  <HEAD>\r\n" + 
				"    <LINK href=\"/favicon.png\" rel=\"shortcut icon\"/>\r\n" + 
				"    <META content=\"IE=8\" http-equiv=\"X-UA-Compatible\"/>\r\n" + 
				"    <TITLE>Teammates - Coordinator</TITLE>\r\n" + 
				"    <LINK href=\"/stylesheets/main.css\" rel=\"stylesheet\" type=\"text/css\"/>\r\n" + 
				"    <LINK href=\"/stylesheets/evaluation.css\" rel=\"stylesheet\" type=\"text/css\"/>\r\n" + 
				"    <SCRIPT language=\"JavaScript\" src=\"/js/jquery-1.6.2.min.js\"/>\r\n" + 
				"    <SCRIPT language=\"JavaScript\" src=\"/js/tooltip.js\"/>\r\n" + 
				"    <SCRIPT language=\"JavaScript\" src=\"/js/date.js\"/>\r\n" + 
				"    <SCRIPT language=\"JavaScript\" src=\"/js/CalendarPopup.js\"/>\r\n" + 
				"    <SCRIPT language=\"JavaScript\" src=\"/js/AnchorPosition.js\"/>\r\n" + 
				"    <SCRIPT language=\"JavaScript\" src=\"/js/helper.js\"/>\r\n" + 
				"    <SCRIPT language=\"JavaScript\" src=\"/js/constants.js\"/>\r\n" + 
				"    <SCRIPT language=\"JavaScript\" src=\"/js/common.js\"/>\r\n" + 
				"    <SCRIPT language=\"JavaScript\" src=\"/js/coordCoursePageNew.js\"/>\r\n" + 
				"    <SCRIPT language=\"JavaScript\" src=\"/js/coordinator.js\"/>\r\n" + 
				"  </HEAD>\r\n" +
				"</HTML>";
		assertEquals(expected,HtmlHelper.cleanupHtml(original));
		
		original = "<HTML><HEAD><SCRIPT language=\"JavaScript\" src=\"a.js\" ></SCRIPT></HEAD><BODY id=\"5\"><P>abc</P></BODY></HTML>";
		expected = "<HTML>"+EOL
				+"  <HEAD>"+EOL
				+EOL
				+"    <SCRIPT language=\"JavaScript\" src=\"a.js\"></SCRIPT>"+EOL
				+"  </HEAD>"+EOL
				+"  <BODY id=\"5\">"+EOL
				+"    <P>abc</P>"+EOL
				+"  </BODY>"+EOL
				+"</HTML>"+EOL;
		assertEquals(expected,HtmlHelper.cleanupHtml(original));
		
		assertEquals(expected, HtmlHelper.cleanupHtml(HtmlHelper.cleanupHtml(original)));
		assertEquals(HtmlHelper.cleanupHtml(expected), HtmlHelper.cleanupHtml(original));
		
		original = original.replace("abc", "   abc  "+EOL);
		assertEquals(expected,HtmlHelper.cleanupHtml(original));

		original = original.replace("<HTML>", EOL+"   <HTML>  \t  "+EOL);
		assertEquals(expected,HtmlHelper.cleanupHtml(original));
		
	}

}
