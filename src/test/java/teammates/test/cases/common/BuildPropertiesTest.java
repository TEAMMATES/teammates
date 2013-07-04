package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

import teammates.common.util.Config;
import teammates.common.util.EmailTemplates;
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
				EmailTemplates.STUDENT_EVALUATION_);
		
		assertContainsRegex("${reportUrl}",
				EmailTemplates.STUDENT_EVALUATION_PUBLISHED);
		
		assertContainsRegex("${studentName}{*}${joinFragment}",
				EmailTemplates.STUDENT_COURSE_JOIN);
		
		assertContainsRegex("${joinUrl}{*}${key}",
				EmailTemplates.FRAGMENT_STUDENT_COURSE_JOIN);
	}

}
