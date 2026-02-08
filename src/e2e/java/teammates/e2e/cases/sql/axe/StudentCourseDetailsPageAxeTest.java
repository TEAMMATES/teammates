package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentCourseDetailsPage;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_COURSE_DETAILS_PAGE}.
 */
public class StudentCourseDetailsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/StudentCourseDetailsPageE2ETestSql.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {

        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_COURSE_DETAILS_PAGE)
                .withCourseId("tm.e2e.SCDet.CS2104");
        StudentCourseDetailsPage detailsPage = loginToPage(url, StudentCourseDetailsPage.class,
                testData.students.get("SCDet.alice").getGoogleId());

        Results results = getAxeBuilder().analyze(detailsPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }
}
