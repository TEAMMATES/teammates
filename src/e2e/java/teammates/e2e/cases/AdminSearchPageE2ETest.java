package teammates.e2e.cases;

import java.time.Instant;

import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.e2e.pageobjects.AdminSearchPage;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_SEARCH_PAGE}.
 */
public class AdminSearchPageE2ETest extends BaseE2ETestCase {
    private AdminSearchPage searchPage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminSearchPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    public void allTests() {
        AppUrl url = createUrl(Const.WebPageURIs.ADMIN_SEARCH_PAGE);
        searchPage = loginAdminToPage(url, AdminSearchPage.class);

        StudentAttributes student = testData.students.get("student1InCourse1");
        AccountAttributes studentAccount = testData.accounts.get("student1InCourse1");
        InstructorAttributes instructor = testData.instructors.get("instructor1OfCourse1");
        AccountAttributes instructorAccount = testData.accounts.get("instructor1OfCourse1");

        ______TS("Typical case: Search student google id");
        String searchContent = student.getGoogleId();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        verifyStudentRowContent(student, studentAccount);
        verifyStudentExpandedLinks(student);

        ______TS("Typical case: Reset student google id");
        searchPage.resetStudentGoogleId(student);
        student.googleId = null;
        verifyStudentRowContent(student, studentAccount);

        ______TS("Typical case: Regenerate all links for a course student");
        searchPage.clickExpandStudentLinks();
        WebElement studentRow = searchPage.getStudentRow(student);
        String originalJoinLink = searchPage.getStudentJoinLink(studentRow);

        searchPage.regenerateLinksForStudent(student);
        verifyRegenerateStudentCourseLinks(studentRow, originalJoinLink);
        searchPage.waitForPageToLoad();

        ______TS("Typical case: Search for instructor email");
        searchPage.clearSearchBox();
        searchContent = instructor.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        verifyInstructorRowContent(instructor, instructorAccount);
        verifyInstructorExpandedLinks(instructor);

        ______TS("Typical case: Reset instructor google id");
        searchPage.resetInstructorGoogleId(instructor);
        instructor.googleId = null;
        instructorAccount.institute = null;
        verifyInstructorRowContent(instructor, instructorAccount);

        ______TS("Typical case: Search common course id");
        searchPage.clearSearchBox();
        searchContent = student.getCourse();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        verifyStudentRowContent(student, studentAccount);
        verifyInstructorRowContent(instructor, instructorAccount);

        ______TS("Typical case: Expand and collapse links");
        verifyLinkExpansionButtons(student, instructor);
    }

    private void verifyStudentRowContent(StudentAttributes student, AccountAttributes account) {
        WebElement studentRow = searchPage.getStudentRow(student);
        String actualDetails = searchPage.getStudentDetails(studentRow);
        String actualName = searchPage.getStudentName(studentRow);
        String actualGoogleId = searchPage.getStudentGoogleId(studentRow);
        String actualHomepageLink = searchPage.getStudentHomeLink(studentRow);
        String actualInstitute = searchPage.getStudentInstitute(studentRow);
        String actualComment = searchPage.getStudentComments(studentRow);
        String actualManageAccountLink = searchPage.getStudentManageAccountLink(studentRow);

        String expectedDetails = getExpectedStudentDetails(student);
        String expectedName = student.name;
        String expectedGoogleId = StringHelper.convertToEmptyStringIfNull(student.googleId);
        String expectedInstitute = StringHelper.convertToEmptyStringIfNull(account.institute);
        String expectedComment = StringHelper.convertToEmptyStringIfNull(student.comments);
        String expectedManageAccountLink = getExpectedStudentManageAccountLink(student);
        String expectedHomePageLink = getExpectedStudentHomePageLink(student);

        assertEquals(expectedDetails, actualDetails);
        assertEquals(expectedName, actualName);
        assertEquals(expectedGoogleId, actualGoogleId);
        assertEquals(expectedInstitute, actualInstitute);
        assertEquals(expectedComment, actualComment);
        assertEquals(expectedManageAccountLink, actualManageAccountLink);
        assertEquals(expectedHomePageLink, actualHomepageLink);
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

    private void verifyStudentExpandedLinks(StudentAttributes student) {
        searchPage.clickExpandStudentLinks();
        WebElement studentRow = searchPage.getStudentRow(student);
        String actualEmail = searchPage.getStudentEmail(studentRow);
        String actualJoinLink = searchPage.getStudentJoinLink(studentRow);
        int actualNumExpandedRows = searchPage.getNumExpandedRows(studentRow);

        String expectedEmail = student.email;
        int expectedNumExpandedRows = getExpectedNumExpandedRows(student);

        assertEquals(expectedEmail, actualEmail);
        assertNotEquals("", actualJoinLink);
        assertEquals(expectedNumExpandedRows, actualNumExpandedRows);
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

    private void verifyInstructorRowContent(InstructorAttributes instructor, AccountAttributes account) {
        WebElement instructorRow = searchPage.getInstructorRow(instructor);
        String actualCourseId = searchPage.getInstructorCourseId(instructorRow);
        String actualName = searchPage.getInstructorName(instructorRow);
        String actualGoogleId = searchPage.getInstructorGoogleId(instructorRow);
        String actualHomePageLink = searchPage.getInstructorHomePageLink(instructorRow);
        String actualInstitute = searchPage.getInstructorInstitute(instructorRow);
        String actualManageAccountLink = searchPage.getInstructorManageAccountLink(instructorRow);

        String expectedCourseId = instructor.courseId;
        String expectedName = instructor.name;
        String expectedGoogleId = StringHelper.convertToEmptyStringIfNull(instructor.googleId);
        String expectedManageAccountLink = getExpectedInstructorManageAccountLink(instructor);
        String expectedInstitute = StringHelper.convertToEmptyStringIfNull(account.institute);
        String expectedHomePageLink = getExpectedInstructorHomePageLink(instructor);

        assertEquals(expectedCourseId, actualCourseId);
        assertEquals(expectedName, actualName);
        assertEquals(expectedGoogleId, actualGoogleId);
        assertEquals(expectedHomePageLink, actualHomePageLink);
        assertEquals(expectedInstitute, actualInstitute);
        assertEquals(expectedManageAccountLink, actualManageAccountLink);
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

    private void verifyInstructorExpandedLinks(InstructorAttributes instructor) {
        searchPage.clickExpandInstructorLinks();
        WebElement instructorRow = searchPage.getInstructorRow(instructor);
        String actualEmail = searchPage.getInstructorEmail(instructorRow);
        String actualJoinLink = searchPage.getInstructorJoinLink(instructorRow);

        String expectedEmail = instructor.email;

        assertEquals(expectedEmail, actualEmail);
        assertNotEquals("", actualJoinLink);
    }

    private void verifyLinkExpansionButtons(StudentAttributes student, InstructorAttributes instructor) {
        WebElement studentRow = searchPage.getStudentRow(student);
        WebElement instructorRow = searchPage.getInstructorRow(instructor);

        searchPage.clickExpandStudentLinks();
        searchPage.clickExpandInstructorLinks();
        int numExpandedStudentRows = searchPage.getNumExpandedRows(studentRow);
        int numExpandedInstructorRows = searchPage.getNumExpandedRows(instructorRow);
        assertNotEquals(numExpandedStudentRows, 0);
        assertNotEquals(numExpandedInstructorRows, 0);

        searchPage.clickCollapseInstructorLinks();
        numExpandedStudentRows = searchPage.getNumExpandedRows(studentRow);
        numExpandedInstructorRows = searchPage.getNumExpandedRows(instructorRow);
        assertNotEquals(numExpandedStudentRows, 0);
        assertEquals(numExpandedInstructorRows, 0);

        searchPage.clickExpandInstructorLinks();
        searchPage.clickCollapseStudentLinks();
        searchPage.waitUntilAnimationFinish();

        numExpandedStudentRows = searchPage.getNumExpandedRows(studentRow);
        numExpandedInstructorRows = searchPage.getNumExpandedRows(instructorRow);
        assertEquals(numExpandedStudentRows, 0);
        assertNotEquals(numExpandedInstructorRows, 0);
    }

    private void verifyRegenerateStudentCourseLinks(WebElement studentRow, String originalJoinLink) {
        if (TestProperties.isDevServer() || TestProperties.INCLUDE_EMAIL_VERIFICATION) {
            searchPage.verifyStatusMessage("Student's links for this course have been successfully "
                    + "regenerated, and the email has been sent.");
        }

        String regeneratedJoinLink = searchPage.getStudentJoinLink(studentRow);
        assertNotEquals(regeneratedJoinLink, originalJoinLink);
    }

}
