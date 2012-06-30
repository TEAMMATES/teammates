package teammates.testing.testcases;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teammates.Config;

public class ConfigTest extends BaseTestCase {

	@Test
	public void checkPresence() {
		assertTrue(null != Config.inst().TEAMMATES_APP_URL);
	}

	@Test
	public void checkReadingEmailTemplates() {
		assertContainsRegex("${joinUrl}{*}${submitUrl}",
				Config.inst().EMAIL_TEMPLATE_EVALUATION_OPENING);
	}

}
