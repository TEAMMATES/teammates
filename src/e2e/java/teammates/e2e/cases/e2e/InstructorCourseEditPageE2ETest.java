package teammates.e2e.cases.e2e;

import java.time.ZoneId;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.InstructorCourseEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_EDIT_PAGE}.
 */
public class InstructorCourseEditPageE2ETest extends BaseE2ETestCase {
	CourseAttributes course;
	InstructorAttributes[] instructors = new InstructorAttributes[5];

	@Override
	protected void prepareTestData() {
		testData = loadDataBundle(Const.TestCase.INSTRUCTOR_COURSE_EDIT_PAGE_E2E_TEST_JSON);
		removeAndRestoreDataBundle(testData);

		course = testData.courses.get(Const.TestCase.INS_CRS_EDIT_CS2104);
		instructors[0] = testData.instructors.get(Const.TestCase.INS_CRS_EDIT_HELPER);
		instructors[1] = testData.instructors.get(Const.TestCase.INS_CRS_EDIT_MANAGER);
		instructors[2] = testData.instructors.get(Const.TestCase.INS_CRS_EDIT_OBSERVER);
		instructors[3] = testData.instructors.get(Const.TestCase.INS_CRS_EDIT_COOWNER);
		instructors[4] = testData.instructors.get(Const.TestCase.INS_CRS_EDIT_TUTOR);
	}

	@Test
	public void testAll() {
		______TS(Const.TestCase.VERIFY_CANNOT_EDIT_WITHOUT_PRIVILEGE);
		// log in as instructor with no edit privilege
		AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE).withUserId(instructors[2].googleId)
				.withCourseId(course.getId());
		InstructorCourseEditPage editPage = loginAdminToPage(url, InstructorCourseEditPage.class);

		editPage.verifyCourseNotEditable();
		editPage.verifyInstructorsNotEditable();
		editPage.verifyAddInstructorNotAllowed();

		______TS(Const.TestCase.VERIFY_LOADED_DATA);
		// re-log in as instructor with edit privilege
		url = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE).withUserId(instructors[3].googleId)
				.withCourseId(course.getId());
		editPage = AppPage.getNewPageInstance(browser, url, InstructorCourseEditPage.class);

		editPage.verifyCourseDetails(course);
		editPage.verifyInstructorDetails(instructors[0]);
		editPage.verifyInstructorDetails(instructors[1]);
		editPage.verifyInstructorDetails(instructors[2]);
		editPage.verifyInstructorDetails(instructors[3]);
		editPage.verifyInstructorDetails(instructors[4]);

		______TS(Const.TestCase.ADD_INSTRUCTOR);
		InstructorAttributes newInstructor = InstructorAttributes
				.builder(course.getId(), Const.TestCase.INS_CRS_EDIT_TEST_GMAIL_TMT)
				.withName(Const.TestCase.TEAMMATES_TEST).withIsDisplayedToStudents(true)
				.withDisplayedName(Const.TestCase.INIT_CAP_INSTRUCTOR).withRole(Const.TestCase.TUTOR).build();

		editPage.addInstructor(newInstructor);
		editPage.verifyStatusMessage(
				Const.TestCase.THE_INSTRUCTOR + newInstructor.name + Const.TestCase.HAS_BEEN_ADDED_SUCCESSFULLY
						+ Const.TestCase.AN_EMAIL_CONTAINING_HOW_TO_JOIN_THIS_COURSE_WILL_BE_SENT_TO
						+ newInstructor.email + Const.TestCase.IN_A_FEW_MINUTES);
		editPage.verifyInstructorDetails(newInstructor);
		verifyPresentInDatastore(newInstructor);

		______TS(Const.TestCase.RESEND_INVITE);
		editPage.resendInstructorInvite(newInstructor);
		editPage.verifyStatusMessage(Const.TestCase.AN_EMAIL_HAS_BEEN_SENT_TO + newInstructor.email);

		______TS(Const.TestCase.EDIT_INSTRUCTOR);
		instructors[0].name = Const.TestCase.EDITED_NAME;
		instructors[0].email = Const.TestCase.INS_CRS_EDIT_EDITED_GMAIL_TMT;
		instructors[0].privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, true);
		instructors[0].privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
		instructors[0].privileges.updatePrivilege(Const.TestCase.SECTION_2,
				Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
		instructors[0].privileges.updatePrivilege(Const.TestCase.SECTION_1, Const.TestCase.FIRST_FEEDBACK_SESSION,
				Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, true);

		editPage.editInstructor(1, instructors[0]);
		editPage.toggleCustomCourseLevelPrivilege(1, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
		editPage.toggleCustomCourseLevelPrivilege(1, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
		editPage.toggleCustomSectionLevelPrivilege(1, 1, Const.TestCase.SECTION_2,
				Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
		editPage.toggleCustomSessionLevelPrivilege(1, 2, Const.TestCase.SECTION_1,
				Const.TestCase.FIRST_FEEDBACK_SESSION,
				Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
		editPage.verifyStatusMessage(
				Const.TestCase.THE_INSTRUCTOR_SPACE + instructors[0].name + Const.TestCase.HAS_BEEN_UPDATED);
		editPage.verifyInstructorDetails(instructors[0]);

		// verify in datastore by reloading
		editPage.reloadPage();
		editPage.verifyInstructorDetails(instructors[0]);

		______TS(Const.TestCase.DELETE_INSTRUCTOR);
		editPage.deleteInstructor(newInstructor);
		editPage.verifyStatusMessage(Const.TestCase.INSTRUCTOR_IS_SUCCESSFULLY_DELETED);
		editPage.verifyNumInstructorsEquals(5);
		verifyAbsentInDatastore(newInstructor);

		______TS(Const.TestCase.EDIT_COURSE);
		String newName = Const.TestCase.NEW_COURSE_NAME;
		ZoneId newTimeZone = ZoneId.of(Const.TestCase.ASIA_SINGAPORE);
		course.setName(newName);
		course.setTimeZone(newTimeZone);

		editPage.editCourse(course);
		editPage.verifyStatusMessage(Const.TestCase.THE_COURSE_HAS_BEEN_EDITED);
		editPage.verifyCourseDetails(course);
		verifyPresentInDatastore(course);

		______TS(Const.TestCase.DELETE_COURSE);
		editPage.deleteCourse();
		editPage.verifyStatusMessage(
				Const.TestCase.THE_COURSE + course.getId() + Const.TestCase.HAS_BEEN_DELETED_FULLSTOP
						+ Const.TestCase.YOU_CAN_RESTORE_IT_FROM_THE_RECYCLE_BIN_MANUALLY);
		assertTrue(isCourseInRecycleBin(course.getId()));
	}
}
