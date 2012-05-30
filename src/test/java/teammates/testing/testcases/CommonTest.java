package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;

import org.junit.Test;

import teammates.Common;

public class CommonTest extends BaseTestCase{
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

		try{
			StringWriter writer = new StringWriter();
			FileReader reader = new FileReader("src/test/resources/pages/commonAssertRegexTestPage.html");
			Common.readAndWrite(reader, writer);
			String pageStr = writer.toString();
			
			writer = new StringWriter();
			reader = new FileReader("src/test/resources/pages/commonAssertRegexTestPart.html");
			Common.readAndWrite(reader, writer);
			String inputStr = writer.toString();
			
			Common.assertContainsRegex(inputStr,pageStr);
			Common.assertContainsRegex("Fails on checking assert contains regex",inputStr,pageStr);
		} catch (FileNotFoundException e){
			
		}
	}

}