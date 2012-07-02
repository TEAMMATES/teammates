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
		assertContainsRegex("${joinFragment}{*}${submitUrl}",
				Config.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_);
		
		assertContainsRegex("${joinUrl}{*}${key}",
				Config.inst().STUDENT_EMAIL_FRAGMENT_JOIN_COURSE);
		
		assertContainsRegex("${reportUrl}",
				Config.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED);
	}

}
