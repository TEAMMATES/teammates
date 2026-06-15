package teammates.e2e.cases;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelperExtension;
import teammates.e2e.pageobjects.AdminSearchPage;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_SEARCH_PAGE}.
 */
public class AdminSearchPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadDataBundle("/AdminSearchPageE2ETest.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.ADMIN_SEARCH_PAGE);
        AdminSearchPage searchPage = loginAdminToPage(url, AdminSearchPage.class);

        Course course = testData.courses.get("typicalCourse1");
        Student student = testData.students.get("student1InCourse1");
        Instructor instructor = testData.instructors.get("instructor1OfCourse1");
        AccountVerificationRequest accountVerificationRequest = testData.accountVerificationRequests.get("instructor1OfCourse1");

        ______TS("Typical case: Search student email");
        String searchContent = student.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        String studentDetails = getExpectedStudentDetails(student);
        String studentManageAccountLink = getExpectedStudentManageAccountLink(student);
        searchPage.verifyStudentRowContent(student, course, studentDetails, studentManageAccountLink);

        ______TS("Typical case: Regenerate registration key for a course student");
        searchPage.regenerateStudentKey(student);
        searchPage.verifyRegenerateStudentKey();
        searchPage.waitForPageToLoad();

        ______TS("Typical case: Search for instructor email");
        searchPage.clearSearchBox();
        searchContent = instructor.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        String instructorManageAccountLink = getExpectedInstructorManageAccountLink(instructor);
        searchPage.verifyInstructorRowContent(instructor, course, instructorManageAccountLink);

        ______TS("Typical case: Regenerate registration key for an instructor");
        searchPage.regenerateInstructorKey(instructor);
        searchPage.verifyRegenerateInstructorKey();

        ______TS("Typical case: Search for account request by email");
        searchPage.clearSearchBox();
        searchContent = accountVerificationRequest.getEmail();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyAccountVerificationRequestRowContent(accountVerificationRequest);
        searchPage.verifyAccountVerificationRequestExpandedLinks(accountVerificationRequest);

        ______TS("Typical case: Search common search key");
        searchPage.clearSearchBox();
        searchContent = "Course1";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyAccountVerificationRequestRowContent(accountVerificationRequest);

        ______TS("Typical case: Delete account request successful");
        accountVerificationRequest = testData.accountVerificationRequests.get("unregisteredInstructor1");
        searchContent = accountVerificationRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickDeleteAccountVerificationRequestButton(accountVerificationRequest);
        assertNull(BACKDOOR.getAccountVerificationRequest(accountVerificationRequest.getId()));

        ______TS("Typical case: Edit account request successful");
        accountVerificationRequest = testData.accountVerificationRequests.get("unregisteredInstructor2");
        searchContent = accountVerificationRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickEditAccountVerificationRequestButton(accountVerificationRequest);
        searchPage.fillInEditModalFields("Different name", accountVerificationRequest.getEmail(),
                accountVerificationRequest.getInstitute().getName(), "New comment");
        searchPage.clickSaveEditAccountVerificationRequestButton();
        accountVerificationRequest.setName("Different name");
        accountVerificationRequest.setComments("New comment");
        searchPage.verifyAccountVerificationRequestRowContent(accountVerificationRequest);

        ______TS("Typical case: View comment of account request");
        accountVerificationRequest = testData.accountVerificationRequests.get("unregisteredInstructor2");
        searchContent = accountVerificationRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickViewAccountVerificationRequestAndVerifyCommentsButton(accountVerificationRequest, "New comment");

        ______TS("Edit account request with invalid details");
        accountVerificationRequest = testData.accountVerificationRequests.get("unregisteredInstructor2");
        searchContent = accountVerificationRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickEditAccountVerificationRequestButton(accountVerificationRequest);
        searchPage.fillInEditModalFields(accountVerificationRequest.getName(), "invalid",
                accountVerificationRequest.getInstitute().getName(), "New comment");
        searchPage.closeToastsIfPresent();
        searchPage.clickSaveEditAccountVerificationRequestButton();
        String formattedErrorMessage = String.format("\"%s\" is not acceptable to TEAMMATES as a/an %s because it %s. "
                + "An email address contains some text followed by one '@' sign followed by some more text, "
                + "and should end with a top level domain address like .com. It cannot be longer than %d characters, "
                + "cannot be empty and cannot contain spaces.",
                "invalid", FieldValidator.EMAIL_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                FieldValidator.EMAIL_MAX_LENGTH);
        searchPage.verifyStatusMessage(formattedErrorMessage);

        String name = StringHelperExtension.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);

        searchPage.clickEditAccountVerificationRequestButton(accountVerificationRequest);
        searchPage.fillInEditModalFields(name, accountVerificationRequest.getEmail(),
                accountVerificationRequest.getInstitute().getName(), "New comment");
        searchPage.clickSaveEditAccountVerificationRequestButton();
        formattedErrorMessage = String.format("\"%s\" is not acceptable to TEAMMATES as a/an %s because it %s. "
                + "The value of a/an %s should be no longer than %d characters. It should not be empty.",
                name, FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.REASON_TOO_LONG,
                FieldValidator.PERSON_NAME_FIELD_NAME, FieldValidator.PERSON_NAME_MAX_LENGTH);
        searchPage.verifyStatusMessage(formattedErrorMessage);

        ______TS("Typical case: Approve account request successful");
        accountVerificationRequest = testData.accountVerificationRequests.get("unregisteredInstructor2");
        searchContent = accountVerificationRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickApproveAccountVerificationRequestButton(accountVerificationRequest);
        accountVerificationRequest.setStatus(AccountVerificationRequestStatus.APPROVED);
        searchPage.verifyAccountVerificationRequestRowContent(accountVerificationRequest);

        ______TS("Typical case: Reject account request successfully");
        accountVerificationRequest = testData.accountVerificationRequests.get("unregisteredInstructor3");
        searchContent = accountVerificationRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickRejectAccountVerificationRequestButton(accountVerificationRequest);
        accountVerificationRequest.setStatus(AccountVerificationRequestStatus.REJECTED);
        searchPage.verifyAccountVerificationRequestRowContent(accountVerificationRequest);

        ______TS("Reject account request with empty body");
        accountVerificationRequest = testData.accountVerificationRequests.get("unregisteredInstructor5");
        searchContent = accountVerificationRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickRejectAccountVerificationRequestWithReasonButton(accountVerificationRequest);
        searchPage.fillInRejectionModalBody("");
        searchPage.clickConfirmRejectAccountVerificationRequest();
        searchPage.verifyStatusMessage("Please provide an email body for the rejection email.");
        searchPage.closeRejectionModal();

        ______TS("Typical case: Reject account request with reason successfully");
        accountVerificationRequest = testData.accountVerificationRequests.get("unregisteredInstructor4");
        searchContent = accountVerificationRequest.getEmail();
        searchPage.clearSearchBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickRejectAccountVerificationRequestWithReasonButton(accountVerificationRequest);
        accountVerificationRequest.setStatus(AccountVerificationRequestStatus.REJECTED);
        searchPage.verifyAccountVerificationRequestRowContent(accountVerificationRequest);
    }

    private String getExpectedStudentDetails(Student student) {
        return String.format("%s [%s] (%s)", student.getCourseId(),
                student.getSectionName(),
                student.getTeamName());
    }

    private String getExpectedStudentManageAccountLink(Student student) {
        return student.isRegistered() ? createFrontendUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withAccountId(student.getAccountId())
                .toAbsoluteString()
                : "";
    }

    private String getExpectedInstructorManageAccountLink(Instructor instructor) {
        return createFrontendUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withAccountId(instructor.getAccountId())
                .toAbsoluteString();
    }

    @AfterClass
    public void classTeardown() {
        if (testData != null) {
            for (AccountVerificationRequest request : testData.accountVerificationRequests.values()) {
                BACKDOOR.deleteAccountVerificationRequest(request.getId());
            }
        }
    }
}
