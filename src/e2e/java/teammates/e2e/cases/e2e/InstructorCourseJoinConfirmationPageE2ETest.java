package teammates.e2e.cases.e2e;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.e2e.pageobjects.ErrorReportingModal;
import teammates.e2e.pageobjects.InstructorHomePage;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class InstructorCourseJoinConfirmationPageE2ETest extends BaseE2ETestCase {
	InstructorAttributes newInstructor;

	@Override
	protected void prepareTestData() {
		testData = loadDataBundle(Const.TestCase.INSTRUCTOR_COURSE_JOIN_CONFIRMATION_PAGE_E2E_TEST_JSON);
		removeAndRestoreDataBundle(testData);

		newInstructor = testData.instructors.get(Const.TestCase.ICJ_CONFIRMATION_E2E_T_INSTR_CS1101);
		newInstructor.googleId = Const.TestCase.ICJ_CONFIRMATION_E2E_T_INSTR2;
	}

	@BeforeClass
	public void classSetup() {
		browser.driver.manage().deleteAllCookies();
	}

	@Test
	public void testAll() {
		______TS(Const.TestCase.CLICK_JOIN_LINK_INVALID_KEY);
		String invalidEncryptedKey = Const.TestCase.INVALID_KEY;
		AppUrl joinLink = createUrl(Const.WebPageURIs.JOIN_PAGE).withRegistrationKey(invalidEncryptedKey)
				.withEntityType(Const.EntityType.INSTRUCTOR).withUserId(newInstructor.googleId);
		ErrorReportingModal errorPage = loginAdminToPage(joinLink, ErrorReportingModal.class);

		errorPage.verifyErrorMessage(Const.TestCase.NO_INSTRUCTOR_WITH_GIVEN_REGISTRATION_KEY + invalidEncryptedKey);

		______TS(Const.TestCase.CLICK_JOIN_LINK_VALID_KEY);
		String courseId = testData.courses.get(Const.TestCase.ICJ_CONFIRMATION_E2E_T_CS1101).getId();
		String instructorEmail = newInstructor.email;
		joinLink = createUrl(Const.WebPageURIs.JOIN_PAGE)
				.withRegistrationKey(getKeyForInstructor(courseId, instructorEmail))
				.withEntityType(Const.EntityType.INSTRUCTOR).withUserId(newInstructor.googleId);
		CourseJoinConfirmationPage confirmationPage = loginAdminToPage(joinLink, CourseJoinConfirmationPage.class);

		confirmationPage.verifyJoiningUser(newInstructor.googleId);
		confirmationPage.confirmJoinCourse(InstructorHomePage.class);

		______TS(Const.TestCase.ALREADY_JOINED_NO_CONFIRMATION_PAGE);
		browser.driver.get(joinLink.toAbsoluteString());
		AppPage.getNewPageInstance(browser, InstructorHomePage.class);
	}
}
