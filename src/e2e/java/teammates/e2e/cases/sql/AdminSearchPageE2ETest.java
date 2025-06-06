package teammates.e2e.cases.sql;

import java.time.Instant;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelperExtension;
import teammates.e2e.pageobjects.AdminSearchPageSql;
import teammates.e2e.util.TestProperties;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_SEARCH_PAGE}.
 */
public class AdminSearchPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        if (!TestProperties.INCLUDE_SEARCH_TESTS) {
            return;
        }
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/AdminSearchPageE2ESqlTest.json"));
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        if (!TestProperties.INCLUDE_SEARCH_TESTS) {
            return;
        }

        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_SEARCH_PAGE);
        AdminSearchPageSql searchPage = loginAdminToPage(url, AdminSearchPageSql.class);

        Course course = testData.courses.get("typicalCourse1");
        Student student = testData.students.get("student1InCourse1");
        Instructor instructor = testData.instructors.get("instructor1OfCourse1");
        AccountRequest accountRequest = testData.accountRequests.get("instructor1OfCourse1");

        ______TS("Typical case: Search student email");
        String searchContent = student.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        String studentDetails = getExpectedStudentDetails(student);
        String studentManageAccountLink = getExpectedStudentManageAccountLink(student);
        String studentHomePageLink = getExpectedStudentHomePageLink(student);
        int numExpandedRows = getExpectedNumExpandedRows(student);
        searchPage.verifyStudentRowContent(student, course, studentDetails, studentManageAccountLink,
                studentHomePageLink);
        searchPage.verifyStudentExpandedLinks(student, numExpandedRows);

        ______TS("Typical case: Reset student google id");
        searchPage.resetStudentGoogleId(student);
        student.setGoogleId(null);
        searchPage.verifyStudentRowContentAfterReset(student, course);

        ______TS("Typical case: Regenerate registration key for a course student");
        searchPage.clickExpandStudentLinks();
        String originalJoinLink = searchPage.getStudentJoinLink(student);
        searchPage.regenerateStudentKey(student);
        searchPage.verifyRegenerateStudentKey(student, originalJoinLink);
        searchPage.waitForPageToLoad();

        ______TS("Typical case: Search for instructor email");
        searchPage.clearSearchBox();
        searchContent = instructor.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        String instructorManageAccountLink = getExpectedInstructorManageAccountLink(instructor);
        String instructorHomePageLink = getExpectedInstructorHomePageLink(instructor);
        searchPage.verifyInstructorRowContent(instructor, course, instructorManageAccountLink,
                instructorHomePageLink);
        searchPage.verifyInstructorExpandedLinks(instructor);

        ______TS("Typical case: Reset instructor google id");
        searchPage.resetInstructorGoogleId(instructor);
        searchPage.verifyInstructorRowContentAfterReset(instructor, course);

        ______TS("Typical case: Regenerate registration key for an instructor");
        searchPage.clickExpandInstructorLinks();
        originalJoinLink = searchPage.getInstructorJoinLink(instructor);
        searchPage.regenerateInstructorKey(instructor);
        searchPage.verifyRegenerateInstructorKey(instructor, originalJoinLink);
        searchPage.waitForPageToLoad();

        ______TS("Typical case: Search for account request by email");
        searchPage.clearSearchBox();
        searchContent = accountRequest.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyAccountRequestRowContent(accountRequest);
        searchPage.verifyAccountRequestExpandedLinks(accountRequest);

        ______TS("Typical case: Search common search key");
        searchPage.clearSearchBox();
        searchContent = "Course1";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyStudentRowContentAfterReset(student, course);
        searchPage.verifyInstructorRowContentAfterReset(instructor, course);
        searchPage.verifyAccountRequestRowContent(accountRequest);

        ______TS("Typical case: Expand and collapse links");
        searchPage.verifyLinkExpansionButtons(student, instructor, accountRequest);

        ______TS("Typical case: Reset account request successful");
        searchContent = "ASearch.instructor1@gmail.tmt";
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickResetAccountRequestButton(accountRequest);
        assertNull(BACKDOOR.getAccountRequest(accountRequest.getId()).getRegisteredAt());

        ______TS("Typical case: Delete account request successful");
        accountRequest = testData.accountRequests.get("unregisteredInstructor1");
        searchContent = accountRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickDeleteAccountRequestButton(accountRequest);
        assertNull(BACKDOOR.getAccountRequest(accountRequest.getId()));

        ______TS("Typical case: Edit account request successful");
        accountRequest = testData.accountRequests.get("unregisteredInstructor2");
        searchContent = accountRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickEditAccountRequestButton(accountRequest);
        searchPage.fillInEditModalFields("Different name", accountRequest.getEmail(),
                accountRequest.getInstitute(), "New comment");
        searchPage.clickSaveEditAccountRequestButton();
        accountRequest.setName("Different name");
        accountRequest.setComments("New comment");
        searchPage.verifyAccountRequestRowContent(accountRequest);

        ______TS("Typical case: View comment of account request");
        accountRequest = testData.accountRequests.get("unregisteredInstructor2");
        searchContent = accountRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickViewAccountRequestAndVerifyCommentsButton(accountRequest, "New comment");

        ______TS("Edit account request with invalid details");
        accountRequest = testData.accountRequests.get("unregisteredInstructor2");
        searchContent = accountRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickEditAccountRequestButton(accountRequest);
        searchPage.fillInEditModalFields(accountRequest.getName(), "invalid",
                accountRequest.getInstitute(), "New comment");
        searchPage.clickSaveEditAccountRequestButton();
        String formattedErrorMessage = String.format("\"%s\" is not acceptable to TEAMMATES as a/an %s because it %s. "
                + "An email address contains some text followed by one '@' sign followed by some more text, "
                + "and should end with a top level domain address like .com. It cannot be longer than %d characters, "
                + "cannot be empty and cannot contain spaces.",
                "invalid", FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                FieldValidator.EMAIL_MAX_LENGTH);
        searchPage.verifyStatusMessage(formattedErrorMessage);

        String name = StringHelperExtension.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);

        searchPage.clickEditAccountRequestButton(accountRequest);
        searchPage.fillInEditModalFields(name, accountRequest.getEmail(), accountRequest.getInstitute(), "New comment");
        searchPage.clickSaveEditAccountRequestButton();
        formattedErrorMessage = String.format("\"%s\" is not acceptable to TEAMMATES as a/an %s because it %s. "
                + "The value of a/an %s should be no longer than %d characters. It should not be empty.",
                name, FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH);
        searchPage.verifyStatusMessage(formattedErrorMessage);

        ______TS("Typical case: Approve account request successful");
        accountRequest = testData.accountRequests.get("unregisteredInstructor2");
        searchContent = accountRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickApproveAccountRequestButton(accountRequest);
        accountRequest.setStatus(AccountRequestStatus.APPROVED);
        searchPage.verifyAccountRequestRowContent(accountRequest);

        ______TS("Typical case: Reject account request successfully");
        accountRequest = testData.accountRequests.get("unregisteredInstructor3");
        searchContent = accountRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickRejectAccountRequestButton(accountRequest);
        accountRequest.setStatus(AccountRequestStatus.REJECTED);
        searchPage.verifyAccountRequestRowContent(accountRequest);

        ______TS("Reject account request with empty body");
        accountRequest = testData.accountRequests.get("unregisteredInstructor5");
        searchContent = accountRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickRejectAccountRequestWithReasonButton(accountRequest);
        searchPage.fillInRejectionModalBody("");
        searchPage.clickConfirmRejectAccountRequest();
        searchPage.verifyStatusMessage("Please provide an email body for the rejection email.");
        searchPage.closeRejectionModal();

        ______TS("Typical case: Reject account request with reason successfully");
        accountRequest = testData.accountRequests.get("unregisteredInstructor4");
        searchContent = accountRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickRejectAccountRequestWithReasonButton(accountRequest);
        accountRequest.setStatus(AccountRequestStatus.REJECTED);
        searchPage.verifyAccountRequestRowContent(accountRequest);
    }

    private String getExpectedStudentDetails(Student student) {
        return String.format("%s [%s] (%s)", student.getCourse().getId(),
                student.getSection() == null
                        ? Const.DEFAULT_SECTION
                        : student.getSection().getName(),
                student.getTeam().getName());
    }

    private String getExpectedStudentHomePageLink(Student student) {
        return student.isRegistered() ? createFrontendUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                .withUserId(student.getGoogleId())
                .toAbsoluteString()
                : "";
    }

    private String getExpectedStudentManageAccountLink(Student student) {
        return student.isRegistered() ? createFrontendUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withParam(Const.ParamsNames.INSTRUCTOR_ID, student.getGoogleId())
                .toAbsoluteString()
                : "";
    }

    private int getExpectedNumExpandedRows(Student student) {
        int expectedNumExpandedRows = 2;
        for (FeedbackSession sessions : testData.feedbackSessions.values()) {
            if (sessions.getCourse().equals(student.getCourse())) {
                expectedNumExpandedRows += 1;
                if (sessions.getResultsVisibleFromTime().isBefore(Instant.now())) {
                    expectedNumExpandedRows += 1;
                }
            }
        }
        return expectedNumExpandedRows;
    }

    private String getExpectedInstructorHomePageLink(Instructor instructor) {
        String googleId = instructor.isRegistered() ? instructor.getGoogleId() : "";
        return createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE)
                .withUserId(googleId)
                .toAbsoluteString();
    }

    private String getExpectedInstructorManageAccountLink(Instructor instructor) {
        String googleId = instructor.isRegistered() ? instructor.getGoogleId() : "";
        return createFrontendUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withParam(Const.ParamsNames.INSTRUCTOR_ID, googleId)
                .toAbsoluteString();
    }

    @AfterClass
    public void classTeardown() {
        for (AccountRequest request : testData.accountRequests.values()) {
            BACKDOOR.deleteAccountRequest(request.getId());
        }
    }
}
