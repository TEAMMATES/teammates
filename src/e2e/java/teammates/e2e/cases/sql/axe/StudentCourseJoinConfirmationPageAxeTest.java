package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class StudentCourseJoinConfirmationPageAxeTest extends BaseAxeTestCase {

    private Student newStudent;
    private String newStudentGoogleId;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/StudentCourseJoinConfirmationPageE2ETestSql.json"));

        newStudent = testData.students.get("alice.tmms@SCJoinConf.CS2104");
        newStudentGoogleId = testData.accounts.get("alice.tmms").getGoogleId();
    }

    @Test
    @Override
    public void testAll() {
        AppUrl joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getKeyForStudent(newStudent))
                .withCourseId(testData.courses.get("SCJoinConf.CS2104").getId())
                .withEntityType(Const.EntityType.STUDENT);
        CourseJoinConfirmationPage confirmationPage = loginToPage(
                joinLink, CourseJoinConfirmationPage.class, newStudentGoogleId);

        Results results = getAxeBuilder().analyze(confirmationPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
