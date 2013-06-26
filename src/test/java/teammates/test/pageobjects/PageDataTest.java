package teammates.test.pageobjects;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import teammates.ui.controller.PageData;

public class PageDataTest {
	
	@Test
	public void testTruncate() {
		AssertJUnit.assertEquals("1234567...", PageData.truncate("1234567890xxxx", 10));
		AssertJUnit.assertEquals("1234567890", PageData.truncate("1234567890", 10));
		AssertJUnit.assertEquals("123456789", PageData.truncate("123456789", 10));
	}

}
