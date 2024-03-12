package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.CourseJoinConfirmationPage;

/**
 * SUT: {@link Const.WebPageURIs#JOIN_PAGE}.
 */
public class StudentCourseJoinConfirmationPageAxeTest extends BaseAxeTestCase {
    private StudentAttributes newStudent;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentCourseJoinConfirmationPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/StudentCourseJoinConfirmationPageE2ETest_SqlEntities.json"));

        newStudent = testData.students.get("alice.tmms@SCJoinConf.CS2104");
        newStudent.setGoogleId(sqlTestData.accounts.get("alice.tmms").getGoogleId());
    }

    @Test
    @Override
    public void testAll() {
        AppUrl joinLink = createFrontendUrl(Const.WebPageURIs.JOIN_PAGE)
                .withRegistrationKey(getKeyForStudent(newStudent))
                .withCourseId(testData.courses.get("SCJoinConf.CS2104").getId())
                .withEntityType(Const.EntityType.STUDENT);
        CourseJoinConfirmationPage confirmationPage = loginToPage(
                joinLink, CourseJoinConfirmationPage.class, newStudent.getGoogleId());

        Results results = getAxeBuilder().analyze(confirmationPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }
}
