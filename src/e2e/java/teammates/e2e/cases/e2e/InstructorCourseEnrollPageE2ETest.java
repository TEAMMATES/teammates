package teammates.e2e.cases.e2e;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.InstructorCourseEnrollPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_ENROLL_PAGE}.
 */
public class InstructorCourseEnrollPageE2ETest extends BaseE2ETestCase {

	@Override
	protected void prepareTestData() {
		testData = loadDataBundle(Const.TestCase.INSTRUCTOR_COURSE_ENROLL_PAGE_E2E_TEST_JSON);
		removeAndRestoreDataBundle(testData);
	}

	@Test
	public void testAll() {
		AppUrl url = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
				.withUserId(testData.instructors.get(Const.TestCase.IC_ENROLL_E2E_T_TEAMMATES_TEST).googleId)
				.withCourseId(testData.courses.get(Const.TestCase.IC_ENROLL_E2E_T_CS2104).getId());
		InstructorCourseEnrollPage enrollPage = loginAdminToPage(url, InstructorCourseEnrollPage.class);

		______TS(Const.TestCase.ADD_ROWS_TO_ENROLL_SPREADSHEET);
		int numRowsToAdd = 30;
		enrollPage.addEnrollSpreadsheetRows(numRowsToAdd);
		enrollPage.verifyNumAddedEnrollSpreadsheetRows(numRowsToAdd);

		______TS(Const.TestCase.ENROLL_STUDENTS_TO_EMPTY_COURSE);
		StudentAttributes student1 = createCourseStudent(Const.TestCase.SECTION_1, Const.TestCase.TEAM_1,
				Const.TestCase.ALICE_BETSY, Const.TestCase.ALICE_B_TMMS_GMAIL_TMT, Const.TestCase.COMMENT_FOR_ALICE);
		StudentAttributes student2 = createCourseStudent(Const.TestCase.SECTION_1, Const.TestCase.TEAM_1,
				Const.TestCase.BENNY_CHARLES, Const.TestCase.BENNY_C_TMMS_GMAIL_TMT, Const.TestCase.COMMENT_FOR_BENNY);
		StudentAttributes student3 = createCourseStudent(Const.TestCase.SECTION_2, Const.TestCase.TEAM_2,
				Const.TestCase.CHARLIE_DAVIS, Const.TestCase.CHARLIE_D_TMMS_GMAIL_TMT,
				Const.TestCase.COMMENT_FOR_CHARLIE);

		StudentAttributes[] studentsEnrollingToEmptyCourse = { student1, student2, student3 };

		enrollPage.enroll(studentsEnrollingToEmptyCourse);
		enrollPage.verifyStatusMessage(Const.TestCase.ENROLLMENT_SUCCESSFUL_SUMMARY_GIVEN_BELOW);
		enrollPage.verifyResultsPanelContains(studentsEnrollingToEmptyCourse, null, null, null, null);

		// refresh page to confirm enrollment
		enrollPage = AppPage.getNewPageInstance(browser, url, InstructorCourseEnrollPage.class);
		enrollPage.verifyExistingStudentsTableContains(studentsEnrollingToEmptyCourse);

		// verify students in datastore
		assertEquals(getStudent(student1), student1);
		assertEquals(getStudent(student2), student2);
		assertEquals(getStudent(student3), student3);

		______TS(Const.TestCase.ENROLL_AND_MODIFY_STUDENTS_IN_EXISTING_COURSE);
		// modify team details of existing student
		student3.team = Const.TestCase.TEAM_3;
		// add valid new student
		StudentAttributes student4 = createCourseStudent(Const.TestCase.SECTION_2, Const.TestCase.TEAM_2,
				Const.TestCase.DANNY_ENGRID, Const.TestCase.DANNY_E_TMMS_GMAIL_TMT, Const.TestCase.COMMENT_FOR_DANNY);
		// add new student with invalid email
		StudentAttributes student5 = createCourseStudent(Const.TestCase.SECTION_1, Const.TestCase.TEAM_2,
				Const.TestCase.INVALID_STUDENT, Const.TestCase.INVALID_EMAIL, Const.TestCase.COMMENT_FOR_INVALID);

		// student2 included to test modified without change table
		StudentAttributes[] studentsEnrollingToExistingCourse = { student2, student3, student4, student5 };
		enrollPage.enroll(studentsEnrollingToExistingCourse);
		enrollPage.verifyStatusMessage(Const.TestCase.SOME_STUDENTS_FAILED_TO_BE_ENROLLED_SEE_THE_SUMMARY_BELOW);

		StudentAttributes[] newStudentsData = { student4 };
		StudentAttributes[] modifiedStudentsData = { student3 };
		StudentAttributes[] modifiedWithoutChangeStudentsData = { student2 };
		StudentAttributes[] errorStudentsData = { student5 };
		StudentAttributes[] unmodifiedStudentsData = { student1 };

		enrollPage.verifyResultsPanelContains(newStudentsData, modifiedStudentsData, modifiedWithoutChangeStudentsData,
				errorStudentsData, unmodifiedStudentsData);

		// verify students in datastore
		assertEquals(getStudent(student1), student1);
		assertEquals(getStudent(student2), student2);
		assertEquals(getStudent(student3), student3);
		assertEquals(getStudent(student4), student4);
		assertNull(getStudent(student5));

		// refresh page to confirm enrollment
		enrollPage = AppPage.getNewPageInstance(browser, url, InstructorCourseEnrollPage.class);
		StudentAttributes[] expectedExistingData = { student1, student2, student3, student4 };
		enrollPage.verifyExistingStudentsTableContains(expectedExistingData);
	}

	private StudentAttributes createCourseStudent(String section, String team, String name, String email,
			String comments) {
		return StudentAttributes.builder(Const.TestCase.IC_ENROLL_E2E_T_CS2104, email).withName(name)
				.withComment(comments).withTeamName(team).withSectionName(section).build();
	}
}
