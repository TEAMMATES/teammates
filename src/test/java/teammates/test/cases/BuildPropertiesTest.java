package teammates.test.cases;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import teammates.logic.BuildProperties;

public class BuildPropertiesTest extends BaseTestCase {

	@Test
	public void checkPresence() {
		assertTrue(null != BuildProperties.inst().TEAMMATES_APP_URL);
	}

	@Test
	public void checkReadingEmailTemplates() {
		assertContainsRegex("${joinFragment}{*}${submitUrl}",
				BuildProperties.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_);
		
		assertContainsRegex("${reportUrl}",
				BuildProperties.inst().STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED);
		
		assertContainsRegex("${studentName}{*}${joinFragment}",
				BuildProperties.inst().STUDENT_EMAIL_TEMPLATE_COURSE_JOIN);
		
		assertContainsRegex("${joinUrl}{*}${key}",
				BuildProperties.inst().STUDENT_EMAIL_FRAGMENT_COURSE_JOIN);
	}

}
