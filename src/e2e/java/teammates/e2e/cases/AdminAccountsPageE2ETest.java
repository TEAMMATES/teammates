package teammates.e2e.cases;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminAccountsPage;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.AccountData;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_ACCOUNTS_PAGE}.
 */
public class AdminAccountsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadDataBundle("/AdminAccountsPageE2ETest.json"));
    }

    @Test
    @Override
    public void testAll() {
        ______TS("verify loaded data");
        UUID accountId = testData.accounts.get("AAccounts.instr2").getId();
        AppUrl accountsPageUrl = createFrontendUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withAccountId(accountId);
        AdminAccountsPage accountsPage = loginAdminToPage(accountsPageUrl, AdminAccountsPage.class);

        AccountData account = getAccount(accountId);
        accountsPage.verifyAccountDetails(account);

        ______TS("action: remove instructor from course");
        Instructor instructor = testData.instructors.get("AAccounts.instr2-AAccounts.CS2103");
        String courseId = instructor.getCourseId();
        verifyPresentInDatabase(instructor);

        accountsPage.clickRemoveInstructorFromCourse(courseId);
        accountsPage.verifyStatusMessage("Instructor is successfully deleted from course \"" + courseId + "\"");
        verifyAbsentInDatabase(instructor);

        ______TS("action: remove student from course");
        Student student = testData.students.get("AAccounts.instr2-student-CS2103");
        courseId = student.getCourseId();
        verifyPresentInDatabase(student);

        accountsPage.clickRemoveStudentFromCourse(courseId);
        accountsPage.verifyStatusMessage("Student is successfully deleted from course \"" + courseId + "\"");
        verifyAbsentInDatabase(student);

        ______TS("action: delete account entirely");

        accountsPage.clickDeleteAccount();
        accountsPage.verifyStatusMessage("Account is successfully deleted.");

        assertNull(getAccount(accountId));
    }
}
