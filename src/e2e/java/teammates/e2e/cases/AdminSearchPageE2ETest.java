package teammates.e2e.cases;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminSearchPage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_SEARCH_PAGE}.
 */
public class AdminSearchPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminSearchPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createUrl(Const.WebPageURIs.ADMIN_SEARCH_PAGE);
        AdminSearchPage searchPage = loginAdminToPage(url, AdminSearchPage.class);

        StudentAttributes student = testData.students.get("student1InCourse1");
        AccountAttributes studentAccount = testData.accounts.get("student1InCourse1");
        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        AccountAttributes instructorAccount = testData.accounts.get("instructor1OfCourse1");

        ______TS("Typical case: Search student email");
        String searchContent = student.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        String studentDetails = getExpectedStudentDetails(student);
        String studentManageAccountLink = getExpectedStudentManageAccountLink(student);
        String studentHomePageLink = getExpectedStudentHomePageLink(student);
        int numExpandedRows = getExpectedNumExpandedRows(student);
        searchPage.verifyStudentRowContent(student, studentAccount, studentDetails, studentManageAccountLink,
                studentHomePageLink);
        searchPage.verifyStudentExpandedLinks(student, numExpandedRows);

        ______TS("Typical case: Reset student google id");
        searchPage.resetStudentGoogleId(student);
        student.googleId = null;
        studentManageAccountLink = getExpectedStudentManageAccountLink(student);
        studentHomePageLink = getExpectedStudentHomePageLink(student);
        searchPage.verifyStudentRowContent(student, studentAccount, studentDetails, studentManageAccountLink,
                studentHomePageLink);

        ______TS("Typical case: Regenerate all links for a course student");
        searchPage.clickExpandStudentLinks();
        String originalJoinLink = searchPage.getStudentJoinLink(student);

        searchPage.regenerateLinksForStudent(student);
        searchPage.verifyRegenerateStudentCourseLinks(student, originalJoinLink);
        searchPage.waitForPageToLoad();

        ______TS("Typical case: Search for instructor email");
        searchPage.clearSearchBox();
        searchContent = instructor.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        String instructorManageAccountLink = getExpectedInstructorManageAccountLink(instructor);
        String instructorHomePageLink = getExpectedInstructorHomePageLink(instructor);
        searchPage.verifyInstructorRowContent(instructor, instructorAccount, instructorManageAccountLink,
                instructorHomePageLink);
        searchPage.verifyInstructorExpandedLinks(instructor);

        ______TS("Typical case: Reset instructor google id");
        searchPage.resetInstructorGoogleId(instructor);
        instructor.googleId = null;
        instructorAccount.institute = null;
        instructorManageAccountLink = getExpectedInstructorManageAccountLink(instructor);
        instructorHomePageLink = getExpectedInstructorHomePageLink(instructor);
        searchPage.verifyInstructorRowContent(instructor, instructorAccount, instructorManageAccountLink,
                instructorHomePageLink);

        ______TS("Typical case: Search common course id");
        searchPage.clearSearchBox();
        searchContent = student.getCourse();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyStudentRowContent(student, studentAccount, studentDetails, studentManageAccountLink,
                studentHomePageLink);
        searchPage.verifyInstructorRowContent(instructor, instructorAccount, instructorManageAccountLink,
                instructorHomePageLink);

        ______TS("Typical case: Expand and collapse links");
        searchPage.verifyLinkExpansionButtons(student, instructor);
    }

    private String getExpectedStudentDetails(StudentAttributes student) {
        return String.format("%s [%s] (%s)", student.course,
                student.section == null ? Const.DEFAULT_SECTION : student.section, student.team);
    }

    private String getExpectedStudentHomePageLink(StudentAttributes student) {
        return student.isRegistered() ? createUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                .withUserId(student.googleId)
                .toAbsoluteString()
                : "";
    }

    private String getExpectedStudentManageAccountLink(StudentAttributes student) {
        return student.isRegistered() ? createUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withParam(Const.ParamsNames.INSTRUCTOR_ID, student.googleId)
                .toAbsoluteString()
                : "";
    }

    private int getExpectedNumExpandedRows(StudentAttributes student) {
        int expectedNumExpandedRows = 2;
        for (FeedbackSessionAttributes sessions : testData.feedbackSessions.values()) {
            if (sessions.getCourseId().equals(student.course)) {
                expectedNumExpandedRows += 1;
                if (sessions.getResultsVisibleFromTime().isBefore(Instant.now())) {
                    expectedNumExpandedRows += 1;
                }
            }
        }
        return expectedNumExpandedRows;
    }

    private String getExpectedInstructorHomePageLink(InstructorAttributes instructor) {
        String googleId = instructor.isRegistered() ? instructor.googleId : "";
        return createUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE)
                .withUserId(googleId)
                .toAbsoluteString();
    }

    private String getExpectedInstructorManageAccountLink(InstructorAttributes instructor) {
        String googleId = instructor.isRegistered() ? instructor.googleId : "";
        return createUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withParam(Const.ParamsNames.INSTRUCTOR_ID, googleId)
                .toAbsoluteString();
    }

}
