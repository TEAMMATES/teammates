package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.test.cases.BaseTestCase;

public class BuildPropertiesTest extends BaseTestCase {

	@Test
	public void checkPresence() {
		assertTrue(null != Config.TEAMMATES_APP_URL);
		assertTrue(Config.PERSISTENCE_CHECK_DURATION > 0);
	}

	@Test
	public void checkReadingEmailTemplates() {
		assertContainsRegex("${joinFragment}{*}${submitUrl}",
				Config.STUDENT_EMAIL_TEMPLATE_EVALUATION_);
		
		assertContainsRegex("${reportUrl}",
				Config.STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED);
		
		assertContainsRegex("${studentName}{*}${joinFragment}",
				Config.STUDENT_EMAIL_TEMPLATE_COURSE_JOIN);
		
		assertContainsRegex("${joinUrl}{*}${key}",
				Config.STUDENT_EMAIL_FRAGMENT_COURSE_JOIN);
	}

}
