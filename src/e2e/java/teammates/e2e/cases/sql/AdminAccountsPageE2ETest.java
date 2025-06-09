package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AdminAccountsPage;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.AccountData;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_ACCOUNTS_PAGE}.
 */
public class AdminAccountsPageE2ETest extends BaseE2ETestCase {

    String googleId = "tm.e2e.AAccounts.instr2";

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadSqlDataBundle("/AdminAccountsPageE2ETestSql.json"));
    }

    @Test
    @Override
    public void testAll() {
        ______TS("verify loaded data");
        AppUrl accountsPageUrl = createFrontendUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                .withParam(Const.ParamsNames.INSTRUCTOR_ID, googleId);
        AdminAccountsPage accountsPage = loginAdminToPage(accountsPageUrl, AdminAccountsPage.class);

        AccountData account = getAccount(googleId);
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
        Student student2 = testData.students.get("AAccounts.instr2-student-CS2104");
        Student student3 = testData.students.get("AAccounts.instr2-student-CS1101");
        verifyPresentInDatabase(student2);
        verifyPresentInDatabase(student3);

        accountsPage.clickDeleteAccount();
        accountsPage.verifyStatusMessage("Account \"" + googleId + "\" is successfully deleted.");

        assertNull(getAccount(googleId));

        // student entities should be deleted
        verifyAbsentInDatabase(student2);
        verifyAbsentInDatabase(student3);
    }
}
