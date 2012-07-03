package teammates.testing.testcases;

import static org.junit.Assert.*;

import org.junit.Test;

import teammates.ui.Helper;

public class HelperTest extends BaseTestCase {

	@Test
	public void testTruncate() {
		assertEquals("1234567...", Helper.truncate("1234567890xxxx", 10));
		assertEquals("1234567890", Helper.truncate("1234567890", 10));
		assertEquals("123456789", Helper.truncate("123456789", 10));
	}

}
