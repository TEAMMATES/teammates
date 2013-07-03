package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.test.cases.BaseTestCase;

public class BuildPropertiesTest extends BaseTestCase {

	@Test
	public void checkPresence() {
		assertTrue(null != Config.APP_URL);
		assertTrue(Config.PERSISTENCE_CHECK_DURATION > 0);
	}

	@Test
	public void checkReadingEmailTemplates() {
		assertContainsRegex("${joinFragment}{*}${submitUrl}",
				Config.EMAIL_TEMPLATE_STUDENT_EVALUATION_);
		
		assertContainsRegex("${reportUrl}",
				Config.EMAIL_TEMPLATE_STUDENT_EVALUATION_PUBLISHED);
		
		assertContainsRegex("${studentName}{*}${joinFragment}",
				Config.EMAIL_TEMPLATE_STUDENT_COURSE_JOIN);
		
		assertContainsRegex("${joinUrl}{*}${key}",
				Config.EMAIL_TEMPLATE_FRAGMENT_STUDENT_COURSE_JOIN);
	}

}
