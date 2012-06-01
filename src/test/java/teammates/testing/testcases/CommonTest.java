package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Common;

public class CommonTest extends BaseTestCase{
	@BeforeClass
	public static void setUp(){
		
	}
	
	@Test
	public void testGenerateStringOfLength(){
		assertEquals(5, Common.generateStringOfLength(5).length());
		assertEquals(0, Common.generateStringOfLength(0).length());
	}
	
	@Test
	public void testIsWhiteSpace(){
		assertEquals(true, Common.isWhiteSpace(""));
		assertEquals(true, Common.isWhiteSpace("       "));
		assertEquals(true, Common.isWhiteSpace("\t\n\t"));
		assertEquals(true, Common.isWhiteSpace(Common.EOL));
		assertEquals(true, Common.isWhiteSpace(Common.EOL+"   "));
	}
	
	@Test
	public void testAssertContains(){
		Common.assertContains("404 Page Not Found","Error: 404 Page Not Found. Check the URL.");
		Common.assertContains("Fails on checking assert contains","404 Page Not Found","Error: 404 Page Not Found. Check the URL.");
	}
	
	@Test
	public void testAssertContainsRegex(){
		Common.assertContainsRegex("404 Page Not Found","Error: 404 Page Not Found. Check the URL.");
		Common.assertContainsRegex("Fails on checking assert contains regex","404 Page Not Found","Error: 404 Page Not Found. Check the URL.");

		String pageStr = Common.getFileContents(Common.TEST_PAGES_FOLDER+"commonAssertRegexTestPage.html");
		
		String inputStr = Common.getFileContents(Common.TEST_PAGES_FOLDER+"commonAssertRegexTestPart.html");
		
		Common.assertContainsRegex(inputStr,pageStr);
		Common.assertContainsRegex("Fails on checking assert contains regex",inputStr,pageStr);
		
		Common.assertContainsRegex("<div>{*}</div>","<html><body><div>Testing</div><a href='index.html'>HOME</a></body></html>");
		Common.assertContainsRegex("Fails on checking assert contains regex","<div>{*}</div>","<html><body><div>Testing</div><a href='index.html'>HOME</a></body></html>");
	}

}