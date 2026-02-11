package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorStudentRecordsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_RECORDS_PAGE}.
 */
public class InstructorStudentRecordsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorStudentRecordsPageE2ETestSql.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl recordsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                .withCourseId(testData.courses.get("CS2104").getId())
                .withStudentEmail(testData.students.get("benny.c.tmms@ISR.CS2104").getEmail());

        InstructorStudentRecordsPage recordsPage =
                loginToPage(recordsPageUrl, InstructorStudentRecordsPage.class,
                testData.instructors.get("teammates.test.CS2104").getGoogleId());

        Results results = getAxeBuilder().analyze(recordsPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
