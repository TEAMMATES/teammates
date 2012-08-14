package teammates.test.cases;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teammates.common.Common;

public class BuildPropertiesTest extends BaseTestCase {

	@Test
	public void checkPresence() {
		assertTrue(null != Common.TEAMMATES_APP_URL);
	}

	@Test
	public void checkReadingEmailTemplates() {
		assertContainsRegex("${joinFragment}{*}${submitUrl}",
				Common.STUDENT_EMAIL_TEMPLATE_EVALUATION_);
		
		assertContainsRegex("${reportUrl}",
				Common.STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED);
		
		assertContainsRegex("${studentName}{*}${joinFragment}",
				Common.STUDENT_EMAIL_TEMPLATE_COURSE_JOIN);
		
		assertContainsRegex("${joinUrl}{*}${key}",
				Common.STUDENT_EMAIL_FRAGMENT_COURSE_JOIN);
	}

}
