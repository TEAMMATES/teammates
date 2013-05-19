package teammates.test.cases;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import teammates.ui.controller.Helper;

public class HelperTest extends BaseTestCase {

	@Test
	public void testTruncate() {
		AssertJUnit.assertEquals("1234567...", Helper.truncate("1234567890xxxx", 10));
		AssertJUnit.assertEquals("1234567890", Helper.truncate("1234567890", 10));
		AssertJUnit.assertEquals("123456789", Helper.truncate("123456789", 10));
	}

}
