package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

import teammates.common.util.Constants;
import teammates.test.cases.BaseTestCase;

public class BuildPropertiesTest extends BaseTestCase {

	@Test
	public void checkPresence() {
		assertTrue(null != Constants.TEAMMATES_APP_URL);
		assertTrue(Constants.PERSISTENCE_CHECK_DURATION > 0);
	}

	@Test
	public void checkReadingEmailTemplates() {
		assertContainsRegex("${joinFragment}{*}${submitUrl}",
				Constants.STUDENT_EMAIL_TEMPLATE_EVALUATION_);
		
		assertContainsRegex("${reportUrl}",
				Constants.STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED);
		
		assertContainsRegex("${studentName}{*}${joinFragment}",
				Constants.STUDENT_EMAIL_TEMPLATE_COURSE_JOIN);
		
		assertContainsRegex("${joinUrl}{*}${key}",
				Constants.STUDENT_EMAIL_FRAGMENT_COURSE_JOIN);
	}

}
