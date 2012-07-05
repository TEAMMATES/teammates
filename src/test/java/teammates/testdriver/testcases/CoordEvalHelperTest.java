package teammates.testdriver.testcases;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import teammates.ui.CoordEvalHelper;

public class CoordEvalHelperTest extends BaseTestCase {
	
	@Test
	public void testGetTimeOptions(){
		CoordEvalHelper helper = new CoordEvalHelper();
		ArrayList<String> timeOptions = helper.getTimeOptions(false);
		String retValue = "";
		for(String option: timeOptions){
			retValue += option + "\r\n";
		}
		
		String expected = "<option value=\"1\">0100H</option>\r\n" + 
				"<option value=\"2\">0200H</option>\r\n" + 
				"<option value=\"3\">0300H</option>\r\n" + 
				"<option value=\"4\">0400H</option>\r\n" + 
				"<option value=\"5\">0500H</option>\r\n" + 
				"<option value=\"6\">0600H</option>\r\n" + 
				"<option value=\"7\">0700H</option>\r\n" + 
				"<option value=\"8\">0800H</option>\r\n" + 
				"<option value=\"9\">0900H</option>\r\n" + 
				"<option value=\"10\">1000H</option>\r\n" + 
				"<option value=\"11\">1100H</option>\r\n" + 
				"<option value=\"12\">1200H</option>\r\n" + 
				"<option value=\"13\">1300H</option>\r\n" + 
				"<option value=\"14\">1400H</option>\r\n" + 
				"<option value=\"15\">1500H</option>\r\n" + 
				"<option value=\"16\">1600H</option>\r\n" + 
				"<option value=\"17\">1700H</option>\r\n" + 
				"<option value=\"18\">1800H</option>\r\n" + 
				"<option value=\"19\">1900H</option>\r\n" + 
				"<option value=\"20\">2000H</option>\r\n" + 
				"<option value=\"21\">2100H</option>\r\n" + 
				"<option value=\"22\">2200H</option>\r\n" + 
				"<option value=\"23\">2300H</option>\r\n" + 
				"<option value=\"24\" selected=\"selected\">2359H</option>";
		assertEquals (expected, retValue.trim());
		
	}

}
